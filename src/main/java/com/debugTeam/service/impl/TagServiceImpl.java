package com.debugTeam.service.impl;

import com.debugTeam.dao.TagDao;
import com.debugTeam.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagDao tagDao;

    @Override
    public boolean addTag(String id, String phoneNum, String picName, String data, int markTime) throws FileAlreadyExistsException, FileNotFoundException {
        return tagDao.addTag(id,phoneNum,picName,data,markTime);
    }

    @Override
    public boolean updateTag(String id, String phoneNum, String picName, String data, int markTime) throws FileNotFoundException {
        return tagDao.updateTag(id,phoneNum,picName,data,markTime);
    }

    @Override
    public String getTag(String id, String phoneNum, String picName) throws IOException{
        return tagDao.getTag(id,phoneNum,picName);
    }
}
