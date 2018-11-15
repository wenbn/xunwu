package www.ucforward.com.controller.user;

import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.base.BaseController;
import www.ucforward.com.entity.HsSysUser;
import www.ucforward.com.manager.IMemberManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/8/21
 */
@Controller
@ResponseBody
public class UserController extends BaseController{

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IMemberManager memberManager;

    /**
     * 处理字符串 日期参数转换异常
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

    }

    /**
     * 获取内部用户列表
     * @return
     */
    @RequiresPermissions("user:list")
    @PostMapping(value="/user/list")
    public String getUserlList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0); //当前页
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"),-1); //当前页
        int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"),0); //语言版本
        String keyword = request.getParameter("keyword"); //语言版本
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("languageVersion",languageVersion);
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize == -1 ? AppRquestParamsConstant.APP_PAGE_SIZE : pageSize); //页显示条数
        condition.put("isDel", 0); //页显示条数
        condition.put("locked", 0); //页显示条数
        if(StringUtil.hasText(keyword)){
            condition.put("keyword", keyword); //页显示条数
        }
        vo = memberManager.getUserList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取内部用户详情
     * @return
     */
    @RequiresPermissions("user:detail")
    @PostMapping(value="/user/detail/{userId}")
    public String getUserDetail(@PathVariable Integer userId) throws Exception {
        ResultVo vo = memberManager.getUserDetail(userId);
        return JsonUtil.toJson(vo);
    }

    /**
     * 修改内部用户
     * @return
     */
    @RequiresPermissions("user:update")
    @PostMapping(value="/user/update")
    public String updateUser(HsSysUser user,String[] roles) throws Exception {
        //修改用户信息
        if(user.getId()== null || user.getId().intValue()<=0){//内部用户ID
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"内部用户ID不能为空"));
        }
        ResultVo vo = memberManager.updateUser(user,roles);
        return JsonUtil.toJson(vo);
    }

    //用户删除
    @RequiresPermissions("user:delete")
    @PostMapping(value="/user/delete")
    public String deleteUser(Integer userId) throws Exception {
        //修改用户信息
        if(userId== null || userId.intValue()<=0){//内部用户ID
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"内部用户ID不能为空"));
        }
        HsSysUser user = new HsSysUser();
        user.setId(userId);
        user.setIsDel(1);
        ResultVo vo = memberManager.updateUser(user,null);
        return JsonUtil.toJson(vo);
    }

    /**
     * 新增内部用户
     * @return
     */
    @RequiresPermissions("user:add")
    @PostMapping(value="/user/add")
    public String addUser(HsSysUser user,String[] roles) throws Exception {
        //修改用户信息
        if(!StringUtil.hasText(user.getUsername())){//内部用户账号
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"内部用户账号不能为空"));
        }
        if(!StringUtil.hasText(user.getUsercode())){//内部用户姓名
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"内部用户姓名不能为空"));
        }
        if(!StringUtil.hasText(user.getMobile())){//内部用户手机号
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"手机号不能为空"));
        }
        ResultVo vo = memberManager.addUser(user,roles);
        return JsonUtil.toJson(vo);
    }

}
