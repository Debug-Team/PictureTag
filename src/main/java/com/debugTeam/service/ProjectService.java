package com.debugTeam.service;

import com.debugTeam.entity.Project;

import java.util.ArrayList;


public interface ProjectService {

    /**
     * 新建一个项目
     * @param project 项目类
     * @return 是否成功
     */
    boolean startNewProject(Project project);

    /**
     * 选择关闭一个项目
     * @param id 项目id
     * @return 是否成功
     */
    boolean closeExistedProject(String id);

    /**
     * 根据id获取项目
     * @param id 项目id
     * @return 项目
     */
    Project getProject(String id);

    /**
     * 得到所有项目
     * @return 得到所有项目
     */
    ArrayList<Project> getAllProject();

    /**
     * 标记者选择开启一个新项目
     * @param phoneNum 标记者手机号
     * @param id 项目编号
     * @return 返回选择是否成功，若项目满人则失败
     */
    boolean startNewJob(String phoneNum, String id);

    /**
     * 踢出一个项目成员
     * @param phoneNum 标记者手机号
     * @param id 项目编号
     * @return 返回选择是否成功，若该人不在项目中则失败
     */
    boolean kickOut(String phoneNum, String id);

    /**
     * 更新项目信息
     * @param project
     * @return 返回是否成功
     */
    boolean updateProject(Project project);
}
