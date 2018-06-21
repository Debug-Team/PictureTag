package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.debugTeam.dao.ProjectDao;
import com.debugTeam.dao.UserDao;
import com.debugTeam.dto.ResProject;
import com.debugTeam.entity.*;
import com.debugTeam.service.AdministratorService;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.ClassificationHelper;
import com.debugTeam.util.JsonHelper;
import com.debugTeam.util.ResponseObject;
import com.debugTeam.util.ZipHelper;
import com.debugTeam.util.dbscan.DBScan;
import com.debugTeam.util.dbscan.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    @Autowired
    private AdministratorService administratorService;

    private final String tmppath = "data"+File.separator+"tmp";
    private final String proPath = "data"+File.separator+"project";

    /**
     * 得到所有项目
     * @return json
     */
    @PostMapping(value = "/allprojects", produces="application/text; charset=utf-8")
    public @ResponseBody
    String allprojects(){
        ArrayList<Project> projects = projectService.getAllProject();
        Optional<String> ret = projects
                .stream()
                .map((p) -> JSON.toJSONString(p))
                .reduce((p1,p2) -> p1 + "-;-" + p2);
        return ret.orElse("");
    }
    /**
     * 得到当日上传的项目
     * @param date 时间
     * @return json
     */
    @PostMapping(value = "/dailyProjects", produces="application/text; charset=utf-8")
    public @ResponseBody
    String dailyProjects(@RequestParam("date") String date){

        ArrayList<Project> projects = projectService.getAllProject();
        ArrayList<Project> res = new ArrayList<>();
        for (Project p : projects){
            String s = p.getId().substring(4,6) + '-' + p.getId().substring(6,8);
            if (s.equals(date)){
                res.add(p);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projects", res);
        return jsonObject.toString();
    }

    /**
     * 标注者判断项目是否已完成
     * @param phonenum 手机号
     * @param id 项目
     * @return
     */
    @PostMapping(value = "/markerJobIsFinished", produces="application/text; charset=utf-8")
    public @ResponseBody
    String markerJobIsFinished(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id){
        Marker marker = userSevice.getMarker(phonenum);
        MarkerJob job = marker.getMarkerJob(id);
        boolean flag = false;

        if (job!=null){
            flag = job.getIsFinished();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isFinished", flag);
        return jsonObject.toString();
    }

    /**
     * 得到项目评分
     * @param id 项目id
     * @return
     */
    @PostMapping(value = "/getProjectRate", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getProjectRate(@RequestParam("id") String id){
        Project project = projectService.getProject(id);

        ArrayList<String> markers = project.getMarkerList();
        ArrayList<Double> scorelist = new ArrayList<>();

        for(int i=0; i<markers.size(); i++) {
            Marker marker = userSevice.getMarker(markers.get(i));
            for (int j = 0; j < marker.getJobList().size(); j++) {
                MarkerJob job = marker.getMarkerJobList().get(j);
                if (id.equals(marker.getJobList().get(j))) {
                    scorelist.add(job.getScore());
                    break;
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("scorelist", scorelist);
        return json.toString();
    }

    /**
     * 热点区域提示，得到该图片的其他人的标注数据
     * @param id
     * @param pic
     *
     * @return { "data" : [tag1, tag2, ... ] }
     */
    @PostMapping(value = "/hotTagArea", produces="application/text; charset=utf-8")
    public @ResponseBody
    String hotTagArea(@RequestParam("id") String id, @RequestParam("pic") String pic,
                      @RequestParam("userphone") String userphone){

        String tagPath = "data"+File.separator+"project"+File.separator+id+File.separator+ "tags";
        ArrayList<String> markerList = projectService.getProject(id).getMarkerList();
        ArrayList<String> tags = new ArrayList<>();
        int x1=0, y1=0, z1=0, n1=0, num=0;

        for (int i=0; i<markerList.size(); i++){
            String makerName = markerList.get(i);
            if (makerName.equals(userphone)){
                continue;
            }
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

        ArrayList<Point> points = new ArrayList<>();
        for (String text : tags){
            JSONObject json = JSONObject.parseObject(text);
            JSONObject coordinates = (JSONObject) json.get("coordinates");
            double x = coordinates.getDouble("start_x");
            double y = coordinates.getDouble("start_y");
            double z = coordinates.getDouble("end_x");
            double n = coordinates.getDouble("end_y");
            Point point = new Point(x,y,z,n);
            points.add(point);
        }

        //聚类
        DBScan dbScan = new DBScan(100,3);
        dbScan.process(points);
        //找最大簇
        int maxCluster = 0, max = 0;
        Map<Integer, Integer> table = new HashMap<>();
        for (Point p:points) {
            if (!table.containsKey(p.getCluster())){
                table.put(p.getCluster(), 1);
            }
            else{
                table.put(p.getCluster(), table.get(p.getCluster())+1);
            }
        }
        for (Integer key : table.keySet()){
            if (table.get(key) > max){
                maxCluster = key;
            }
        }
        //找质心
        for (Point p : points){
            if (p.getCluster() == maxCluster){
                num++;
                x1+=p.x;
                y1+=p.y;
                z1+=p.z;
                n1+=p.n;
            }
        }
        if (num!=0){
            x1/=num; y1/=num; z1/=num; n1/=num;
        }

//        HashMap<String,ArrayList<String>> map = new HashMap<>();
//        map.put("data",tags);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sx",x1);
        jsonObject.put("sy",y1);
        jsonObject.put("ex",z1);
        jsonObject.put("ey",n1);

        return jsonObject.toString();
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
            double credits = (double) project.getAward() / (project.getPicList().size());
            double distribute = countDistribute(project, type, amounts, ClassificationHelper.converToIntArray(new ArrayList<>(classificationList)));

            map.put(project, distribute);
            sumDistribute += distribute;

            maxCredits = maxCredits > credits ? maxCredits : credits;
            minCredits = minCredits < credits ? minCredits : credits;
            totalCredits += credits;
        }

        for (Map.Entry<Project, Double> entry : map.entrySet()) {
            Project p = entry.getKey();
            recommandcredits += (p.getAward() * 1.0 / (p.getPicList().size())) * entry.getValue();
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
    private static double countDistribute(Project project, double type, double amounts, double[] classificationList){

        //类型
        double distribute = 0.0;
        if (project.getType() == type) distribute += 1;

        //数据量
        double t = amounts/project.getPicList().size();
        distribute += (t <= 1 ? t : 1/t) * 0.5;

        //种类
        double t1[] = ClassificationHelper.converToIntArray(project.getClassificationList());
        double t2[] = classificationList;

        double c1 = 0.0, c2 = 0.0, c3 = 0.0;
        for (int i=0; i<t1.length; i++){
            c1 += t1[i]*t2[i];
            c2 += t1[i]*t1[i];
            c3 += t2[i]*t2[i];
        }
        if ((Math.sqrt(c2)*Math.sqrt(c3) == 0)) return 0.0;
        distribute += (c1 / (Math.sqrt(c2)*Math.sqrt(c3)));
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
     * 标注者结束markerjob
     * @param phonenum 标注者
     * @param id 项目id
     * @return
     */
    @PostMapping(value = "/finishMakerJob", produces="application/text; charset=utf-8")
    public @ResponseBody
    String finishMakerJob(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id){
        Marker marker = userSevice.getMarker(phonenum);
        MarkerJob job = marker.getMarkerJob(id);

        if (job.getMarkedPicList().size() < job.getPicList().size()){
            return new ResponseObject(-1, "项目没有全部标注完毕").toString();
        }
        marker.getMarkerJob(id).setIsFinished(true);
        userSevice.updateUser(marker);

        return new ResponseObject(1,"结束成功").toString();
    }

    /**
     * 从悬赏榜开始新的标注任务
     * @param phonenum 标注者手机号
     * @param id 项目id
     * @return 状态信息
     */
    @PostMapping(value = "/startNewWantedJob", produces="application/text; charset=utf-8")
    public @ResponseBody
    String startNewWantedJob(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id){
        if(projectService.startNewJob(phonenum,id)){
            Marker marker = userSevice.getMarker(phonenum);
            marker.setCredits(25, "接受悬赏任务");
            userSevice.updateUser(marker);
            administratorService.updateDailyProjectAccept();
            return new ResponseObject(1,"成功").toString();
        }
        else
            return new ResponseObject(0,"失败").toString();
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
        if(projectService.startNewJob(phonenum,id)){
            administratorService.updateDailyProjectAccept();
            return new ResponseObject(1,"成功").toString();
        }
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
    String getprojectlist(@RequestParam("phonenum") String phonenum, @RequestParam("state") int state){

        System.out.println(phonenum+state);
        Stream<Project> ret = null;

        //推荐项目，迭代3智能推荐
        if(state==1){
            ret = recommandProject(phonenum).stream();
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

        String str = ret.map((p) -> JSON.toJSONString(p))
                .reduce((p1,p2) -> p1 + "-;-" + p2)
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
     * @param tagcolor 整体标注tag-颜色
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

        Uploader uploader = userSevice.getUploader(owner);
        if (uploader.getCredits() < markedPersonNum*award){
            return new ResponseObject(-4,"积分不足，请积分页面充值！").toString();
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
            Project project = new Project(projectname,type,projectId,0.25,award,description,owner,markedPersonNum,tagArr,picNames,tagcolor,tagState);
            project.setClassificationList(new ArrayList<>(categories));
            projectService.startNewProject(project);
            //测试
            System.out.println(JsonHelper.project2json(projectService.getProject(projectId)));
            //测试end

            administratorService.updateDailyProjectUpload();
            return new ResponseObject(1,"上传成功").toString();

        }catch (Exception e){
            e.printStackTrace();
            deleteDir(new File(proPath+File.separator+projectId));
            return new ResponseObject(-3,"服务器异常，请重试").toString();
        }
    }

    /**
     * 递归删除文件
     * @param dir
     */
    public static void deleteDir(File dir){
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for(int i=0; i<files.length; i++) {
                deleteDir(files[i]);
            }
        }
        dir.delete();
    }

    /**
     * 个性化推荐项目
     * 方案：基于内容（用户本身） + 协同过滤（用户）

             基于内容：
             - 用户填写的兴趣
             - 用户实际的选择

             协同过滤：
             - 相似用户的选择

     * @param phonenum 标注者手机号
     * @return 推荐项目列表
     */
    private List<Project> recommandProject(String phonenum){

        Map<String, Double> contentMap = contentRecommand(phonenum);
        Map<String, Double> collabrativeMap = collabrativeFilter(phonenum);

        for (String key : collabrativeMap.keySet()){
            if (contentMap.containsKey(key)){
                contentMap.replace(key, contentMap.get(key)+collabrativeMap.get(key));
            }
            else{
                contentMap.put(key, collabrativeMap.get(key));
            }
        }

        //降序比较器
        Comparator<Map.Entry<String, Double>> valueComparator = new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (int) (o2.getValue()-o1.getValue());
            }
        };
        List<Map.Entry<String, Double>> list = new ArrayList<>(contentMap.entrySet());
        Collections.sort(list,valueComparator);

        List<Project> resProjects = list
                .stream()
                .map(entry -> projectService.getProject(entry.getKey()))
                .collect(Collectors.toList());

        return resProjects;
    }

    /**
     * 基于内容
     * @param phonenum
     */
    private Map<String, Double> contentRecommand(String phonenum){

        Marker marker = userSevice.getMarker(phonenum);
        double[] data = userLikeCount(marker);

        //初始化数据
        double final_num = data[0];
        double final_type = data[1];
        double[] final_classfications = new double[data.length-2];
        System.arraycopy(data, 2, final_classfications, 0, final_classfications.length);

        //计算
        final int Max_Campare_Number = 1000;
        Map<String, Double> map = new HashMap<>();
        List<Project> projects = projectService.getAllProject()
                .stream()
                .filter((p) -> !p.getMarkerList().contains(phonenum))
                .filter((p) -> p.getMarkedPersonNum() > p.getMarkerList().size())
                .filter((p) -> !p.isEnded())
                .limit(Max_Campare_Number)
                .collect(Collectors.toList());

        for (int i=0; i<projects.size(); i++){
            Project p = projects.get(i);
            map.put(p.getId(), countDistribute(p, final_type, final_num, final_classfications));
        }

        return map;
    }


    /**
     * 协同过滤
     * @param phonenum
     * @return
     */
    private Map<String, Double> collabrativeFilter(String phonenum){

        final int k_near_number = 10;
        Map<String, Double> resMap = new HashMap<>();
        Marker this_marker = userSevice.getMarker(phonenum);
        ArrayList<Marker> markers = userSevice.getMarkerList();
        Map<Marker, Double> simiMap = new HashMap<>();

        for (Marker marker : markers){
            double similarity = campareMarkerSimilarity(this_marker, marker);
            simiMap.put(marker, similarity);
        }

        //降序比较器
        Comparator<Map.Entry<Marker, Double>> valueComparator = new Comparator<Map.Entry<Marker, Double>>() {
            @Override
            public int compare(Map.Entry<Marker, Double> o1,
                               Map.Entry<Marker, Double> o2) {
                return (int) (o2.getValue()-o1.getValue());
            }
        };
        List<Map.Entry<Marker, Double>> list = new ArrayList<>(simiMap.entrySet());
        Collections.sort(list,valueComparator);

        //选出相似最高的k个标注者
        List<Marker> wait_markers = list.stream()
                .limit(k_near_number)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        for (Marker marker : wait_markers){
            List<Project> projects = marker.getJobList().stream()
                    .map(id -> projectService.getProject(id))
                    .filter((p) -> !p.getMarkerList().contains(phonenum))
                    .filter((p) -> p.getMarkedPersonNum() > p.getMarkerList().size())
                    .filter((p) -> !p.isEnded())
                    .collect(Collectors.toList());

            for (Project p : projects){
                resMap.put(p.getId(), 0.5);
            }
        }
        return resMap;
    }

    /**
     * 计算两个用户间的相似度
     * @param m1
     * @param m2
     * @return 相似度 0~1
     */
    private double campareMarkerSimilarity(Marker m1, Marker m2){

        double[] likecount1 = userLikeCount(m1);
        double[] likecount2 = userLikeCount(m2);

        double[] fiveD1 = m1.getFiveDimension();
        double[] fiveD2 = m2.getFiveDimension();

        double c1 = 0, c2 = 0, c3 = 0;
        for (int i=0; i<likecount1.length; i++){
            c1 += likecount1[i]*likecount2[i];
            c2 += likecount1[i]*likecount2[i];
            c3 += likecount1[i]*likecount2[i];
        }
        for (int i=0; i<fiveD1.length; i++){
            c1 += fiveD1[i]*fiveD2[i];
            c2 += fiveD1[i]*fiveD2[i];
            c3 += fiveD1[i]*fiveD2[i];
        }

        if (Math.sqrt(c2)*Math.sqrt(c3)==0) return 0;
        return  c1 / (Math.sqrt(c2)*Math.sqrt(c3));
    }

    /**
     * 计算出用户兴趣和实际选择整合和的选择倾向
     * @param marker
     * @return
     */
    private double[] userLikeCount(Marker marker){

        //用户填写兴趣
        int num = marker.getInterestNum();
        int type = marker.getInterestType();
        double[] categories = ClassificationHelper.converToIntArray(marker.getInterestClassification());

        //用户实际偏好
        List<Project> userProjects = marker.getMarkerJobList()
                .stream()
                .map(job -> projectService.getProject(job.getId()))
                .collect(Collectors.toList());
        int pSize = userProjects.size();

        //获取用户实际偏好数据
        double realNum = 0;
        double realType[] = new double[4];
        double realCategories[] = new double[ClassificationHelper.getClassfications().size()];
        for(int i=0; i<pSize; i++){
            Project p = userProjects.get(i);
            realNum += p.getPicList().size();
            realType[p.getType()]++;
            double[] tmp = ClassificationHelper.converToIntArray(p.getClassificationList());
            for (int j=0; j<tmp.length; j++){
                if (tmp[j]==1){
                    realCategories[j]++;
                }
            }
        }

        //标准化
        double rNum = pSize == 0 ? 0 : realNum / pSize;
        int rType = 1;
        if (realType[rType]<realType[2]){
            rType = 2;
        }
        if (realType[rType]<realType[3]){
            rType = 3;
        }

        //实际与兴趣加权整合
        final double const_Contri = 20;
        double final_num = 0, final_type = 0;
        double[] final_classfications = new double[ClassificationHelper.getClassfications().size()];

        final_num = (const_Contri*num + rNum*pSize) / (const_Contri+pSize);

        if (realType[rType] >= const_Contri){
            final_type = rType;
        }
        else{
            final_type = type;
        }

        for (int i=0; i<categories.length; i++){
            final_classfications[i] = (realCategories[i]+categories[i]*pSize) / (const_Contri+pSize);
        }

        double[] res = new double[final_classfications.length+2];
        res[0] = final_num;
        res[1] = final_type;
        System.arraycopy(final_classfications, 0, res, 2, final_classfications.length);

        return res;
    }

}