package com.debugTeam.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 20:52 2018/4/1
 * @Modified By:
 */
public class Uploader implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private String phoneNum;  //电话号码
    private String userName;  //用户名
    private String password;  //密码

    private int empiricalValue;  //经验值
    private int credits;  //积分值，上传者初始有50000

    private ArrayList<String> projectList;  //上传者发布的项目列表
    private ArrayList<String> markerList;  //给自己标记过的标记者列表

    public Uploader(String phoneNum, String userName, String password) {
        this.phoneNum = phoneNum;
        this.userName = userName;
        this.password = password;

        this.empiricalValue = 0;
        this.credits = 50000;

        this.projectList = new ArrayList<>();
        this.markerList = new ArrayList<>();
    }

    //添加新的标记者
    public void addMarker(String markerPhoneNum) {
        this.markerList.add(markerPhoneNum);
    }

    //删除超时的标记者
    public void deleteMarker(String markerPhoneNum) {
        this.markerList.remove(markerPhoneNum);
    }

    public void setProject(String project) {
        this.projectList.add(project);
    }

    public int getEmpiricalValue() {
        return empiricalValue;
    }

    public int getCredits() {
        return credits;
    }

    public void setEmpiricalValue(int empiricalValue) {
        this.empiricalValue += empiricalValue;
    }

    public void setCredits(int credits) {
        this.credits += credits;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getProjectList() {
        return projectList;
    }

    public ArrayList<String> getMarkerList() {
        return markerList;
    }

}
