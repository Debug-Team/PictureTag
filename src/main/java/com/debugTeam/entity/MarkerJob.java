package com.debugTeam.entity;

import com.debugTeam.dao.impl.ProjectDaoImpl;
import com.debugTeam.dao.impl.UserDaoImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 21:45 2018/4/12
 * @Modified By:
 */
public class MarkerJob implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private String id;  //项目编号
    private ArrayList<String> picList;  //任务/项目所有图片列表
    private ArrayList<String> unmarkedPicList;  //任务未标记的图片列表
    private ArrayList<String> markedPicList;  //任务已标记的图片列表
    private ArrayList<String> tagList;  //任务的标记数据列表
    private double score;  //任务评分
    private boolean isFinished;  //是否完成任务
    private String startTime; //接受任务起始时间
    private double currentCut;  //接受任务时候的分成

    public MarkerJob(String id, ArrayList<String> picList) {
        this.id = id;
        this.picList = picList;

        this.unmarkedPicList = new ArrayList<>();
        this.unmarkedPicList.addAll(picList);

        this.markedPicList = new ArrayList<>();
        this.tagList = new ArrayList<>();

        this.score = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        this.startTime = df.format(new Date());

        this.currentCut = calculateCurrentCut();
    }

    //更新已标记未标记列表
    public void updateList(String picName) {
        tagList.add(picName);
        markedPicList.add(picName);
        unmarkedPicList.remove(picName);
    }

    //计算接任务时候应该获取多少分成
    private double calculateCurrentCut() {
        return new ProjectDaoImpl().calculateCurrentCut(this.id);
    }

    public double getCurrentCut() {
        return currentCut;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean finished) {
        isFinished = finished;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getPicList() {
        return picList;
    }

    public ArrayList<String> getUnmarkedPicList() {
        return unmarkedPicList;
    }

    public ArrayList<String> getMarkedPicList() {
        return markedPicList;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }
}
