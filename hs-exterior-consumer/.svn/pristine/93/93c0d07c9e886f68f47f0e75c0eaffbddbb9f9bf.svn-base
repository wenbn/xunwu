package www.ucforward.com.controller;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.HsMsgSetting;
import www.ucforward.com.manager.MsgManager;
import www.ucforward.com.umeng.util.UmengUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: lsq
 * @Date: 2018/8/26 16:59
 * @Description:
 */
@Controller
@RequestMapping("msg")
public class MsgController {
    /**
     * 日志记录器
     */
    private static Logger logger = LoggerFactory.getLogger(MsgController.class);

    @Resource
    private MsgManager msgManager;

    /**
     * 获取消息列表
     * @param request
     * @return
     */
    @RequestMapping(value="getMsgList", method = RequestMethod.POST)
    @ResponseBody
    public String getMsgList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            //token
            String token = StringUtil.trim(request.getParameter("token"));
            //获取当前登陆用户用户名（手机号）
            String mobile = RequestUtil.analysisToken(token).getMoble();
            //用户id
            String userId = RequestUtil.analysisToken(token).getUserId();
            //获取客户端类型
            //客户端类型 android:1 ios:2 微信:3 其他:4
            Integer client = getClient(request);
            Map<Object,Object> condition = new HashMap<>(5);
            condition.put("userName",mobile);
            //用户平台 外部:1 外获:2 外看:3
            condition.put("platform",1);
            condition.put("client",client);
            condition.put("userId",userId);
            result = msgManager.getMsgList(condition);
        }catch (Exception e){
            logger.error("MsgController getMsgList Method Exception :——》:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取消息历史
     * @param request
     * @return
     */
    @RequestMapping(value="getMsgHistory", method = RequestMethod.POST)
    @ResponseBody
    public String getMsgHistory(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>(16);
        try{
            Map map = RequestUtil.getParameterMap(request);
            //消息类型code
            String msgCode = StringUtil.trim(map.get("msgCode"));
            //当前页
            String pageIndex = StringUtil.trim(request.getParameter("pageIndex"),"1");
            //token
            String token = StringUtil.trim(request.getParameter("token"));
            //获取客户端类型
            Integer client = getClient(request);
            condition.put("client",client);
            //获取当前登陆用户用户名（手机号）
            String mobile = RequestUtil.analysisToken(token).getMoble();
            condition.put("msgCode",msgCode);
            condition.put("userName",mobile);
            condition.put("platform",1);
            //当前页
            condition.put("pageIndex",pageIndex);
            //每页显示条数
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel",0);
            result = msgManager.getMsgHistory(condition);
        }catch (Exception e){
            logger.error("MsgController getMsgHistory Method Exception :——》:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取消息类型
     * @param request
     * @return
     */
    @RequestMapping(value="getMsgType", method = RequestMethod.POST)
    @ResponseBody
    public String getMsgType(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            Map map = RequestUtil.getParameterMap(request);
            //类型 1外部 2外获 3外看 4区域长
            String type = StringUtil.trim(map.get("type"));
            result = msgManager.getMsgType(type);
        }catch (Exception e){
            logger.error("MsgController getMsgType Method Exception :——》:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    @RequestMapping(value="getMemMsgSetting", method = RequestMethod.POST)
    @ResponseBody
    public String getMemMsgSetting(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>(16);
        try{
            Map map = RequestUtil.getParameterMap(request);
            //token
            String token = StringUtil.trim(request.getParameter("token"));
            //获取当前登陆用户用户名（手机号）
            String mobile = RequestUtil.analysisToken(token).getMoble();
            //platform  平台类型 外部:1 外获:2 外看:3  其他为测试
            String platform = request.getParameter("platform");
            condition.put("platform",platform);
            condition.put("userName",mobile);
            condition.put("isDel",0);
            result = msgManager.getMemMsgSetting(condition);
        }catch (Exception e){
            logger.error("MsgController getMemMsgSetting Method Exception :——》:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 用户设置消息
     * @param request
     * @param hsMsgSetting
     * @return
     */
    @RequestMapping(value="setMemMsgSetting", method = RequestMethod.POST)
    @ResponseBody
    public String setMemMsgSetting(HttpServletRequest request, HsMsgSetting hsMsgSetting){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>(16);
        try{
            //token
            String token = StringUtil.trim(request.getParameter("token"));
            //获取当前登陆用户用户名（手机号）
            String mobile = Objects.requireNonNull(RequestUtil.analysisToken(token)).getMoble();
            String openCodeStr = hsMsgSetting.getOpenCode();
            //利用HashSet出去重复值
            HashSet<String> openCodeSet = new HashSet<>(Arrays.asList(openCodeStr.split(",")));
            String openCode = openCodeSet.stream().map(str -> str + ",").collect(Collectors.joining());
            //处理多余逗号
            if(StringUtil.hasText(openCode)){
                if(openCode.startsWith(",")){
                    openCode = openCode.substring(1);
                }
                if(openCode.endsWith(",")){
                    openCode = openCode.substring(0,openCode.length() -  1);
                }
            }


            hsMsgSetting.setOpenCode(openCodeStr);
            hsMsgSetting.setCreateTime(new Date());
            hsMsgSetting.setUserName(mobile);
            result = msgManager.setMemMsgSetting(hsMsgSetting);
        }catch (Exception e){
            logger.error("MsgController setMemMsgSetting Method Exception :——》:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    @RequestMapping(value="/msgPushTest", method = RequestMethod.POST)
    @ResponseBody
    public String msgPushTest(HttpServletRequest request){
        boolean b = false;
        try {
            String language = request.getParameter("language");
            String alias = request.getParameter("alias");
            String msg = request.getParameter("msg");
            String platform = request.getParameter("platform");
            String msgCode = request.getParameter("msgCode");
            if(!StringUtil.hasText(alias) || !StringUtil.hasText(msg)|| !StringUtil.hasText(platform)|| !StringUtil.hasText(msgCode)){
                return JsonUtil.toJson("请求参数错误");
            }
            b = UmengUtil.aliasSend(StringUtil.getAsInt(language),alias, msg, StringUtil.getAsInt(platform,0),StringUtil.getAsInt(msgCode,1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtil.toJson(b);
    }

    /**
     * 获取客户端
     * @param request
     * @return  android:1 ios:2 微信:3 其他:4
     */
    public static Integer getClient(HttpServletRequest request){
        /**User Agent中文名为用户代理，简称 UA，它是一个特殊字符串头，使得服务器
         能够识别客户使用的操作系统及版本、CPU 类型、浏览器及版本、浏览器渲染引擎、浏览器语言、浏览器插件等*/
        String agent= request.getHeader("user-agent");
        //客户端类型常量
        Integer type;
        if(agent.contains("Android") || agent.contains("Linux")){
            //android
            type = 1;
        } else if(agent.contains("iPhone")||agent.contains("iPod")||agent.contains("iPad")) {
            //ios
            type = 2;
        } else if(agent.indexOf("micromessenger") > 0){
            //微信
            type = 3;
        }else {
            //其他 PC
            type = 4;
        }
        return type;
    }


}
