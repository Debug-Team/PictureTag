package com.debugTeam.dao.impl;

import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 10:51 2018/4/2
 * @Modified By:
 */
@Repository
public class UserDaoImpl implements UserDao {

    //调用系统文件分隔符
    private final String file_separator = System.getProperty("file.separator");
    private final String path = "data"+ file_separator + "user";
    private final String markerPath = path + file_separator + "marker";
    private final String uploaderPath = path + file_separator + "uploader";

    public UserDaoImpl(){
        System.out.println("init UserDaoImpl");
        File dataFile = new File("data");
        if(!dataFile.exists())
            dataFile.mkdir();

        File userFile = new File(path);
        if(!userFile.exists())
            userFile.mkdir();

        File markerFile = new File(markerPath);
        if(!markerFile.exists())
            markerFile.mkdir();

        File uploaderFile = new File(uploaderPath);
        if(!uploaderFile.exists())
            uploaderFile.mkdir();
    }

    @Override
    public String getPassword(String phoneNum)
            throws UserNotExistedException, UserDuplicateException {
        String result = null;
        String currentMarkerPath = markerPath + file_separator +phoneNum + ".ser";
        String currentUploaderPath = uploaderPath + file_separator + phoneNum + ".ser";
        File markerFile = new File(currentMarkerPath);
        File uploaderFile = new File(currentUploaderPath);

        if((!markerFile.exists()) && (!uploaderFile.exists()))
            throw new UserNotExistedException();
        else if(markerFile.exists()) {
            Marker marker = (Marker) IOHelper.readProject(currentMarkerPath);
            result = marker.getPassword();
        }
        else if(uploaderFile.exists()) {
            Uploader uploader = (Uploader) IOHelper.readProject(currentUploaderPath);
            result = uploader.getPassword();
        }
        else {
            throw new UserDuplicateException();
        }

        return result;
    }

    @Override
    public Marker getMarker(String phoneNum) {
        Marker result = null;
        String currentMarkerPath = markerPath + file_separator + phoneNum + ".ser";
        File markerFile = new File(currentMarkerPath);

        if(markerFile.exists())
            result = (Marker) IOHelper.readProject(currentMarkerPath);
        return result;
    }

    @Override
    public Uploader getUploader(String phoneNum) {
        Uploader result = null;
        String currentUploaderPath = uploaderPath + file_separator + phoneNum + ".ser";
        File uploaderFile = new File(currentUploaderPath);

        if(uploaderFile.exists())
            result = (Uploader) IOHelper.readProject(currentUploaderPath);
        return result;
    }

    @Override
    public ArrayList<Marker> getMarkerList() {
        ArrayList<Marker> result = new ArrayList<>();
        File markerListFile = new File(markerPath);
        String[] markerNameList = markerListFile.list();
        for (int i = 0; i < markerNameList.length; i++) {
            String currentMarkerPath = markerPath + file_separator + markerNameList[i];
            File markerFile = new File(currentMarkerPath);

            if(markerFile.exists()) {
                Marker marker = (Marker) IOHelper.readProject(currentMarkerPath);
                result.add(marker);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Uploader> getUploaderList() {
        ArrayList<Uploader> result = new ArrayList<>();
        File uploaderListFile = new File(uploaderPath);
        String[] uploaderNameList = uploaderListFile.list();
        for (int i = 0; i < uploaderNameList.length; i++) {
            String currentUploaderPath = uploaderPath + file_separator + uploaderNameList[i];
            File uploaderFile = new File(currentUploaderPath);

            if(uploaderFile.exists()) {
                Uploader uploader = (Uploader) IOHelper.readProject(currentUploaderPath);
                result.add(uploader);
            }
        }
        return result;
    }

    @Override
    public boolean isUserExisted(String phoneNum) {
        String currentMarkerPath = markerPath + file_separator + phoneNum + ".ser";
        String currentUploaderPath = uploaderPath + file_separator + phoneNum + ".ser";
        File markerFile = new File(currentMarkerPath);
        File uploaderFile = new File(currentUploaderPath);
        if((!markerFile.exists()) && (!uploaderFile.exists()))
            return false;
        else
            return true;
    }

    @Override
    public boolean signUp(Marker marker) {
        String phoneNum = marker.getPhoneNum();
        String currentMarkerPath = markerPath + file_separator + phoneNum + ".ser";
        File file = new File(currentMarkerPath);
        if(file.exists())
            return false;
        else {
            IOHelper.writeProject(marker, currentMarkerPath);
            return true;
        }
    }

    @Override
    public boolean signUp(Uploader uploader) {
        String phoneNum = uploader.getPhoneNum();
        String currentUploaderPath = uploaderPath + file_separator + phoneNum + ".ser";
        File file = new File(currentUploaderPath);
        System.out.println(file.getAbsolutePath());
        if(file.exists())
            return false;
        else {
            IOHelper.writeProject(uploader, currentUploaderPath);
            return true;
        }
    }

    @Override
    public boolean updateUser(Uploader uploader) {
        String phoneNum = uploader.getPhoneNum();
        String currentUploaderPath = uploaderPath + file_separator + phoneNum + ".ser";
        File file = new File(currentUploaderPath);
        System.out.println(file.getAbsolutePath());
        if(file.exists()) {
            IOHelper.writeProject(uploader, currentUploaderPath);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean updateUser(Marker marker) {
        String phoneNum = marker.getPhoneNum();
        String currentMarkerPath = markerPath + file_separator + phoneNum + ".ser";
        File file = new File(currentMarkerPath);
        if(file.exists()) {
            IOHelper.writeProject(marker, currentMarkerPath);
            return true;
        }
        else
            return false;
    }

}
