package com.example.demo;

import com.example.demo.filter.FirstFilter;
import com.example.demo.listener.SecondListener;
import com.example.demo.servlet.SecondServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.logging.Filter;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

    /**
     * 配置方式启动servlet
     * 注册servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean getServletRegistrationBean(){
        ServletRegistrationBean bean =  new ServletRegistrationBean(new SecondServlet());
        bean.addUrlMappings("/second");
        return bean;
    }

    /**
     * 配置方式启动servlet
     * 注册 filter
     * @return
     */
    @Bean
    public FilterRegistrationBean getFilterRegistrationBean(){
        FilterRegistrationBean filter =  new FilterRegistrationBean(new FirstFilter());
//        filter.addUrlPatterns(new String[]{"*.do","*.jsp"});
        filter.addUrlPatterns("/second");
        return filter;
    }


    /**
     * 配置方式启动listener
     * 注册 listener
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<SecondListener> getServletListenerRegistrationBean(){
        ServletListenerRegistrationBean<SecondListener> listener =  new ServletListenerRegistrationBean<SecondListener>(new SecondListener());
        return listener;
    }

}
