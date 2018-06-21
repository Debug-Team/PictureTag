package test.java.service;

import com.debugTeam.service.ProjectService;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class ProjectServiceImplTest {

    ProjectService projectService = TestHelper.PROJECT_SERVICE;

    @Test
    public void startNewProject() {
        System.out.println(projectService.startNewJob("111","11"));
    }

    @Test
    public void closeExistedProject() {
        System.out.println(projectService.closeExistedProject("111"));
    }

    @Test
    public void getProject() {
        System.out.println(projectService.getAllProject());
    }

    @Test
    public void getAllProject() {
        System.out.println(projectService.getAllProject());
    }

    @Test
    public void startNewJob() {
        System.out.println(projectService.startNewJob("111","111"));
    }
}