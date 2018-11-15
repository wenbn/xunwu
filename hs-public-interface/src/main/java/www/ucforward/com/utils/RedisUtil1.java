package www.ucforward.com.utils;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.utils.StringUtil;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.*;

/**
 * https://blog.csdn.net/u010823097/article/details/51720406
 */
public class RedisUtil1 {

    private static JedisPool pool = null;
    private static Logger logger = Logger.getLogger(RedisUtil1.class);
    private static Properties properties = new Properties();
    static {
        try{
            properties.load(RedisUtil1.class.getResourceAsStream("/redis/redis.properties"));
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * 建立集群连接池
     *
     */
    @Test
    public void createJedisClusterPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(10);
        // 最大空闲数
        poolConfig.setMaxIdle(2);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        poolConfig.setMaxWaitMillis(1000);
        Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
        nodes.add(new HostAndPort("192.168.0.116", 6379));
        nodes.add(new HostAndPort("192.168.0.116", 6380));
        nodes.add(new HostAndPort("192.168.0.116", 6381));
        nodes.add(new HostAndPort("192.168.0.116", 6382));
        nodes.add(new HostAndPort("192.168.0.116", 6383));
        nodes.add(new HostAndPort("192.168.0.116", 6384));
        JedisCluster cluster = new JedisCluster(nodes, poolConfig);
        cluster.set("name2","wenbn");
        String name = cluster.get("name");
        System.out.println(name);
        cluster.set("age2", "18");
        System.out.println(cluster.get("age"));
        try {
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     *
     */
    private static void createJedisPool() {
        if(pool == null){
            synchronized (RedisUtil1.class){
                if(pool == null){
                    JedisPoolConfig config = new JedisPoolConfig();
                    //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
                    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
                    config.setMaxTotal(500);
                    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
                    config.setMaxIdle(500);
                    //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
                    config.setMaxWaitMillis(1000 * 100);
                    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
                    config.setTestOnBorrow(true);
                    config.setTestWhileIdle(true);
                    System.out.println(properties.getProperty("redis.host")+"=="+properties.getProperty("redis.port")+"=="+properties.getProperty("redis.pwd"));
                    pool = new JedisPool(config, properties.getProperty("redis.host"),
                            StringUtil.getAsInt(properties.getProperty("redis.port")), 5000);
                    logger.info("request redis server:"+properties.getProperty("redis.host"));
                }
            }
        }
    }

    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (pool == null)
            createJedisPool();
    }


    /**
     * 获取一个jedis 对象
     *
     * @return
     */
    public static synchronized Jedis getJedis() {
        try{
            if (pool == null){
                poolInit();
            }
            return pool.getResource();
        }catch ( JedisConnectionException e){
            logger.warn("jedisInfo ... NumActive=" + pool.getNumActive()
                    + ", NumIdle=" + pool.getNumIdle()
                    + ", NumWaiters=" + pool.getNumWaiters()
                    + ", isClosed=" + pool.isClosed());
            logger.error("GetJedis error,", e);
            pool.destroy();
            pool = null;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            poolInit();

        }
        return pool.getResource();
    }

    /**
     * 归还一个连接
     *
     * @param jedis
     */
    public static synchronized void returnRes(Jedis jedis) {
        close(jedis);
    }

    public static void close(Jedis jedis) {
        if(jedis!=null){
            jedis.close();
        }

    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public static synchronized <T> T safeGetT(String key) {
        Jedis jedis = null;
        T value = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if (null != jedis) {
                    jedis.auth(properties.getProperty("redis.pwd"));
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    if(timeoutCount > 10){
                        break;
                    }
                    timeoutCount++;
                }
            }
            byte[] bytes = jedis.get(key.getBytes());
            value = (T) SerializationUtil.deserialize(bytes);
            return value;
        }catch (Exception e){
            logger.warn("jedisInfo ... NumActive=" + pool.getNumActive()
                    + ", NumIdle=" + pool.getNumIdle()
                    + ", NumWaiters=" + pool.getNumWaiters()
                    + ", isClosed=" + pool.isClosed());
            logger.error("Redis safeGet error,", e);
        }finally {
            returnRes(jedis);
        }
        return value;
    }

    /**
     * 安全获取value
     * @param key
     */
    public static synchronized String safeGet(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if (null != jedis) {
                    jedis.auth(properties.getProperty("redis.pwd"));
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    if(timeoutCount > 10){
                        break;
                    }
                    timeoutCount++;
                }
            }
            value = jedis.get(key);
        }catch (Exception e){
            logger.warn("jedisInfo ... NumActive=" + pool.getNumActive()
                    + ", NumIdle=" + pool.getNumIdle()
                    + ", NumWaiters=" + pool.getNumWaiters()
                    + ", isClosed=" + pool.isClosed());
            logger.error("Redis safeGet error,", e);
        }finally {
            returnRes(jedis);
        }
        return value;
    }


    /**
     * 绝对设置方法（保证一定能够使用可用的链接设置 数据）
     * Jedis连接使用后返回连接池
     * @param key
     * @param object
     */
    public static void safeSet(String key, Object object) {
        safeSet(key,object,60*30);
    }

    /**
     * 绝对设置方法（保证一定能够使用可用的链接设置 数据）
     * Jedis连接使用后返回连接池
     * @param key
     * @param object
     * @param time 过期时间
     */
    public static synchronized void safeSet(String key, Object object, int time) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if(timeoutCount>10){
                    break;
                }
                if (null != jedis) {
                    jedis.auth(properties.getProperty("redis.pwd"));
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    timeoutCount++;
                }
            }
            if (time > 0) {
                jedis.setex(key.getBytes(), time, SerializationUtil.serialize(object));
            }
        }catch (Exception e){
            logger.info("Redis safeSet is error:"+e.getMessage());
        }finally {
            returnRes(jedis);
        }
    }

    /**
     * 绝对设置方法（保证一定能够使用可用的链接设置 数据）
     * Jedis连接使用后返回连接池
     * @param key
     * @param value
     */
    public static synchronized void safeSet(String key, String value) {
        safeSet(key,value,60*30);
    }

    /**
     * 绝对设置方法（保证一定能够使用可用的链接设置 数据）
     * Jedis连接使用后返回连接池
     * @param key
     * @param time
     * @param value
     */
    public static synchronized void safeSet(String key, String value, int time) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if(timeoutCount>10){
                    break;
                }
                if (null != jedis) {
                    jedis.auth(properties.getProperty("redis.pwd"));
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    timeoutCount++;
                }
            }
            if (time > 0) {
                jedis.setex(key, time, value);
            }
        }catch (Exception e){
            logger.info("Redis safeSet is error:"+e.getMessage());
        }finally {
            returnRes(jedis);
        }
    }

    /**
     * 删除缓存值
     * @param key
     */
    public static synchronized void safeDel(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if(timeoutCount>10){
                    break;
                }
                if (null != jedis) {
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    timeoutCount++;
                }
            }
            jedis.del(key);
        }catch (Exception e){
            logger.info("Redis safeGet is error:"+e.getMessage());
        }finally {
            returnRes(jedis);
        }
    }

    public static Boolean existKey(final String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            int timeoutCount = 0;
            while (true) {
                if(timeoutCount>10){
                    break;
                }
                if (null != jedis) {
                    break;
                } else {
                    Thread.sleep(timeoutCount*100);
                    jedis = getJedis();
                    timeoutCount++;
                }
            }
            return jedis.exists(key);
        }catch (Exception e){
            logger.info("Redis safeGet is error:"+e.getMessage());
            return false;
        }finally {
            returnRes(jedis);
        }
    }


    public static boolean isExistCache(String key) {
        boolean result = false;
        if (key!=null && !key.equals("")) {
            result = existKey(key).booleanValue();
        }
        return result;
    }




    public static Long incr(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incr(key);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static void hset(String key,String field,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key,field,value);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static String hget(String key,String field){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key,field);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(jedis);
        }
        return null;
    }

    public static Map<String,String> hgetAll(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    /**
     *
     * @param timeout 0表示永久 单位秒
     * @param key key
     * @return [key,value]
     */
    public static String blpop(int timeout,String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            List<String> list = jedis.blpop(timeout, key);
            return list.get(1);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static String blpop(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            List<String> list = jedis.blpop(0, key);
            return list.get(1);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static void lpush(String key,String... value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.lpush(key,value);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }
    }

    /**
     *
     * @param timeout 0表示永久 单位秒
     * @param key key
     * @return [key,value]
     */
    public static String brpop(int timeout,String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            List<String> list = jedis.brpop(timeout, key);
            return list.get(1);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static String brpop(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            List<String> list = jedis.brpop(0, key);
            return list.get(1);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }finally {
            close(jedis);
        }
    }

    public static void rpush(String key,String... value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.rpush(key,value);
        }catch (Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        }
    }

    /**
     * 获取key过期时间 -1表示永久 -2表示该key不存在
     * @param key
     * @return
     */
    public static long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisException(e.getMessage(),e);
        } finally {
            close(jedis);
        }
    }

    public static void main(String[] args) {
//        Jedis jedis = RedisUtil.getJedis();
//        jedis.set("test","hello Jedis Pool ");
//        jedis.set("hello ","Jedis Pool");
//        System.out.println(jedis.get("test"));
//        jedis.close();
//        System.out.println(jedis.get("test"));
//        Jedis jedis10 = pool.getResource();
//       System.out.println(jedis10.get("hello"+"10"));
//       System.out.println(jedis10.get("hello"+"1"));
//        jedis10.close();
        Map<Object,Object> map = Maps.newHashMap();
        map.put("name","name1");
        map.put("value","value1");
        map.put("date",new Date());

//        Jedis jedis = RedisUtil.getJedis();
//        jedis.set("user".getBytes(), SerializationUtil.serialize(map));
//        byte[] bs = jedis.get("user".getBytes());
//        Map desUser = (Map) SerializationUtil.deserialize(bs);
//        System.out.println(desUser);

        RedisUtil1.safeSet("user",map);
        Map<Object,Object> returnMap = RedisUtil1.safeGetT("user");
        System.out.println(returnMap);


//        StringBuffer sb = new StringBuffer();
//        String city ="Dubai";
//        String community = "Academic City";
//        List<Map<Object, Object>> citys = JsonUtil.parseJSON2List(RedisUtil.safeGet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY));
//        for (Map<Object, Object> cityMap : citys) {
//            if(StringUtil.trim(cityMap.get("cityNameEn")).equals(city) || StringUtil.trim(cityMap.get("cityNameCn")).equals(city)){
//                sb.append(StringUtil.trim(cityMap.get("cityCode")));
//                List<Map<Object,Object>> subList = (List<Map<Object,Object>>) cityMap.get("sub");
//                for (Map<Object, Object> subMap : subList) {
//                    if(StringUtil.trim(subMap.get("cityNameEn")).equals(community) || StringUtil.trim(subMap.get("cityNameCn")).equals(community)){
//                        sb.append(StringUtil.trim(subMap.get("cityCode")));
//                        break;
//                    }
//                }
//            }
//        }
//        System.out.println("=="+sb.toString());

    }
}