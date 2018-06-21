package test.java.dao;

import com.debugTeam.dao.AdministratorDao;
import com.debugTeam.entity.Administrator;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

public class AdministratorDaoTest {

    AdministratorDao administratorDao = TestHelper.ADMINISTRATOR_DAO;

    @Test
    public void updateAdministrator() {
        administratorDao.updateAdministrator(new Administrator());
    }

    @Test
    public void getAdministrator() {
        System.out.println(administratorDao.getAdministrator().toString());
    }

    @Test
    public void updateDailyMarkerRegisterNum() {
        administratorDao.updateDailyMarkerRegisterNum();
    }

    @Test
    public void updateDailyMarkerLoginNum() {
        administratorDao.updateDailyMarkerLoginNum();
    }

    @Test
    public void updateDailyUploaderRegisterNum() {
        administratorDao.updateDailyUploaderRegisterNum();
    }

    @Test
    public void updateDailyUploaderLoginNum() {
        administratorDao.updateDailyUploaderLoginNum();
    }

    @Test
    public void updateDailyProjectUpload() {
        administratorDao.updateDailyProjectUpload();
    }

    @Test
    public void updateDailyProjectAccept() {
        administratorDao.updateDailyProjectAccept();
    }
}
