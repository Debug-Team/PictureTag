package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.DailyMission;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.MarkerJob;
import com.debugTeam.entity.Project;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.ResponseObject;
import org.apache.tools.ant.taskdefs.MakeUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MissonController {

    @Autowired
    private UserSevice userSevice;
    @Autowired
    private ProjectService projectService;

    /**
     * 得到悬赏榜单项目
     * @param phonenum 手机号
     * @return 至多10个项目
     */
    @PostMapping(value = "/getWantedList", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getWantedList(@RequestParam("phonenum") String phonenum){

        Project projects[] = projectService.getAllProject().stream()
                .filter((p) -> !p.getMarkerList().contains(phonenum))
                .filter((p) -> !p.isEnded())
                .sorted((p1,p2) -> p1.getRankCredit()>p2.getRankCredit() ? 1 : 0)
                .limit(10)
                .toArray(Project[]::new);

        Map<String,Project[]> map = new HashMap<>();
        map.put("data", projects);
        return JSON.toJSONString(map);
    }

    /**
     * 得到每日任务列表
     * @param phonenum 标注者手机号
     * @return 每日任务列表
     */
    @PostMapping(value = "/getDailyMisson", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getDailyMisson(@RequestParam("phonenum") String phonenum){
        Marker marker = userSevice.getMarker(phonenum);
        marker.initDailyMission();
        userSevice.updateUser(marker);
        ArrayList<DailyMission> missions = marker.getDailyMissionList();
        ArrayList<DailyMission> isDone = new ArrayList<>(), isDoing = new ArrayList<>(), unTake = new ArrayList<>();
        for(int i=0; i<missions.size(); i++){
            DailyMission mission = missions.get(i);
            if (mission.getIsEnded()){
                isDone.add(mission);
                continue;
            }
            if (mission.getIsAccepted()){
                isDoing.add(mission);
                continue;
            }
            unTake.add(mission);
        }

        HashMap<String,ArrayList<DailyMission>> map = new HashMap<>();
        map.put("isDone",isDone);
        map.put("isDoing",isDoing);
        map.put("unTake",unTake);
        return JSON.toJSONString(map);
    }


    /**
     * 接受任务
     * @param phonenum 标注者id
     * @param type 任务类型
     * @return 状态信息
     */
    @PostMapping(value = "/acceptDailyMisson", produces="application/text; charset=utf-8")
    public @ResponseBody
    String acceptDailyMisson(@RequestParam("phonenum") String phonenum, @RequestParam("type") int type){
        Marker marker = userSevice.getMarker(phonenum);
        ArrayList<DailyMission> missions = marker.getDailyMissionList();

        for(int i=0; i<missions.size(); i++){
            DailyMission mission = missions.get(i);
            if (mission.getType() == type){
                mission.setIsAccepted(true);
                userSevice.updateUser(marker);
                return new ResponseObject(1,"接受成功").toString();
            }
        }

        return new ResponseObject(0,"接受失败").toString();
    }

    /**
     * 领取任务奖励
     * @param phonenum 标注者id
     * @param type 任务类型
     * @return 状态信息
     */
    @PostMapping(value = "/acceptAward", produces="application/text; charset=utf-8")
    public @ResponseBody
    String acceptAward(@RequestParam("phonenum") String phonenum, @RequestParam("type") int type){
        Marker marker = userSevice.getMarker(phonenum);
        ArrayList<DailyMission> missions = marker.getDailyMissionList();

        for(int i=0; i<missions.size(); i++){
            DailyMission mission = missions.get(i);
            if (mission.getType() == type){
                mission.setIsAwarded(true);
                marker.setCredits(mission.getAwardCredits()
                        , "完成每日任务 " + mission.getType() + " 奖励");
                marker.setEmpiricalValue(marker.getEmpiricalValue()+mission.getAwardEmpiricalValue());
                userSevice.updateUser(marker);
                return new ResponseObject(1,"领取成功").toString();
            }
        }

        return new ResponseObject(0,"领取失败").toString();
    }

}
