package com.debugTeam.entity;

import com.debugTeam.util.SimpleHelper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 20:52 2018/4/1
 * @Modified By:
 */
public class Project implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private String name;  //项目名称
    private String id;  //唯一标识的项目id
    private String owner;  //项目发起者手机号
    private int type;  //项目类型 1-整体标注 2-标框标注 3-轮廓标注
    private double cut;  //项目平台分成，取值范围0-1
    private int award;  //给每个标记者的奖励
    private boolean isEnded;  //项目是否结束
    private boolean isFull;  //选这个项目的是否满员
    private int markedPersonNum;  //这个项目需要多少人进行重复标记
    private String startTime;  //项目开始时间
    private String endTime;  //项目结束时间
    private String description;  //项目描述
    private ArrayList<String> tags;  //上传者预设标签
    private ArrayList<String> picList;  //项目所有图片列表
    private ArrayList<String> markerList;  //项目的标记者列表
    private ArrayList<String> classificationList;  //项目类别标签
    private int process;  //现在的标记进度，已标记多少张图片
    private String tagColor;  //标框标注的{tag-颜色}Json
    private int tagState;  //轮廓标注的标注类型状态
    private boolean isHalfDoneAlarmed; //完成50%进度提示
    private boolean isFullDoneAlarmed; //完成100%进度提示
    private boolean isRated; //是否评分

    public Project(String name, int type, String id, double cut, int award, String description,
                   String owner, int markedPersonNum, ArrayList<String> tags,
                   ArrayList<String> picList, String tagColor, int tagState) {
        this.name = name;
        this.id = id;  //设置主键
        this.owner = owner;
        this.startTime = id;
        this.endTime = "";
        this.type = type;

        this.cut = cut;
        this.award = award;
        this.isEnded = false;
        this.isFull = false;
        this.markedPersonNum = markedPersonNum;
        this.description = description;

        this.tags = tags;
        this.picList = picList;
        this.process = 0;

        this.classificationList = new ArrayList<>();
        this.markerList = new ArrayList<>();
        this.tagColor = tagColor;
        this.tagState = tagState;
        this.isHalfDoneAlarmed = false;
        this.isFullDoneAlarmed = false;
        this.isRated = false;
    }

    //获取悬赏榜单排名分
    public double getRankCredit() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date startDate = null;
        try {
            startDate = df.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        int remainTime = SimpleHelper.daysBetween(startDate, currentDate);  //剩余时间
        int picNum = this.picList.size();  //数据量
        int markerNum = markerList.size();
        int neededMarkerRate = (markedPersonNum - markerNum) / markedPersonNum;  // 剩余需要人数/总人数
        return remainTime + picNum + (neededMarkerRate * 100);
    }

    //添加标记者
    public boolean addMarker(String phoneNum) {
        if(this.markerList.size() < this.markedPersonNum) {
            this.markerList.add(phoneNum);
            //项目人数已满不能再被搜索到
            if(this.markerList.size() == this.markedPersonNum)
                this.isFull = true;
            return true;
        }
        else
            return false;
    }

    //删除标记者
    public boolean deleteMarker(String phoneNum) {
        if(markerList.contains(phoneNum)) {
            this.markerList.remove(phoneNum);
            this.isFull = false;
            return true;
        }
        else
            return false;
    }

    //关闭项目
    public void setClose() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        this.endTime = df.format(new Date());
        isEnded = true;
    }

    public void setClassificationList(ArrayList<String> classificationList) {
        this.classificationList = classificationList;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public void setMarkerList(ArrayList<String> markerList) {
        this.markerList = markerList;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public double getCut() {
        return cut;
    }

    public int getAward() {
        return award;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public boolean isFull() {
        return isFull;
    }

    public ArrayList<String> getClassificationList() {
        return classificationList;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean getIsEnded() {
        return isEnded;
    }

    public boolean getIsFull() {
        return isFull;
    }

    public int getMarkedPersonNum() {
        return markedPersonNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getPicList() {
        return picList;
    }

    public ArrayList<String> getMarkerList() {
        return markerList;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public int getTagState() {
        return tagState;
    }

    public void setTagState(int tagState) {
        this.tagState = tagState;
    }

    public boolean isHalfDoneAlarmed() {
        return isHalfDoneAlarmed;
    }

    public void setHalfDoneAlarmed(boolean halfDoneAlarmed) {
        isHalfDoneAlarmed = halfDoneAlarmed;
    }

    public boolean isFullDoneAlarmed() {
        return isFullDoneAlarmed;
    }

    public void setFullDoneAlarmed(boolean fullDoneAlarmed) {
        isFullDoneAlarmed = fullDoneAlarmed;
    }
}
