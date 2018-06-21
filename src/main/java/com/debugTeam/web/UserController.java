package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.debugTeam.entity.*;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.JsonHelper;
import com.debugTeam.util.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserSevice userSevice;
    @Autowired
    private ProjectService projectService;

    /**
     * 上传者积分充值
     * @param userphone 上传者
     * @param amounts 积分数量，正数
     * @return json
     */
    @PostMapping(value = "/uploaderRecharge", produces="application/text; charset=utf-8")
    public @ResponseBody
    String uploaderRecharge(@RequestParam("userphone") String userphone,
                          @RequestParam("amounts") int amounts){
        Uploader uploader = userSevice.getUploader(userphone);
        uploader.setCredits(amounts, "充值积分");
        userSevice.updateUser(uploader);

        return new ResponseObject(1,"充值成功！").toString();
    }

    /**
     * 得到标注者历史评分信息
     * @param userphone 标注者
     * @return json
     */
    @PostMapping(value = "/makerHistoryRating", produces="application/text; charset=utf-8")
    public @ResponseBody
    String makerHistoryRating(@RequestParam("userphone") String userphone){
        Marker marker = userSevice.getMarker(userphone);
        List<Double> list = marker.getMarkerJobList().stream()
                .filter(job -> job.getScore() == 0)
                .map(MarkerJob::getScore)
                .collect(Collectors.toList());

        List<Double> total_list = new ArrayList<>();
        List<Project> projects = marker.getJobList().stream()
                .map(id -> projectService.getProject(id))
                .collect(Collectors.toList());

        for (Project project : projects){
            ArrayList<String> markers = project.getMarkerList();
            ArrayList<Double> scorelist = new ArrayList<>();

            for(int i=0; i<markers.size(); i++) {
                Marker m = userSevice.getMarker(markers.get(i));
                for (int j = 0; j < m.getJobList().size(); j++) {
                    MarkerJob job = m.getMarkerJobList().get(j);
                    if (project.getId().equals(m.getJobList().get(j))) {
                        scorelist.add(job.getScore());
                        break;
                    }
                }
            }

            total_list.add(scorelist.stream()
                    .collect(Collectors.averagingDouble(id -> (double)id)));
        }

        JSONObject json = new JSONObject();
        json.put("data", list);
        json.put("others", total_list);

        return json.toJSONString();
    }

    /**
     * 标注者积分兑换
     * @param userphone 标注者
     * @param amounts 积分数量，正数
     * @return json
     */
    @PostMapping(value = "/markerExchange", produces="application/text; charset=utf-8")
    public @ResponseBody
    String markerExchange(@RequestParam("userphone") String userphone,
                                  @RequestParam("amounts") int amounts){
        Marker marker = userSevice.getMarker(userphone);
        if (marker.getCredits() < amounts){
            return new ResponseObject(-1, "积分不足，兑换失败").toString();
        }

        marker.setCredits(-amounts,"积分兑换");
        userSevice.updateUser(marker);
        return new ResponseObject(1, "兑换"+amounts+"积分！").toString();
    }

    /**
     * 得到上传者积分明细
     * @param userphone
     * @return {"time": [],
                "detail": [],
                "credits": []}
     */
    @PostMapping(value = "/getUploaderCreditsDetail", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getUploaderCreditsDetail(@RequestParam("userphone") String userphone){
        Uploader uploader = userSevice.getUploader(userphone);
        Map<String, String> map = uploader.getCreditsHistory();

        String json = JsonHelper.creditsHistory2json(map);
        return json;
    }

    /**
     * 得到标注者积分明细
     * @param userphone
     * @return {"time": [],
    "detail": [],
    "credits": []}
     */
    @PostMapping(value = "/getMarkerCreditsDetail", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getMarkerCreditsDetail(@RequestParam("userphone") String userphone){
        Marker marker = userSevice.getMarker(userphone);
        Map<String, String> map = marker.getCreditsHistory();

        String json = JsonHelper.creditsHistory2json(map);
        return json;
    }

    /**
     * 得到标注者勋章
     * @param userphone
     * @return json
     */
    @PostMapping(value = "/getMarkerMedals", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getMarkerMedals(@RequestParam("userphone") String userphone){

        Marker marker = userSevice.getMarker(userphone);
        ArrayList<Medal> medals = marker.getMedalList();

        JSONObject json = new JSONObject();
        json.put("medals", medals);

        return json.toJSONString();
    }

    /**
     * 得到标注者兴趣
     * @param userphone
     * @return json
     */
    @PostMapping(value = "/getMarkerInterests", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getMarkerInterests(@RequestParam("userphone") String userphone){

        Marker marker = userSevice.getMarker(userphone);
        Map<String, Integer> map = new HashMap<>();
        map.put("type", marker.getInterestType());
        map.put("amounts", marker.getInterestNum());

        JSONObject json = new JSONObject();
        json.putAll(map);
        json.put("classificationList", marker.getInterestClassification());

        return json.toJSONString();
    }

    /**
     * 标注者兴趣设定
     * @param type 类型
     * @param amounts 数量
     * @param classificationList 分类
     * @return json
     */
    @PostMapping(value = "/setMarkerInterests", produces="application/text; charset=utf-8")
    public @ResponseBody
    String setMarkerInterests(@RequestParam("userphone") String userphone, @RequestParam("type") int type,
                              @RequestParam("amounts") int amounts,
                              @RequestParam("classificationList") List<String> classificationList){

        Marker marker = userSevice.getMarker(userphone);
        int recordAmounts = 0;
        switch (amounts){
            case 1:
                recordAmounts = 25;
                break;
            case 2:
                recordAmounts = 75;
                break;
            case 3:
                recordAmounts = 150;
                break;
            case 4:
                recordAmounts = 300;
                break;
            default:
                recordAmounts = 75;
                break;
        }
        marker.setInterestNum(recordAmounts);
        marker.setInterestType(type);
        marker.setInterestClassification(new ArrayList<>(classificationList));
        userSevice.updateUser(marker);

        return new ResponseObject(1,"设置成功！").toString();
    }

    /**
     * 得到标注者5维属性图
     * @param phonenum 标注者手机号
     * @return {"reputation": 0.0, "work": 0.0, "empiricalValue": 0.0, "credits": 0.0, "ratings": 0.0}
     * 数值在0~100之间
     */
    @PostMapping(value = "/get5Dstatistics", produces="application/text; charset=utf-8")
    public @ResponseBody
    String get5Dstatistics(@RequestParam("phonenum") String phonenum){
        Marker marker = userSevice.getMarker(phonenum);
        double[] data = marker.getFiveDimension();
        Map<String, Double> map = new HashMap<>();

        map.put("reputation", data[0]);
        map.put("work", data[1]);
        map.put("empiricalValue", data[2]);
        map.put("credits", data[3]);
        map.put("ratings", data[4]);

        ArrayList<Marker> markers = userSevice.getMarkerList();

        double[] total = new double[5];
        for (Marker m : markers){
            double[] tmp_5D = m.getFiveDimension();
            for (int i=0; i<5; i++){
                total[i] += tmp_5D[i];
            }
        }
        if (markers.size()!=0){
            for (int i=0; i<5; i++){
                total[i] /= markers.size();
            }
        }

        map.put("total_reputation", total[0]);
        map.put("total_work", total[1]);
        map.put("total_empiricalValue", total[2]);
        map.put("total_credits", total[3]);
        map.put("total_ratings", total[4]);

        return JSON.toJSONString(map);
    }

    /**
     * 得到上传者的经验和积分
     * @param phonenum 标注者手机号
     * @return json
     */
    @PostMapping(value = "/getCredits", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getCredits(@RequestParam("phonenum") String phonenum){
        Uploader uploader = userSevice.getUploader(phonenum);
        Map<String,String> map = new HashMap<>();

        map.put("credits",uploader.getCredits()+"");
        map.put("empiricalValue",uploader.getEmpiricalValue()+"");
        map.put("username",uploader.getUserName());

        return JSON.toJSONString(map);
    }

    /**
     * 得到标注者的全部个人信息
     * @param phonenum 标注者手机号
     * @return json
     */
    @PostMapping(value = "/getMarkerDetail", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getMarkerDetail(@RequestParam("phonenum") String phonenum){
        Marker marker = userSevice.getMarker(phonenum);
        if(marker!=null){
            return JSON.toJSONString(marker);
        }
        else return "";
    }

    /**
     * 得到标注者排名，前10名
     * @return 格式为 {user1}#{user2}#...#{user10}  #user {"username":"用户名","credits":"积分"}
     */
    @PostMapping(value = "/getUserRank", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getUserRank(){
        return userSevice.getMarkerList()
                .stream()
                .sorted((m1,m2) -> m1.getCredits()>m2.getCredits() ? 1 : 0)
                .limit(10)
                .map(marker -> toRankStr(marker.getUserName(),marker.getCredits()))
                .reduce((s1,s2) -> s1+"#"+s2)
                .orElse("");
    }

    /**
     * 得到标注者自己的排名
     * @param phonenum 标注者手机号
     * @return 排名
     */
    @PostMapping(value = "/getOwnRank", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getOwnRank(@RequestParam("phonenum") String phonenum){

        Marker marker = userSevice.getMarker(phonenum);

        //得到积分大于当前用户的人数
        long rank = userSevice.getMarkerList()
                .stream()
                .filter((m) -> m.getCredits()>marker.getCredits())
                .count();

        return rank+1+"";
    }

    /**
     * 将用户排名信息转换为json格式
     * @param username 用户名
     * @param credits 积分
     * @return json
     */
    private String toRankStr(String username, int credits){
        Map<String,String> map = new HashMap<>();
        map.put("username",username);
        map.put("credits",credits+"");

        return JSON.toJSONString(map);
    }
}
