package www.ucforward.com.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.utils.ImgCodeUtils;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Controller
@RequestMapping("/code")
public class RandomCodeController {

//    @Resource
//    private MemberManager memberManager;

    /**
     * 获取图形验证码
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public void getImgCode(HttpServletRequest request, HttpServletResponse response,HttpSession session){
        try{
            ImgCodeUtils.getCode(request,response,session);
            //ImgCodeUtils.getAuthCode(request,response,session);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 校验验证码
     */
    @ResponseBody
    @RequestMapping(value = "/check",method = RequestMethod.POST)
    public String checkCode(HttpServletRequest request, HttpServletResponse response){
        ResultVo vo = new ResultVo();
        Map<Object,Object> map = RequestUtil.getParameterMap(request);
        String imgType = StringUtil.trim(map.get("imgType"));
        String imgCode = StringUtil.trim(map.get("imgCode"));
        if(!StringUtil.hasText(imgType) || !StringUtil.hasText(imgCode)){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        String ip = RequestUtil.getIpAddress(request);
        String cacheCode = "";
        if(imgType.equals("1")){//注册验证码
            cacheCode = RedisUtil.safeGet(RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY+ip);
        }
        if(cacheCode.equals(imgCode)){
            vo.setResult(0);
            vo.setMessage("校验成功");
        }else{
            vo.setResult(-1);
            vo.setMessage("验证码错误");
        }
        return JsonUtil.toJson(vo);
    }

}
