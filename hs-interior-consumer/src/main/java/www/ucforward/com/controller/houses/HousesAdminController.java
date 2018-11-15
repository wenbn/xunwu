package www.ucforward.com.controller.houses;

import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.entity.HsHouseComplain;
import www.ucforward.com.entity.HsHouseLog;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.manager.HousesAdminManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("houses")
public class HousesAdminController {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(HousesAdminController.class); // 日志记录

    @Resource
    private HousesAdminManager housesManager;

    /**
     * 获取房源列表申请
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="selectHousingApplys", method = RequestMethod.POST)
    @ResponseBody
    public  String selectHousingApplys(HttpServletRequest request,HttpServletResponse response) throws  IllegalAccessException{
        ResultVo vo = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String pageIndex = StringUtil.trim(map.get("pageIndex"),"1");//当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"),"0"));//语言版本
            int isCheck = StringUtil.getAsInt(StringUtil.trim(map.get("isCheck"),"1"));//是否审核
            condition.put("pageIndex",pageIndex);//当前页
            condition.put("isCheck",isCheck);//当前页
            condition.put("languageVersion",languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数
            vo = housesManager.selectHousingApplys(condition);
        } catch (Exception e) {
            logger.error("HousesController controller selectHousingApplys Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取房源申请详情
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="getHousingApplyDetail", method = RequestMethod.POST)
    @ResponseBody
    public  String getHousingApplyDetail(HttpServletRequest request,HttpServletResponse response) throws  IllegalAccessException{
        ResultVo vo = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id"));//房源申请ID
            if(!StringUtil.hasText(id)){
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            condition.put("id",id);
            vo = housesManager.getHousingApplyDetail(condition);
        } catch (Exception e) {
            logger.error("HousesController controller getHousingApplyDetail Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 测试接口
     * 创建房源索引
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="createHouseIndex", method = RequestMethod.POST)
    @ResponseBody
    public  String createHouseIndex(HttpServletRequest request,HttpServletResponse response) throws  IllegalAccessException{
        ResultVo vo = new ResultVo();
        Map<Object,Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id"));//房源ID
            if(!StringUtil.hasText(id)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            condition.put("id",id);//房源申请ID
            condition.put("isCheck",1);//审核状态
            condition.put("isDel",0);//审核状态
            vo = housesManager.createHouseIndex(condition);
        } catch (Exception e) {
            logger.error("HousesController controller checkHousingApply  Method Exception :——》:" + e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 测试接口
     * 查询房源数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="findHouses", method = RequestMethod.POST)
    @ResponseBody
    public  String findHouseIndex(HttpServletRequest request, HttpServletResponse response, HouseSearchCondition condition) throws  IllegalAccessException{
        ResultVo vo = new ResultVo();
        try {
            //Map map = RequestUtil.getParameterMap(request);
           // condition.setKeywords("大三房");
            condition.setCity("深圳市");
//            condition.setMinPrice(10000);
//            condition.setMaxPrice(20000);
//            condition.setMinArea(10);
//            condition.setMaxArea(100);
            //condition.setHouseConfigDictcode("20025");
            vo = housesManager.findHouseIndex(condition);
        } catch (Exception e) {
            logger.error("HousesController controller checkHousingApply  Method Exception :——》:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 处理下架功能
     * @param request
     * @param response
     * @param houseLog
     * @return
     */
    @RequestMapping(value = "toHandleWithdraw",method = RequestMethod.POST)
    @ResponseBody
    public String toHandleWithdraw(HttpServletRequest request, HttpServletResponse response, HsHouseLog houseLog){
        ResultVo result = new ResultVo();
        try {
            String houseId = houseLog.getHouseId() == null ? "" :houseLog.getHouseId().toString();
            if(!StringUtil.hasText(houseId)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            result = housesManager.toHandleWithdraw(houseLog);
        } catch (Exception e) {
            logger.error("HousesAdminController toHandleWithdraw Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取业主预约获取房源申请
     * @param request
     * @return
     */
    @RequestMapping(value = "getOwnerHousingApplication", method = RequestMethod.POST)
    @ResponseBody
    public String getOwnerHousingApplication(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try{
            Map map = RequestUtil.getParameterMap(request);
            //当前页
            Integer pageIndex = StringUtil.getAsInt(StringUtil.trim(map.get("pageIndex")),1);
            Integer pageSize = StringUtil.getAsInt(StringUtil.trim(map.get("pageSize")),1);
            //每页显示数量
            pageSize = pageSize == 1 ? AppRquestParamsConstant.APP_PAGE_SIZE : pageSize;
            condition.put("isDel", 0);
            condition.put("pageIndex", pageIndex);//当前页
            condition.put("pageSize", pageSize); //每页显示数量
            int leaseType = StringUtil.getAsInt(StringUtil.trim(map.get("leaseType")), -1);//预约类型（0：出租，1：出售）
            if(leaseType != -1){
                condition.put("leaseType",leaseType);
            }
            int applyType = StringUtil.getAsInt(StringUtil.trim(map.get("applyType")), -1);//申请类型（0：自主完善，1：联系客服上传，2：业务员上传）
            if(applyType != -1){
                condition.put("applyType", applyType);
            }
            int isCheck = StringUtil.getAsInt(StringUtil.trim(map.get("isCheck")), -1);//状态（1：待审核，2：审核通过，3：审核不通过）
            if(isCheck != -1){
                condition.put("isCheck",isCheck);
            }
            result = housesManager.getOwnerHousingApplication(condition);
        }catch (Exception e){
            logger.error("HousesController getOwnerHousingApplication Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取业主预约获取房源申请详情
     * @param request
     * @return
     */
    @RequestMapping(value = "getOwnerHousingApplicationById", method = RequestMethod.POST)
    @ResponseBody
    public String getOwnerHousingApplicationById(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            Map map = RequestUtil.getParameterMap(request);
            String idStr = StringUtil.trim(map.get("id"));
            if(!StringUtil.hasText(idStr)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            int id = StringUtil.getAsInt(idStr);
            result = housesManager.getOwnerHousingApplicationById(id);
        }catch (Exception e){
            logger.error("HousesController getOwnerHousingApplication Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 检查业务员是否在房源附近500m
     * @param request
     * @return
     */
    @RequestMapping(value = "check/position", method = RequestMethod.POST)
    @ResponseBody
    public String checkUserPosition(HttpServletRequest request){
        ResultVo result = null;
        try{
            Map map = RequestUtil.getParameterMap(request);
            int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")),-1);
            double lon = StringUtil.getDouble(StringUtil.trim(map.get("lon")), -1);
            double lat = StringUtil.getDouble(StringUtil.trim(map.get("lat")), -1);
            if(houseId == -1 || lon ==-1 || lat == -1){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
            }
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("houseId",houseId);
            condition.put("lon",lon);
            condition.put("lat",lat);
            //计算业务员位置
            result = housesManager.checkUserPosition(condition);
        }catch (Exception e){
            logger.error("HousesController getOwnerHousingApplication Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增房源投诉
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addHousingComplain", method = RequestMethod.POST)
    @ResponseBody
    public String addHousingComplain(HttpServletRequest request, HttpServletResponse response, HsHouseComplain hsHouseComplain) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(hsHouseComplain);
            HsHouseComplain houseComplain = (HsHouseComplain) requestObj;
            String houseId = houseComplain.getHouseId() == null ? "" : houseComplain.getHouseId().toString();
            String complainReason = houseComplain.getComplainReason() == null ? "" : houseComplain.getComplainReason().toString();
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer userid = user.getUserid();
            String mobile = user.getMobile();

            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(complainReason) || userid == null) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            //获取角色
            //投诉来源 (1外部 2外获 3外看 4区域长)
            int platform = 2;
            List<Map<Object, Object>> roleList = user.getRoleList();
            for(Map<Object,Object> role : roleList){
                if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                    platform = 3;
                }else if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                    platform = 4;
                }
            }
            houseComplain.setPlatform(platform);
            //业务员id
            houseComplain.setSalesmanId(userid);
            houseComplain.setMobile(mobile);
            houseComplain.setComplainCode(UUID.randomUUID().toString());


            int complainType = houseComplain.getComplainType() == null ? 0 : houseComplain.getComplainType();
            //投诉类型
            houseComplain.setComplainType(complainType);
            houseComplain.setCreateBy(userid);
            houseComplain.setCreateTime(new Date());
            result = housesManager.addHousingRelationInfo(houseComplain);
        } catch (Exception e) {
            logger.error("HousesController addHousingComplain Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }
}
