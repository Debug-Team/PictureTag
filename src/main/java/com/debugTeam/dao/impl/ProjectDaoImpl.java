package com.debugTeam.dao.impl;

import com.debugTeam.dao.AdministratorDao;
import com.debugTeam.dao.ProjectDao;
import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.Administrator;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Project;
import com.debugTeam.entity.Uploader;
import com.debugTeam.util.SimpleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    @Autowired
    private UserDao userDao;
    @Autowired
    private AdministratorDao administratorDao;

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
            Uploader uploader = userDao.getUploader(project.getOwner());

            //上传者扣除积分
            int cost = -project.getMarkedPersonNum() * project.getAward();  //总花费
            uploader.setCredits(cost, "创建新项目 " + project.getId());

            uploader.setProject(project.getId());
            uploader.setEmpiricalValue(50);  //设置上传者经验奖励

            //更新上传者信息
            userDao.updateUser(uploader);
            return true;
        }
    }

    @Override
    public boolean closeExistedProject(String id) {
        //初始化项目
        Project project = getProject(id);
        //初始化管理员
        Administrator administrator = administratorDao.getAdministrator();

        if(project.getIsEnded() == true)
            return false;
        else {
            //设置结束
            project.setClose();
            //平台分得
            int platformEarned = 0;
            //标记者分得奖励
            ArrayList<String> markerList = project.getMarkerList();
            for(String phoneNum : markerList) {
                Marker marker = userDao.getMarker(phoneNum);
                //标记者奖励
                int award = (int)marker.getMarkerJob(project.getId()).getCurrentCut()
                        * project.getAward();
                //平台分得
                platformEarned += project.getAward() - award;

                marker.setCredits(award, "完成标注任务 " + project.getId() + " 奖励");
                marker.updateCreditsMap(award);

                marker.setEmpiricalValue(35);
                marker.updateEmpiricalValueMap(35);
                //更新标记者信息
                userDao.updateUser(marker);
            }

            //上传者获得经验
            Uploader uploader = userDao.getUploader(project.getOwner());
            uploader.setEmpiricalValue(50);

            //管理员记录平台分成
            int totalCredits = project.getMarkedPersonNum()*project.getAward();
            administrator.setCredits(platformEarned, "项目 " + project.getId() + " 盈利分成", totalCredits);

            //更新上传者信息
            userDao.updateUser(uploader);

            //更新标记者信息
            administratorDao.updateAdministrator(administrator);

            //更新项目信息
            updateProject(project);

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
        Project project = getProject(id);
        //初始化标记者
        Marker marker = userDao.getMarker(phoneNum);
        //初始化上传者
        String uploaderPhoneNum = project.getOwner();
        Uploader uploader = userDao.getUploader(uploaderPhoneNum);

        //标记者获取经验奖励
        marker.setEmpiricalValue(25);
        marker.updateEmpiricalValueMap(25);

        //设置相互之间关系
        if(project.addMarker(marker.getPhoneNum())) {
            marker.chooseNewJob(project.getId(), uploader.getPhoneNum());
            uploader.addMarker(marker.getPhoneNum());

            //更新信息
            updateProject(project);
            userDao.updateUser(uploader);
            userDao.updateUser(marker);

            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean kickOut(String phoneNum, String id) {
        //初始化项目
        Project project = getProject(id);
        //初始化标记者
        Marker marker = userDao.getMarker(phoneNum);
        //初始化上传者
        String uploaderPhoneNum = project.getOwner();
        Uploader uploader = userDao.getUploader(uploaderPhoneNum);

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
        updateProject(project);
        userDao.updateUser(uploader);
        userDao.updateUser(marker);

        return true;
    }

    @Override
    public double calculateCurrentCut(String id) {
        Project project = getProject(id);
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentDate = new Date();
        Date startDate = null;
        try {
            startDate = df.parse(project.getId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //项目已经发布多少天
        int gapDate = SimpleHelper.daysBetween(startDate, currentDate);
        //还需要的标记者占全部标记者的比例
        double neededMarkerRate = 1 - project.getMarkerList().size() / project.getMarkedPersonNum();

        //project list average
        double[] ave = getAveProjectRate();
        int aveGapDate = (int)ave[0];
        double aveNeededMarkerRate = ave[1];

        double rank = 1.5;
        if(aveGapDate != 0 && aveNeededMarkerRate != 0)
            rank = gapDate/aveGapDate + neededMarkerRate/aveNeededMarkerRate;

        if(neededMarkerRate != 1)
            return rank < 1 ? 0.65:
                    rank < 1.5 ? 0.70:
                            rank < 2 ? 0.75:
                                    rank < 2.5 ? 0.80:
                                            rank < 3 ? 0.85: 0.90;
        else
            return 0;
    }

    private double[] getAveProjectRate() {
        double[] result = new double[2];
        ArrayList<Project> projectList = getAllProjects();
        if(projectList == null || projectList.size() == 0) {
            return result;
        }
        else {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Date currentDate = new Date();
            int gapDate = 0;
            double neededMarkerRate = 0.0;
            for(Project project: projectList) {
                Date startDate = null;
                try {
                    startDate = df.parse(project.getId());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                gapDate += SimpleHelper.daysBetween(startDate, currentDate);
                neededMarkerRate += 1 - project.getMarkerList().size() / project.getMarkedPersonNum();
            }
            result[0] = gapDate / projectList.size();
            result[1] = neededMarkerRate / projectList.size();
            return result;
        }
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
