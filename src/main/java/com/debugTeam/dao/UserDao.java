package com.debugTeam.dao;

import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;

import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 11:58 2018/4/1
 * @Modified By:
 */
public interface UserDao {

    /**
     * 根据电话号码获取密码
     * @param phoneNum 电话号码
     * @return 密码
     * @throws UserNotExistedException 用户不存在异常
     * @throws UserDuplicateException 用户同时存在于上传者和标记者目录的异常
     */
    public String getPassword(String phoneNum) throws UserNotExistedException, UserDuplicateException;

    //获取用户时同时调用两个方法，不存在则返回null
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
     * 获取所有标记者的列表，用于统计计算
     * @return 标记者列表
     */
    public ArrayList<Marker> getMarkerList();

    /**
     * 获取所有上传者的列表，用于统计计算
     * @return 上传者列表
     */
    public ArrayList<Uploader> getUploaderList();

    /**
     * 根据电话号码查找用户是否存在
     * @param phoneNum 电话号码
     * @return 用户是否存在
     */
    public boolean isUserExisted(String phoneNum);

    /**
     * 保存标记者对象
     * @param marker 标记者
     * @return 是否成功
     */
    public boolean signUp(Marker marker);

    /**
     * 保存上传者对象
     * @param uploader 上传者
     * @return 是否成功
     */
    public boolean signUp(Uploader uploader);

    /**
     * 更新上传者对象
     * @param uploader 新上传者
     * @return 更新是否成功
     */
    public boolean updateUser(Uploader uploader);

    /**
     * 更新标记者对象
     * @param marker 新标记者
     * @return 更新是否成功
     */
    public boolean updateUser(Marker marker);
}
