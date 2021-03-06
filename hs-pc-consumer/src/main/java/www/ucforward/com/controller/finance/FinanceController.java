package www.ucforward.com.controller.finance;

import com.google.common.collect.Maps;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.base.BaseController;
import www.ucforward.com.entity.ActiveUser;
import www.ucforward.com.entity.HsHousingOrder;
import www.ucforward.com.entity.HsOrderCommissionRecord;
import www.ucforward.com.manager.IHousesManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务
 * @author wenbn
 * @version 1.0
 * @date 2018/8/30
 */
@Controller
@ResponseBody
public class FinanceController  extends BaseController {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(FinanceController.class);

    @Resource
    private IPayManager payManager;
    @Resource
    private IOrderManager orderManager;
    @Resource
    private IHousesManager housesManager;



    /**
     * 获取订单列表
     * @return
     */
    @PostMapping(value="/order/list")
    public String getOrderList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),1); //当前页
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"),AppRquestParamsConstant.APP_PAGE_SIZE); //当前页
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1); //订单类型 0-租房->1-买房
        int payStatus = StringUtil.getAsInt(request.getParameter("payStatus"),-1); //支付状态 0-未付款 1- 已支付
        int payWay = StringUtil.getAsInt(request.getParameter("payWay"),-1); //支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
        int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"),-1); //订单是否审核 1已支付未审核 ，2已审核
        //订单编号
        String orderCode = StringUtil.trim(request.getParameter("orderCode"));
        //交易状态 0:交易中 1:交易成功 2:交易失败
        String tradingStatus = StringUtil.trim(request.getParameter("tradingStatus"));
        Map map = RequestUtil.getParameterMap(request);
        Map<Object,Object> condition = Maps.newHashMap();
