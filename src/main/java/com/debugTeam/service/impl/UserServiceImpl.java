package com.debugTeam.service.impl;

import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;
import com.debugTeam.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserSevice{
    @Autowired
    private UserDao userDao;

    @Override
    public Marker getMarker(String phoneNum) {
        return userDao.getMarker(phoneNum);
    }

    @Override
    public Uploader getUploader(String phoneNum) {
        return userDao.getUploader(phoneNum);
    }

    @Override
    public ArrayList<Marker> getMarkerList() {
        return userDao.getMarkerList();
    }

    @Override
    public ArrayList<Uploader> getUploaderList() {
        return userDao.getUploaderList();
    }

    @Override
    public boolean updateUser(Marker marker) {
        return userDao.updateUser(marker);
    }
}
