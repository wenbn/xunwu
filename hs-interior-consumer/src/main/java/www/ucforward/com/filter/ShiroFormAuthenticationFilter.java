package www.ucforward.com.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/21
 */
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest req = WebUtils.toHttp(request);
//        String firstLoginToken = req.getParameter("token");
//        System.out.println("token==="+firstLoginToken);
        if(this.isLoginRequest(request, response)) {
            if(this.isLoginSubmission(request, response)) {
                return this.executeLogin(request, response);
            } else {
                return true;
            }
        } else {
            //用户未登录
            ResultVo vo = new ResultVo();
            vo.setMessage(ResultConstant.SYS_REQUIRED_UNLOGIN_ERROR_VALUE);
            vo.setResult(ResultConstant.SYS_REQUIRED_UNLOGIN_ERROR);
            JsonUtil.writeJson(vo, (HttpServletResponse) response);
            return false;
        }
    }

}
