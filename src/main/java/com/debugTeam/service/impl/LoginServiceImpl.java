package com.debugTeam.service.impl;

import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;
import com.debugTeam.service.LoginService;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import com.debugTeam.util.exception.UserWrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService{

    @Autowired
    private UserDao dao;

    @Override
    public int login(String phone, String password) throws UserWrongPasswordException, UserNotExistedException, UserDuplicateException {
        String s = dao.getPassword(phone);
        if(!s.equals(password)){
            throw new UserWrongPasswordException();
        }

        //1为上传者，0为标记着
        if(dao.getMarker(phone)!=null)
            return 0;
        return 1;
    }

    @Override
    public boolean signup(String username, String password, String phone, String type) throws UserDuplicateException {
        //新建上传者
        if(dao.isUserExisted(phone))
            throw new UserDuplicateException();
        if(type.equals("1")){
            Uploader uploader = new Uploader(phone,username,password);
            dao.signUp(uploader);
        }else if(type.equals("0")){
            Marker marker = new Marker(phone,username,password);
            dao.signUp(marker);
        }
        return true;
    }
}
