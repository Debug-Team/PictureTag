package com.debugTeam.dao.impl;

import com.debugTeam.dao.ProjectDao;
import com.debugTeam.dao.TagDao;
import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.DailyMission;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Project;
import com.debugTeam.util.SMSHelper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description: tag输入输出类
 * @Data: Create in 10:53 2018/4/2
 * @Modified By:
 */
@Repository
public class TagDaoImpl implements TagDao {

    //调用系统文件分隔符
    private final String file_separator = System.getProperty("file.separator");
    private final String path = "data" + file_separator + "project";


    public TagDaoImpl(){
        System.out.println("init TagDaoImpl");
        File file = new File("data");
        file.mkdir();

        File projectFile = new File(path);
        projectFile.mkdir();
    }


    @Override
    public boolean addTag(String id, String phoneNum, String picName, String data, int aveMarkTime) throws FileNotFoundException, FileAlreadyExistsException {
        String currentPath = path + file_separator + id;
        String tagPath = currentPath + file_separator + "tags";
        File file = new File(currentPath);
        if(!file.exists())
            throw new FileNotFoundException();

        File tagFile = new File(tagPath);
        if(!file.exists())
            tagFile.mkdir();

        tagPath += file_separator + picName.replaceFirst("\\.", "#") + "_" + phoneNum + ".txt";
        File tmpFile = new File(tagPath);
        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //写入tag
        IOHelper.writeTag(data, tagPath);

        //更新项目信息
        ProjectDao pjd = new ProjectDaoImpl();
        Project project = pjd.getProject(id);
        project.setProcess(project.getProcess()+1);

        //更新标记者信息
        UserDao usd = new UserDaoImpl();
        Marker marker = usd.getMarker(phoneNum);
        marker.updateList(id, picName);  //更新已标记未标记列表
        marker.updateHotMap();  //设置更新热点图
        marker.setTagTimeAnalyse();  //设置每天不同时段(24h)标记图片数统计
        marker.setEmpiricalValue(10);  //每标记一张图片给10点经验
        marker.updateEmpiricalValueMap(10);  //更新每天经验map
        marker.addNewAveMarkTime(aveMarkTime);  //添加新的平均标记时间

        //更新标记者每日任务
        //总标记数的每日更新
        for (DailyMission dm: marker.getDailyMissionList()) {
            if(dm.getType() == 6) {
                dm.updateMissionState();
            }
        }
        //三类标记图片每日任务和成就的更新
        switch (project.getType()) {
            case 1:
                //每日任务
                for (DailyMission dm: marker.getDailyMissionList()) {
                    if(dm.getType() == 3) {
                        dm.updateMissionState();
                    }
                }
                //成就更新
                marker.getMedalList().get(0).updateState();
                break;
            case 2:
                for (DailyMission dm: marker.getDailyMissionList()) {
                    if(dm.getType() == 4) {
                        dm.updateMissionState();
                    }
                }
                marker.getMedalList().get(1).updateState();
                break;
            case 3:
                for (DailyMission dm: marker.getDailyMissionList()) {
                    if(dm.getType() == 5) {
                        dm.updateMissionState();
                    }
                }
                marker.getMedalList().get(2).updateState();
                break;
            default:
                break;
        }

        usd.updateUser(marker);

        //短信通知
        int totalTimes = project.getMarkedPersonNum() * project.getPicList().size();

        //项目100%进度通知
        if(project.getProcess() >= totalTimes && !project.isFullDoneAlarmed()){
            project.setFullDoneAlarmed(true);
            try {
                SMSHelper.sendSMS(id,id,"100%",phoneNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //项目50%进度通知
        if(project.getProcess() >= totalTimes/2 && !project.isHalfDoneAlarmed()){
            project.setHalfDoneAlarmed(true);
            try {
                SMSHelper.sendSMS(id,id,"50%",phoneNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pjd.updateProject(project);

        return true;
    }

    @Override
    public boolean updateTag(String id, String phoneNum, String picName, String data, int aveMarkTime) throws FileNotFoundException {
        String currentPath = path + file_separator + id;
        String tagPath = currentPath + file_separator + "tags";
        File file = new File(currentPath);
        if(!file.exists())
            throw new FileNotFoundException();

        File tagFile = new File(tagPath);
        if(!file.exists())
            tagFile.mkdir();

        tagPath += file_separator + picName.replaceFirst("\\.", "#") + "_" + phoneNum + ".txt";
        try {
            IOHelper.writeTag(data, tagPath);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
        }

        //添加新的平均标记时间
        UserDao usd = new UserDaoImpl();
        Marker marker = usd.getMarker(phoneNum);
        marker.addNewAveMarkTime(aveMarkTime);

        return true;
    }

    @Override
    public String getTag(String id, String phoneNum, String picName) throws IOException {
        String currentPath = path + file_separator + id;
        String tagPath = currentPath + file_separator + "tags";
        File file = new File(currentPath);
        if(!file.exists())
            throw new FileNotFoundException();

        File tagFile = new File(tagPath);
        if(!file.exists())
            tagFile.mkdir();

        tagPath += file_separator + picName.replaceFirst("\\.", "#") + "_" + phoneNum + ".txt";
        String result = IOHelper.readTag(tagPath);
        return result;
    }

    @Override
    public ArrayList<String> getTagList(String id) throws FileNotFoundException {
        ArrayList<String> result = new ArrayList<>();
        String currentPath = path + file_separator + id;
        String tagPath = currentPath + file_separator + "tags";
        File file = new File(currentPath);
        if(!file.exists())
            throw new FileNotFoundException();

        File tagFile = new File(tagPath);
        if(!file.exists())
            tagFile.mkdir();

        String[] tagList = tagFile.list();
        for(String tag : tagList)
            result.add(tag.substring(0, tag.length()-4));

        return result;
    }

}
