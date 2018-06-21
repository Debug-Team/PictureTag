package test.java;

import com.debugTeam.service.UserSevice;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class UserServiceImplTest {

    UserSevice userSevice = TestHelper.USER_SEVICE;

    @Test
    public void getMarker() {
        userSevice.getMarker("000000");
    }

    @Test
    public void getUploader() {
        userSevice.getUploader("000000");
    }

    @Test
    public void getMarkerList() {
        userSevice.getMarkerList();
    }

    @Test
    public void getUploaderList() {
        userSevice.getUploaderList();
    }
}