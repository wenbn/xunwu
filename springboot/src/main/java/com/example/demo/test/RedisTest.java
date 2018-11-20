package com.example.demo.test;

import com.example.demo.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 添加
     */
    @Test
    public void testRedisSet(){
        this.redisTemplate.opsForValue().set("test","adsferrrr");
    }

    /**
     * 获取
     */
    @Test
    public void testRedisGet(){
        Object test = this.redisTemplate.opsForValue().get("test");
        System.out.println(test);
    }

    /**
     * 删除
     */
    @Test
    public void testRedisDelete(){
        Boolean test = this.redisTemplate.delete("test");
        System.out.println(test);
    }

}
