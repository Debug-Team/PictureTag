package com.debugTeam.service;

import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import com.debugTeam.util.exception.UserWrongPasswordException;


public interface LoginService {

    /**
     * 登陆验证
     * @param phone 手机号
     * @param password 密码
     * @return 用户类型
     */
    int login(String phone, String password) throws UserWrongPasswordException, UserNotExistedException, UserDuplicateException;

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param type 用户类型 1为上传者，0为标记者
     * @return 是否成功
     */
    boolean signup(String username, String password,
                           String phone, String type) throws UserDuplicateException;

}
