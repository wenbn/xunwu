package com.example.demo.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * springboot配置filter 方式二
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
public class SecondFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("SecondFilter start ...");
        chain.doFilter(request,response);
        System.out.println("SecondFilter end ...");
    }

    @Override
    public void destroy() {

    }
}
