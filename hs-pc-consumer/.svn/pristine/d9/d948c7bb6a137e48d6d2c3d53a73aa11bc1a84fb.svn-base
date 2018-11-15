package www.ucforward.com.controller.operation;

import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.controller.base.BaseController;
import www.ucforward.com.entity.HsSysPlatformSetting;
import www.ucforward.com.manager.ICommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/9/11
 */
@Controller
@ResponseBody
public class PlatformSetController extends BaseController {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(PlatformSetController.class);

    @Resource
    private ICommonManager commonManager;

    /**
     * 获取平台设置列表
     * @return
     */
    @PostMapping(value="/platform/setting/list")
    public String getPlatformSettingList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0); //当前页
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
        vo = commonManager.getPlatformSettingList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 修改平台设置
     * @return
     */
    @PostMapping(value="/platform/setting/update")
    public String updatePlatformSetting(HsSysPlatformSetting platformSetting) throws Exception {
        //修改平台设置
        ResultVo vo = commonManager.updatePlatformSetting(platformSetting);
        return JsonUtil.toJson(vo);
    }


    /**
     * 新增平台设置
     * @return
     */
    @PostMapping(value="/platform/setting/add")
    public String addPlatformSetting(HsSysPlatformSetting platformSetting) throws Exception {
        //新增平台设置
        ResultVo vo = commonManager.addPlatformSetting(platformSetting);
        return JsonUtil.toJson(vo);
    }

    /**
     * 删除平台设置
     * @return
     */
    @PostMapping(value="/platform/setting/detail/{id}")
    public String getPlatformSettingDetail(@PathVariable Integer id) throws Exception {
        //修改平台设置
        ResultVo vo = commonManager.getPlatformSettingDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     * 删除平台设置
     * @return
     */
    @PostMapping(value="/platform/setting/delete/{id}")
    public String deletePlatformSetting(@PathVariable Integer id) throws Exception {
        //修改平台设置
        ResultVo vo = commonManager.deletePlatformSetting(id);
        return JsonUtil.toJson(vo);
    }

}
