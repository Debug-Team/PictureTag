package com.debugTeam.entity;

import com.debugTeam.dao.impl.IOHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 20:51 2018/4/1
 * @Modified By:
 */
public class Marker implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private String phoneNum;  //电话号码
    private String userName;  //用户名
    private String password;  //密码

    private int empiricalValue;  //经验值
    private int credits;  //积分，标记者初始有1000

    //基本统计
    private ArrayList<String> uploaderList;  //接过的项目的上传者列表
    private ArrayList<MarkerJob> jobList;  //接受的标记项目列表，内含具体信息
    private ArrayList<Integer> aveMarkTimeList;  //平均标记时间
//    private ArrayList<Double> scoreList;  //评分列表 0-5分
    private Map<String, Integer> hotMap;  //标记热点图
    private Map<String, Integer> creditsMap;  //每天的积分值
    private Map<String, Integer> empiricalValueMap;  //每天的经验值
    private int[] jobTypeNum;  //三类标注的数量，依次为：整体标注、标框标注、轮廓标注
    private int[] timeMarkNum;  //每个小时段标注的图片数量统计
    private int kickOutCount;  //被踢出数

    //每日任务
    private String lastLoginTime;  //最后登录时间
    private ArrayList<DailyMission> dailyMissionList;  //每日任务列表

    private ArrayList<Medal> medalList;  //勋章墙

    //兴趣
    private int interestNum;  //可能感兴趣标注数量
    private int interestType;  //可能感兴趣类型
    private ArrayList<String> interestClassification;  //可能感兴趣分类

    public Marker(String phoneNum, String userName, String password) {
        this.phoneNum = phoneNum;
        this.userName = userName;
        this.password = password;

        this.empiricalValue = 0;
        this.credits = 1000;

        this.uploaderList = new ArrayList<>();
        this.jobList = new ArrayList<>();
        this.aveMarkTimeList = new ArrayList<>();

        this.jobTypeNum = new int[3];
        this.timeMarkNum = new int[24];

        this.hotMap = new HashMap<>();
        this.creditsMap = new HashMap<>();
        this.empiricalValueMap = new HashMap<>();

        this.lastLoginTime = "";
        this.dailyMissionList = new ArrayList<>();

        this.medalList = new ArrayList<>();
        this.medalList.add(new Medal(1));
        this.medalList.add(new Medal(2));
        this.medalList.add(new Medal(3));

        this.interestNum = 0;
        this.interestType = 0;
        this.interestClassification = new ArrayList<>();

        this.kickOutCount = 0;
    }

    //获取五维数据 0-信誉度 1-勤劳 2-经验 3-积分 4-评分 均为100分制
    public double[] getFiveDimension() {
        double[] result = new double[5];

        //计算信誉
        result[0] = 100 - kickOutCount > 0 ? 100 - kickOutCount : 0;

        //计算勤劳
        double industriousScore = 0;
        for(String date : hotMap.keySet()) {
            industriousScore += hotMap.get(date);
        }
        if(hotMap.size() != 0)
            industriousScore = industriousScore/hotMap.size() + 50;
        if(industriousScore > 100)  //懒得想好的平均标记张数map到100分区间
            industriousScore = 100;
        result[1] = industriousScore;

        //计算经验
        double empiricalValueScore = 0;
        for(String date : empiricalValueMap.keySet()) {
            empiricalValueScore += empiricalValueMap.get(date);
        }
        empiricalValueScore = empiricalValueScore/300 + 50;
        if(empiricalValueScore > 100)  //懒得想...
            empiricalValueScore = 100;
        result[2] = empiricalValueScore;

        //计算积分
        double creditsScore = 0;
        for(String date : creditsMap.keySet()) {
            creditsScore += creditsMap.get(date);
        }
        creditsScore = creditsScore/300 + 50;
        if(creditsScore > 100)  //懒得想...
            creditsScore = 100;
        result[3] = creditsScore;

        //计算评分
        result[4] = 100;
        for(MarkerJob job: jobList) {
            if(job.getScore() != 0) {
                result[4] = jobList.stream()
                        .map(p -> p.getScore())
                        .reduce(Double::sum)
                        .orElse(0.0) / jobList.size() * 20;
                break;
            }
        }

        return result;
    }

    public void increaseKickOutCount() {
        this.kickOutCount++;
    }

    //更新每日任务
    public void initDailyMission() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(!date.equals(this.lastLoginTime)) {
            this.lastLoginTime = date;
            this.dailyMissionList = new DailyMissionFactory().updateDailyMission();
        }
    }

    //添加新任务
    public void chooseNewJob(String id, String uploaderPhoneNum) {
        //初始化项目
        String file_separator = System.getProperty("file.separator");
        Project project = (Project) IOHelper.readProject("data" + file_separator + "project" + file_separator + id + file_separator + id + ".ser");
        MarkerJob mj = new MarkerJob(project.getId(), project.getPicList());

        //添加任务类型统计
        //顺带每日任务的记录
        switch (project.getType()) {
            case 1:
                for (DailyMission dm: dailyMissionList) {
                    if(dm.getType() == 0) {
                        dm.updateMissionState();
                    }
                }
                jobTypeNum[0]++;
                break;
            case 2:
                for (DailyMission dm: dailyMissionList) {
                    if(dm.getType() == 1) {
                        dm.updateMissionState();
                    }
                }
                jobTypeNum[1]++;
                break;
            case 3:
                for (DailyMission dm: dailyMissionList) {
                    if(dm.getType() == 2) {
                        dm.updateMissionState();
                    }
                }
                jobTypeNum[2]++;
                break;
            default:
                break;
        }

        //关联项入队维护
        this.jobList.add(mj);
        this.uploaderList.add(uploaderPhoneNum);
    }

    //添加新任务
    public void quitJob(String id, String uploaderPhoneNum) {
        //初始化项目
        String file_separator = System.getProperty("file.separator");
        Project project = (Project) IOHelper.readProject("data" + file_separator + "project" + file_separator + id + file_separator + id + ".ser");
        MarkerJob mj = getJob(id);

        //删除任务类型统计
        switch (project.getType()) {
            case 1:
                jobTypeNum[0]--;
                break;
            case 2:
                jobTypeNum[1]--;
                break;
            case 3:
                jobTypeNum[2]--;
                break;
            default:
                break;
        }

        //关联项出队维护
        if(jobList.contains(mj))
            this.jobList.remove(mj);
        if(uploaderList.contains(uploaderPhoneNum))
            this.uploaderList.remove(uploaderPhoneNum);
    }

    //根据某个id获取job
    public MarkerJob getJob(String id) {
        MarkerJob result = null;
        for(MarkerJob mj : jobList) {
            if(mj.getId().equals(id))
                result = mj;
        }
        return result;
    }

    //获取标记者承接的所有job列表
    public ArrayList<String> getJobList() {
        ArrayList<String> result = new ArrayList<>();
        for(MarkerJob mj : jobList)
            result.add(mj.getId());
        return result;
    }

    //更新已标记未标记列表
    public void updateList(String id, String picName) {
        for(MarkerJob mj : jobList) {
            if(mj.getId().equals(id))
                mj.updateList(picName);
        }
    }

    //更新每天不同时段(24h)标记图片数统计
    public void setTagTimeAnalyse() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");  //只有时分秒
        String hourTime = sdf.format(new Date()).substring(0, 2);  //只取小时数
        int hour = Integer.parseInt(hourTime);
        timeMarkNum[hour]++;
    }

    //更新热点图
    public void updateHotMap() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(hotMap.containsKey(date))
            hotMap.put(date, hotMap.get(date) + 1);
        else
            hotMap.put(date, 1);
    }

    //更新积分图
    public void updateCreditsMap(int credits) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(creditsMap.containsKey(date))
            creditsMap.put(date, creditsMap.get(date) + credits);
        else
            creditsMap.put(date, credits);
    }

    //更新经验值图
    public void updateEmpiricalValueMap(int empiricalValue) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if(empiricalValueMap.containsKey(date))
            empiricalValueMap.put(date, empiricalValueMap.get(date) + empiricalValue);
        else
            empiricalValueMap.put(date, empiricalValue);
    }

    public ArrayList<Integer> getAveMarkTimeList() {
        return aveMarkTimeList;
    }

    public ArrayList<MarkerJob> getMarkerJobList() { return jobList;}

    //添加新的平均标记时间
    public void addNewAveMarkTime(int aveMarkTime) {
        this.aveMarkTimeList.add(aveMarkTime);
    }

    public int getInterestNum() {
        return interestNum;
    }

    public void setInterestNum(int interestNum) {
        this.interestNum = interestNum;
    }

    public int getInterestType() {
        return interestType;
    }

    public void setInterestType(int interestType) {
        this.interestType = interestType;
    }

    public ArrayList<String> getInterestClassification() {
        return interestClassification;
    }

    public void setInterestClassification(ArrayList<String> interestClassification) {
        this.interestClassification = interestClassification;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public ArrayList<Medal> getMedalList() {
        return medalList;
    }

    public ArrayList<DailyMission> getDailyMissionList() {
        return dailyMissionList;
    }

    public int getEmpiricalValue() {
        return empiricalValue;
    }

    public int[] getJobTypeNum() {
        return jobTypeNum;
    }

    public ArrayList<String> getUploaderList() {
        return uploaderList;
    }

    public Map<String, Integer> getHotMap() {
        return hotMap;
    }

    public Map<String, Integer> getCreditsMap() {
        return creditsMap;
    }

    public Map<String, Integer> getEmpiricalValueMap() {
        return empiricalValueMap;
    }

    public int[] getTimeMarkNum() {
        return timeMarkNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCredits() {
        return credits;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setEmpiricalValue(int empiricalValue) {
        this.empiricalValue += empiricalValue;
    }

    public void setCredits(int credits) {
        this.credits += credits;
    }
}
