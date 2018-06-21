package com.debugTeam.util;

import com.alibaba.fastjson.JSON;
import com.debugTeam.entity.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    /**
     * 将project对象转换为json
     * @param project
     * @return json字符串
     */
    public static String project2json(Project project) {

        return JSON.toJSONString(project);

    }

    /**
     * 将点坐标转化为json
     * @param page_num 标记张数
     * @param ave_time 平均时间
     * @return json
     */
    public static String pagenum_avetime(int page_num, int ave_time){
        Map<String,Integer> map = new HashMap<>();
        map.put("page_num",page_num);
        map.put("ave_time",ave_time);
        return JSON.toJSONString(map);
    }

    /**
     * 将所有项目的list转换为统计信息json
     * @param projects 项目
     * @return all_total 全部项目 all_closed 全部完成 all_running 全部进行中
     *          前缀1_ 2_ 3_ 分别对应1-整体标注 2-标框标注 3-轮廓标注
     */
    public static String projectTypeStatistics(List<Project> projects){

        //全部项目统计
        Map<String,Long> map = new HashMap<>();
        map.put("all_total",projects.stream().count());
        map.put("all_closed",projects.stream().filter(p -> p.isEnded()).count());
        map.put("all_running",map.get("all_total")-map.get("all_closed"));

        //整体标注统计
        List<Project> type1Stream = Arrays.asList(projects.stream()
                .filter((p) -> p.getType() == 1)
                .toArray(Project[]::new));
        map.put("category_total",type1Stream.stream().count());
        map.put("category_closed",type1Stream.stream().filter(p -> p.isEnded()).count());
        map.put("category_running",map.get("category_total")-map.get("category_closed"));

        //标框标注统计
        List<Project> type2Stream = Arrays.asList(projects.stream()
                .filter((p) -> p.getType() == 2)
                .toArray(Project[]::new));
        map.put("rect_total",type2Stream.stream().count());
        map.put("rect_closed",type2Stream.stream().filter(p -> p.isEnded()).count());
        map.put("rect_running",map.get("rect_total")-map.get("rect_closed"));

        //轮廓标注统计
        List<Project> type3Stream = Arrays.asList(projects.stream()
                .filter((p) -> p.getType() == 3)
                .toArray(Project[]::new));
        map.put("border_total",type3Stream.stream().count());
        map.put("border_closed",type3Stream.stream().filter(p -> p.isEnded()).count());
        map.put("border_running",map.get("border_total")-map.get("border_closed"));

        return JSON.toJSONString(map);
    }

}
