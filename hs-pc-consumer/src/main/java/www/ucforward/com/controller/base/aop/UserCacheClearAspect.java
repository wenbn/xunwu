package www.ucforward.com.controller.base.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.ucforward.com.controller.base.annotation.RedisCacheClear;
import www.ucforward.com.realm.HsPcRealm;

import javax.annotation.Resource;

/**
 * @U注解实现类,切面类
 */
@Component
@Aspect
public class UserCacheClearAspect {


    @Resource
    private HsPcRealm myRealm;

    @Autowired
    private RedisManager redisManager;




    /**
     * 清除后台权限模块更新时的缓存
     */
    @AfterReturning("@annotation(www.ucforward.com.controller.base.annotation.UserCacheClear)")
    public void AfterReturning(){
        myRealm.clearCached();
    }

    /**
     * 清空Redis中指定某个key的缓存
     * @param joinPoint
     */
    @AfterReturning("@annotation(www.ucforward.com.controller.base.annotation.RedisCacheClear)")
    public void AfterReturningRedis(JoinPoint joinPoint){
        RedisCacheClear annotation = joinPoint.getThis().getClass().getAnnotation(RedisCacheClear.class);
        String key = annotation.key();
        redisManager.del(key.getBytes());

    }

}
