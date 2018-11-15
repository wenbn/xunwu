package www.ucforward.com.controller.operation;

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
import www.ucforward.com.entity.HsChannel;
import www.ucforward.com.manager.ICommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 频道管理类
 * @author wenbn
 * @version 1.0
 * @date 2018/8/20
 */
@Controller
@ResponseBody
public class ChannelController extends BaseController{

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Resource
    private ICommonManager commonManager;

    /**
     * 处理字符串 日期参数转换异常
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 获取频道列表
     * @return
     */
    @RequiresPermissions("channel:list")
    @PostMapping(value="/channel/list")
    public String getChannelList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0); //当前页
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"),-1); //当前页
        int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"),0); //语言版本
        int channelType = StringUtil.getAsInt(request.getParameter("channelType"),-1); //频道类型（1：PC网站，2：触屏端，3：App）
        int channelState = StringUtil.getAsInt(request.getParameter("channelState"),-1); //频道的状态（0：不启用，1：启用）
        int isDel = StringUtil.getAsInt(request.getParameter("isDel"),-1); //是否删除0:不删除，1：删除
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("languageVersion",languageVersion);
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize == -1 ? AppRquestParamsConstant.APP_PAGE_SIZE : pageSize); //页显示条数
        if(channelType != -1){
            condition.put("channelType",channelType);
        }
        if(channelState != -1){
            condition.put("channelState",channelState);
        }
        if(isDel != -1){
            condition.put("isDel",isDel);
        }
        vo = commonManager.getChannelList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取该频道下已经绑定的展位数据
     * @return
     */
    @RequiresPermissions("channel:update")
    @PostMapping(value="/load/channel/bind/data/{channelId}")
    public String getChannelBindData(@PathVariable Integer channelId) throws Exception {
        //通过频道ID获取已绑定的展位数据
        ResultVo vo = commonManager.getChannelBindData(channelId);
        return JsonUtil.toJson(vo);
    }

    /**
     * 解除该频道下已经绑定的展位数据
     * @return
     */
    @RequiresPermissions("channel:update")
    @PostMapping(value="/relieve/channel/bind/{channelId}/{boothIds}")
    public String relieveChannelBind(@PathVariable Integer channelId,@PathVariable String boothIds) throws Exception {
        if(channelId<=0){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        if(!StringUtil.hasText(boothIds)){//绑定的数据为空时直接返回绑定失败
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        List<Integer> boothIdsList =Arrays.asList(boothIds.split(","))
                .stream().map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());
        //解除该频道下已经绑定的展位数据
        ResultVo vo = commonManager.relieveChannelBind(channelId,boothIdsList);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取未绑定的展位数据
     * @return
     */
    @RequiresPermissions("channel:update")
    @PostMapping(value="/load/channel/unbind/data/{channelId}/{channelType}")
    public String getChannelUnBindData(@PathVariable Integer channelId,@PathVariable Integer channelType) throws Exception {
        //通过频道ID获取未绑定的展位数据
        ResultVo vo = commonManager.getChannelUnBindData(channelId,channelType);
        return JsonUtil.toJson(vo);
    }

    /**
     * 频道绑定展位
     * @return
     */
    @RequiresPermissions("channel:update")
    @PostMapping(value="/channel/binding/data/{channelId}/{boothIds}")
    public String bindingChannelData(@PathVariable Integer channelId,@PathVariable String boothIds) throws Exception {
        ResultVo vo = null;
        if(channelId<=0){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        if(!StringUtil.hasText(boothIds)){//绑定的数据为空时直接返回绑定失败
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE));
        }
        List<Integer> boothIdsList =Arrays.asList(boothIds.split(","))
                .stream().map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());
        //绑定展位数据
        vo = commonManager.bindingChannelData(channelId,boothIdsList);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取频道详情
     * @return
     */
    @RequiresPermissions("channel:detail")
    @PostMapping(value="/channel/detail/{channelId}")
    public String getChannelDetail(@PathVariable Integer channelId) throws Exception {
        ResultVo vo = commonManager.getChannelDetail(channelId);
        return JsonUtil.toJson(vo);
    }

    /**
     * 新增频道
     * @return
     */
    @RequiresPermissions("channel:add")
    @PostMapping(value="/channel/add")
    public String addChannel(HsChannel channel) throws Exception {
        ResultVo vo = null;
        if(!StringUtil.hasText(channel.getChannelName())){//频道名称
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道名称不能为空"));
        }
        if(!StringUtil.hasText(channel.getChannelAliasName())){//频道别名
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道别名不能为空"));
        }
        if(channel.getChannelType()==null){//频道类型（1：PC网站，2：触屏端，3：App）
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道类型不能为空"));
        }
        //添加频道
        vo = commonManager.addChannel(channel);
        return JsonUtil.toJson(vo);
    }

    /**
     * 修改频道
     * @param channel
     * @return
     * @throws Exception
     */
    @RequiresPermissions("channel:update")
    @PostMapping(value="/channel/update")
    public String updateChannel(HsChannel channel) throws Exception {
        ResultVo vo = null;
        if(channel.getId()== null || channel.getId().intValue()<=0){//频道ID
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道ID不能为空"));
        }
        if(!StringUtil.hasText(channel.getChannelName())){//频道名称
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道名称不能为空"));
        }
        if(StringUtil.hasText(channel.getChannelAliasName())){//频道别名
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道别名不能修改"));
        }
        if(channel.getChannelType()==null){//频道类型（1：PC网站，2：触屏端，3：App）
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"频道类型不能为空"));
        }
        //修改频道
        vo = commonManager.updateChannel(channel);
        return JsonUtil.toJson(vo);
    }

    /**
     * 频道展位
     * @return
     */
    @RequiresPermissions("channel:delete")
    @PostMapping(value="/channel/delete/{channelId}")
    public String deleteChannel(@PathVariable Integer channelId) throws Exception {
        if(channelId == null){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"展位ID不能为空"));
        }
        HsChannel channel = new HsChannel();
        channel.setId(channelId);
        channel.setIsDel(1);
        return JsonUtil.toJson(commonManager.updateChannel(channel));
    }

}
