package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.debugTeam.entity.Administrator;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Project;
import com.debugTeam.entity.Uploader;
import com.debugTeam.service.AdministratorService;
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

import java.text.DecimalFormat;
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
    @Autowired
    private AdministratorService administratorService;

    /**
     * 得到当日平台积分明细
     * @param date 时间
     * @return json
     */
    @PostMapping(value = "/dailyProfitsDetail", produces="application/text; charset=utf-8")
    public @ResponseBody
    String dailyProfitsDetail(@RequestParam("date") String date){
        Administrator administrator = administratorService.getAdministrator();
        Map<String, String> map = administrator.getCreditsHistory();
        Map<String, String> resmap = new HashMap<>();


        List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
        for (Map.Entry<String, String> entry : list){
            if(entry.getKey().substring(5,10).equals(date)){
                resmap.put(entry.getKey(), entry.getValue());
            }
        }

        return JsonHelper.creditsHistory2json(resmap);
    }

    /**
     * 平台运行状况
     * 登录注册活跃度，任务注册接取活跃度，积分获取，分成变动
     * @return json
     */
    @PostMapping(value = "/platformStatus", produces="application/text; charset=utf-8")
    public @ResponseBody
    String platformStatus(){
        final int totalDays = 90;
        final int nowadays = 7;

        JSONObject totalLogin = JSON.parseObject(loginSignupCheck(totalDays));
        JSONObject totalProject = JSON.parseObject(ProjectUploadAccept(totalDays));
        JSONObject totalCredits = JSON.parseObject(platformProfits(totalDays));

        int loginT = 0, signupT = 0, uploadT = 0, acceptT = 0, creditsT = 0;
        int loginC = 0, signupC = 0, uploadC = 0, acceptC = 0, creditsC = 0;
        double cutT = 0.0, cutC = 0.0;

        for (int i=0; i<totalDays; i++){
            loginT += ((JSONArray)totalLogin.get("markerLogin")).getInteger(i) +
                    ((JSONArray)totalLogin.get("uploaderLogin")).getInteger(i);
            signupT += ((JSONArray)totalLogin.get("markerSignup")).getInteger(i) +
                    ((JSONArray)totalLogin.get("uploaderSignup")).getInteger(i);

            uploadT += ((JSONArray)totalProject.get("projectUpload")).getInteger(i);
            acceptT += ((JSONArray)totalProject.get("projectAccept")).getInteger(i);

            creditsT += ((JSONArray)totalCredits.get("creditsList")).getInteger(i);
            cutT += ((JSONArray)totalCredits.get("cutList")).getDouble(i);

            //近7天记录
            if (i>=(totalDays-nowadays)){
                loginC += ((JSONArray)totalLogin.get("markerLogin")).getInteger(i) +
                        ((JSONArray)totalLogin.get("uploaderLogin")).getInteger(i);
                signupC += ((JSONArray)totalLogin.get("markerSignup")).getInteger(i) +
                        ((JSONArray)totalLogin.get("uploaderSignup")).getInteger(i);

                uploadC += ((JSONArray)totalProject.get("projectUpload")).getInteger(i);
                acceptC += ((JSONArray)totalProject.get("projectAccept")).getInteger(i);

                creditsC += ((JSONArray)totalCredits.get("creditsList")).getInteger(i);
                cutC += ((JSONArray)totalCredits.get("cutList")).getDouble(i);
            }
        }

        double loginRate = countUpRate(loginT, loginC);
        double signupRate = countUpRate(signupT, signupC);
        double uploadRate = countUpRate(uploadT, uploadC);
        double acceptRate = countUpRate(acceptT, acceptC);
        double creditsRate = countUpRate(creditsT, creditsC);
        double cutRate = countUpRate(cutT, cutC);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        //运营建议
        if (loginRate < 0 && loginRate < -0.1){
            sb1.append("用户活跃度下降明显,");
            sb2.append("建议多做折扣福利活动,");
        }
        if (signupRate < 0 && signupRate < -0.1){
            sb1.append("用户注册量明显下降,");
            sb2.append("可以多投放广告，做网站推广,");
        }
        if (signupRate < 0 && signupRate < -0.1){
            sb1.append("标注者接取明显下降,");
            sb2.append("可以加大给标注者的分成,");
        }
        if (creditsRate < 0 && creditsRate < -0.1){
            sb1.append("平台积分赚取显著下降，");
            sb2.append("可以调整平台策略");
        }
        if (cutRate < 0 && cutRate < -0.1){
            sb1.append("平台分成持续下降，");
        }

        //运行状况得分
        double base = 60;
        double complicatedRate = (loginRate + signupRate + uploadRate +
                acceptRate + creditsRate + cutRate) / 6;
        double score = (int) (base + complicatedRate * 8);
        score = score >= 100 ? 100 : score;
        score = score <= 0 ? 0 : score;

        JSONObject json = new JSONObject();
        json.put("loginRate", loginRate);
        json.put("signupRate", signupRate);
        json.put("uploadRate", uploadRate);
        json.put("acceptRate", acceptRate);
        json.put("creditsRate", creditsRate);
        json.put("cutRate", cutRate);
        json.put("score", score);
        json.put("recommand", sb1.append(" 建议：").append(sb2).toString());

        return json.toString();
    }

    //计算增长率
    private double countUpRate(double total, double now){
        final int totalDays = 90;
        final int nowadays = 7;

        double nows = now / nowadays * totalDays;
        double rate = (total == 0 ? 0 : (nows - total) / total);

        DecimalFormat dFormat=new DecimalFormat("#.00");
        String s= dFormat.format(rate);
        Double t = Double.valueOf(s);

        return t;
    }

    /**
     * 7日平台盈利情况表（积分+分成）
     * @return json
     */
    @PostMapping(value = "/platformProfits", produces="application/text; charset=utf-8")
    public @ResponseBody
    String platformProfits(){
        return platformProfits(7);
    }

    /**
     * 7日项目上传和接取统计
     * @return json
     */
    @PostMapping(value = "/ProjectUploadAccept", produces="application/text; charset=utf-8")
    public @ResponseBody
    String ProjectUploadAccept(){
        return ProjectUploadAccept(7);
    }

    /**
     * 7日登录注册数据统计
     * @return json
     */
    @PostMapping(value = "/loginSignupCheck", produces="application/text; charset=utf-8")
    public @ResponseBody
    String loginSignupCheck(){
        return loginSignupCheck(7);
    }

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


    /**
     * 平台盈利情况表（积分+分成）
     * @param days 天数
     * @return json
     */
    private String platformProfits(int days){

        Administrator administrator = administratorService.getAdministrator();
        Map<String, Integer> map = administrator.getDailycreditsHistory();
        Map<String, Integer> totalMap = administrator.getTotalDailyCreditsHistory();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Integer> creditsList = new ArrayList<>();
        ArrayList<Double> cutList = new ArrayList<>();

        //设定初始时间为当前时间-6天
        Calendar celender = Calendar.getInstance();
        celender.setTime(new Date());
        celender.add(Calendar.DATE, -(days-1));

        for(int i = 0; i<days; i++){
            celender.add(Calendar.DATE,+1);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(celender.getTime());
            dateList.add(date.substring(5));
            creditsList.add(map.getOrDefault(date,0));

            if (map.containsKey(date)){
                int t = totalMap.get(date);
                double cut = t == 0 ? 0.25 : map.get(date) * 1.0 / totalMap.get(date);
                cut = ((int)(cut * 100)) * 1.0 / 100;
                cutList.add(cut);
            }
            else{
                cutList.add(0.25);
            }
        }

        JSONObject json = new JSONObject();
        json.put("dateList",dateList);
        json.put("creditsList",creditsList);
        json.put("cutList",cutList);

        return json.toJSONString();
    }


    /**
     * 项目上传和接取统计
     * @param days 天数
     * @return json
     */
    private String ProjectUploadAccept(int days){

        Administrator administrator = administratorService.getAdministrator();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Integer> projectUpload = new ArrayList<>();
        ArrayList<Integer> projectAccept = new ArrayList<>();

        //设定初始时间为当前时间-6天
        Calendar celender = Calendar.getInstance();
        celender.setTime(new Date());
        celender.add(Calendar.DATE,-(days-1));

        for(int i = 0; i<days; i++){
            celender.add(Calendar.DATE,+1);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(celender.getTime());
            dateList.add(date.substring(5));
            projectUpload.add(administrator.getDailyProjectUpload().getOrDefault(date,0));
            projectAccept.add(administrator.getDailyProjectAccept().getOrDefault(date,0));
        }

        JSONObject json = new JSONObject();
        json.put("dateList",dateList);
        json.put("projectUpload",projectUpload);
        json.put("projectAccept",projectAccept);

        return json.toJSONString();
    }


    /**
     * 登录注册统计
     * @param days 天数
     * @return json
     */
    private String loginSignupCheck(int days){

        Administrator administrator = administratorService.getAdministrator();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Integer> markerLogin = new ArrayList<>();
        ArrayList<Integer> markerSignup = new ArrayList<>();
        ArrayList<Integer> uploaderLogin = new ArrayList<>();
        ArrayList<Integer> uploaderSignup = new ArrayList<>();

        //设定初始时间为当前时间-6天
        Calendar celender = Calendar.getInstance();
        celender.setTime(new Date());
        celender.add(Calendar.DATE,-(days-1));

        for(int i = 0; i<days; i++){
            celender.add(Calendar.DATE,+1);
            String today = new SimpleDateFormat("yyyy-MM-dd").format(celender.getTime());
            dateList.add(today.substring(5));
            markerLogin.add(administrator.getDailyMarkerLoginNum().getOrDefault(today,0));
            markerSignup.add(administrator.getDailyMarkerRegisterNum().getOrDefault(today,0));
            uploaderLogin.add(administrator.getDailyUploaderLoginNum().getOrDefault(today,0));
            uploaderSignup.add(administrator.getDailyUploaderRegisterNum().getOrDefault(today,0));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dateList",dateList);
        jsonObject.put("markerLogin",markerLogin);
        jsonObject.put("markerSignup",markerSignup);
        jsonObject.put("uploaderLogin",uploaderLogin);
        jsonObject.put("uploaderSignup",uploaderSignup);

        return jsonObject.toJSONString();
    }

}
