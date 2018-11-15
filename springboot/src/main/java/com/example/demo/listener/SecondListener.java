package com.example.demo.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
public class SecondListener implements ServletContextListener {

    /**
     * 初始化方法
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SecondListener is init ...");

    }

    /**
     * 销毁方法
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("SecondListener is destroyed ...");
    }
}

