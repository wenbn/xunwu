package com.example.demo.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * springboot配置filter 方式一
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@WebFilter(filterName = "FirstFilter" ,urlPatterns = {"/first"})
public class FirstFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("doFilter start ...");
        chain.doFilter(request,response);
        System.out.println("doFilter end ...");
    }

    @Override
    public void destroy() {

    }
}
