package com.debugTeam.service;

import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;

import java.util.ArrayList;

public interface UserSevice {

    /**
     * 根据电话号码获取标记者
     * @param phoneNum 电话号码
     * @return 标记者
     */
    public Marker getMarker(String phoneNum);

    /**
     * 根据电话号码获取上传者
     * @param phoneNum 电话号码
     * @return 上传者
     */
    public Uploader getUploader(String phoneNum);

    /**
     * 获取所有标记者的列表，用于计算积分排名
     * @return 标记者列表
     */
    public ArrayList<Marker> getMarkerList();

    /**
     * 获取所有上传者的列表，用于统计计算
     * @return 上传者列表
     */
    public ArrayList<Uploader> getUploaderList();

    /**
     * 更新标记者对象
     * @param marker 新标记者
     * @return 更新是否成功
     */
    public boolean updateUser(Marker marker);
}
