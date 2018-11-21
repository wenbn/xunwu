package com.example.demo.config;

import com.example.demo.quartz.FirstJob;
import com.example.demo.quartz.QuartzJob;
import com.example.demo.quartz.SecondJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

/**
 *
 * https://blog.csdn.net/mengruobaobao/article/details/79106343
 * @author wenbn
 * @version 1.0
 * @date 2018/11/21
 */
@Configuration
public class QuartzConfig {

    /**
     * 1.创建Job对象
     */
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean(){
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        //关联我们自己的Job类
        factory.setJobClass(QuartzJob.class);
        return factory;
    }

    // 配置定时任务1
    @Bean(name = "firstJobDetail")
    public MethodInvokingJobDetailFactoryBean firstJobDetail(FirstJob firstJob) {
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        // 是否并发执行
        jobDetail.setConcurrent(false);
        // 为需要执行的实体类对应的对象
        jobDetail.setTargetObject(firstJob);
        // 需要执行的方法
        jobDetail.setTargetMethod("task");
        return jobDetail;
    }


    // 配置触发器1
    @Bean(name = "firstTrigger")
    public SimpleTriggerFactoryBean firstTrigger(JobDetail firstJobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(firstJobDetail);
        // 设置任务启动延迟
        trigger.setStartDelay(0);
        // 每5秒执行一次
        trigger.setRepeatInterval(5000);
        return trigger;
    }

    // 配置定时任务2
    @Bean(name = "secondJobDetail")
    public MethodInvokingJobDetailFactoryBean secondJobDetail(SecondJob secondJob) {
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        // 是否并发执行
        jobDetail.setConcurrent(false);
        // 为需要执行的实体类对应的对象
        jobDetail.setTargetObject(secondJob);
        // 需要执行的方法
        jobDetail.setTargetMethod("task");
        return jobDetail;
    }

    // 配置触发器2
    @Bean(name = "secondTrigger")
    public CronTriggerFactoryBean secondTrigger(JobDetail secondJobDetail) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(secondJobDetail);
        // cron表达式
        trigger.setCronExpression("0/6 * * * * ?");
        return trigger;
    }

    // 配置Scheduler
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(Trigger firstTrigger, Trigger secondTrigger) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        // 延时启动，应用启动1秒后
        bean.setStartupDelay(1);
        // 注册触发器
        bean.setTriggers(firstTrigger,secondTrigger);
        return bean;
    }

}
