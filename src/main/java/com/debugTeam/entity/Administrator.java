package com.debugTeam.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Administrator implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private int credits;  //平台总盈利

    private HashMap<String, Integer> dailyMarkerRegisterNum;  //每日标记者注册记录
    private HashMap<String, Integer> dailyMarkerLoginNum;  //每日标记者登录记录
    private HashMap<String, Integer> dailyUploaderRegisterNum;  //每日上传者注册记录
    private HashMap<String, Integer> dailyUploaderLoginNum;  //每日上传者登录记录

    private HashMap<String, Integer> dailyProjectUpload;  //每日项目上传数
    private HashMap<String, Integer> dailyProjectAccept;  //每日项目接受数

    private Map<String, String> creditsHistory;  //积分获取记录<date, credits-cause>
    private Map<String, Integer> dailycreditsHistory; //平台每日积分获取<date, credits>
    private Map<String, Integer> totalDailyCreditsHistory; //总积分获取，为了算平台平均分成

    public Administrator() {
        this.dailyMarkerRegisterNum = new HashMap<>();
        this.dailyMarkerLoginNum = new HashMap<>();
        this.dailyUploaderRegisterNum = new HashMap<>();
        this.dailyUploaderLoginNum = new HashMap<>();
        this.dailyProjectUpload = new HashMap<>();
        this.dailyProjectAccept = new HashMap<>();

        this.creditsHistory = new HashMap<>();
        this.dailycreditsHistory = new HashMap<>();
        this.totalDailyCreditsHistory = new HashMap<>();
    }

    //记录平台总盈利
    public void setCredits(int credits, String cause, int totalCredits) {
        this.credits += credits;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        creditsHistory.put(date, credits + "!" + cause);

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = df2.format(new Date());
        if(dailycreditsHistory.containsKey(date2)){
            dailycreditsHistory.put(date2, dailycreditsHistory.get(date2) + credits);
            totalDailyCreditsHistory.put(date2, totalDailyCreditsHistory.get(date2) + totalCredits);
        }
        else{
            dailycreditsHistory.put(date2, credits);
            totalDailyCreditsHistory.put(date2, totalCredits);
        }
    }

    public void updateDailyMarkerRegisterNum() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyMarkerRegisterNum.containsKey(date))
            dailyMarkerRegisterNum.put(date, dailyMarkerRegisterNum.get(date) + 1);
        else
            dailyMarkerRegisterNum.put(date, 1);
    }

    public void updateDailyMarkerLoginNum() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyMarkerLoginNum.containsKey(date))
            dailyMarkerLoginNum.put(date, dailyMarkerLoginNum.get(date) + 1);
        else
            dailyMarkerLoginNum.put(date, 1);
    }

    public void updateDailyUploaderRegisterNum() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyUploaderRegisterNum.containsKey(date))
            dailyUploaderRegisterNum.put(date, dailyUploaderRegisterNum.get(date) + 1);
        else
            dailyUploaderRegisterNum.put(date, 1);
    }

    public void updateDailyUploaderLoginNum() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyUploaderLoginNum.containsKey(date))
            dailyUploaderLoginNum.put(date, dailyUploaderLoginNum.get(date) + 1);
        else
            dailyUploaderLoginNum.put(date, 1);
    }

    public void updateProjectUpload(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyProjectUpload.containsKey(date))
            dailyProjectUpload.put(date, dailyProjectUpload.get(date) + 1);
        else
            dailyProjectUpload.put(date, 1);
    }

    public void updateProjectAccept(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(dailyProjectAccept.containsKey(date))
            dailyProjectAccept.put(date, dailyProjectAccept.get(date) + 1);
        else
            dailyProjectAccept.put(date, 1);
    }

    public HashMap<String, Integer> getDailyMarkerRegisterNum() {
        return dailyMarkerRegisterNum;
    }

    public HashMap<String, Integer> getDailyMarkerLoginNum() {
        return dailyMarkerLoginNum;
    }

    public HashMap<String, Integer> getDailyUploaderRegisterNum() {
        return dailyUploaderRegisterNum;
    }

    public HashMap<String, Integer> getDailyUploaderLoginNum() {
        return dailyUploaderLoginNum;
    }

    public HashMap<String, Integer> getDailyProjectUpload() {
        return dailyProjectUpload;
    }

    public HashMap<String, Integer> getDailyProjectAccept() {
        return dailyProjectAccept;
    }

    public Map<String, String> getCreditsHistory() {
        return creditsHistory;
    }

    public Map<String, Integer> getDailycreditsHistory() {
        return dailycreditsHistory;
    }

    public Map<String, Integer> getTotalDailyCreditsHistory() {
        return totalDailyCreditsHistory;
    }
}
