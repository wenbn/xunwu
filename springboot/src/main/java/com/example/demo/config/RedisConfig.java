package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * https://www.cnblogs.com/xiaoping1993/p/7761123.html
 * redis配置
 * @author wenbn
 * @version 1.0
 * @date 2018/11/19
 */
@Configuration
public class RedisConfig {

    /**
     * Redis连接池配置
     * @return
     */
    @Bean
    public JedisPoolConfig getJedisPoolConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(500);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(500);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * 180);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        //config.setTestOnBorrow(true);
        return config;
    }

    /**
     * 2.创建JedisConnectionFactory：配置redis链接信息
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig config){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        //关联链接池的配置对象
        jedisConnectionFactory.setPoolConfig(config);
        //配置链接Redis的信息
        //主机地址
//        jedisConnectionFactory.setHostName("120.77.220.25");
//        //端口
//        jedisConnectionFactory.setPort(6379);
//        jedisConnectionFactory.setPassword("ucforward^2018");
        return jedisConnectionFactory;
    }

    /**
     * 3.创建RedisTemplate:用于执行Redis操作的方法
     */
//    @Bean
//    public RedisTemplate<String,Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory){
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        //关联
//        template.setConnectionFactory(jedisConnectionFactory);
//
//        //为key设置序列化器
//        template.setKeySerializer(new StringRedisSerializer());
//        //为value设置序列化器
//        template.setValueSerializer(new StringRedisSerializer());
//
//        return template;
//    }


    /**
     * 3.创建RedisTemplate:自定义序列化器
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory){
        StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactory);
        template.setConnectionFactory(jedisConnectionFactory);
        setSerializer(template);//设置序列化工具
        template.afterPropertiesSet();
        return template;
    }

    private void setSerializer(StringRedisTemplate template){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
    }
}
