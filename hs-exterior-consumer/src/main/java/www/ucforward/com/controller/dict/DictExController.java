package www.ucforward.com.controller.dict;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 获取数据字典
 * @author wenbn
 * @version 1.0
 * @date 2018/8/24
 */
@Controller
@ResponseBody
public class DictExController {

    private static Logger logger = LoggerFactory.getLogger(DictExController.class); // 日志记录
    /**
     * 获取展位列表
     * @return
     */
    @RequestMapping(value="/get/dict/{type}",method = RequestMethod.POST)
    @NoRequireLogin
    public String getDictList(@PathVariable Integer type, HttpServletRequest request) throws Exception {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));//req就是request请求
        Browser browser = userAgent.getBrowser();//获取浏览器信息 
        OperatingSystem os = userAgent.getOperatingSystem(); //获取操作系统信息
        StringBuffer userInfo = new StringBuffer();
        userInfo.append("操作系统=SystemOs ："+os.toString()+" 浏览器= Browser ："+browser.toString());
        ResultVo vo = new ResultVo();
        String cacheKey ="";
        if(type==0){//获取房源配置
            cacheKey ="SYS_HOUSE_CONFIG_DICTCODE";
        }else if(type==1){//获取房屋类型
            cacheKey ="SYS_HOUSING_TYPE_DICTCODE";
        }else if(type==2){//房屋状态
            cacheKey ="SYS_HOUSE_STATE_DICTCODE";
        }else if(type==3){//房屋配套
            cacheKey ="SYS_HOUSE_SELF_CONTAINED_DICTCODE";
        }else{
            logger.warn("exterior get/dict call to fails"+userInfo);
        }
        if(RedisUtil.existKey(cacheKey)) {
            Map<Object, Object> list = RedisUtil.safeGet(cacheKey);
//            Map<Object, Object> list = JsonUtil.parseJSON2Map(RedisUtil.safeGet(cacheKey));
            vo.setDataSet(list);
        }

        return JsonUtil.toJson(vo);
    }
}
