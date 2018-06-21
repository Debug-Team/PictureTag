package com.debugTeam.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public interface TagService {
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
    boolean addTag(String id, String phoneNum, String picName, String data, int marktime)
            throws FileAlreadyExistsException, FileNotFoundException;

    /**
     * 更新tag
     * @param id 项目编号
     * @param phoneNum 标记者手机号
     * @param picName pic/tag名字
     * @param data 标记数据
     * @return 是否成功
     * @throws FileNotFoundException 文件不存在异常
     */
    boolean updateTag(String id, String phoneNum, String picName, String data, int marktime) throws FileNotFoundException;

    /**
     * 获取tag
     * @param id 项目编号
     * @param phoneNum 标记者手机号
     * @param picName pic/tag名字
     * @return tag的json字符串
     * @throws FileNotFoundException 文件不存在异常
     */
    String getTag(String id, String phoneNum, String picName) throws IOException;
}
