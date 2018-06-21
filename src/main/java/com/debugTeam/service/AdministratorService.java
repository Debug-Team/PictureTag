package com.debugTeam.service;

import com.debugTeam.entity.Administrator;

public interface AdministratorService {

    /**
     * 更新管理员
     * @param administrator 管理员
     */
    void updateAdministrator(Administrator administrator);

    /**
     * 获取管理员
     * @return 管理员
     */
    Administrator getAdministrator();

    /**
     * 更新标记者日注册
     */
    void updateDailyMarkerRegisterNum();

    /**
     * 更新标记者日登陆
     */
    void updateDailyMarkerLoginNum();

    /**
     * 更新上传者日注册
     */
    void updateDailyUploaderRegisterNum();

    /**
     * 更新上传者日登陆
     */
    void updateDailyUploaderLoginNum();

    /**
     * 更新项目上传
     */
    void updateDailyProjectUpload();

    /**
     * 更新项目接受
     */
    void updateDailyProjectAccept();
}
