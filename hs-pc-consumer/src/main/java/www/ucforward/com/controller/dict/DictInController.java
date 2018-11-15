package www.ucforward.com.controller.dict;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.controller.base.BaseController;
import www.ucforward.com.entity.HsSysDictcodeGroup;
import www.ucforward.com.entity.HsSysDictcodeItem;
import www.ucforward.com.manager.ICommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.*;

/**
 * 获取数据字典
 * @author wenbn
 * @version 1.0
 * @date 2018/8/24
 */
@Controller
@ResponseBody
public class DictInController extends BaseController {

    @Resource
    private ICommonManager commonManager;

    /**
     * 获取展位列表
     * @return
     */
    @RequestMapping(value="/get/dict/{type}",method = RequestMethod.POST)
    public String getDictList(@PathVariable Integer type) throws Exception {
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
        }
        if(RedisUtil.existKey(cacheKey)) {
            Map<Object, Object> list = RedisUtil.safeGet(cacheKey);
//            Map<Object, Object> list = JsonUtil.parseJSON2Map(RedisUtil.safeGet(cacheKey));
            vo.setDataSet(list);
        }
        return JsonUtil.toJson(vo);
    }

}
