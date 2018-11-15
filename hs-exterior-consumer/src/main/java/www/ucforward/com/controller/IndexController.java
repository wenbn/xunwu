package www.ucforward.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/28
 */
@Controller
@RequestMapping("index")
public class IndexController {

    @Resource
    private CommonManager commonManager;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

//    @KafkaListener(topics = "test")
//    private void handleMessage(String content) {
//        System.out.println(content);
//    }


    /**
     * 加载首页信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
//    @RequestMapping(value="/load",method = RequestMethod.POST)
    @RequestMapping(value="/load")
    @ResponseBody
    @NoRequireLogin
    public String get(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ResultVo vo = null;
        Map<Object,Object> map = RequestUtil.getParameterMap(request);
        int language = StringUtil.getAsInt(StringUtil.trim(map.get("language")),0);//语言版本
        String token = StringUtil.trim(map.get("token")); //token
        Map<Object,Object> condition = new HashMap<Object,Object>();
//        String boothAliasNames = "hs_app_index_banner_booth";
//        condition.put("boothAliasName", Arrays.asList(boothAliasNames.split(",")));
        condition.put("channelAliasName", "hs_app_index");
        condition.put("language",language);
        condition.put("channelType",3);//频道类型（1：PC网站，2：触屏端，3：App）
        condition.put("channelState",1);//频道的状态（0：不启用，1：启用）
        if(StringUtil.hasText(token)){//未登录视为新用户
            condition.put("token",token);
        }
        condition.put("isDel",0);
        vo = commonManager.getIndexDatas(condition);
        //kafkaTemplate.send("test", "first message......");
        return JsonUtil.toJson(vo);
    }

}
