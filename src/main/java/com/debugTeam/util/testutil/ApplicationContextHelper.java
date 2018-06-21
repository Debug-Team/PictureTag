package com.debugTeam.util.testutil;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 获取 Spring
 */
public class ApplicationContextHelper {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext("classpath*:/applicationContext.xml");
        }
        return applicationContext;
    }
}
