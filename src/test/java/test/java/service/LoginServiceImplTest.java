package test.java.service;

import com.debugTeam.service.LoginService;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import com.debugTeam.util.exception.UserWrongPasswordException;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class LoginServiceImplTest {

    LoginService loginService = TestHelper.LOGIN_SERVICE;

    @Test
    public void login() {
        try {
            loginService.signup("000000","000000","000000","0");
            loginService.signup("111111","111111","111111","1");
            try {
                loginService.login("000000","000000");
            } catch (UserWrongPasswordException e) {
                e.printStackTrace();
            } catch (UserNotExistedException e) {
                e.printStackTrace();
            }
        } catch (UserDuplicateException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signup() {
        try {
            loginService.signup("000000","000000","000000","0");
            loginService.signup("111111","111111","111111","1");
        } catch (UserDuplicateException e) {
            e.printStackTrace();
        }
    }
}