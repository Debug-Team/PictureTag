package com.debugTeam.dao.impl;

import com.debugTeam.dao.AdministratorDao;
import com.debugTeam.entity.Administrator;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class AdministratorDaoImpl implements AdministratorDao {

    //调用系统文件分隔符
    private final String file_separator = System.getProperty("file.separator");
    private final String administratorPath = "data" + file_separator + "administrator.ser";

    public AdministratorDaoImpl() {
        System.out.println("init AdministratorDaoImpl");
        File dataFile = new File("data");
        if(!dataFile.exists())
            dataFile.mkdir();
        Administrator administrator = new Administrator();
        updateAdministrator(administrator);
    }

    @Override
    public void updateAdministrator(Administrator administrator) {
        File file = new File(administratorPath);
        System.out.println(file.getAbsolutePath());
        IOHelper.writeProject(administrator, administratorPath);
    }

    @Override
    public Administrator getAdministrator() {
        Administrator result = null;
        File AdminFile = new File(administratorPath);

        if(AdminFile.exists())
            result = (Administrator) IOHelper.readProject(administratorPath);
        return result;
    }

    @Override
    public void updateDailyMarkerRegisterNum() {
        Administrator administrator = getAdministrator();
        administrator.updateDailyMarkerRegisterNum();
        updateAdministrator(administrator);
    }

    @Override
    public void updateDailyMarkerLoginNum() {
        Administrator administrator = getAdministrator();
        administrator.updateDailyMarkerLoginNum();
        updateAdministrator(administrator);
    }

    @Override
    public void updateDailyUploaderRegisterNum() {
        Administrator administrator = getAdministrator();
        administrator.updateDailyUploaderRegisterNum();
        updateAdministrator(administrator);
    }

    @Override
    public void updateDailyUploaderLoginNum() {
        Administrator administrator = getAdministrator();
        administrator.updateDailyUploaderLoginNum();
        updateAdministrator(administrator);
    }

    @Override
    public void updateDailyProjectUpload() {
        Administrator administrator = getAdministrator();
        administrator.updateProjectUpload();
        updateAdministrator(administrator);
    }

    @Override
    public void updateDailyProjectAccept() {
        Administrator administrator = getAdministrator();
        administrator.updateProjectAccept();
        updateAdministrator(administrator);
    }

}
