package com.debugTeam.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClassificationHelper {

    private static String[] classifications = {"人物","食品/饮料","体育","行业/工艺","商业/金融","建筑",
            "旅游/度假","自然/风景","科学/技术","美容/时尚","交通/运输","宗教","动植物","健康/医疗",
            "计算机/通信","生活百科","地点/地标","背景/素材","教育","情感","音乐","其他"};

    /**
     * 得到分类
     * @return
     */
    public static List<String> getClassfications(){
        return Arrays.asList(classifications);
    }

    public static int[] converToIntArray(ArrayList<String> categories){
        HashMap<String,Integer> map = getClassficationMap();
        int res[] = new int[classifications.length];

        for (String s: categories){
            res[map.get(s)] = 1;
        }
        return res;
    }

    public static HashMap<String,Integer> getClassficationMap(){

        HashMap<String,Integer> map = new HashMap<>();
        for (int i=0; i<classifications.length; i++){
            map.put(classifications[i], i);
        }
        return map;
    }
}