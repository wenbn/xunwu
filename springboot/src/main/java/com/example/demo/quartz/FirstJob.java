package com.example.demo.quartz;


import com.example.demo.pojo.HsAdvertData;
import com.example.demo.serviceInter.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