//        condition.putAll(map);
        condition.put("isDel",0);
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize); //页显示条数
        if(orderType!= -1){
            condition.put("orderType",orderType); //订单类型 0-租房->1-买房
        }
        if(payStatus!= -1){
            condition.put("payStatus",payStatus); //支付状态 0-未付款 1- 已支付
        }
        if(isCheck != -1){//已支付未审核
            condition.put("orderStatus",isCheck);
        }
        if(payWay != -1){//支付方式
            condition.put("payWay",payWay);
        }
        if(StringUtil.hasText(orderCode)){
            condition.put("orderCode",orderCode);
        }
        if(StringUtil.hasText(tradingStatus)){
            condition.put("tradingStatus",tradingStatus);
        }
        vo = orderManager.getOrderList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 订单导出excel
     * @param request
     * @param response
     */
    @RequestMapping("/orderExport")
    public @ResponseBody void orderExport(HttpServletRequest request, HttpServletResponse response){
        response.reset(); //清除buffer缓存
        try {
            // 指定下载的文件名，浏览器都会使用本地编码，即GBK，浏览器收到这个文件名后，用ISO-8859-1来解码，然后用GBK来显示
            // 所以我们用GBK解码，ISO-8859-1来编码，在浏览器那边会反过来执行。
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("订单信息.xls".getBytes("GBK"),"ISO-8859-1"));
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            //订单类型 0-租房->1-买房
            int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1);
            //支付状态 0-未付款 1- 已支付
            int payStatus = StringUtil.getAsInt(request.getParameter("payStatus"),-1);
            //支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
            int payWay = StringUtil.getAsInt(request.getParameter("payWay"),-1);
            //订单是否审核 1已支付未审核 ，2已审核
            int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"),-1);
            //交易状态 0:交易中 1:交易成功 2:交易失败
            String tradingStatus = StringUtil.trim(request.getParameter("tradingStatus"));
            Map map = RequestUtil.getParameterMap(request);
            Map<Object,Object> condition = Maps.newHashMap();
            //订单类型 0-租房->1-买房
            if(orderType!= -1){
                condition.put("orderType",orderType);
            }
            //支付状态 0-未付款 1- 已支付
            if(payStatus!= -1){
                condition.put("payStatus",payStatus);
            }
            //已支付未审核
            if(isCheck != -1){
                condition.put("orderStatus",isCheck);
            }
            //支付方式
            if(payWay != -1){
                condition.put("payWay",payWay);
            }
            if(StringUtil.hasText(tradingStatus)){
                condition.put("tradingStatus",tradingStatus);
            }
            condition.put("isDel",0);

            //导出Excel对象
            XSSFWorkbook workbook = orderManager.orderExport(condition);
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
     * 获取订单详情
     * @return
     */
    @PostMapping(value="/order/detail/{id}")
    public String getOrderDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = null;
        vo = orderManager.getOrderDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     * 线下订单支付
     * @param request
     * @return
     */
    @PostMapping(value="/offline/order/pay")
    public String offlineOrderPay(HttpServletRequest request){
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
            vo = payManager.offlineOrderPay(condition);
        } catch (Exception e) {
            logger.error("FinanceController controller: offlineOrderPay Exception message:" + e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 审核订单支付
     * @param request
     * @return
     */
    @PostMapping(value="/check/order/pay")
    public String checkOrderPay(HttpServletRequest request){
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
            vo = payManager.checkOrderPay(condition);
        } catch (Exception e) {
            logger.error("FinanceController controller: offlineOrderPay Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 获取成单结佣列表
     * @return
     */
    @PostMapping(value="/final/commission/list")
    public String getOrderCommissionRecordList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0); //当前页
        //每页显示数量
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"),AppRquestParamsConstant.APP_PAGE_SIZE);
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1); //订单类型 0-租房->1-买房
        int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"),-1); //订单是否审核 0已支付未审核 ，1已审核
        int isSettleAccounts = StringUtil.getAsInt(request.getParameter("isSettleAccounts"),-1);//是否完成结算0:未结算，1：已结算
        String keyword = request.getParameter("keyword"); //查询关键字
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", pageSize); //页显示条数
        if(orderType!= -1){
            condition.put("orderType",orderType); //订单类型 0-租房->1-买房
        }
        if(isCheck != -1){//已支付未审核
            condition.put("isCheck",isCheck);
        }
        if(isSettleAccounts != -1){//是否完成结算0:未结算，1：已结算
            condition.put("isSettleAccounts",isSettleAccounts);
        }
        if(StringUtil.hasText(keyword)){
            condition.put("keyword",keyword);
        }
        //查询最终成单列表
        vo = orderManager.getCommissionOrderList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取成单结佣详情
     * @return
     */
    @PostMapping(value="/final/commission/detail/{id}")
    public String getOrderCommissionRecordDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = null;
        if(id == null){//已支付未审核
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        //查询最终成单详情
        vo = orderManager.getOrderCommissionRecordDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取成单结佣
     * @return
     */
    @PostMapping(value="/final/commission/update")
    public String updateOrderCommissionRecord(HttpServletRequest request,HsOrderCommissionRecord commissionRecord) throws Exception {
        ResultVo vo = null;
        //公司服务费（暂存到备用字段Standby5传递到server方便计算）
        String company = request.getParameter("company");
        commissionRecord.setStandby5(company);
        //修改最终成单
        vo = orderManager.updateOrderCommissionRecord(commissionRecord);
        return JsonUtil.toJson(vo);
    }

    /**
     * 成单结佣导出
     * @param request
     * @param response
     */
    @RequestMapping("/order/commission/export")
    public @ResponseBody void orderCommissionExport(HttpServletRequest request, HttpServletResponse response){
        //清除buffer缓存
        response.reset();
        try {
            // 指定下载的文件名，浏览器都会使用本地编码，即GBK，浏览器收到这个文件名后，用ISO-8859-1来解码，然后用GBK来显示
            // 所以我们用GBK解码，ISO-8859-1来编码，在浏览器那边会反过来执行。
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("成单结佣信息.xls".getBytes("GBK"),"ISO-8859-1"));
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            //订单类型 0-租房->1-买房
            int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1);
            //订单是否审核 0已支付未审核 ，1已审核
            int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"),-1);
            //是否完成结算0:未结算，1：已结算
            int isSettleAccounts = StringUtil.getAsInt(request.getParameter("isSettleAccounts"),-1);

            Map<Object,Object> condition = Maps.newHashMap();
            if(orderType!= -1){
                condition.put("orderType",orderType);
            }
            if(isCheck != -1){
                condition.put("isCheck",isCheck);
            }
            if(isSettleAccounts != -1){
                condition.put("isSettleAccounts",isSettleAccounts);
            }
            //导出Excel对象
            XSSFWorkbook workbook = orderManager.orderCommissionExport(condition);
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
     * 获取退款列表
     * @return
     */
    @PostMapping(value="/refund/list")
    public String getRefundList(HttpServletRequest request) throws Exception {
        ResultVo vo;
        //当前页
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),1);
        //每页显示数量
        int pageSize = StringUtil.getAsInt(request.getParameter("pageSize"), AppRquestParamsConstant.APP_PAGE_SIZE);
        //订单类型 0-租房->1-买房
        int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
        //是否退款0:未退款，1：已退款
        int isRefund = StringUtil.getAsInt(request.getParameter("isRefund"),-1);
        //查询关键字
        String keyword = request.getParameter("keyword");

        Map<Object,Object> condition = Maps.newHashMap();

        if(StringUtil.hasText(keyword)){
            condition.put("keyword",keyword);
        }
        if(orderType > -1){
            //订单类型 0-租房->1-买房
            condition.put("orderType",orderType);
        }
        if(isRefund > -1){
            condition.put("isRefund",isRefund);
        }
        condition.put("pageIndex",pageIndex);
        //页显示条数
        condition.put("pageSize", pageSize);
        //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
        List<Integer> refundStatuss = new ArrayList<>();
        refundStatuss.add(1);
        refundStatuss.add(2);
//        refundStatuss.add(3);
        condition.put("refundStatuss",refundStatuss);
        vo = orderManager.refundReviewList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取退款详情
     * @return
     */
    @PostMapping(value="/refund/detail/{id}")
    public String getRefundDetail(@PathVariable Integer id){
        if(id == null || id < 1){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        ResultVo vo = orderManager.getRefundDetail(id);
        return JsonUtil.toJson(vo);
    }


    /**
     * 审核退款
     * @return
     */
    @PostMapping(value="/refund/check")
    public String checkRefund(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        String id = request.getParameter("id");
        //是否审核 1：审核通过 2审核不通过
        int isCheck = StringUtil.getAsInt(request.getParameter("isCheck"));
        //备注
        String remark = request.getParameter("remark");
        //应退金额
        String refundableAmount = request.getParameter("refundableAmount");
        //其他手续费
        String otherHandlingFee = request.getParameter("otherHandlingFee");
        if(!StringUtil.hasText(id)){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        if(isCheck != 1 && isCheck != 2){
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("id",id);
        if(isCheck == 2){
            if(!StringUtil.hasText(remark)){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE + ":请注明拒绝退款理由"));
            }
        }
        if(!StringUtil.hasText(remark)){
            condition.put("remark",remark);
        }
        Subject subject = SecurityUtils.getSubject();
        ActiveUser user = (ActiveUser) subject.getPrincipal();
        condition.put("userId",user.getUserid());
        condition.put("isCheck",isCheck);
        condition.put("refundableAmount",refundableAmount);
        condition.put("otherHandlingFee",otherHandlingFee);
        vo = orderManager.checkRefund(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 导出退款列表
     * @param request
     * @param response
     */
    @RequestMapping("/refund/export")
    public @ResponseBody void refundExport(HttpServletRequest request, HttpServletResponse response){
        //清除buffer缓存
        response.reset();
        try {
            // 指定下载的文件名，浏览器都会使用本地编码，即GBK，浏览器收到这个文件名后，用ISO-8859-1来解码，然后用GBK来显示
            // 所以我们用GBK解码，ISO-8859-1来编码，在浏览器那边会反过来执行。
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("退款信息.xls".getBytes("GBK"),"ISO-8859-1"));
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            //订单类型 0-租房->1-买房
            int orderType = StringUtil.getAsInt(request.getParameter("orderType"));
            //是否退款0:未退款，1：已退款
            int isRefund = StringUtil.getAsInt(request.getParameter("isRefund"),-1);

            Map<Object,Object> condition = Maps.newHashMap();

            if(orderType > -1){
                //订单类型 0-租房->1-买房
                condition.put("orderType",orderType);
            }
            if(isRefund > -1){
                condition.put("isRefund",isRefund);
            }
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            List<Integer> refundStatuss = new ArrayList<>();
            refundStatuss.add(1);
            refundStatuss.add(2);
            refundStatuss.add(3);
            condition.put("refundStatuss",refundStatuss);
            //导出Excel对象
            XSSFWorkbook workbook = orderManager.refundExport(condition);
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
     * 房源投诉列表
     * @return
     */
    @PostMapping(value="/finance/house/complain/list")
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
        condition.put("transferType",5);
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
    @RequestMapping(value="/finance/complaint/order/update",method = RequestMethod.POST)
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
        condition.put("userType",5);
        vo = housesManager.updateComplaintGrabOrder(condition);
        return JsonUtil.toJson(vo);
    }


}
