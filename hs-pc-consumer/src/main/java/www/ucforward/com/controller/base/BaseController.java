package www.ucforward.com.controller.base;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.springframework.web.bind.annotation.ExceptionHandler;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.realm.HsPcRealm;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/21
 */
public abstract class BaseController {

    /**
     * 登录认证异常
     */
    @ExceptionHandler({UnknownAccountException.class, UnauthenticatedException.class, AuthenticationException.class })
    public void authenticationException(HttpServletRequest request, HttpServletResponse response) {
        ResultVo vo = new ResultVo();
        vo.setMessage(ResultConstant.SYS_REQUIRED_UNLOGIN_ERROR_VALUE);
        vo.setResult(ResultConstant.SYS_REQUIRED_UNLOGIN_ERROR);
        JsonUtil.writeJson(vo,response);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({ UnauthorizedException.class, AuthorizationException.class })
    public void authorizationException(HttpServletRequest request, HttpServletResponse response) {
        ResultVo vo = new ResultVo();
        vo.setMessage(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
        vo.setResult(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR);
        JsonUtil.writeJson(vo,response);
    }

    /**
     * 输出JSON
     *
     * @param response
     * @author SHANHY
     * @create 2017年4月4日
     */
    private void writeJson(Object object, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.write(JsonUtil.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void clearAuth(){
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        HsPcRealm authRealm = (HsPcRealm)rsm.getRealms().iterator().next();
        authRealm.clearAuthz();
    }
}
