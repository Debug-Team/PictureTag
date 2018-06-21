package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.debugTeam.dao.ProjectDao;
import com.debugTeam.dao.UserDao;
import com.debugTeam.dto.ResProject;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.MarkerJob;
import com.debugTeam.entity.Project;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.ClassificationHelper;
import com.debugTeam.util.JsonHelper;
import com.debugTeam.util.ResponseObject;
import com.debugTeam.util.ZipHelper;
import org.apache.commons.math3.analysis.function.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserSevice userSevice;

    private final String tmppath = "data"+File.separator+"tmp";
    private final String proPath = "data"+File.separator+"project";

    /**
     * 热点区域提示，得到该图片的其他人的标注数据
     * @param id
     * @param pic
     * @return { "data" : [tag1, tag2, ... ] }
     */
    @PostMapping(value = "/hotTagArea", produces="application/text; charset=utf-8")
    public @ResponseBody
    String hotTagArea(@RequestParam("id") String id, @RequestParam("pic") String pic){

        String tagPath = "data"+File.separator+"project"+File.separator+id+File.separator+ "tags";
        ArrayList<String> markerList = projectService.getProject(id).getMarkerList();
        ArrayList<String> tags = new ArrayList<>();

        for (int i=0; i<markerList.size(); i++){
            String makerName = markerList.get(i);
            String path = tagPath + File.separator + pic.replaceFirst("\\.", "#") + "_" + makerName + ".txt";
            File file = new File(path);
            try {
                if (file.exists()){
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    tags.add(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HashMap<String,ArrayList<String>> map = new HashMap<>();
        map.put("data",tags);
        return JSON.toJSONString(map);
    }

    /**
     * 为项目评分
     * @param id 项目id
     * @param scoreList 评分list
     * @return 状态
     */
    @PostMapping(value = "/ratingProject", produces="application/text; charset=utf-8")
    public @ResponseBody
    String ratingProject(@RequestParam("id") String id,
                         @RequestParam("scoreList") List<Double> scoreList){
        Project project = projectService.getProject(id);
        project.setRated(true);

        ArrayList<String> markers = project.getMarkerList();
        if (scoreList.size()!=markers.size())
            return new ResponseObject(-1,"未知错误，请联系管理员").toString();

        for(int i=0; i<scoreList.size(); i++) {
            Marker marker = userSevice.getMarker(markers.get(i));
            for (int j = 0; j < marker.getJobList().size(); j++) {
                MarkerJob job = marker.getMarkerJobList().get(j);
                if (id.equals(marker.getJobList().get(j))) {
                    job.setScore(scoreList.get(i));
                    break;
                }
            }
            userSevice.updateUser(marker);
        }

        projectService.updateProject(project);
        return new ResponseObject(1,"评价成功").toString();
    }

    /**
     * 价格预测
     * @param type 类型
     * @param amounts 数量
     * @param classificationList 分类
     * @return 最大，最小，平均，推荐价格
     */
    @PostMapping(value = "/predictCredits", produces="application/text; charset=utf-8")
    public @ResponseBody
    String predictCredits(@RequestParam("type") int type, @RequestParam("amounts") int amounts,
                          @RequestParam("classificationList") List<String> classificationList){
        System.out.println("begin predict");
        ArrayList<Project> projects = projectService.getAllProject();
        Map<Project,Double> map = new HashMap<>();
        int sumDistribute = 0;
        double recommandcredits = 0, avgcredits = 0, maxCredits = 0, minCredits = Integer.MAX_VALUE;
        double totalCredits = 0;

        for(int i = 0; i<projects.size(); i++){
            Project project = projects.get(i);
            double credits = (double) project.getAward() / (project.getPicList().size() * project.getMarkedPersonNum());
            double distribute = countDistribute(project, type, amounts, classificationList);

            map.put(project, distribute);
            sumDistribute += distribute;

            maxCredits = maxCredits > credits ? maxCredits : credits;
            minCredits = minCredits < credits ? minCredits : credits;
            totalCredits += credits;
        }

        for (Map.Entry<Project, Double> entry : map.entrySet()) {
            Project p = entry.getKey();
            recommandcredits += (p.getAward() * 1.0 / (p.getPicList().size()*p.getMarkedPersonNum())) * entry.getValue();
        }

        recommandcredits = sumDistribute != 0 ? recommandcredits / sumDistribute : 0;
        avgcredits = projects.size() != 0 ? totalCredits / projects.size() : 0;

        if (minCredits == Integer.MAX_VALUE) minCredits = 0;

        Map<String,String> res = new HashMap<>();
        res.put("maxCredits", String.format("%.2f", maxCredits*amounts));
        res.put("minCredits", String.format("%.2f", minCredits*amounts));
        res.put("avgCredits", String.format("%.2f", avgcredits*amounts));
        res.put("recommandcredits", String.format("%.2f", recommandcredits*amounts));

        return JSON.toJSONString(res);
    }

    //计算项目相似度，标注类型,种类的权重均为1,数量权重为0.5
    private static double countDistribute(Project project, int type, int amounts, List<String> classificationList){

        //类型
        double distribute = 0;
        if (project.getType() == type) distribute += 1;

        //数据量
        double t = (double) amounts/project.getPicList().size();
        distribute += (t <= 1 ? t : 1/t) * 0.5;

        //种类
        int t1[] = ClassificationHelper.converToIntArray(project.getClassificationList());
        int t2[] = ClassificationHelper.converToIntArray(new ArrayList<>(classificationList));

        double c1 = 0, c2 = 0, c3 = 0;
        for (int i=0; i<t1.length; i++){
            c1 += t1[i]*t2[i];
            c2 += t1[i]*t1[i];
            c3 += t2[i]*t2[i];
        }
        distribute += c1 / (Math.sqrt(c2)*Math.sqrt(c3));

        return distribute;
    }

    /**
     * 得到所有分类信息
     * @return 分类list
     */
    @PostMapping(value = "/getClassification", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getClassification(){
        Map<String, List<String>> map = new HashMap<>();
        map.put("categories", ClassificationHelper.getClassfications());
        return JSON.toJSONString(map);
    }

    /**
     * 标注开始新的标注任务
     * @param phonenum 标注者手机号
     * @param id 项目id
     * @return 状态信息
     */
    @PostMapping(value = "/startNewJob", produces="application/text; charset=utf-8")
    public @ResponseBody
    String startNewJob(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id){
        if(projectService.startNewJob(phonenum,id))
            return new ResponseObject(1,"成功").toString();
        else
            return new ResponseObject(0,"失败").toString();
    }

    /**
     * 发起者关闭项目
     * @param id
     * @return 状态信息
     */
    @PostMapping(value = "/closeproject", produces="application/text; charset=utf-8")
    public @ResponseBody
    String closeExistedProject(@RequestParam("id") String id){
        projectService.closeExistedProject(id);
        return new ResponseObject(1,"关闭成功").toString();
    }

    /**
     * 通过项目id得到项目信息
     * @param id
     * @return project json
     */
    @PostMapping(value = "/getproject", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getProject(@RequestParam String id){
        return JsonHelper.project2json(projectService.getProject(id));
    }

    /**
     * 上传者查看自己的所有项目（预览信息）
     * @param phonenum 上传者账号
     * @return 项目信息 格式为 "{项目1}-{项目2}-{项目3}"
     */
    @PostMapping(value = "/previewproject", produces="application/text; charset=utf-8")
    public @ResponseBody
    String previewProject(@RequestParam("phonenum") String phonenum){

        List<Project> projects = userSevice.getUploader(phonenum).getProjectList()
                .stream()
                .map((id) -> projectService.getProject(id))
                .collect(Collectors.toList());

        for(Project p : projects){
            p.setMarkerList(idsToNameList(p));
        }

        Optional<String> ret = projects
                        .stream()
                        .map((p) -> JSON.toJSONString(p))
                        .reduce((p1,p2) -> p1 + "-;-" + p2);

        return ret.orElse("");
    }

    /**
     * 将project中的用户list由id换为username
     * @param project
     */
    private ArrayList<String> idsToNameList(Project project){
        ArrayList<String> ids = new ArrayList<>();
        for(String id : project.getMarkerList()){
            ids.add(userSevice.getMarker(id).getUserName());
        }
        return ids;
    }

    /**
     * 标注者主界面查看3类项目（预览信息）
     * @param phonenum 标注者账号
     * @param state 状态字 1-推荐项目 2-全部项目 3-我的项目
     * @return 项目信息 格式为 "{项目1}-{项目2}-{项目3}
     */
    @PostMapping(value = "/getprojectlist", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getProjtreamectlist(@RequestParam("phonenum") String phonenum, @RequestParam("state") int state){

        System.out.println(phonenum+state);
        //推荐项目数最大为16
        final int Max_TuiJian_Number = 16;
        Stream<Project> ret = null;

        //推荐项目，迭代3智能推荐
        if(state==1){
            ArrayList<Project> pros = projectService.getAllProject();
            Collections.shuffle(pros);
            ret = pros.stream()
                    .filter((p) -> !p.getMarkerList().contains(phonenum))
                    .filter((p) -> p.getMarkedPersonNum() > p.getMarkerList().size())
                    .filter((p) -> !p.isEnded())
                    .limit(Max_TuiJian_Number);
        //全部项目
        }else if(state==2){
            ret = projectService.getAllProject().stream()
                    .filter((p) -> !p.getMarkerList().contains(phonenum))
                    .filter((p) -> !p.isEnded());
        //我的项目
        }else if(state==3){
            ret = userSevice.getMarker(phonenum).getJobList()
                    .stream()
                    .map((id) -> projectService.getProject(id));
        }

        String str = ret.map((p) -> new ResProject(p).toString())
                .reduce((p1,p2) -> p1 + '-' + p2)
                .orElse("");

        System.out.println(str);
        return str;
    }

    /**
     * 上传项目
     * @param type 项目类型，1-整体标注 2-标框标注 3-轮廓标注
     * @param cut 平台分成，double
     * @param award 总积分奖励
     * @param description 项目描述
     * @param owner 上传者电话号码id
     * @param markedPersonNum 标记人数
     * @param file zip文件
     * @param tags 标记tag的list
     * @param tagcolor 分类标注tag-颜色
     * @param tagState 轮廓标注的状态字
     * @return 上传结果
     */
    @PostMapping(value = "/upload", produces="application/text; charset=utf-8")
    public @ResponseBody
    String startproject(@RequestParam("type") int type,
                        @RequestParam("cut") double cut, @RequestParam("award") int award,
                        @RequestParam("description") String description, @RequestParam("owner") String owner,
                        @RequestParam("markedPersonNum") int markedPersonNum, @RequestParam("file") MultipartFile file,
                        @RequestParam("tags") String tags, @RequestParam("tagcolor") String tagcolor,
                        @RequestParam("tagstate") int tagState, @RequestParam("projectname") String projectname,
                        @RequestParam("categories") List<String> categories){

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String projectId = df.format(new Date());

        if (categories.isEmpty()){
            return new ResponseObject(-4,"未填写分类！如无合适分类请填写其他").toString();
        }

        try{
            //创建临时文件夹放置zip文件
            File tmpfile = new File(tmppath);
            if(!tmpfile.exists()){
                tmpfile.mkdir();
            }

            //文件为空
            if(file.isEmpty())
                return new ResponseObject(0,"文件为空").toString();

            //将zip放在tmp文件夹中
            String filename = file.getOriginalFilename();
            System.out.println(filename);
            if(!filename.endsWith(".zip")){
                return new ResponseObject(-1,"请上传zip格式的压缩文件").toString();
            }
            File zipFile = new File(tmppath+File.separator+projectId+".zip");
            System.out.println(zipFile.getAbsolutePath());
            file.transferTo(zipFile);

            //解压缩
            File proDest = new File(proPath+File.separator+projectId);
            if(!proDest.exists()){
                proDest.mkdir();
            }
            File picDest = new File(proPath+File.separator+projectId+File.separator+"pics");
            if(!picDest.exists()){
                picDest.mkdir();
            }
            File tagsDest = new File(proPath+File.separator+projectId+File.separator+"tags");
            if(!tagsDest.exists()){
                tagsDest.mkdir();
            }
            ZipHelper.unZip(zipFile.getAbsolutePath(),picDest.getAbsolutePath());

            //获取piclist
            String[] names = picDest.list();
            ArrayList<String> picNames = new ArrayList<>();
            try{
                for(String name:names){
                    //避免MacOS生成zip的隐藏文件
                    if(name.equals("__MACOSX"))
                        continue;
                    String[] t = name.split("\\.");
                    String suffix = t[1].toLowerCase();
                    if(!(suffix.equals("jpg")||suffix.equals("png")||suffix.equals("jpeg")))
                        throw new Exception();
                    picNames.add(name);
                }
            }catch (Exception e){
                e.printStackTrace();
                deleteDir(proDest);
                return new ResponseObject(-2,"压缩文件内文件格式错误，请检查后重新上传").toString();
            }

            ArrayList<String> tagArr = new ArrayList<>(Arrays.asList(tags.split("；")));
            Project project = new Project(projectname,type,projectId,cut,award,description,owner,markedPersonNum,tagArr,picNames,tagcolor,tagState);
            project.setClassificationList(new ArrayList<>(categories));
            System.out.println(JSON.toJSON(project));
            projectService.startNewProject(project);
            //测试
            System.out.println(JsonHelper.project2json(projectService.getProject(projectId)));
            //测试end
            return new ResponseObject(1,"上传成功").toString();

        }catch (Exception e){
            e.printStackTrace();
            deleteDir(new File(proPath+File.separator+projectId));
            return new ResponseObject(-3,"服务器异常，请重试").toString();
        }
    }

    public static void deleteDir(File dir){
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for(int i=0; i<files.length; i++) {
                deleteDir(files[i]);
            }
        }
        dir.delete();
    }
}
