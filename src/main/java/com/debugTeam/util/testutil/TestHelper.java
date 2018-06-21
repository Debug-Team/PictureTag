package com.debugTeam.util.testutil;

import com.debugTeam.dao.ProjectDao;
import com.debugTeam.dao.TagDao;
import com.debugTeam.dao.UserDao;
import com.debugTeam.service.LoginService;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.TagService;
import com.debugTeam.service.UserSevice;
import org.springframework.context.ApplicationContext;

/**
 * 获取service层对象，以便测试
 */
public class TestHelper {

    public static final LoginService LOGIN_SERVICE;
    public static final ProjectService PROJECT_SERVICE;
    public static final TagService TAG_SERVICE;
    public static final UserSevice USER_SEVICE;

    public static final ProjectDao PROJECT_DAO;
    public static final TagDao TAG_DAO;
    public static final UserDao USER_DAO;


    static {
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        LOGIN_SERVICE = applicationContext.getBean(LoginService.class);
        PROJECT_SERVICE = applicationContext.getBean(ProjectService.class);
        TAG_SERVICE = applicationContext.getBean(TagService.class);
        USER_SEVICE = applicationContext.getBean(UserSevice.class);

        PROJECT_DAO = applicationContext.getBean(ProjectDao.class);
        TAG_DAO = applicationContext.getBean(TagDao.class);
        USER_DAO = applicationContext.getBean(UserDao.class);
    }
}
