package www.ucforward.com.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.utils.EncryptionUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.PayLoadVo;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    Logger logger = Logger.getLogger(LoginInterceptor.class);

    @Override
    @ResponseBody
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        // 处理handler;
        if (handler instanceof HandlerMethod) {
            // 判断当前method上是否有标签;
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getMethodAnnotation(NoRequireLogin.class) == null) {
                ResultVo resultVo = new ResultVo();
                String token = request.getParameter("token");
                if (token == null) {//请求参数错误
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                    response.getWriter().write(JsonUtil.toJson(resultVo));
                    return false;
                }
                String[] params = token.split("\\.");
                if (params.length != 3) {
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                    response.getWriter().write(JsonUtil.toJson(resultVo));
                    return false;
                }

                PayLoadVo payLoadVo = JsonUtil.jsonToObjectT(EncryptionUtil.getFromBase64(params[1]), PayLoadVo.class);
                String signature = EncryptionUtil.getFromBase64(params[2]);
                if (payLoadVo == null || StringUtils.isBlank(signature)) {
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                    response.getWriter().write(JsonUtil.toJson(resultVo));
                    return false;
                }
                String rdsSignature = RedisUtil.safeGet(payLoadVo.getIss());
                if (StringUtils.isBlank(rdsSignature) || !rdsSignature.equals(signature)) {
                    resultVo.setResult(ResultConstant.BUS_MEMBER_LOGIN_TIMEOUT);
                    resultVo.setMessage(ResultConstant.BUS_MEMBER_LOGIN_TIMEOUT_VALUE);
                    response.getWriter().write(JsonUtil.toJson(resultVo));
                    return false;
                } else {
                    RedisUtil.safeSet(payLoadVo.getIss(), rdsSignature, 86400);
                }
            }
        }
        return true;
    }
}
