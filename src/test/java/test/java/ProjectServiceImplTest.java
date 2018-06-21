package test.java;

import com.debugTeam.service.ProjectService;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class ProjectServiceImplTest {

    ProjectService projectService = TestHelper.PROJECT_SERVICE;

    @Test
    public void startNewProject() {
    }

    @Test
    public void closeExistedProject() {

    }

    @Test
    public void getProject() {
        projectService.getAllProject();
    }

    @Test
    public void getAllProject() {
    }

    @Test
    public void startNewJob() {
    }
}