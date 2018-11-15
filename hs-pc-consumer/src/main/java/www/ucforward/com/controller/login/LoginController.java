package www.ucforward.com.controller.login;

import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.manager.IMemberManager;
import www.ucforward.com.utils.GoogleMapUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * PC登录
 * @author wenbn
 * @version 1.0
 * @date 2018/8/20
 */
@Controller
@RequestMapping
@ResponseBody
public class LoginController {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(LoginController.class); // 日志记录

    @Value("${PASSWORD_HASHITERATIONS}")
    private String PASSWORD_HASHITERATIONS;

    @Resource
    private SessionDAO sessionDao;
    @Resource
    private IMemberManager memberManager;

    /**
     * 系统管理员登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "login" ,method = RequestMethod.POST)
    public String login(@RequestParam("username") String username, @RequestParam("password") String password){
        ResultVo vo = new ResultVo();
        Subject subject = SecurityUtils.getSubject();
        //Session session = subject.getSession();
        UsernamePasswordToken token=new UsernamePasswordToken(username,password);
        try {
            subject.login(token);
            // 剔除其他此账号在其它地方登录
            List<Session> loginedList = getLoginedSession(subject);
            for (Session session : loginedList) {
                session.stop();
                RedisUtil.safeDel("shiro_redis_session:"+session.getId());
            }
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            vo.setDataSet(user.getRoleList());
            vo.setResult(ResultConstant.SYS_REQUIRED_LOGIN_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_LOGIN_SUCCESS_VALUE);
        }catch (UnknownAccountException e){
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":账号未注册");
        }catch (LockedAccountException e){
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":账号被锁定");
        }catch (DisabledAccountException e){
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":账号已被禁用，请联系管理员");
        }catch (ConcurrentAccessException e){
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":登录异常,不可多次登录");
        }catch (IncorrectCredentialsException e){
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":密码错误");
        }catch(Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":未知错误");
        }finally {
            return JsonUtil.toJson(vo);
        }
    }

    //遍历同一个账户的session
    private List<Session> getLoginedSession(Subject currentUser) {
        Collection<Session> list = ((DefaultSessionManager) ((DefaultSecurityManager) SecurityUtils
                .getSecurityManager()).getSessionManager()).getSessionDAO()
                .getActiveSessions();
        List<Session> loginedList = new ArrayList<Session>();
        ActiveUser loginUser = (ActiveUser) currentUser.getPrincipal();
        for (Session session : list) {
            Subject s = new Subject.Builder().session(session).buildSubject();
            if (s.isAuthenticated()) {
                ActiveUser user = (ActiveUser) s.getPrincipal();
                if (user.getUsercode().equalsIgnoreCase(loginUser.getUsercode())) {
                    if (!session.getId().equals(currentUser.getSession().getId())) {
                        loginedList.add(session);
                    }
                }
            }
        }
        return loginedList;
    }

    /**
     * 系统管理员退出登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        ResultVo vo = new ResultVo();
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        vo.setMessage("退出登录成功");
        return JsonUtil.toJson(vo);
    }

    /**
     * 系统管理员退出登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/get/permissions")
    public String getUserPermissions(HttpServletRequest request, HttpServletResponse response){
        Map<Object,Object> condition = Maps.newHashMap();
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userid = user.getUserid();
        //查询用户角色
        condition.put("isForbidden",0);
        condition.put("isDel",0);
        condition.put("userId",userid);
        //查询用户所能访问的资源名称
        ResultVo vo = memberManager.getUserPermissions(condition);
        return JsonUtil.toJson(vo);
    }


}
