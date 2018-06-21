package com.debugTeam.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 11:58 2018/4/1
 * @Modified By:
 */
public interface TagDao {

    /**
     * 新增tag
     * @param id 项目编号
     * @param phoneNum 标记者手机号
     * @param picName pic/tag名字
     * @param data 标记数据
     * @return 是否成功
     * @throws FileAlreadyExistsException 文件重复异常
     * @throws FileNotFoundException 文件不存在异常
     */
    public boolean addTag(String id, String phoneNum, String picName, String data,
                          int aveMarkTime) throws FileAlreadyExistsException, FileNotFoundException;

    /**
     * 更新tag
     * @param id 项目编号
     * @param phoneNum 标记者手机号
     * @param picName pic/tag名字
     * @param data 标记数据
     * @return 是否成功
     * @throws FileNotFoundException 文件不存在异常
     */
    public boolean updateTag(String id, String phoneNum, String picName, String data,
                             int aveMarkTime) throws FileNotFoundException;

    /**
     * 获取tag
     * @param id 项目编号
     * @param phoneNum 标记者手机号
     * @param picName pic/tag名字
     * @return tag的json字符串
     * @throws FileNotFoundException 文件不存在异常
     */
    public String getTag(String id, String phoneNum, String picName) throws IOException;

    /**
     * 获取项目所有标记
     * @param id 项目编号
     * @return 所有的标记列表
     * @throws FileNotFoundException 文件不存在异常
     */
    public ArrayList<String> getTagList(String id) throws FileNotFoundException;

}
