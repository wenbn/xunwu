package www.ucforward.com.controller.msg;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.MsgDetails;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.entity.HsMsgSetting;
import www.ucforward.com.manager.MsgManager;
import www.ucforward.com.umeng.util.UmengUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            //获取当前登陆用户用户名（手机号）
            String mobile = user.getMobile();
            List<Map<Object, Object>> roleList = user.getRoleList();

            //外获:2 外看:3
            Integer platform = 2;
            for (Map<Object, Object> role : roleList) {
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                    //外看业务员
                    platform = 3;
                    break;
                }
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    //钥匙管理员（区域长）
                    platform = 4;
                    break;
                }
            }
            //获取客户端类型
            Integer client = getClient(request);

            Map<Object,Object> condition = new HashMap<>(5);
            condition.put("userName",mobile);
            condition.put("platform",platform);
            condition.put("client",client);
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
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            //外获:2 外看:3
            Integer platform = 2;
            for (Map<Object, Object> role : roleList) {
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                    //外看业务员
                    platform = 3;
                    break;
                }
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    //钥匙管理员（区域长）
                    platform = 4;
                    break;
                }
            }
            //获取客户端类型
            Integer client = getClient(request);
            condition.put("client",client);

            //获取当前登陆用户用户名（手机号）
            String mobile = user.getMobile();
            condition.put("msgCode",msgCode);
            condition.put("userName",mobile);
            condition.put("platform",platform);
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

    /**
     * 获取用户消息设置
     * @param request
     * @return
     */
    @RequestMapping(value="getMemMsgSetting", method = RequestMethod.POST)
    @ResponseBody
    public String getMemMsgSetting(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>(16);
        try{
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            //获取当前登陆用户用户名（手机号）
            String mobile = user.getMobile();
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
    public String setMemMsgSetting(HttpServletRequest request, @Valid HsMsgSetting hsMsgSetting, BindingResult br){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>(16);
        try{
            //校验数据
            if (br.hasErrors()) {
                FieldError fieldError = br.getFieldErrors().get(0);
                String defaultMessage = fieldError.getDefaultMessage();
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, defaultMessage));
            }
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            //获取当前登陆用户用户名（手机号）
            String mobile = user.getMobile();
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
            hsMsgSetting.setOpenCode(openCode);
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

    /**
     * 推送系统消息
     * @param request
     * @return
     */
    @RequestMapping(value="pushSysMsg", method = RequestMethod.POST)
    @ResponseBody
    public String pushSysMsg(HttpServletRequest request){
        ResultVo resultVo = new ResultVo();
        boolean b = false;
        try {
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            //获取当前登陆用户用户名（手机号）
            Integer userId = user.getUserid();
            //语言版本 0:中文,1:英文
            String language = request.getParameter("language");
            //推送详情
            String msg = request.getParameter("msg");
            //平台类型 测试:0 外部:1 外获:2 外看:3
            String platform = request.getParameter("platform");
            //推送客户端 0:所有用户 1:android 2:ios 3:个别用户
            String client = request.getParameter("client");
            //推送类容
            String details = request.getParameter("details");
            //用户名（手机号）特定推送给某个用户，或几个用户时使用。多个用户用逗号分割，最多50个
            String userName = request.getParameter("userName");
            if(!StringUtil.hasText(language) || !StringUtil.hasText(msg)|| !StringUtil.hasText(platform)){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(resultVo);
            }

            //当推送客户端为个别用户时，userName不能为空
            if("3".equals(client)){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":暂未开通推送个别用户功能，敬请期待");
                return JsonUtil.toJson(resultVo);
                /*if(StringUtil.hasText(userName)){
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                    return JsonUtil.toJson(b);
                }*/
            }

            Map<Object,Object> condition = new HashMap<>(7);
            MsgDetails msgDetails = new MsgDetails();
            msgDetails.setSysMsg(details);
            String detailsStr = JSON.toJSONString(msgDetails);
            condition.put("userName",userName);
            condition.put("language",language);
            condition.put("msg",msg);
            condition.put("platform",platform);
            condition.put("client",client);
            condition.put("userId",userId);
            condition.put("details",detailsStr);
            resultVo = msgManager.pushSysMsg(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MsgController pushSysMsg Method Exception :——》:" + e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(resultVo);
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
