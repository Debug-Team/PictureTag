package com.debugTeam.dao;

import com.debugTeam.entity.Administrator;

public interface AdministratorDao {

    /**
     * 更新管理员
     * @param administrator 管理员
     */
    public void updateAdministrator(Administrator administrator);

    /**
     * 获取管理员
     * @return 管理员
     */
    public Administrator getAdministrator();

    /**
     * 更新标记者日注册
     */
    public void updateDailyMarkerRegisterNum();

    /**
     * 更新标记者日登陆
     */
    public void updateDailyMarkerLoginNum();

    /**
     * 更新上传者日注册
     */
    public void updateDailyUploaderRegisterNum();

    /**
     * 更新上传者日登陆
     */
    public void updateDailyUploaderLoginNum();

    /**
     * 更新项目上传
     */
    public void updateDailyProjectUpload();

    /**
     * 更新项目接受
     */
    public void updateDailyProjectAccept();

}
