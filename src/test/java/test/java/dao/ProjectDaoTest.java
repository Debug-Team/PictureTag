package test.java.dao;

import com.debugTeam.dao.ProjectDao;
import com.debugTeam.entity.Project;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

import java.util.ArrayList;

//没压缩包，暂时不测
public class ProjectDaoTest {

    ProjectDao projectDao = TestHelper.PROJECT_DAO;

    Project project = new Project("asda", 1, "20180618205012", 0.75,
            1, "aaaaaaa", "321321", 1,
            new ArrayList<String>(), new ArrayList<String>(), "sdsd", 1);

    @Test
    public void startNewProject() {
        projectDao.startNewProject(project);
    }

    @Test
    public void closeExistedProject() {
        projectDao.closeExistedProject(project.getId());
    }

    @Test
    public void getProject() {
        projectDao.getProject(project.getId());
    }

    @Test
    public void getAllProjects() {
        projectDao.getAllProjects();
    }

    @Test
    public void startNewJob() {
        projectDao.startNewJob("sdsd", project.getId());
    }

    @Test
    public void updateProject() {
        projectDao.updateProject(project);
    }
}