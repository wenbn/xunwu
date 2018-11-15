package www.ucforward.com.controller.interior;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.HousesAdminManager;
import www.ucforward.com.manager.MemberAdminManager;
import www.ucforward.com.manager.OrderAdminManager;
import www.ucforward.com.socket.WebSocket;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 业务员控制器
 * Created by Administrator on 2018/7/12.
 */
@Controller
@RequestMapping("salesman")
public class SalesmanController {

    private static Logger logger = LoggerFactory.getLogger(SalesmanController.class); // 日志记录

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

    }

    @Resource
    private MemberAdminManager memberAdminManager;
    @Resource
    private OrderAdminManager orderAdminManager;
    @Resource
    private HousesAdminManager housesAdminManager;

    /**
     * 查询外货业务员派单列表信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOutGainDispatchOrder",method = RequestMethod.POST)
    @ResponseBody
    public String getOutGainDispatchOrder(HttpServletRequest request, HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            //任务类型（0外获任务；1：外看任务；2合同任务）
            List<Integer> taskTypes = new ArrayList<>();
            taskTypes.add(0);
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员")) {
                    condition.put("userId",user.getUserid()); //业务员ID
                    condition.put("isFinished",0);//是否完成 0：未完成，1：已完成
                    isTrue = true;
                    taskTypes.add(4);
                }else{
                    condition.put("keysManagerId",user.getUserid()); //业务员ID
                    condition.put("isFinishedSendKeys",0);//钥匙管理员任务是否完成0:未完成，1：已完成
                    List<Integer> isFinisheds = Lists.newArrayList();
                    isFinisheds.add(0);
                    isFinisheds.add(1);
                    condition.put("isFinished",1);//1：已完成
                    condition.put("isFinisheds",isFinisheds);//钥匙管理员任务是否完成0:未完成，1：已完成
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0")); //语言版本
            condition.put("isTransferOrder",0); //未转单
            condition.put("isDel",0); //是否删除 0 ：未删除，1：已删除
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion);

            condition.put("taskTypes",taskTypes);
//            condition.put("taskType",0); //0：外获任务 1：外看任务 2：合同任务
            condition.put("needSort",1); //需要排序
            condition.put("orderBy","ESTIMATED_TIME"); //需要排序
            condition.putAll(map);
            result = memberAdminManager.getOutGainDispatchOrder(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getDispatchOrder Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 批量转单操作
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "batchDispathOrder",method = RequestMethod.POST)
    @ResponseBody
    public String batchDispathOrder(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object,Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String ids = StringUtil.trim(map.get("ids")); //任务ids
            String times = StringUtil.trim(map.get("times")); //任务开始时间，以逗号分隔
            String taskType = StringUtil.trim(map.get("taskType")); //任务类型： 0：外获，1：外看
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    condition.put("roleName","钥匙管理员");
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            if(!StringUtil.hasText(ids) || !StringUtil.hasText(taskType)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.put("userId",user.getUserid());
            condition.putAll(map);

            result = memberAdminManager.batchDispathOrder(condition);
        } catch (Exception e) {
            logger.error("SalesmanController batchDispathOrder Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 外获任务详情信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "outGainOrderDetail",method = RequestMethod.POST)
    @ResponseBody
    public String outGainOrderDetail(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String applyId = StringUtil.trim(map.get("applyId")); //申请ID
            String poolId = StringUtil.trim(map.get("poolId")); //订单池ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(applyId) || !StringUtil.hasText(poolId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);

            result = memberAdminManager.outGainOrderDetail(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 到达客户
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "arriveAtCustomer",method = RequestMethod.POST)
    @ResponseBody
    public String arriveAtCustomer(HttpServletRequest request, HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //任务ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(id)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("memberId",user.getUserid());
            condition.put("isArrive",1); //是否到达 0：未到达 1；已到达

            result = memberAdminManager.operateUserOrderTask(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 客户取消预约操作
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "customerCancelAppointment" ,method = RequestMethod.POST)
    @ResponseBody
    public String customerCancelAppointment(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String taskType = StringUtil.trim(map.get("taskType")); //任务类型（0：外获任务，1：外看任务）
            String cancelType = StringUtil.trim(map.get("cancelType")); //取消预约类型（0：业主取消预约，1：租客/买家取消预约）
            String feedbackType = StringUtil.trim(map.get("feedbackType")); //反馈类型
            String feedbackDesc = StringUtil.trim(map.get("feedbackDesc")); //反馈描述

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(taskType) || !StringUtil.hasText(cancelType) || !StringUtil.hasText(feedbackType) || !StringUtil.hasText(feedbackDesc)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("memberId",user.getUserid());
            result = memberAdminManager.cancelAppointment(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取已上传房源列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getUploadedHousingList",method = RequestMethod.POST)
    @ResponseBody
    public String getUploadedHousingList(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                    isTrue = true;
                    condition.put("userId",user.getUserid()); //业务员ID
                }else if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                    condition.put("keysManagerId",user.getUserid()); //业务员ID
                    condition.put("isFinishedSendKeys",1); //
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0")); //语言版本
            condition.put("isDel",0); //未删除
            condition.put("isFinished",1); //未删除
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion);
            result = memberAdminManager.getUploadedHousingList(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取申请详细信息(上传房源页面)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getApplicationDetails",method = RequestMethod.POST)
    @ResponseBody
    public String getApplicationDetails(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        //TODO 查询图片
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String applyId = StringUtil.trim(map.get("applyId")); //申请ID
            if(!StringUtil.hasText(applyId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            result = memberAdminManager.getApplicationDetails(condition);

        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 上传房源
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadHousing",method = RequestMethod.POST)
    @ResponseBody
    public String uploadHousing(HttpServletRequest request, HsMainHouse hsMainHouse, HsHouseCredentialsData hsHouseCredentialsData,HsHouseImg hsHouseImg){
        ResultVo result = new ResultVo();
        try {
            //TODO 自动应答设置,
            Map<Object,Object> condition = new HashMap<>();
            Map map = RequestUtil.getParameterMap(request);

            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            Object houseObj = RequestUtil.handleRequestBeanData(hsMainHouse);
            HsMainHouse mainHouse = (HsMainHouse) houseObj;

            Object credentialsObj = RequestUtil.handleRequestBeanData(hsHouseCredentialsData);
            HsHouseCredentialsData houseCredentialsData = (HsHouseCredentialsData) credentialsObj;

            Object imgObj = RequestUtil.handleRequestBeanData(hsHouseImg);
            HsHouseImg houseImg = (HsHouseImg) imgObj;

            String taskId = request.getParameter("taskId"); //任务ID
            int houseId = StringUtil.getAsInt(request.getParameter("houseId"),-1); //房源ID
            if(houseId == -1){//房源Id是否存在
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            if(!StringUtil.hasText(taskId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String json = StringUtil.trim(map.get("setting")).replaceAll("&quot;","\"");

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            mainHouse.setCreateBy(user.getUserid());
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                    condition.put("userId",user.getUserid());
                }else{
                    condition.put("keysManagerId",user.getUserid());
                    condition.put("keysManagerRemark",request.getParameter("keysManagerRemark"));//钥匙管理员说明
                }
            }
            condition.put("setting",json);
            condition.put("taskId",taskId);
            mainHouse.setId(houseId);
            result = memberAdminManager.uploadHousing(mainHouse,houseCredentialsData,houseImg,condition);

        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取外获人员个人绩效
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOutGainPersonalPerformance",method = RequestMethod.POST)
    @ResponseBody
    public String getOutGainPersonalPerformance(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") ){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("userId",user.getUserid()); //业务员ID
            condition.put("taskType",0); //0外获任务

            result = memberAdminManager.getOutGainPersonalPerformance(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取外看人员派单列表信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOutLookDispatchOrder",method = RequestMethod.POST)
    @ResponseBody
    public String getOutLookDispatchOrder(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            //任务类型（0外获任务；1：外看任务；2合同任务）
            List<Integer> taskTypes = new ArrayList<>();
            taskTypes.add(1);
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                    isTrue = true;
                    taskTypes.add(3);
                    condition.put("userId",user.getUserid()); //业务员ID
                }else if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                    condition.put("keysManagerId",user.getUserid()); //业务员ID
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0")); //语言版本
            condition.put("isFinished",0);//是否完成 0：未完成，1：已完成
            condition.put("isDel",0); //是否删除 0 ：未删除，1：已删除
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion);
            //condition.put("userId",user.getUserid());
            condition.put("taskTypes",taskTypes);
            condition.putAll(map);
            result = memberAdminManager.getOutLookDispatchOrder(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getDispatchOrder Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取区域长投诉订单列表
     * @param request
     * @return
     */
    @RequestMapping(value = "getComplaintDispatchOrder",method = RequestMethod.POST)
    @ResponseBody
    public String getComplaintDispatchOrder(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                    condition.put("keysManagerId",user.getUserid()); //业务员ID
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0")); //语言版本
            condition.put("isFinished",0);//是否完成 0：未完成，1：已完成
            condition.put("isDel",0); //是否删除 0 ：未删除，1：已删除
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion);
            //任务类型（0外获任务；1：外看任务；2合同任务）
            List<Integer> taskTypes = new ArrayList<>();
            taskTypes.add(5);
            condition.put("taskTypes",taskTypes);
            //用于区分是否查询投诉列表
            condition.put("isComplaint",1);
            condition.putAll(map);
            result = memberAdminManager.getOutLookDispatchOrder(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getComplaintDispatchOrder Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取外看、外获投诉派单详情
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getComplaintDispatchOrderDetail",method = RequestMethod.POST)
    @ResponseBody
    public String getComplaintDispatchOrderDetail(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>(3);
        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String poolId = StringUtil.trim(map.get("poolId")); //订单池ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员") || StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(poolId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("taskId",taskId);
            condition.put("id",poolId);
            result = memberAdminManager.getComplaintDispatchOrderDetail(condition);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取外看派单任务详情
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOutLookDispatchOrderDetail",method = RequestMethod.POST)
    @ResponseBody
    public String getOutLookDispatchOrderDetail(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String poolId = StringUtil.trim(map.get("poolId")); //订单池ID
            String houseId = StringUtil.trim(map.get("houseId")); //房源主信息ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(poolId) || !StringUtil.hasText(houseId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.putAll(map);
            result = memberAdminManager.getOutLookDispatchOrderDetail(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 完成看房
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "finishedLookHouse",method = RequestMethod.POST)
    @ResponseBody
    public String finishedLookHouse(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //任务ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object, Object>> roleList = user.getRoleList();

            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                //没有操作权限
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(id)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("memberId",user.getUserid());
            condition.put("isFinished",1); //是否完成看房 0：未完成 1:已完成

            result = memberAdminManager.operateUserOrderTask(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 获取上传房源详细信息(编辑上传页面)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getUploadHouseDetail",method = RequestMethod.POST)
    @ResponseBody
    public String getUploadHouseDetail(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            /*boolean isTrue = false;
           for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }*/
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            if(!StringUtil.hasText(houseId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.putAll(map);
            result = memberAdminManager.getUploadHouseDetail(condition);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 修改已上传的房源
     * @param request
     * @param response
     * @param hsMainHouse
     * @param hsHouseCredentialsData
     * @return
     */
    @RequestMapping(value = "updateUploadedHouse",method = RequestMethod.POST)
    @ResponseBody
    public String updateUploadedHouse(HttpServletRequest request,HttpServletResponse response,HsMainHouse hsMainHouse,HsHouseCredentialsData hsHouseCredentialsData,HsHouseImg hsHouseImg){
        ResultVo result = new ResultVo();
        try {
            Map<Object,Object> condition = new HashMap<>();
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            Object houseObj = RequestUtil.handleRequestBeanData(hsMainHouse);
            HsMainHouse mainHouse = (HsMainHouse) houseObj;

            Object credentialsObj = RequestUtil.handleRequestBeanData(hsHouseCredentialsData);
            HsHouseCredentialsData houseCredentialsData = (HsHouseCredentialsData) credentialsObj;

            Object imgObj = RequestUtil.handleRequestBeanData(hsHouseImg);
            HsHouseImg houseImg = (HsHouseImg) imgObj;

            String json = StringUtil.trim(map.get("setting")).replaceAll("&quot;","\"");

            boolean isTrue = false;

            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外获业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            mainHouse.setCreateBy(user.getUserid());
            condition.put("setting",json);
            result = memberAdminManager.updateUploadedHouse(mainHouse,houseCredentialsData,houseImg,condition);
        } catch (IllegalAccessException e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 外看业务员已带看房列表
     *
     * @return
     */
    @RequestMapping(value="/look/finished/list",method = RequestMethod.POST)
    @ResponseBody
    public String lookHouseFinishedList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        Map<Object,Object> condition = new HashMap<>();
        Map map = RequestUtil.getParameterMap(request);
        try {
            Integer userid = user.getUserid();
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1");//当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0"));//语言版本
            condition.put("pageIndex",pageIndex);//当前页
            condition.put("languageVersion",languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数
            condition.put("isFinished",1);//已完成
            condition.put("isDel",0);//未删除
            condition.put("userId",userid);
            vo = memberAdminManager.selectLookForHouseFinished(condition);
        } catch (Exception e) {
            logger.error("UserAdminController sendLocation Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 外看业务员已带看房详情
     * @param taskId 任务Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/look/finished/detail",method = RequestMethod.POST)
    @ResponseBody
    public String lookHouseFinishedDetail(@RequestParam(required = false) int taskId) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        Map<Object,Object> condition = new HashMap<>();
        try {
            condition.put("taskId",taskId);//任务ID
            condition.put("userId",user.getUserid());//业务员ID
            condition.put("isFinished",1);//已完成
            condition.put("isDel",0);//未删除
            vo = memberAdminManager.selectLookForHouseFinishedDetail(condition);
        } catch (Exception e) {
            logger.error("UserAdminController sendLocation Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 外看业务员带看房源评论
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/house/evaluation/add",method = RequestMethod.POST)
    @ResponseBody
    public String addHouseEvaluation(HttpServletRequest request, @Valid HsHouseEvaluation evaluation, BindingResult br) throws Exception {
        ResultVo vo = null;
        if(br.hasErrors()){//校验数据
            FieldError fieldError = br.getFieldErrors().get(0);
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Integer userid = user.getUserid();
            evaluation.setUserId(userid);
            evaluation.setCreateBy(userid);//业务员ID
            evaluation.setCreateTime(new Date());
            evaluation.setValuatorType(0);//业务员评价
            evaluation.setIsCheck(1);
            vo = memberAdminManager.addHouseEvaluation(evaluation);
        } catch (Exception e) {
            logger.error("UserController addHouseEvaluation Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 抢单列表
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/grab/orders/list",method = RequestMethod.POST)
    @ResponseBody
    public String grabOrdersList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        //订单类型 0外获订单->1-外看订单->2合同订单->3-投诉（外看）->4投诉（外获）->5投诉（区域长）
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
        List<Integer> orderTypes = new ArrayList<>();
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                return JsonUtil.toJson(ResultVo.success());
            }
            if(StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
                orderTypes.add(0);
                orderTypes.add(1);
                orderTypes.add(2);
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                isTrue =true;
                orderTypes.add(1);
                orderTypes.add(3);
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                orderTypes.add(0);
                orderTypes.add(2);
                orderTypes.add(4);
                break;
            }

        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Integer userid = user.getUserid();
            Map<Object,Object> condition = new HashMap<Object,Object>();
            condition.put("isOpenOrder",1);//是否开启抢单
            if(orderType > 0){
                condition.put("orderType",orderType);
            }else{
                condition.put("orderTypes",orderTypes);
            }
            condition.put("isFinished",0);//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            condition.put("isDel",0);//已删除
            condition.put("userid",userid);//业务员ID
            String pageIndex = StringUtil.trim(request.getParameter("pageIndex"),"1");//当前页
            condition.put("pageIndex",pageIndex);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数
            vo = orderAdminManager.grabOrdersList(condition);
        } catch (Exception e) {
            logger.error("SalesmanController grabOrdersList Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 投诉订单抢单详情
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/complaint/grab/order/detail",method = RequestMethod.POST)
    @ResponseBody
    public String getComplaintGrabOrderDetail(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        //订单池ID
        int poolId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("poolId")),-1);
        if( poolId == -1 ){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                break;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Map<Object,Object> condition = new HashMap<Object,Object>(2);
            //订单池ID
            condition.put("id",poolId);
            //业务员ID
            condition.put("userId",user.getUserid());
            vo = orderAdminManager.getGrabOrderDetail(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SalesmanController getComplaintGrabOrderDetail Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 更新投诉订单
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/complaint/grab/order/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateComplaintGrabOrderDetail(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        Map<Object,Object> condition = new HashMap<Object,Object>(5);
        //投诉ID
        int id = StringUtil.getAsInt(StringUtil.trim(request.getParameter("id")),-1);
        //任务id
        int taskId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("taskId")));
        //备注
        String remark = request.getParameter("remark");
        //投诉是否属实：0未核实 1情况属实 2情况不属实
        int isVerified = StringUtil.getAsInt(request.getParameter("isVerified"));
        if( id == -1 || taskId == -1 || !StringUtil.hasText(remark) || isVerified < 1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }

        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                isTrue =true;
                //区域长备注
                condition.put("remarkKeyManager",remark);
            }
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                isTrue =true;
                //外看备注
                condition.put("remarkLookOutside",remark);
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                //外获备注
                condition.put("remarkOutOf",remark);
                break;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            //投诉ID
            condition.put("id",id);
            //任务id
            condition.put("taskId",taskId);
            //业务员ID
            condition.put("userId",user.getUserid());
            condition.put("isVerified",isVerified);
            vo = orderAdminManager.updateComplaintGrabOrderDetail(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SalesmanController updateComplaintGrabOrderDetail Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 投诉抢单详情
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/grab/order/detail",method = RequestMethod.POST)
    @ResponseBody
    public String getGrabOrderDetail(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int poolId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("poolId")),-1);//订单池ID
        if( poolId == -1 ){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                isTrue =true;
                break;
            }
        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Map<Object,Object> condition = new HashMap<Object,Object>();
            condition.put("id",poolId);//订单池ID
            condition.put("userId",user.getUserid());//业务员ID
            vo = orderAdminManager.getGrabOrderDetail(condition);
        } catch (Exception e) {
            logger.error("SalesmanController grabOrdersList Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 查询房源数据字典数据(业主申请页面)
     * @return
     */
    @RequestMapping(value = "getHouseDictcode", method = RequestMethod.POST)
    @ResponseBody
    public String getHouseDictcode() {
        ResultVo result = null;
        try {
            result = housesAdminManager.getHouseDictcode();
        } catch (Exception e) {
            logger.error("HousesController controller: getHouseDictcode Exception message:" + e);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 房源详情
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/house/detail/{id}",method = RequestMethod.POST)
    @ResponseBody
    public String getHouseDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = null;
        try {
            vo = housesAdminManager.getHouseDetail(id);
        } catch (Exception e) {
            logger.error("SalesmanController getHouseDetail Method Exception :——》:" + e);
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 业务员抢单动作
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/grab/order/action",method = RequestMethod.POST)
    @ResponseBody
    public String grabOrderAction(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int poolId  = StringUtil.getAsInt(request.getParameter("poolId"),-1);
        int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"), 0);
        if(poolId == -1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        List<Integer> orderTypes = new ArrayList<>();
        Integer salesmanType = 0;//业务员类型 0 外获业务员，1外看业务员
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("系统管理员")){
                isTrue =true;
                orderTypes.add(0);
                orderTypes.add(1);
                orderTypes.add(2);
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                isTrue =true;
                orderTypes.add(1);
                orderTypes.add(3);
                salesmanType = 1;//外看业务员
                break;
            }
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                orderTypes.add(0);
                orderTypes.add(2);
                orderTypes.add(4);
                break;
            }

        }
        if(!isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Integer userid = user.getUserid();
            String username = user.getMobile();
            Map<Object,Object> condition = new HashMap<Object,Object>();
            condition.put("poolId",poolId);//订单池ID
            condition.put("userid",userid);//业务员ID
            condition.put("salesmanType",salesmanType);//业务员类型
            //用户名（手机号）
            condition.put("username",username);
            //语言版本
            condition.put("languageVersion",languageVersion);
            vo = orderAdminManager.grabOrderAction(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SalesmanController grabOrdersList Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 业务员修改见面时间和见面地点
     * @param request
     * @return
     * @throws Exception
     */
    @RequiresUser
    @RequestMapping(value="/update/pool/info",method = RequestMethod.POST)
    @ResponseBody
    public String updatePoolInfo(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        int poolId = StringUtil.getAsInt(request.getParameter("poolId"));
        String estimatedTime = request.getParameter("estimatedTime");//预计完成时间
        String appointmentMeetPlace = request.getParameter("appointmentMeetPlace");//见面地点
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            if(poolId == -1){//订单池id为空
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
            }
            HsSystemOrderPool orderPool = new HsSystemOrderPool();
            orderPool.setId(poolId);
            if(StringUtil.hasText(estimatedTime)){
                Date _estimatedTime = DateUtils.stringtoDate(estimatedTime,"yyyy-MM-dd HH:mm:ss");
                Date nowDate =new Date();
                if(_estimatedTime.getTime()<=nowDate.getTime()){
                    return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"修改时间不能小于当前时间"));
                }
                orderPool.setEstimatedTime(_estimatedTime);
            }
            if(StringUtil.hasText(appointmentMeetPlace)){
                orderPool.setAppointmentMeetPlace(appointmentMeetPlace);
            }
            Integer userId = user.getUserid();
            condition.put("userId",userId);
            condition.put("orderPool",orderPool);
            vo = memberAdminManager.updatePoolInfo(condition);
            orderPool=null;
        } catch (Exception e) {
            logger.error("SalesmanController getContractOrdersList Method Exception :——》:" + e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return JsonUtil.toJson(vo);
    }


    /**
     * 外获业务员合同单列表
     * @param request
     * @return
     * @throws Exception
     */
    @RequiresUser
    @RequestMapping(value="/contract/orders/list",method = RequestMethod.POST)
    @ResponseBody
    public String getContractOrdersList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                break;
            }
        }
        if(isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Map<Object,Object> condition = new HashMap<Object,Object>();
            String pageIndex = StringUtil.trim(request.getParameter("pageIndex"),"1");//当前页
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"),0);//语言版本
            condition.put("pageIndex",pageIndex);//当前页
            condition.put("languageVersion",languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数
            Integer userId = user.getUserid();
            condition.put("userId",userId);
            condition.put("taskType",2);//查询自己的合同单
            condition.put("isDel",0);//已删除
            condition.put("isFinished",0);//任务未完成
            condition.put("isTransferOrder",0);//未转单
            vo = memberAdminManager.getContractOrdersList(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getContractOrdersList Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 外获业务员合同单记录
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/contract/orders/records",method = RequestMethod.POST)
    @ResponseBody
    public String getContractOrderRecordsList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object, Object>> roleList = user.getRoleList();
        boolean isTrue = false;
        for (Map<Object, Object> role : roleList) {
            if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                isTrue =true;
                break;
            }
        }
        if(isTrue){
            //无操作权限
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
        }
        try {
            Map<Object,Object> condition = new HashMap<Object,Object>();
            String pageIndex = StringUtil.trim(request.getParameter("pageIndex"),"1");//当前页
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"),0);//语言版本
            condition.put("pageIndex",pageIndex);//当前页
            condition.put("languageVersion",languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数
            Integer userId = user.getUserid();
            condition.put("userId",userId);
            condition.put("taskType",2);//查询自己的合同单
            condition.put("isDel",0);//已删除
            condition.put("isFinished",1);//已完成
            condition.put("isTransferOrder",0);//未转单
            vo = memberAdminManager.getContractOrderRecordsList(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getContractOrdersList Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取外看人员个人绩效
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOutLookPersonalPerformance",method = RequestMethod.POST)
    @ResponseBody
    public String getOutLookPersonalPerformance(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();

        try {
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();

            boolean isTrue = true;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("userId",user.getUserid()); //业务员ID
            condition.put("taskType",1); //外看任务

            result = memberAdminManager.getOutLookPersonalPerformance(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }


    /**
     * 业务员查询考勤
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/query/attendance",method = RequestMethod.POST)
    @ResponseBody
    public String getInquiryAttendance(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();

        try {
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            condition.put("userId",user.getUserid()); //业务员ID
//            condition.put("queryMonthsAgo",1);//查询前几个月的数据,
            condition.put("queryCurrentMonth",1);//非空即为查当月的数据
            //selectCustomColumnNamesList
            result = memberAdminManager.queryUserAttendances(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    @RequestMapping(value = "getProgressInfo",method = RequestMethod.POST)
    @ResponseBody
    public String getProgressInfo(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> map = RequestUtil.getParameterMap(request);
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<Object,Object>> roleList = user.getRoleList();
        String houseId = StringUtil.trim(map.get("houseId")); //房源ID

        boolean isTrue = false;
        if(roleList.size() > 0){
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员") || StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
        }

        if(!isTrue){
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
            return JsonUtil.toJson(result);
        }

        if(!StringUtil.hasText(houseId)){
            result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(result);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 修改见面时间
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateMeetTime",method = RequestMethod.POST)
    @ResponseBody
    public String updateMeetTime(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String poolId = StringUtil.trim(map.get("poolId")); //订单池ID

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(poolId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            condition.putAll(map);
            condition.put("userId",user.getUserid());
            result = memberAdminManager.updateMeetTime(condition); //修改见面时间
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 修改见面地点
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateMeetPlace",method = RequestMethod.POST)
    @ResponseBody
    public String updateMeetPlace(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String poolId = StringUtil.trim(map.get("poolId")); //订单池ID

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(poolId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            condition.putAll(map);
            condition.put("userId",user.getUserid());
            result = memberAdminManager.updateMeetPlace(condition); //修改见面时间
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);

    }


    /**
     * 获取业务员信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/info",method = RequestMethod.POST)
    @ResponseBody
    public String getUserInfo(HttpServletRequest request){
        ResultVo result = null;
        //查询条件
        Map<Object,Object> condition = Maps.newHashMap();
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        condition.put("id",user.getUserid());//业务员ID
        condition.put("locked",0);//账号是否锁( 0未锁定 , 1：锁定)
        condition.put("isDel",0);//是否删除( 0：未删除， 1:删除)
        //获取业务员信息
        result = memberAdminManager.getUserInfo(condition);
        condition = null;
        return JsonUtil.toJson(result);
    }


    /**
     * 获取房源钥匙,扫二维码的处理事件
     * @param request
     * @return
     */
    @RequestMapping(value = "/get/house/key")
    @ResponseBody
    public String getHouseKey(HttpServletRequest request){
        ResultVo result = null;
        int houseId = StringUtil.getAsInt(request.getParameter("houseId"),-1);//房源ID
        String houseCode = request.getParameter("houseCode");//房源编号
        if(houseId == -1 || !StringUtil.hasText(houseCode)){//请求参数错误
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }

        //查询条件
        Map<Object,Object> condition = Maps.newHashMap();
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        HsHouseKeyCases keyCases = new HsHouseKeyCases();
        keyCases.setHouseId(houseId);
        keyCases.setHouseCode(houseCode);
        keyCases.setUserId(user.getUserid());//业务员ID
        keyCases.setCreateTime(new Date());
        keyCases.setCreateBy(user.getUserid());
        keyCases.setRemarks("业务员扫二维码获取房源钥匙");
        condition.put("key",keyCases);

        //业务员获取房源钥匙
        result = memberAdminManager.getHouseKey(condition);
        if(ResultConstant.SYS_REQUIRED_SUCCESS == result.getResult()){
            try {
                WebSocket.sendMessageAll(StringUtil.trim(houseId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        condition = null;
        return JsonUtil.toJson(result);
    }

    /**
     * 检查房源钥匙是否过期，检测二维码是否被扫
     * @param request
     * @return
     */
    @RequestMapping(value = "/check/key/isExpire")
    @ResponseBody
    public String checkKeyIsExpire(HttpServletRequest request){
        ResultVo result = null;
        int houseId = StringUtil.getAsInt(request.getParameter("houseId"),-1);//房源ID
        if(houseId == -1){//请求参数错误
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        //业务员获取房源钥匙
        result = memberAdminManager.checkKeyIsExpire(houseId,user.getUserid());
        return JsonUtil.toJson(result);
    }

    /**
     * 检查是否可以显示房源二维码
     * @param request
     * @return
     */
    @RequestMapping(value = "/check/show/qrcode")
    @ResponseBody
    public String checkShowHouseQrcode(HttpServletRequest request){
        ResultVo result = null;
        int houseId = StringUtil.getAsInt(request.getParameter("houseId"),-1);//房源ID
        if(houseId == -1){//请求参数错误
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        //业务员获取房源钥匙
        result = memberAdminManager.checkShowHouseQrcode(houseId,user.getUserid());
        return JsonUtil.toJson(result);
    }


    /**
     * 获取房源钥匙数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/my/keys",method = RequestMethod.POST)
    @ResponseBody
    public String loadMyHousekeys(HttpServletRequest request){
        ResultVo result = null;
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("userId",user.getUserid());
        condition.put("isExpire",0);//是否过期 0:未过期，1：已过期
        //业务员获取房源钥匙数据
        result = memberAdminManager.loadMyHousekeys(condition);
        return JsonUtil.toJson(result);
    }

    /**
     * 获取钥匙管理员派单任务列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getKeysManagerTaskList",method = RequestMethod.POST)
    @ResponseBody
    public String getKeysManagerTaskList(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }

            if(!isTrue){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE));
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"))); //语言版本
            condition.put("isFinished",0); //未完成
            condition.put("isDel",0); //未删除
            condition.put("pageIndex",pageIndex); //当前页
            condition.put("pageSize",AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion);
            condition.put("userId",user.getUserid());
            result = memberAdminManager.getKeysManagerTaskList(condition);
        } catch (Exception e) {
            logger.error("SalesmanController getKeysManagerTaskList Exception Message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 完成合同任务
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "finishedContractTask",method = RequestMethod.POST)
    @ResponseBody
    public String finishedContractTask(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();

            boolean isTrue = false;
            if(roleList.size() > 0){
                for(Map<Object,Object> role : roleList){
                    if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                        isTrue = true;
                    }
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String holdContractPic = StringUtil.trim(map.get("holdContractPic")); //手持合同半身照

            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(holdContractPic)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("userId",user.getUserid());

            result = memberAdminManager.finishedContractTask(condition);
        } catch (Exception e) {
            logger.error("SalesmanController finishedContractTask Exception Message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取区域长个人绩效信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getKeysManagerPersonalPerformance",method = RequestMethod.POST)
    @ResponseBody
    public String getKeysManagerPersonalPerformance(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            if(roleList.size() > 0){
                for(Map<Object,Object> role : roleList){
                    if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                        isTrue = true;
                    }
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("keysManagerId",user.getUserid()); //钥匙管理员ID
            result = memberAdminManager.getKeysManagerPersonalPerformance(condition); //查询区域长个人绩效信息
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 获取送钥匙任务列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getDeliveryKeysTaskList",method = RequestMethod.POST)
    @ResponseBody
    public String getDeliveryKeysTaskList(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;

            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            if(!StringUtil.hasText(StringUtil.trim(map.get("pageIndex")))){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0"));
            condition.put("isFinishedSendKeys",0); //是否完成送钥匙任务
            condition.put("isFinished",0); //业务员外看任务未完成
            condition.put("isDel",0); //是否删除，0：未删除，1：已删除
            condition.put("languageVersion",languageVersion); //语言版本
            condition.put("keysManagerId",user.getUserid()); //钥匙管理员ID
            condition.put("taskType",1); ////任务类型（送钥匙为外看任务）
            condition.putAll(map);
            condition.remove("pageIndex"); //当前页
            condition.remove("pageSize"); //当前页
            result = memberAdminManager.getDeliveryKeysTaskList(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取派送钥匙记录信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getDeliveredKeysRecordList",method = RequestMethod.POST)
    @ResponseBody
    public String getDeliveredKeysRecordList(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR,ResultConstant.SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(StringUtil.trim(map.get("pageIndex")))){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"))); //语言版本
            condition.put("isFinished",1); //外看任务完成
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize",AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            condition.put("languageVersion",languageVersion); //语言版本
            condition.put("keysManagerId",user.getUserid()); //钥匙管理员ID
            condition.put("taskType",1); //任务类型（外看任务）

            result = memberAdminManager.getDeliveredKeysRecordList(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 获取派送钥匙任务详情信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getDeliveryKeysTaskDetail",method = RequestMethod.POST)
    @ResponseBody
    public String getDeliveryKeysTaskDetail(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String taskId = StringUtil.trim(map.get("taskId")); //任务ID
            String houseId = StringUtil.trim(map.get("houseId")); //房源主信息ID

            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }

            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            if(!StringUtil.hasText(taskId) || !StringUtil.hasText(houseId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.putAll(map);

            result = memberAdminManager.getDeliveryKeysTaskDetail(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 完成钥匙派送
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "finishedDeliveryKeys",method = RequestMethod.POST)
    @ResponseBody
    public String finishedDeliveryKeys(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //任务ID
            String taskId = StringUtil.trim(map.get("taskId"));
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }

            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            if(!StringUtil.hasText(taskId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.putAll(map);
            condition.put("userId",user.getUserid());

            result = memberAdminManager.finishedDeliveryKeys(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 到达送钥匙地点
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "arriveDeliveryKeysPlace",method = RequestMethod.POST)
    @ResponseBody
    public String arriveDeliveryKeysPlace(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            List<Map<Object,Object>> roleList = user.getRoleList();
            boolean isTrue = false;
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    isTrue = true;
                }
            }
            if(!isTrue){
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR,ResultConstant.SYS_REQUIRED_UNOPERATION_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            if(!StringUtil.hasText(houseId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            result = memberAdminManager.arriveDeliveryKeysPlace(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 我的结佣记录表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "my/brokerages",method = RequestMethod.POST)
    @ResponseBody
    public String myBrokerages(HttpServletRequest request,HttpServletResponse response){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            int type = StringUtil.getAsInt(StringUtil.trim(map.get("type"))); //记录当前用户的角色 1：外看 2：外获 3：区域长
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1"); //当前页
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize",AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer userid = user.getUserid();
//            if(type == 1){
//                condition.put("userId4",userid);
//            }else if(type == 2){
//                condition.put("userId2",userid);
//            }else if(type == 3){
//                condition.put("userId3",userid);
//            }
            condition.put("type",type);
//            condition.put("isCheck",0);//是否审核0:未审核，1：已审核
//            condition.put("isSettleAccounts",0);//是否完成结算0:未结算，1：已结算
            result = memberAdminManager.myBrokerages(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

}
