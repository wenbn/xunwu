package com.example.demo.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定义任务类
 * @author wenbn
 * @version 1.0
 * @date 2018/11/20
 */
@Component
public class QuartzJob implements Job {

    /**
     * 任务被触发时所执行的方法
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("=============="+new Date());
    }
}
