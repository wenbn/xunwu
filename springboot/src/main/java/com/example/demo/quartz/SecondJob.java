package com.example.demo.quartz;

/**
 *
 * @author wenbn
 * @version 1.0
 * @date 2018/11/21
 */
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SecondJob{

    public void task(){
        System.out.println("任务2执行....");
    }

}
