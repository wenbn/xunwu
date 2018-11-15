package www.ucforward.com.controller.internal;

import com.google.common.collect.Maps;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.base.BaseController;
import www.ucforward.com.dto.PaperContract;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.entity.HsHouseNewBuilding;
import www.ucforward.com.entity.HsHouseNewBuildingMemberApply;
import www.ucforward.com.entity.HsMemberFinancialLoansApply;
import www.ucforward.com.manager.IHousesManager;
import www.ucforward.com.manager.IMemberManager;
import www.ucforward.com.manager.IOrderManager;
import www.ucforward.com.manager.IPayManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 内勤端管理
 * @author wenbn
 * @version 1.0
 * @date 2018/8/30
 */
@Controller
@ResponseBody
public class InternalController extends BaseController {


    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(InternalController.class);

    @Resource
    private IOrderManager orderManager;
    @Resource
    private IHousesManager housesManager;
    @Resource
    private IPayManager payManager;
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
     * 内勤查看出租/出售订单列表
     * @return
     */
    @PostMapping(value="internal/order/list")
    public String getRentOrSaleOrderList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),1); //当前页
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"),0); //订单类型 0-租房->1-买房
        //订单编号
        String orderCode = StringUtil.trim(request.getParameter("orderCode"));
        //交易状态 0:交易中 1:交易成功 2:交易失败
        String tradingStatus = StringUtil.trim(request.getParameter("tradingStatus"));
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", StringUtil.getAsInt(request.getParameter("pageSize"),AppRquestParamsConstant.APP_PAGE_SIZE)); //页显示条数
        //订单类型 0-租房->1-买房
        condition.put("orderType",orderType);
        //支付状态 0-未付款 1- 已支付
        condition.put("payStatus",1);
        //内勤看到的订单为财务审核过后
        condition.put("internalOrderStatus",1);
        //筛选条件 订单状态
        if(StringUtil.hasText(tradingStatus)){
            condition.put("tradingStatus",tradingStatus);
        }
        //筛选条件 订单code
        if(StringUtil.hasText(orderCode)){
            condition.put("orderCodeLike","%" + orderCode + "%");
        }
        vo = orderManager.getRentOrSaleOrderList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 内勤查看出租/出售订单详情
     * @return
     */
    @PostMapping(value="internal/order/detail/{id}")
    public String getRentOrSaleOrderDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = orderManager.getRentOrSaleOrderDetail(id);
        return JsonUtil.toJson(vo);
    }


    /**
     * 修改交易进度（房源进度）
     * @return
     */
    @PostMapping(value="/progress/update")
    public String progressUpdate(HttpServletRequest request) throws Exception {
        ResultVo vo;
        try {
            String progressCode = request.getParameter("progressCode");
            int houseId = StringUtil.getAsInt(request.getParameter("houseId"));
            int orderId = StringUtil.getAsInt(request.getParameter("orderId"));
            //订单备注
            String remark = request.getParameter("remark");
            if(!StringUtil.hasText(progressCode) || houseId < 1 || orderId< 1){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("progressCode",progressCode);
            condition.put("userId",user.getUserid());
            condition.put("houseId",houseId);
            condition.put("orderId",orderId);
            condition.put("remark",remark);
            vo = orderManager.progressUpdate(condition);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 新增人员申购信息
     * @param request
     * @param buildingMemberApply
     * @return
     */
    @RequestMapping(value="house/addPurchase", method = RequestMethod.POST)
    @ResponseBody
    public String addPurchase(HttpServletRequest request, HsHouseNewBuildingMemberApply buildingMemberApply){
        ResultVo result = new ResultVo();
        try{
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer userid = user.getUserid();
            buildingMemberApply.setMemberId(userid);
            buildingMemberApply.setCreateBy(userid);
            result = housesManager.addPurchase(buildingMemberApply);
        }catch (Exception e){
            logger.error("HousesController addPurchase Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增直售楼盘信息
     * @param request
     * @param hsHouseNewBuilding
     * @return
     */
    @RequestMapping(value="house/addDirectSalesProperty", method = RequestMethod.POST)
    @ResponseBody
    public String addDirectSalesProperty(HttpServletRequest request, HsHouseNewBuilding hsHouseNewBuilding){
        ResultVo result = new ResultVo();
        try{
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer userid = user.getUserid();
            hsHouseNewBuilding.setProjectCode(UUID.randomUUID().toString());
            hsHouseNewBuilding.setCreateBy(userid);
            result = housesManager.addDirectSalesProperty(hsHouseNewBuilding);
        }catch (Exception e){
            logger.error("HousesController addDirectSalesProperty Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 获取楼盘区域
     * @return
     */
    @RequestMapping(value="house/getPropertyArea", method = RequestMethod.POST)
    @ResponseBody
    public String getPropertyArea(){
        ResultVo result = new ResultVo();
        try{
            result = housesManager.getPropertyArea();
        }catch (Exception e){
            logger.error("HousesController getPropertyArea Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取直售楼盘信息
     * @param request
     * @return
     */
    @RequestMapping(value="house/getDirectSalesDetails", method = RequestMethod.POST)
    @ResponseBody
    public String getDirectSalesDetails(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            String id = request.getParameter("id");

            result = housesManager.getDirectSalesDetails(StringUtil.getAsInt(id));
        }catch (Exception e){
            logger.error("HousesController getDirectSalesDetails Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取直售楼盘列表
     * @param request
     * @return
     */
    @RequestMapping(value="house/getDirectSalesPropertyList", method = RequestMethod.POST)
    @ResponseBody
    public String getDirectSalesPropertyList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>(16);
        try{
            Map map = RequestUtil.getParameterMap(request);
            //当前页
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1);
            //开发商
            String developers = StringUtil.trim(map.get("developers"));
            //区域 城市-社区-子社区
            String area = StringUtil.trim(map.get("area"));
            if(StringUtil.hasText(area)){
                String[] split = area.split("-");
                if(split.length != 3){
                    result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                    result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                    return JsonUtil.toJson(result);
                }
                condition.put("city",split[0]);
                condition.put("community",split[1]);
                condition.put("subCommunity",split[2]);
            }
            if(StringUtil.hasText(developers)){
                condition.put("developers",developers);
            }
            condition.put("pageIndex", pageIndex);
            //页显示条数
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel",0);
            result = housesManager.getDirectSalesPropertyList(condition);
        }catch (Exception e){
            logger.error("HousesController getDirectSalesPropertyList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 修改楼盘信息（新增、修改、删除）
     * @param request
     * @param hsHouseNewBuilding 楼盘信息
     * @return
     */
    @RequestMapping(value="house/updateDirectSalesDetails", method = RequestMethod.POST)
    @ResponseBody
    public String updateDirectSalesDetails(HttpServletRequest request,HsHouseNewBuilding hsHouseNewBuilding){
        ResultVo result = new ResultVo();
        try{
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            hsHouseNewBuilding.setUpdateBy(user.getUserid());
            result = housesManager.updateDirectSalesDetails(hsHouseNewBuilding);
        }catch (Exception e){
            logger.error("HousesController getDirectSalesDetails Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 1。内勤已确认正式合同
     * @param request
     * @return
     */
    @PostMapping(value="/confirmed/online/contract")
    public String confirmedOnlineContract(HttpServletRequest request){
        ResultVo vo =null;
        try {
            Map map = RequestUtil.getParameterMap(request);
            String orderCode = StringUtil.trim(map.get("orderCode"));//订单编码
            if(!StringUtil.hasText(orderCode)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            vo = orderManager.confirmedOnlineContract(condition);
        } catch (Exception e) {
            logger.error("FinanceController confirmedOnlineContract: offlineOrderPay Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 2。内勤分配业务员派线下合同单
     * @param request
     * @return
     */
    @PostMapping(value="/send/offline/contract")
    public String sendOfflineContract(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            String appointmentDoorTime = request.getParameter("appointmentDoorTime");//见面时间
            String appointmentMeetPlace = request.getParameter("appointmentMeetPlace");//见面地点
            if(!StringUtil.hasText(orderCode) || !StringUtil.hasText(appointmentDoorTime) || !StringUtil.hasText(appointmentMeetPlace)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID

            Date _appointmentDoorTime = DateUtils.reverse2Date(appointmentDoorTime);
            long close = 120*60*1000;//2个小时
            Date appointmentDoorTimeDate = new Date(_appointmentDoorTime.getTime() - close);//见面时间
            Date nowDate = new Date();//当前时间
            if(appointmentDoorTimeDate.getTime()<nowDate.getTime()){
                return JsonUtil.toJson(ResultVo.error(
                        ResultConstant.SYS_REQUIRED_FAILURE,
                        "见面时间必须大于当前时间+2小时"
                ));
            }
            condition.put("appointmentDoorTime", _appointmentDoorTime);//见面时间
            condition.put("appointmentMeetPlace", appointmentMeetPlace);//见面地点
            vo = orderManager.sendOfflineContract(condition);
        } catch (Exception e) {
            logger.error("FinanceController confirmedOnlineContract: sendOfflineContract Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 3。内勤确认已签署合同
     * @param request
     * @return
     */
    @PostMapping(value="/confirm/signed/contract")
    public String confirmSignedContract(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            if(!StringUtil.hasText(orderCode)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            vo = orderManager.confirmSignedContract(condition);
        } catch (Exception e) {
            logger.error("FinanceController confirmedOnlineContract: sendOfflineContract Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 4。确认Ejari注册完成
     * @param request
     * @return
     */
    @PostMapping(value="/confirm/ejari/register")
    public String confirmEjariRegister(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            if(!StringUtil.hasText(orderCode)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            vo = orderManager.confirmEjariRegister(condition);
        } catch (Exception e) {
            logger.error("FinanceController confirmedOnlineContract: sendOfflineContract Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 4。卖家NOC / 买家贷款
     * @param request
     * @return
     */
    @PostMapping(value="/noc/or/loans")
    public String nocOrLoans(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            if(!StringUtil.hasText(orderCode)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            vo = orderManager.nocOrLoans(condition);
        } catch (Exception e) {
            logger.error("FinanceController confirmedOnlineContract: sendOfflineContract Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 5。完成产权变更
     * @param request
     * @return
     */
    @PostMapping(value="/registration/alteration")
    public String registrationAlteration(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            if(!StringUtil.hasText(orderCode)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            vo = orderManager.registrationAlteration(condition);//完成产权变更
        } catch (Exception e) {
            logger.error("FinanceController registrationAlteration  Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 业主或买家拒绝签订正式合同
     * @param request
     * @return
     */
    @PostMapping(value="/reject/formal/contract")
    public String rejectFormalContract(HttpServletRequest request){
        ResultVo vo =null;
        try {
            String orderCode = request.getParameter("orderCode");//订单编码
            String identityType = request.getParameter("identityType");//身份类型 0：业主 1：买家
            if(!StringUtil.hasText(orderCode) || !StringUtil.hasText(identityType)){
                vo = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderCode",orderCode);
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            condition.put("updateId",user.getUserid());//保存修改人ID
            condition.put("identityType",identityType);//身份类型 0：业主 1：买家
            vo = orderManager.rejectFormalContract(condition);//完成产权变更
        } catch (Exception e) {
            logger.error("FinanceController registrationAlteration  Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取贷款信息列表
     * @return
     */
    @PostMapping(value="/loans/list")
    public String getLoansList(HttpServletRequest request){
        ResultVo vo;
        try {
            //当前页
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1);
            int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"), AppRquestParamsConstant.APP_PAGE_SIZE);
            //筛选条件 用户手机号
            String telephone = request.getParameter("telephone");

            Map<Object ,Object> condition = Maps.newHashMap();
            if(StringUtil.hasText(telephone)){
                condition.put("telephone",telephone);
            }
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize",pageSize);
            condition.put("isDel",0);
            vo = memberManager.getLoansList(condition);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取贷款详情
     */
    @RequestMapping(value="loans/detail/{id}")
    public String getLoansdetail(@PathVariable Integer id){
        ResultVo result = new ResultVo();
        try{
            if(id==null){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            result = memberManager.getLoansdetail(id);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 更新贷款信息
     * @return
     */
    @RequestMapping(value="loans/update")
    public String updateLoans(HsMemberFinancialLoansApply memberFinancialLoansApply){
        if(memberFinancialLoansApply.getId()== null || memberFinancialLoansApply.getId().intValue()<=0){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE + "ID不能为空"));
        }
        ResultVo vo = memberManager.updateLoans(memberFinancialLoansApply);
        return JsonUtil.toJson(vo);
    }


    /**
     * 获取开发商直售项目申购人员列表
     * @param request
     * @return
     */
    @RequestMapping(value="building/member/list")
    public String getBuildingMemberList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            //当前页
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1);
            int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"), AppRquestParamsConstant.APP_PAGE_SIZE);
            //项目id
            int id = StringUtil.getAsInt(request.getParameter("id"));

            Map<Object ,Object> condition = Maps.newHashMap();
            if(id < 1){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize",pageSize);
            condition.put("projectId",id);
            condition.put("signatureNotNull",1);
            condition.put("isDel",0);
            result = housesManager.getBuildingMemberList(condition);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 导出开发商直售项目申购人员列表
     * @param request
     * @param response
     */
    @RequestMapping("/buildingMemberExport/{id}")
    public @ResponseBody void buildingMemberExport(@PathVariable Integer id,HttpServletRequest request, HttpServletResponse response){
        response.reset(); //清除buffer缓存
        try {
            // 指定下载的文件名，浏览器都会使用本地编码，即GBK，浏览器收到这个文件名后，用ISO-8859-1来解码，然后用GBK来显示
            // 所以我们用GBK解码，ISO-8859-1来编码，在浏览器那边会反过来执行。
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("申请人信息.xls".getBytes("GBK"),"ISO-8859-1"));
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            Map<Object ,Object> condition = Maps.newHashMap();

            condition.put("projectId",id);
            condition.put("isDel",0);
            //导出Excel对象
            XSSFWorkbook workbook = housesManager.buildingMemberExport(condition);
            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            bufferedOutPut.flush();
            workbook.write(bufferedOutPut);
            bufferedOutPut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退款申请
     * @param request
     * @return
     */
    @RequestMapping(value="refund/request")
    public String requestRefund(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            //订单id
            int orderId = StringUtil.getAsInt(request.getParameter("orderId"));
            //订单类型 0-租房->1-买房
            int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
            //订单编号
            String orderCode = request.getParameter("orderCode");
            //订单金额
            String orderAmount = request.getParameter("orderAmount");
            //平台服务费
            String platformServiceAmount = request.getParameter("platformServiceAmount");
            if(orderId < 1 || orderType < 0 || !StringUtil.hasText(orderCode) || !StringUtil.hasText(orderAmount) || !StringUtil.hasText(platformServiceAmount)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            //获取用户id
            ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer userId = user.getUserid();
            Map<Object ,Object> condition = Maps.newHashMap();
            condition.put("orderId",orderId);
            condition.put("orderType",orderType);
            condition.put("orderCode",orderCode);
            condition.put("orderAmount",orderAmount);
            condition.put("platformServiceAmount",platformServiceAmount);
            condition.put("userId",userId);
            result = orderManager.requestRefund(condition);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取退款审核列表
     * @return
     */
    @RequestMapping(value="refund/review/list")
    public String refundReviewList(HttpServletRequest request){
        ResultVo vo;
        //当前页
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),1);
        //每页显示数量
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"), AppRquestParamsConstant.APP_PAGE_SIZE);
        //订单类型 0-租房->1-买房
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
        //订单编号
        String orderCode = StringUtil.trim(request.getParameter("orderCode"));

        Map<Object,Object> condition = Maps.newHashMap();

        //筛选条件 订单code
        if(StringUtil.hasText(orderCode)){
            condition.put("orderCodeLike","%" + orderCode + "%");
        }
        if(orderType > -1){
            //订单类型 0-租房->1-买房
            condition.put("orderType",orderType);
        }
        condition.put("pageIndex",pageIndex);
        //页显示条数
        condition.put("pageSize", pageSize);
        condition.put("refundStatus",0);
        vo = orderManager.refundReviewList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 退款审核详情
     * @return
     */
    @RequestMapping(value="refund/review/detail/{id}")
    public String refundReviewDetail(@PathVariable Integer id){
        ResultVo vo = new ResultVo();
        if(id == null || id < 0){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        vo = orderManager.refundReviewDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     *
     * 内勤主管同意退款申请
     * @return
     */
    @RequestMapping(value="refund/review/pass")
    public String refundReviewPass(HttpServletRequest request){
        ResultVo vo = new ResultVo();
        //退款id
        int id = StringUtil.getAsInt(request.getParameter("id"));
        if(id < 0){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        String remark = request.getParameter("remark");
        //获取用户id
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userId = user.getUserid();
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("userId",userId);
        condition.put("id",id);
        condition.put("remark",remark);
        vo = orderManager.refundReviewPass(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 内勤主管拒绝退款申请
     * @param request
     * @return
     */
    @RequestMapping(value="refund/review/refuse")
    public String refundReviewRefuse(HttpServletRequest request){
        ResultVo vo = new ResultVo();
        //退款id
        int id = StringUtil.getAsInt(request.getParameter("id"));
        if(id < 0){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        String remark = request.getParameter("remark");
        //获取用户id
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userId = user.getUserid();
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("userId",userId);
        condition.put("id",id);
        condition.put("remark",remark);
        vo = orderManager.refundReviewRefuse(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 关单记录
     * @param request
     * @return
     */
    @RequestMapping(value="internal/close/order/list")
    public String closeOrderList(HttpServletRequest request){
        ResultVo vo;
        //当前页
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),1);
        //每页显示数量
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"), AppRquestParamsConstant.APP_PAGE_SIZE);
        //订单类型 0-租房->1-买房
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
        //获取用户id
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userId = user.getUserid();
        //交易状态 0:交易中 1:交易成功 2:交易失败
        List<Integer> tradingStatuss = new ArrayList<>();
        tradingStatuss.add(1);
        tradingStatuss.add(2);
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize",pageSize);
        condition.put("tradingStatuss",tradingStatuss);
        //关单人
        condition.put("closeBy",userId);
        //是否取消0:不取消，1：用户取消 2：业主取消
        condition.put("isCancel", 0);
        //是否删除0:不删除，1：删除
        condition.put("isDel", 0);
        if(orderType > -1){
            condition.put("orderType", orderType);
        }
        vo = housesManager.closeOrderList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取我的成单结佣列表
     * @return
     */
    @PostMapping(value="/internal/final/commission/list")
    public String getOrderCommissionRecordList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        //当前页
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0);
        //每页显示数量
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"),AppRquestParamsConstant.APP_PAGE_SIZE);
        //订单类型 0-租房->1-买房
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1);
        //订单是否审核 0已支付未审核 ，1已审核
        int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"),-1);
        //是否完成结算0:未结算，1：已结算
        int isSettleAccounts = StringUtil.getAsInt(request.getParameter("isSettleAccounts"),-1);
        //查询关键字
        String keyword = request.getParameter("keyword");
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize);
        if(orderType!= -1){
            condition.put("orderType",orderType);
        }
        if(isCheck != -1){
            condition.put("isCheck",isCheck);
        }
        if(isSettleAccounts != -1){
            condition.put("isSettleAccounts",isSettleAccounts);
        }
        if(StringUtil.hasText(keyword)){
            condition.put("keyword",keyword);
        }
        //获取用户id
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userId = user.getUserid();
        //内勤业务员id
        condition.put("userId5",userId);
        //查询最终成单列表
        vo = orderManager.getCommissionOrderList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取成单结佣详情
     * @return
     */
    @PostMapping(value="/internal/final/commission/detail/{id}")
    public String getInternalOrderCommissionRecordDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = null;
        if(id == null){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        //查询最终成单详情
        vo = orderManager.getInternalOrderCommissionRecordDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     * 房源投诉列表
     * @return
     */
    @PostMapping(value="/internal/house/complain/list")
    public String getHouseComplainList(HttpServletRequest request){
        ResultVo vo;
        Map<Object,Object> condition = Maps.newHashMap();
        //当前页（默认1）
        Integer pageIndex = StringUtil.getAsInt(StringUtil.trim(request.getParameter("pageIndex")),1);
        //每页显示数量（默认10）
        Integer pageSize = StringUtil.getAsInt(StringUtil.trim(request.getParameter("pageSize")),AppRquestParamsConstant.APP_PAGE_SIZE );
        /**
         * 筛选条件 预约类型、投诉编号
         */
        //预约类型（0：出租，1：出售）
        int leaseType = StringUtil.getAsInt(StringUtil.trim(request.getParameter("leaseType")),-1);
        if(leaseType != -1){
            condition.put("leaseType",leaseType);
        }
        //投诉编号
        String complainCode = StringUtil.trim(request.getParameter("complainCode"));
        if(StringUtil.hasText(complainCode)){
            condition.put("complainCode",complainCode);
        }
        condition.put("status",1);
        condition.put("transferType",4);
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize);
        vo = housesManager.getHouseComplainList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 更新投诉订单
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/internal/complaint/order/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateComplaintGrabOrder(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        Map<Object,Object> condition = new HashMap<Object,Object>(5);
        //投诉ID
        int id = StringUtil.getAsInt(StringUtil.trim(request.getParameter("id")),-1);
        //备注
        String remark = request.getParameter("remark");
        //投诉是否属实：0未核实 1情况属实 2情况不属实
        int isVerified = StringUtil.getAsInt(request.getParameter("isVerified"));
        if( id == -1 || !StringUtil.hasText(remark) || isVerified < 1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        //投诉ID
        condition.put("id",id);
        //业务员ID
        condition.put("userId",user.getUserid());
        condition.put("isVerified",isVerified);
        condition.put("remark",remark);
        //人员类型 内勤4 财务5
        condition.put("userType",4);
        vo = housesManager.updateComplaintGrabOrder(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 生成线下合同
     * @param request
     * @param paperContract 出租合同信息
     * @returnobtained/list
     */
    @RequestMapping(value="/generating/contract",method = RequestMethod.POST)
    @ResponseBody
    public String generatingContract(HttpServletRequest request, PaperContract paperContract){
        ResultVo vo = null;
        Map<Object,Object> condition = new HashMap<Object,Object>(5);
        //订单ID
        int orderId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("orderId")),-1);
        //语言版本 0中文 1英文 2阿拉伯语
        int languageVersion = StringUtil.getAsInt(StringUtil.trim(request.getParameter("languageVersion")), 1);
        //见面时间
        String estimatedTime = StringUtil.trim(request.getParameter("estimatedTime"));
        //见面地点
        String appointmentMeetPlace = StringUtil.trim(request.getParameter("appointmentMeetPlace"));
        //是否派送合同订单 0生成 1不生成
        int isDelivery = StringUtil.getAsInt(StringUtil.trim(request.getParameter("isDelivery")),-1);
        if( orderId == -1 || isDelivery == -1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        //生成合同订单必须填写见面时间见面地点
        if(isDelivery == 0 && (!StringUtil.hasText(appointmentMeetPlace) || !StringUtil.hasText(estimatedTime))){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        paperContract.setUserId(user.getUserid());
        paperContract.setOrderId(orderId);
        paperContract.setLanguageVersion(languageVersion);
        paperContract.setIsDelivery(isDelivery);
        vo = orderManager.generatingContract(paperContract);
        return JsonUtil.toJson(vo);
    }

    /**
     * 取消订单
     * @param request
     * @return
     */
    @RequestMapping(value="/cancel/order",method = RequestMethod.POST)
    @ResponseBody
    public String cancelOrder(HttpServletRequest request){
        ResultVo vo = null;
        Map<Object,Object> condition = new HashMap<Object,Object>(5);
        //订单ID
        int orderId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("orderId")));
        //是否取消0:不取消，1：用户取消 2：业主取消
        int isCancel = StringUtil.getAsInt(StringUtil.trim(request.getParameter("isCancel")));
        String remark = StringUtil.trim(request.getParameter("remark"));
        if( orderId == -1 || isCancel == -1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ActiveUser user = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        condition.put("id",orderId);
        condition.put("userId",user.getUserid());
        condition.put("isCancel",isCancel);
        condition.put("remark",remark);
        vo = orderManager.cancelOrder(condition);
        return JsonUtil.toJson(vo);
    }

}
