package com.example.demo.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 监听器 实现方式一
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@WebListener
public class FirstListener implements ServletContextListener {

    /**
     * 初始化方法
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("FirstListener is init ...");

    }

    /**
     * 销毁方法
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("FirstListener is destroyed ...");
    }
}
