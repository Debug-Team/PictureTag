package com.debugTeam.dao.impl;

import com.debugTeam.dao.ProjectDao;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Project;
import com.debugTeam.entity.Uploader;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 20:53 2018/4/1
 * @Modified By:
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

    //调用系统文件分隔符
    private final String file_separator = System.getProperty("file.separator");
    private final String path = "data" + file_separator + "project";

    public ProjectDaoImpl(){
        System.out.println("init ProjectDaoImpl");
        File dataFile = new File("data");
        if(!dataFile.exists())
            dataFile.mkdir();

        File file = new File(path);
        if(!file.exists())
            file.mkdir();
    }

    @Override
    public boolean startNewProject(Project project) {
        String id = project.getId();
        String currentPath = path + file_separator + id;
        File file = new File(currentPath);
        if(!file.exists())
            return false;
        else {
            //储存项目
            currentPath += file_separator + id + ".ser";
            IOHelper.writeProject(project, currentPath);

            //初始化上传者
            UserDaoImpl usd = new UserDaoImpl();
            Uploader uploader = usd.getUploader(project.getOwner());

            //上传者扣除积分
            int cost = -project.getMarkedPersonNum() * project.getAward();  //总花费
            uploader.setCredits(cost);

            uploader.setProject(project.getId());
            uploader.setEmpiricalValue(50);  //设置上传者经验奖励

            //更新上传者信息
            usd.updateUser(uploader);
            return true;
        }
    }

    @Override
    public boolean closeExistedProject(String id) {
        String currentPath = path + file_separator + id + file_separator + id + ".ser";
        Project project = (Project) IOHelper.readProject(currentPath);

        if(project.getIsEnded() == true)
            return false;
        else {
            //设置结束
            project.setClose();

            //标记者分得奖励
            UserDaoImpl usd = new UserDaoImpl();

            int award = project.getAward();
            ArrayList<String> markerList = project.getMarkerList();
            for(String phoneNum : markerList) {
                Marker marker = usd.getMarker(phoneNum);

                marker.setCredits(award);
                marker.updateCreditsMap(award);

                marker.setEmpiricalValue(35);
                marker.updateEmpiricalValueMap(35);
                //更新标记者信息
                usd.updateUser(marker);
            }

            //上传者获得经验
            Uploader uploader = usd.getUploader(project.getOwner());
            uploader.setEmpiricalValue(50);

            //更新上传者信息
            usd.updateUser(uploader);

            //更新项目信息
            IOHelper.writeProject(project, currentPath);

            return true;
        }
    }

    @Override
    public Project getProject(String id) {
        String currentPath = path + file_separator + id + file_separator + id + ".ser";
        Project project = (Project) IOHelper.readProject(currentPath);
        return project;
    }

    @Override
    public ArrayList<Project> getAllProjects() {
        ArrayList<Project> result = new ArrayList<>();
        File file = new File(path);
        String[] projectIdList = file.list();
        for(String id : projectIdList) {
            //去掉
            if(!id.equals(".DS_Store"))
                result.add(getProject(id));
        }
        return result;
    }

    @Override
    public boolean startNewJob(String phoneNum, String id) {
        //初始化项目
        String projectPath = path + file_separator + id + file_separator + id + ".ser";
        Project project = (Project) IOHelper.readProject(projectPath);
        //初始化标记者
        String markPath = "data"+ file_separator + "user" + file_separator + "marker" + file_separator + phoneNum + ".ser";
        Marker marker = (Marker) IOHelper.readProject(markPath);
        //初始化上传者
        String uploaderPhoneNum = project.getOwner();
        String uploaderPath = "data"+ file_separator + "user" + file_separator + "uploader"
                + file_separator + uploaderPhoneNum + ".ser";
        Uploader uploader = (Uploader) IOHelper.readProject(uploaderPath);

        //标记者获取经验奖励
        marker.setEmpiricalValue(25);
        marker.updateEmpiricalValueMap(25);

        //设置相互之间关系
        if(project.addMarker(marker.getPhoneNum())) {
            marker.chooseNewJob(project.getId(), uploader.getPhoneNum());
            uploader.addMarker(marker.getPhoneNum());

            //更新信息
            IOHelper.writeProject(project, projectPath);
            IOHelper.writeProject(uploader, uploaderPath);
            IOHelper.writeProject(marker, markPath);

            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean kickOut(String phoneNum, String id) {
        //初始化项目
        String projectPath = path + file_separator + id + file_separator + id + ".ser";
        Project project = (Project) IOHelper.readProject(projectPath);
        //初始化标记者
        String markPath = "data"+ file_separator + "user" + file_separator + "marker" + file_separator + phoneNum + ".ser";
        Marker marker = (Marker) IOHelper.readProject(markPath);
        //初始化上传者
        String uploaderPhoneNum = project.getOwner();
        String uploaderPath = "data"+ file_separator + "user" + file_separator + "uploader"
                + file_separator + uploaderPhoneNum + ".ser";
        Uploader uploader = (Uploader) IOHelper.readProject(uploaderPath);

        if(!project.getMarkerList().contains(marker.getPhoneNum()))
            return false;

        //相关信息维护
        project.deleteMarker(marker.getPhoneNum());
        marker.quitJob(project.getId(), uploader.getPhoneNum());
        uploader.deleteMarker(marker.getPhoneNum());

        //扣除经验值
        marker.setEmpiricalValue(-25);
        marker.updateEmpiricalValueMap(-25);

        //统计踢出次数
        marker.increaseKickOutCount();

        //更新信息
        IOHelper.writeProject(project, projectPath);
        IOHelper.writeProject(uploader, uploaderPath);
        IOHelper.writeProject(marker, markPath);

        return true;
    }

    @Override
    public boolean updateProject(Project project) {
        String id = project.getId();
        String currentPath = path + file_separator + id;
        File file = new File(currentPath);
        if(file.exists()) {
            currentPath += file_separator + id + ".ser";
            IOHelper.writeProject(project, currentPath);
            return true;
        }
        else
            return false;
    }
}
