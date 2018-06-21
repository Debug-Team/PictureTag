package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Project;
import com.debugTeam.entity.Uploader;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.JsonHelper;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计数据controller
 */
@Controller
public class StatisticsController {

    @Autowired
    private UserSevice userSevice;
    @Autowired
    private ProjectService projectService;

    /**
     * 平均标记时间和标记张数分析
     * @param phonenum 标记者手机号
     * @return json
     */
    @PostMapping(value = "/tagTimeCrossNum", produces="application/text; charset=utf-8")
    public @ResponseBody
    String tagTimeCrossNum(@RequestParam("phonenum") String phonenum){
        ArrayList<Integer> nums = userSevice.getMarker(phonenum).getAveMarkTimeList();
        ArrayList<String> data = new ArrayList<>();
        int total = 0;
        for(int i=0; i<nums.size(); i++){
            total += nums.get(i);
            String s = JsonHelper.pagenum_avetime(i+1,total/(i+1));
            data.add(s);
        }
        Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>(){{put("data",data);}};
        return JSON.toJSONString(map);
    }

    /**
     * 管理员 用户数和标记张数统计
     * @return
     */
    @PostMapping(value = "/userNumCrossTagNums", produces="application/text; charset=utf-8")
    public @ResponseBody
    String userNumCrossTagNums(){
        TreeMap<Integer,Integer> map = new TreeMap<>();
        ArrayList<Marker> markers = userSevice.getMarkerList();
        for(int i=0;i<markers.size();i++){
            int num[] = markers.get(i).getJobTypeNum();
            int total = num[0]+num[1]+num[2];
            if(map.containsKey(total)){
                map.put(total,map.get(total)+1);
            }
            else{
                map.put(total,1);
            }
        }

        //原始数据
        ArrayList<Map<String,Integer>> res = new ArrayList<>();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()){
            int i = (Integer) it.next();
            res.add(new HashMap<String,Integer>(){{put("x",i);put("y",map.get(i));}});
        }

        //正态分布
        ArrayList<Map<String,Double>> normalres = new ArrayList<>();
        WeightedObservedPoints obs = new WeightedObservedPoints();
        it = map.keySet().iterator();
        while (it.hasNext()){
            int i = (Integer) it.next();
            obs.add(i,map.get(i));
        }
        //根据拟合结果得到参数
        List<WeightedObservedPoint> list = obs.toList();
        if(list.size()>3) {
            double[] parameters = GaussianCurveFitter.create().fit(list);
            NormalDistribution normal = new NormalDistribution(parameters[1], parameters[2]);
            double l = map.firstKey();
            double r = map.lastKey();
            for (double i = l; i <= r; i += (r - l) / 20) {
                double x = i;
                normalres.add(new HashMap<String, Double>() {{
                    put("x", x);
                    put("y", normal.density(x));
                }});
            }
        }

        return JSON.toJSONString(res) + "-;-" + JSON.toJSONString(normalres);
    }

    /**
     * 上传者查看三类项目统计
     * @param phonenum 上传者手机号
     * @return 格式与管理员统计一致
     */
    @PostMapping(value = "/get3type", produces="application/text; charset=utf-8")
    public @ResponseBody
    String get3type(@RequestParam("phonenum") String phonenum){

        Uploader uploader = userSevice.getUploader(phonenum);
        List<Project> projects
                    = Arrays.asList(uploader.getProjectList()
                                        .stream()
                                        .map((id) -> projectService.getProject(id))
                                        .toArray(Project[]::new));

        return JsonHelper.projectTypeStatistics(projects);
    }

    /**
     * 标记者查看三类项目统计
     * @param phonenum 标注者手机号
     * @return 格式为{"1":x,"2":y,"3":z} 1-整体标注 2-标框标注 3-轮廓标注
     */
    @PostMapping(value = "/marker3type", produces="application/text; charset=utf-8")
    public @ResponseBody
    String marker3type(@RequestParam("phonenum") String phonenum){
        Marker marker = userSevice.getMarker(phonenum);
        int[] data = marker.getJobTypeNum();

        Map<String,Integer> map = new HashMap<>();
        map.put("1",data[0]);
        map.put("2",data[1]);
        map.put("3",data[2]);

        return JSON.toJSONString(map);
    }


    /**
     * 管理员查看所有项目统计信息
     * @return all_total 全部项目 all_closed 全部完成 all_running 全部进行中
     *          前缀1_ 2_ 3_ 分别对应1-整体标注 2-标框标注 3-轮廓标注
     */
    @PostMapping(value = "/projectCount", produces="application/text; charset=utf-8")
    public @ResponseBody
    String projectCount(){
        ArrayList<Project> projects= projectService.getAllProject();

        return JsonHelper.projectTypeStatistics(projects);
    }

    /**
     * 管理员查看所有用户统计信息
     * @return JSON {"markerNum":标记者人数 , "uploaderNum":上传者人数 , "totalNum":总人数}
     */
    @PostMapping(value = "/peopleCount", produces="application/text; charset=utf-8")
    public @ResponseBody
    String peopleCount(){
        int markerNum = userSevice.getMarkerList().size();
        int uploaderNum = userSevice.getUploaderList().size();

        Map<String,Integer> map = new HashMap<>();
        map.put("markerNum",markerNum);
        map.put("uploaderNum",uploaderNum);
        map.put("totalNum",markerNum+uploaderNum);

        return JSON.toJSONString(map);
    }

    /**
     * 标记者积分获取与网站平均比较
     * @param phonenum 标注者手机号
     * @return json
     */
    @PostMapping(value = "/creditsCmp", produces="application/text; charset=utf-8")
    public @ResponseBody
    String creditsCmp(@RequestParam("phonenum") String phonenum){

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<String> selfData = new ArrayList<>();
        ArrayList<String> totalData = new ArrayList<>();

        //设定初始时间为当前时间-6天
        Calendar celender = Calendar.getInstance();
        celender.setTime(new Date());
        celender.add(Calendar.DATE,-6);

        for(int i = 0; i <7; i++){
            celender.add(Calendar.DATE,+1);
            String today = new SimpleDateFormat("yyyy-MM-dd").format(celender.getTime());
            dateList.add(today.substring(5));
            selfData.add(userSevice.getMarker(phonenum).getCreditsMap().getOrDefault(today,0)+"");
            totalData.add(getTotalAvgCredits(today)+"");
        }

        Map<String, ArrayList<String>> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("selfData",selfData);
        map.put("totalData",totalData);

        return JSON.toJSONString(map);
    }

    /**
     * 得到网站所有标记者平均积分获取
     * @param date 时间
     * @return 平均积分
     */
    private int getTotalAvgCredits(String date){

        ArrayList<Marker> markers = userSevice.getMarkerList();

        int totalCredits = markers.stream()
                .map(marker -> marker.getCreditsMap().getOrDefault(date,0))
                .reduce((c1,c2) -> c1+c2)
                .orElse(0);

        return markers.size() == 0 ? 0 : totalCredits/markers.size();
    }
}
