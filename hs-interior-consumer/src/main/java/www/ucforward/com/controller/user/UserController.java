package www.ucforward.com.controller.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.entity.HsHouseEvaluation;
import www.ucforward.com.entity.HsSysUserLocation;
import www.ucforward.com.manager.MemberAdminManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务员操作类
 * @author wenbn
 * @version 1.0
 * @date 2018/7/12
 */
@Controller
@RequestMapping("/user")
public class UserController {


    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(UserController.class); // 日志记录

    @Autowired
    private MemberAdminManager memberAdminManager;

    /**
     * 业务员提交当前定位
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/sendLocation",method = RequestMethod.POST)
    @ResponseBody
    public String sendLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultVo vo = new ResultVo();
        Map map = RequestUtil.getParameterMap(request);
        String userId = StringUtil.trim(map.get("userId"));
        double longitude = StringUtil.getDouble(StringUtil.trim(map.get("longitude")));//经度
        double latitude = StringUtil.getDouble(StringUtil.trim(map.get("latitude")));//纬度
        Subject subject = SecurityUtils.getSubject();
        ActiveUser user = (ActiveUser) subject.getPrincipal();
//        if(map==null){
//            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
//            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
//            return JsonUtil.toJson(vo);
//        }
//        if(!StringUtil.hasText(userId) || !StringUtil.hasText(longitude) || !StringUtil.hasText(latitude)){
//            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
//            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
//            return JsonUtil.toJson(vo);
//        }

        HsSysUserLocation userLocation = new HsSysUserLocation();
        latitude = 22.543096;
        longitude = 114.057865;
        int rd=(int)(Math.random()>0.5?1:0);
        int x=(int)((Math.random()*9+1)*100000)/10000000;
        int y=(int)((Math.random()*9+1)*100000)/10000000;
        if(rd>0){
            latitude = longitude+x;
            latitude = latitude+y;
        }else{
            latitude = longitude-x;
            latitude = latitude-y;
        }
        userLocation.setUserId(StringUtil.trim(user.getUserid()));
        userLocation.setLatitude(StringUtil.trim(latitude));
        userLocation.setLongitude(StringUtil.trim(longitude));
        userLocation.setCreateTime(vo.getSystemTime());
        try {
            vo = memberAdminManager.sendLocation(userLocation);
        } catch (Exception e) {
            logger.error("UserAdminController sendLocation Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }



}
