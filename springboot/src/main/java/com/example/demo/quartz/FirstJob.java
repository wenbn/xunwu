package com.example.demo.quartz;


import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
/**
 * https://blog.csdn.net/wk52525/article/details/79100973
 * @author wenbn
 * @version 1.0
 * @date 2018/11/21
 */
public class FirstJob{



    public void task() throws Exception {

        System.out.println("任务1执行....");
    }
}
