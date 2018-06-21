package com.debugTeam.entity;

import java.io.Serializable;

public class DailyMission implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private int type;  //每日任务种类
    private boolean isAccepted;  //已接收任务
    private boolean isEnded;  //已完成任务
    private boolean isAwarded;  //已领取任务奖励
    private int neededNum;  //任务完成的数目条件
    private int alreadyNum;  //已做的数目
    private int awardCredits;  //奖励积分
    private int awardEmpiricalValue;  //奖励经验
    private String description; //任务描述

    public DailyMission(int type) {
        this.type = type;
        this.isEnded = false;
        this.isAwarded = false;
        this.isAccepted = false;
        this.alreadyNum = 0;
        switch (type) {
            case 0:  //接取两个整体项目
                setMission(2, 25, 35, "接受两个整体项目");
                break;
            case 1:  //接取两个标框项目
                setMission(2, 35, 25,"接受两个标框项目");
                break;
            case 2:  //接取两个轮廓项目
                setMission(2, 30, 30,"接受两个轮廓项目");
                break;
            case 3:  //标记10张整体标记图
                setMission(10, 60, 85,"完成10张整体标记图");
                break;
            case 4:  //标记10张标框标记图
                setMission(10, 60, 85,"完成10张标框标记图");
                break;
            case 5:  //标记10张轮廓标记图
                setMission(10, 60, 85,"完成10张轮廓标记图");
                break;
            case 6:  //标记30张图片
                setMission(30, 120, 150,"完成30张图片标注");
                break;
            default:
                break;
        }
    }

    private void setMission(int neededNum, int awardCredits, int awardEmpiricalValue, String description) {
        this.neededNum = neededNum;
        this.awardCredits = awardCredits;
        this.awardEmpiricalValue = awardEmpiricalValue;
        this.description = description;
    }

    //更新已经完成的数量
    public void updateMissionState() {
        this.alreadyNum++;
        if(this.alreadyNum >= this.neededNum) {
            this.isEnded = true;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setIsAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public void setIsEnded(boolean ended) {
        isEnded = ended;
    }

    public void setIsAwarded(boolean awarded) {
        isAwarded = awarded;
    }

    public void setNeededNum(int neededNum) {
        this.neededNum = neededNum;
    }

    public void setAlreadyNum(int alreadyNum) {
        this.alreadyNum = alreadyNum;
    }

    public void setAwardCredits(int awardCredits) {
        this.awardCredits = awardCredits;
    }

    public void setAwardEmpiricalValue(int awardEmpiricalValue) {
        this.awardEmpiricalValue = awardEmpiricalValue;
    }

    public int getType() {
        return type;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public boolean getIsEnded() {
        return isEnded;
    }

    public boolean getIsAwarded() {
        return isAwarded;
    }

    public int getNeededNum() {
        return neededNum;
    }

    public int getAlreadyNum() {
        return alreadyNum;
    }

    public int getAwardCredits() {
        return awardCredits;
    }

    public int getAwardEmpiricalValue() {
        return awardEmpiricalValue;
    }
}
