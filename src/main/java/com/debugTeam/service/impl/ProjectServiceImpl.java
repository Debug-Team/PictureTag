package com.debugTeam.service.impl;

import com.debugTeam.dao.ProjectDao;
import com.debugTeam.entity.Project;
import com.debugTeam.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Override
    public boolean startNewProject(Project project) {
        return projectDao.startNewProject(project);
    }

    @Override
    public boolean closeExistedProject(String id) {
        return projectDao.closeExistedProject(id);
    }

    @Override
    public Project getProject(String id) {
        return projectDao.getProject(id);
    }

    @Override
    public ArrayList<Project> getAllProject() {
        return projectDao.getAllProjects();
    }

    @Override
    public boolean startNewJob(String phoneNum, String id) {
        return projectDao.startNewJob(phoneNum,id);
    }

    @Override
    public boolean kickOut(String phoneNum, String id) {
        return projectDao.kickOut(phoneNum, id);
    }

    @Override
    public boolean updateProject(Project project) {
        return projectDao.updateProject(project);
    }
}
