package www.ucforward.com.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utils.StringUtil;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Controller
public class CityController {

    private static Logger logger = LoggerFactory.getLogger(CityController.class); // 日志记录


    @Resource
    private CommonManager commonManager;


    /**
     * 获取支持的城市信息
     * @return
     */
    @RequestMapping(value="/support/cities")
    @ResponseBody
    @NoRequireLogin
    public String getCities(){
        ResultVo result = null;
        //查询缓存
        List<Map<Object,Object>> citys = null;
        if(RedisUtil.existKey(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY)) {
            citys = RedisUtil.safeGet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY);
            result = ResultVo.success();
            result.setDataSet(citys);
            return JsonUtil.toJson(result);
        }
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            condition.put("isDel",0);
            result = commonManager.getSupportCities(condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                citys= (List<Map<Object, Object>>) result.getDataSet();
                if(citys!=null && citys.size()>0){
                   RedisUtil.safeSet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY, citys, RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_TIME);
                }
            }
        } catch (Exception e) {
            logger.error("CityController controller: getSupportCities Exception message:" + e.getMessage());
            System.out.println(e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return JsonUtil.toJson(result);
    }

    /**
     * 获取支持的城市信息
     * @return
     */
    @RequestMapping(value="address/support/cities")
    @ResponseBody
    @NoRequireLogin
    public String getSupportCities(){
        ResultVo result = null;
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            condition.put("level",1);
            condition.put("isDel",0);
            result = commonManager.getSupportCities(condition);
        } catch (Exception e) {
            logger.error("CityController controller: getSupportCities Exception message:" + e.getMessage());
            System.out.println(e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return JsonUtil.toJson(result);
    }

    /**
     * 获取对应城市支持社区列表
     * @param cityId
     * @return
     */
    @RequestMapping(value="address/support/communities")
    @NoRequireLogin
    @ResponseBody
    public String getSupportCommunitys(@RequestParam(name = "cityId",required = false) String cityId) {
        ResultVo result = null;
        if(!StringUtil.hasText(cityId)){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            condition.put("level",2);
            condition.put("pid",cityId);
            condition.put("isDel",0);
            result = commonManager.getSupportCities(condition);
        } catch (Exception e) {
            logger.error("CityController controller: getSupportCommunitys Exception message:" + e.getMessage());
            System.out.println(e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);

        }
        condition = null;
        return JsonUtil.toJson(result);
    }

    /**
     * 获取对应城市支持子社区列表
     * @param cityId
     * @return
     */
    @RequestMapping(value="address/support/subCommunities")
    @NoRequireLogin
    @ResponseBody
    public String getSupportSubCommunitys(@RequestParam(name = "cityId",required = false) String cityId) {
        ResultVo result = null;
        if(!StringUtil.hasText(cityId)){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            condition.put("level",3);
            condition.put("pid",cityId);
            condition.put("isDel",0);
            result = commonManager.getSupportCities(condition);
        } catch (Exception e) {
            logger.error("CityController controller: getSupportSubCommunitys Exception message:" + e.getMessage());
            System.out.println(e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return JsonUtil.toJson(result);
    }
}
