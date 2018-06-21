package com.debugTeam.service.impl;

import com.debugTeam.dao.AdministratorDao;
import com.debugTeam.entity.Administrator;
import com.debugTeam.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl implements AdministratorService{

    @Autowired
    private AdministratorDao administratorDao;

    @Override
    public void updateAdministrator(Administrator administrator) {
        administratorDao.updateAdministrator(administrator);
    }

    @Override
    public Administrator getAdministrator() {
        return administratorDao.getAdministrator();
    }

    @Override
    public void updateDailyMarkerRegisterNum() {
        administratorDao.updateDailyMarkerRegisterNum();
    }

    @Override
    public void updateDailyMarkerLoginNum() {
        administratorDao.updateDailyMarkerLoginNum();
    }

    @Override
    public void updateDailyUploaderRegisterNum() {
        administratorDao.updateDailyUploaderRegisterNum();
    }

    @Override
    public void updateDailyUploaderLoginNum() {
        administratorDao.updateDailyUploaderLoginNum();
    }

    @Override
    public void updateDailyProjectUpload() {
        administratorDao.updateDailyProjectUpload();
    }

    @Override
    public void updateDailyProjectAccept() {
        administratorDao.updateDailyProjectAccept();
    }
}
