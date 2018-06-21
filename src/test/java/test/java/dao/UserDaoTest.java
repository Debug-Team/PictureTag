package test.java.dao;

import com.debugTeam.dao.UserDao;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.Uploader;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class UserDaoTest {

    UserDao userDao = TestHelper.USER_DAO;
    Marker marker = new Marker("123", "zzw", "123456");
    Uploader uploader = new Uploader("12354", "zyb", "456123");

    @Test
    public void getPassword() {
        try {
            System.out.println(userDao.getPassword(marker.getPhoneNum()));
        } catch (UserNotExistedException e) {
            e.printStackTrace();
        } catch (UserDuplicateException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMarker() {
        userDao.getMarker(marker.getPhoneNum());
    }

    @Test
    public void getUploader() {
        userDao.getUploader(uploader.getPhoneNum());
    }

    @Test
    public void getMarkerList() {
        userDao.getMarkerList();
    }

    @Test
    public void getUploaderList() {
        userDao.getUploaderList();
    }

    @Test
    public void isUserExisted() {
        System.out.println(userDao.isUserExisted(marker.getPhoneNum()));
    }

    @Test
    public void signUp() {
        userDao.signUp(marker);
    }

    @Test
    public void updateUser() {
        userDao.updateUser(marker);
    }
}