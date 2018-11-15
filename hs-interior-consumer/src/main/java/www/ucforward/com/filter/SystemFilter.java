package www.ucforward.com.filter;

import org.apache.commons.lang.StringUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.utils.SystemCacheUtil;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

import static com.alibaba.druid.sql.ast.SQLPartitionValue.Operator.List;

/**
 * Author:wenbn
 * Date:2018/1/6
 * Description:
 */
public class SystemFilter implements Filter {

     public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //检查数据字典缓存
        Map<Object,Object> condition = new HashMap<Object,Object>();
        if (RedisUtil.isExistCache(RedisConstant.SYS_DICTCODE_CACHE_KEY)) {
            List<String> dictcodeCaches =RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY);
            if(dictcodeCaches == null){
                SystemCacheUtil.resetSysDictcodeCache(condition);
            }
            List<String> unCacheDictcodes = new ArrayList<String>();
            for (String dictcodeCache : dictcodeCaches) {
                //判断是否存在缓存中
                if(!RedisUtil.existKey(dictcodeCache)){
                    unCacheDictcodes.add(dictcodeCache);
                }
            }
            if(unCacheDictcodes!=null && unCacheDictcodes.size()>0){
                condition.put("groupCodes",unCacheDictcodes);
                SystemCacheUtil.resetSysDictcodeCache(condition);
            }
        }else{
            SystemCacheUtil.resetSysDictcodeCache(condition);
        }
        //检查积分缓存
        if (!RedisUtil.isExistCache(RedisConstant.SYS_GOLD_RULE_CACHE_KEY)) {
            //重置积分规则缓存
            SystemCacheUtil.resetSysGoldRuleCache();
        }
        chain.doFilter(request, response); // 执行目标资源，放行
    }

    public void destroy() {

    }
}
