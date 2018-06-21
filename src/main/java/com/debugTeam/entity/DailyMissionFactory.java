package com.debugTeam.entity;

import java.util.ArrayList;
import java.util.Random;

public class DailyMissionFactory {
    //随机获取每日任务
    public ArrayList<DailyMission> updateDailyMission() {
        ArrayList<DailyMission> result = new ArrayList<>();
        ArrayList<Integer> mark = new ArrayList<>();
        Random random = new Random();
        int count = 3;
        while (count > 0){
            int num = random.nextInt(7);
            if (!mark.contains(num)) {
                mark.add(num);
                result.add(getDaliyMission(num));
                count--;
            }
        }
        return result;
    }

    //生成每日任务
    public DailyMission getDaliyMission(int type) {
        return new DailyMission(type);
    }
}
