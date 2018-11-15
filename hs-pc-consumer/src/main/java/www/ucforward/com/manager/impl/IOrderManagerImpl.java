package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.*;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.manager.IOrderManager;
import www.ucforward.com.serviceInter.*;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/8/30
 */
@Service
public class IOrderManagerImpl implements IOrderManager {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(IOrderManagerImpl.class);

    @Resource
    private HousesService housesService;
    @Resource
    private OrderService orderService;
    @Resource
    private MemberService memberService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    /**
     * 内勤已确认正式合同
     * @param condition
     * @return
     */
    @Override
    public ResultVo confirmedOnlineContract(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));
                if(orderStatus != 2 && payStatus != 1){//财务已审核,并且支付状态为已支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,内勤已确认正式合同
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(3);
                orderUpdate.setOrderStatusTitle("内勤已确认正式合同");
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    log.setRemarks("内勤已确认正式合同");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);

                }

            }
        } catch (Exception e) {
            logger.warn("Remote call to checkOrderPay fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 内勤分配业务员派线下合同单
     * @param condition
     * @return
     */
    @Override
    public ResultVo sendOfflineContract(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID

            Date appointmentDoorTime = (Date) condition.get("appointmentDoorTime");//设置预计订单开始时间
            String appointmentMeetPlace = StringUtil.trim(condition.get("appointmentMeetPlace"));//见面地点

            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));

                if(payStatus != 1){//财务已审核,并且支付状态为已支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                if(orderStatus != 3){//财务已审核,并且支付状态为已支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"当前节点不可操作");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                int houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));//房源ID

                queryColumn.clear();
                queryColumn.add("ID houseId");//主键ID
                queryColumn.add("HOUSE_ID houseId");//房源id
                queryColumn.add("APPLY_ID applyId");//房源申请ID
                condition.put("queryColumn", queryColumn);
                condition.put("id", houseId);
                vo = orderService.selectCustomColumnNamesList(HsMainHouse.class, condition);

                Map<Object,Object> houseMap = null;
                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) vo.getDataSet();
                    if(houseList==null || houseList.size()!=1){
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"房源数据异常");
                    }
                    houseMap = houseList.get(0);
                }
                if(houseMap == null ){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"房源数据异常");
                }
                int applyId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("applyId")));//房源ID
                Date nowTime = new Date();
                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                orderPool.setOrderCode("SCO_"+ RandomUtils.getRandomCode());
                orderPool.setHouseId(houseId);
                orderPool.setApplyId(applyId);
                orderPool.setOrderType(2);//外获订单
                orderPool.setCreateTime(nowTime);
                orderPool.setIsOpenOrder(0);//开启抢单
                orderPool.setEstimatedTime(appointmentDoorTime);//设置预计订单开始时间
                orderPool.setAppointmentMeetPlace(appointmentMeetPlace);//见面地点

                long close = 30*60*1000;//半个小时
                Date closeDate = new Date(nowTime.getTime() + close);//关单时间
                orderPool.setOpenOrderCloseTime(closeDate);
                long time = 48*60*60*1000;//2天
                Date afterDate = new Date(nowTime.getTime() + time);//过期时间
                orderPool.setCloseTime(afterDate);
                orderPool.setRemark("内勤派线下合同单加入订单池");
                orderPool.setCreateBy(updateId);
                vo = orderService.insert(orderPool);
                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    //插入日志
                    HsSystemOrderPoolLog log = new HsSystemOrderPoolLog();
                    log.setPoolId(orderPool.getId());
                    log.setOrderType(2);//外获订单
                    log.setNodeType(0);//加入订单池
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setOperatorType(1);
                    log.setCreateBy(updateId);
                    vo = orderService.insert(log);

                    //修改订单状态,内勤已确认正式合同
                    HsHousingOrder orderUpdate = new HsHousingOrder();
                    orderUpdate.setId(orderId);
                    orderUpdate.setOrderStatus(4);
                    orderUpdate.setOrderStatusTitle("内勤已分配线下合同派单");
                    orderUpdate.setUpdateBy(updateId);
                    orderUpdate.setUpdateTime(nowTime);
                    orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                    vo = orderService.update(orderUpdate);

                    if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                        HsHousingOrderLog orderLog = new HsHousingOrderLog();
                        orderLog.setOrderId(orderId);
                        orderLog.setRemarks("内勤已分配线下合同派单");
                        orderLog.setCreateTime(nowTime);
                        orderLog.setPostTime(nowTime);
                        orderLog.setCreateBy(updateId);
                        orderLog.setOperatorUid(updateId);
                        orderLog.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员
                        orderService.insert(log);

                    }

                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to checkOrderPay fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 内勤已确认签署合同
     * @param condition
     * @return
     */
    @Override
    public ResultVo confirmSignedContract(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));
                if(payStatus != 1){//未支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                if(orderStatus != 4 ){//财务已审核,并且支付状态为已支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"当前状态不可操作");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,内勤已确认正式合同
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(5);
                orderUpdate.setOrderStatusTitle("内勤已确认签署合同");
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    log.setRemarks("内勤已确认签署合同");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);
                }

            }
        } catch (Exception e) {
            logger.warn("Remote call to checkOrderPay fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 出租场景，确认Ejari注册
     * @param condition
     * @return
     */
    @Override
    public ResultVo confirmEjariRegister(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));//支付状态 0-未付款 1- 已支付
                int orderType = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderType")));//订单类型 0-租房->1-买房
                if(payStatus != 1){//未支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                if(orderType!=0){//只有出租才有ejari注册
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"非法操作");
                }
                if(orderStatus != 5 ){//已签署合同才可以操作
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"当前状态不可操作");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,内勤已确认正式合同
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(6);
                orderUpdate.setOrderStatusTitle("确认Ejari注册完毕");
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    log.setRemarks("确认Ejari注册完毕");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to confirmEjariRegister fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 卖家NOC / 买家贷款
     * @param condition
     * @return
     */
    @Override
    public ResultVo nocOrLoans(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));//支付状态 0-未付款 1- 已支付
                int orderType = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderType")));//订单类型 0-租房->1-买房
                if(payStatus != 1){//未支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                if(orderType!=1){//只有出售才有卖家NOC /买家贷款
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"非法操作");
                }
                if(orderStatus != 5 ){//已签署合同才可以操作
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"当前状态不可操作");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,卖家NOC /买家贷款
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(7);
                orderUpdate.setOrderStatusTitle("卖家NOC /买家贷款");
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    log.setRemarks("卖家NOC /买家贷款");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to nocOrLoans fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 完成产权变更
     * @param condition
     * @return
     */
    @Override
    public ResultVo registrationAlteration(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));//支付状态 0-未付款 1- 已支付
                int orderType = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderType")));//订单类型 0-租房->1-买房
                if(payStatus != 1){//未支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }
                if(orderType!=1){//只有出售才有产权变更
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"非法操作");
                }
                if(orderStatus != 7 ){//卖家NOC /买家贷款（出售)才可以操作
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"当前状态不可操作");
                }
                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,内勤已确认正式合同
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(8);
                orderUpdate.setOrderStatusTitle("完成产权变更");
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    log.setRemarks("完成产权变更");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to registrationAlteration fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 业主或买家拒绝签订正式合同
     * @param condition
     * @return
     */
    @Override
    public ResultVo rejectFormalContract(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int updateId = StringUtil.getAsInt(StringUtil.trim(condition.get("updateId")));//保存修改人ID
            int identityType = StringUtil.getAsInt(StringUtil.trim(condition.get("identityType")));//保存修改人ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("ORDER_CODE orderCode");//房源编号
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){//订单为空时
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
                }
                Map<Object, Object> orderMap = orderList.get(0);
                //判断订单状态是否是待支付
                int orderStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderStatus")));
                int payStatus = StringUtil.getAsInt(StringUtil.trim(orderMap.get("payStatus")));//支付状态 0-未付款 1- 已支付
                int orderType = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderType")));//订单类型 0-租房->1-买房
                if(payStatus != 1 || orderStatus < 1){//未支付
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单未支付");
                }

                //订单ID
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId")));
                Date nowTime = new Date();

                //修改订单状态,内勤已确认正式合同
                HsHousingOrder orderUpdate = new HsHousingOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setOrderStatus(8);
                if(identityType == 0){//身份类型 0：业主 1：买家
                    orderUpdate.setOrderStatusTitle("业主拒绝签订正式合同");
                }else if(identityType == 1){
                    orderUpdate.setOrderStatusTitle("买家拒绝签订正式合同");
                }else {
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"身份异常");
                }
                orderUpdate.setUpdateBy(updateId);
                orderUpdate.setUpdateTime(nowTime);
                orderUpdate.setVersionNo(StringUtil.getAsInt(StringUtil.trim(orderMap.get("versionNo"))));
                vo = orderService.update(orderUpdate);

                if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//请求成功

                    //插入退款记录
                    HsHousingOrderRefund refund = new HsHousingOrderRefund();
                    refund.setOrderId(orderId);
                    refund.setOrderCode((String) orderMap.get("orderCode"));
                    refund.setCreateBy(updateId);
                    refund.setOrderAmount((BigDecimal) orderMap.get("orderAmount"));
                    refund.setRefundType(identityType);
                    if(identityType == 0){//身份类型 0：业主 1：买家
                        refund.setRemark("业主拒绝签订正式合同");
                    }else if(identityType == 1) {
                        refund.setRemark("买家拒绝签订正式合同");
                    }
                    refund.setCreateTime(nowTime);
                    orderService.insert(refund);

                    //插入日志
                    HsHousingOrderLog log = new HsHousingOrderLog();
                    log.setOrderId(orderId);
                    if(identityType == 0){//身份类型 0：业主 1：买家
                        log.setRemarks("业主拒绝签订正式合同");
                        ///是否需要走退款。。。。
                    }else if(identityType == 1){
                        log.setRemarks("买家拒绝签订正式合同");
                    }
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(updateId);
                    log.setOperatorUid(updateId);
                    log.setOperatorType(3);//操作人类型1:普通会员 2:商家 3:系统管理员

                    orderService.insert(log);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to registrationAlteration fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 获取合同列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getContractList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<Object, Object>> resultList = new ArrayList<>();
        try {
            //获取订单列表
            ResultVo orderResultVo = orderService.selectList(new HsHousingOrder(), condition, 0);
            if(orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //获取订单信息失败
                logger.error("lsq:getContractList获取订单信息失败");
                return orderResultVo;
            }
            List<Map<Object, Object>> orderList = (List<Map<Object, Object>>) orderResultVo.getDataSet();
            if(orderList == null || orderList.size() < 1){
                //订单信息为空
                logger.error("lsq:getContractList订单信息为空");
                return orderResultVo;
            }

            //订单id列表
            List<Integer> orderIds = new ArrayList<>();
            //房源id列表
            List<Integer> houseIds = new ArrayList<>();
            //业主id列表
            List<Integer> ownerIds = new ArrayList<>();
            orderList.forEach(map->{
                int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")));
                houseIds.add(houseId);
                int id = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                orderIds.add(id);
                int ownerId = StringUtil.getAsInt(StringUtil.trim(map.get("ownerId")));
                ownerIds.add(ownerId);
            });
            //获取房源信息
            condition.clear();
            condition.put("houseIds",houseIds);
            condition.put("isDel",0);
            ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
            if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                //获取房源信息失败
                logger.error("lsq:getContractList获取房源信息失败");
                return houseResultVo;
            }
            //房源信息列表
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
            //获取业主信息
            condition.put("memberIds",ownerIds);
            ResultVo ownerResultVo = memberService.selectList(new HsMember(), condition, 0);
            if(ownerResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || ownerResultVo.getDataSet() == null){
                //获取业主信息失败
                logger.error("lsq:getContractList获取业主信息失败");
                return ownerResultVo;
            }
            //业主信息列表
            List<HsMember> ownerList = (List<HsMember>)ownerResultVo.getDataSet();
            //获取客户购房信息
            condition.clear();
            condition.put("isDel",0);
            condition.put("orderIds",orderIds);
            ResultVo purchaseResultVo = memberService.selectList(new HsMemberPurchase(), condition, 0);
            if(purchaseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || purchaseResultVo.getDataSet() == null){
                //获取客户购房信息失败
                logger.error("lsq:getContractList获取客户购房信息失败");
                return purchaseResultVo;
            }
            //客户购房信息列表
            List<Map<Object, Object>> purchaseList = (List<Map<Object, Object>>) purchaseResultVo.getDataSet();

            //封装返回值
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<Object, Object> orderMap : orderList) {
                //房源id
                int houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));
                //订单id
                int orderId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("id")));
                //业主id
                int ownerId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("ownerId")));
                //封装房源信息
                //房源地址
                String houseArea = "";
                Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(houseMap -> StringUtil.getAsInt(StringUtil.trim(houseMap.get("id"))) == houseId).findFirst();
                if(houseOptional.isPresent()){
                    Map<Object, Object> house = houseOptional.get();
                    //城市
                    String city = StringUtil.trim(house.get("city"));
                    //社区
                    String community = StringUtil.trim(house.get("community"));
                    //子社区
                    String subCommunity = StringUtil.trim(house.get("subCommunity"));
                    houseArea = city + community + subCommunity;
                }
                orderMap.put("houseArea",houseArea);
                //封装业主信息
                //业主手机号
                String ownerMobile = "";
                Optional<HsMember> ownerOptional = ownerList.stream().filter(ownerMap -> StringUtil.getAsInt(StringUtil.trim(ownerMap.getId())) == ownerId).findFirst();
                if(ownerOptional.isPresent()){
                    HsMember owner = ownerOptional.get();
                    ownerMobile = owner.getMemberMoble();
                }
                orderMap.put("ownerMobile",ownerMobile);
                //封装客户申请时间（完善购房信息的时间）
                String applicationTime = "";
                Optional<Map<Object, Object>> purchaseOptional = purchaseList.stream().filter(purchaseMap -> StringUtil.getAsInt(StringUtil.trim(purchaseMap.get("orderId"))) == orderId).findFirst();
                if(purchaseOptional.isPresent()){
                    Map<Object, Object> purchase = purchaseOptional.get();
                    applicationTime = sdf.format(sdf.parse(StringUtil.trim(purchase.get("createTime"))));
                }
                orderMap.put("applicationTime",applicationTime);
                //合同地址
                String standby2 = StringUtil.trim(orderMap.get("standby2"));
                orderMap.put("standby2",ImageUtil.imgResultUrl(standby2));
                resultList.add(orderMap);
            }
            result.setDataSet(resultList);
            result.setPageInfo(orderResultVo.getPageInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取合同信息
     * @param orderId
     * @return
     */
    @Override
    public ResultVo getContract(int orderId){
        ResultVo result = new ResultVo();
        HsOrderContract orderContract = new HsOrderContract();
        try{
            //1.获取合同信息  合同附加条款、交易类型、订单信息等
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo != null && orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                //议价id
                Integer bargainId = order.getBargainId();
                //房源id
                Integer houseId = order.getHouseId();
                //订单类型 0-租房->1-买房
                Integer orderType = order.getOrderType();
                //订单状态 -3议价成功 -2客户完善信息 0:待付款 1:已支付 2:财务已审核（待确认正式合同）3:已确认正式合同 4:线下合同派单 5:已签署合同 6:确认Ejari合同签订(出租才有,订单结束) 7:卖家NOC /买家贷款（出售） 8:完成产权变更 9:已完成 10:买家拒绝签正式合同 11:业主拒绝签正式合同 12:账务审核不通过 13:申请退款 14:已退款
                Integer orderStatus = order.getOrderStatus();
                //合同附加条款
                String additionalTermsStr = order.getAdditionalTerms();
                JSONArray additionalTerms = JSON.parseArray(additionalTermsStr);
                //2.获取议价信息  获取议价最终的订单信息，价格、起租时间等
                ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
                if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("获取议价信息失败！");
                    return result;
                }
                HsMemberHousingBargain bargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
                //租金价格 单位 迪拉姆/年
                Integer leasePrice = bargain.getLeasePrice();
                //租赁时长,单位年
                Integer leaseDurationYear = bargain.getLeaseDurationYear();
                //起租日期
                Date leaseStartDate = bargain.getLeaseStartDate();
                //3.获取房源信息
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo == null || houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("获取房源信息失败！");
                    return result;
                }
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                //房屋装修 0：带家具，1：不带家具
                String houseDecorationDictcode = house.getHouseDecorationDictcode();
                //4,根据订单类型封装不同的合同返回值
                //封装房源信息
                ModelMapperUtil.getInstance().map(house,orderContract);
                //封装议价信息
                ModelMapperUtil.getInstance().map(bargain,orderContract);
                //合同附加条款
                orderContract.setAdditionalTerms(additionalTerms);
                if(orderType == 0){
                    //出租
                    //根据起租日期及租赁时长，算出租赁结束日期
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(leaseStartDate);
                    calendar.add(Calendar.YEAR, leaseDurationYear);
                    Date contractPeriodTo = calendar.getTime();
                    orderContract.setContractPeriodTo(contractPeriodTo);
                    /**
                     * 根据房屋装修情况、租金价格、租赁时长计算保证金，有家具为总租金的百分之10 没家具为总租金的百分之5
                     * 保证金 = 总租金 * 百分比
                     */
                    //获取总租金  防止结果溢出，将租金转为Long再进行计算
                    Long totalPrice = Long.parseLong(leasePrice.toString())*leaseDurationYear;
                    //百分比
                    double percent = 0.05;
                    //是否有家具
                    String possess = "0";
                    if(possess.equals(houseDecorationDictcode)){
                        //有家具
                        percent = 0.1;
                    }
                    Double db = totalPrice * percent;
                    //防止将Double转换为BigDecimal后精度太高的问题。先将Double转换为字符串
                    String trim = StringUtil.trim(db);
                    BigDecimal securityDepositAmount = new BigDecimal(trim);
                    orderContract.setSecurityDepositAmount(securityDepositAmount);
                }else if(orderType == 1){
                    //出售
                }
                //获取业主信息
                String memberMoble = "";
                String familyName = "";
                String name = "";
                Integer memberId = house.getMemberId();
                ResultVo memberResultVo = memberService.select(memberId, new HsMember());
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    HsMember member = (HsMember) memberResultVo.getDataSet();
                    memberMoble = member.getMemberMoble();
                    familyName = member.getFamilyName();
                    name = member.getName();
                }
                orderContract.setMemberMoble(memberMoble);
                orderContract.setMemberName(StringUtil.trim(familyName) + StringUtil.trim(name));
                orderContract.setOrderStatus(orderStatus);
            }

            result.setDataSet(orderContract);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 查询订单详情
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOrder(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        Map<String,Object> resultMap = new HashMap<>(2);
        try{
            //获取订单信息
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID orderId");
            //订单编号
            queryColumn.add("ORDER_CODE orderCode");
            //房源id
            queryColumn.add("HOUSE_ID houseId");
            //买家id
            queryColumn.add("MEMBER_ID memberId");
            //业主id
            queryColumn.add("OWNER_ID ownerId");
            //订单金额
            queryColumn.add("ORDER_AMOUNT orderAmount");
            //平台服务费
            queryColumn.add("PLATFORM_SERVICE_AMOUNT platformServiceAmount");
            //订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("ORDER_STATUS orderStatus");
            //支付状态 0-未付款 1- 已支付
            queryColumn.add("PAY_STATUS payStatus");
            //交易状态 0:交易中 1:交易成功 2:交易失败
            queryColumn.add("TRADING_STATUS tradingStatus");
            //订单类型 0-租房->1-买房
            queryColumn.add("ORDER_TYPE orderType");
            //是否取消0:不取消，1：责任在用户 2：责任在业主 3:责任在平台
            queryColumn.add("IS_CANCEL isCancel");
            //备注
            queryColumn.add("REMARK remark");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            condition.put("queryColumn", queryColumn);
            //是否取消0:不取消，1：用户取消 2：业主取消
//            condition.put("isCancel", 0);
            //是否删除0:不删除，1：删除
            condition.put("isDel", 0);
            ResultVo orderResultVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || orderResultVo.getDataSet() == null){
                return orderResultVo;
            }
            List<Map<Object, Object>> orderList = (List<Map<Object, Object>>) orderResultVo.getDataSet();
            if(orderList.size() < 1){
                return result;
            }
            Map<Object, Object> orderMap = orderList.get(0);
            resultMap.put("order",orderMap);
            /**
             * 获取人员信息（业主、客户）
             */
            //客户id
            int memberId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("memberId")));
            //业主id
            int ownerId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("ownerId")));
            //客户信息
            Map<String,String> memberMap = new HashMap<>(4);
            //客户姓名
            String memberName = "";
            //客户联系电话
            String memberMobile = "";
            ResultVo memberResultVo = memberService.select(memberId, new HsMember());
            if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                HsMember member = (HsMember) memberResultVo.getDataSet();
                memberName = StringUtil.trim(member.getFamilyName()) + StringUtil.trim(member.getName());
                memberMobile = StringUtil.trim(member.getMemberMoble());
            }
            //业主信息
            //业主姓名
            String ownerName = "";
            //业主联系电话
            String ownerMobile = "";
            ResultVo ownerResultVo = memberService.select(ownerId, new HsMember());
            if(ownerResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && ownerResultVo.getDataSet() != null){
                HsMember owner = (HsMember) ownerResultVo.getDataSet();
                ownerName = StringUtil.trim(owner.getFamilyName()) + StringUtil.trim(owner.getName());
                ownerMobile = StringUtil.trim(owner.getMemberMoble());
            }
            memberMap.put("memberName",memberName);
            memberMap.put("memberMobile",memberMobile);
            memberMap.put("ownerName",ownerName);
            memberMap.put("ownerMobile",ownerMobile);
            resultMap.put("member",memberMap);
            /**
             * 获取房源信息
             */
            int houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));
            //城市
            String city = "";
            //社区
            String community = "";
            //子社区
            String subCommunity = "";
            //房源所在区域名称
            String address = "";
            //房源编号
            String houseCode = "";
            //房源申请id
            int applyId = 0;
            //房屋信息
            Map<String,String> houseMap = new HashMap<>(5);
            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                city = StringUtil.trim(house.getCity());
                community = StringUtil.trim(house.getCommunity());
                subCommunity = StringUtil.trim(house.getSubCommunity());
                address = StringUtil.trim(house.getAddress());
                houseCode = StringUtil.trim(house.getHouseCode());
                applyId = StringUtil.getAsInt(StringUtil.trim(house.getApplyId()));
            }
            houseMap.put("city",city);
            houseMap.put("community",community);
            houseMap.put("subCommunity",subCommunity);
            houseMap.put("address",address);
            houseMap.put("houseCode",houseCode);
            resultMap.put("house",houseMap);
            /**
             * 获取房源进度信息
             */
            condition.clear();
            condition.put("applyId",applyId);
            condition.put("houseId",houseId);
            condition.put("isDel",0);
            List<Map<Object,Object>> progress = housesService.getProgress(condition);
            resultMap.put("progress",progress);
            //封装返回值
            result.setDataSet(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 修改合同内容（增加附加条款）
     * @param additionalTermsList 附加条款
     * @param orderId             订单id
     * @param userId              用户id
     * @return
     */
    @Override
    public ResultVo updateContract(List<String> additionalTermsList,Integer orderId,Integer userId) {
        ResultVo result = new ResultVo();
        try{
            //获取订单信息
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || orderResultVo.getDataSet() == null){
                return orderResultVo;
            }
            HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
            String additionalTerms = JSON.toJSONString(additionalTermsList);
            order.setAdditionalTerms(additionalTerms);
            order.setUpdateBy(userId);
            result = orderService.update(order);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 内勤查看出租出售订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo getRentOrSaleOrderList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            Map<Object, Object> queryFilter = Maps.newHashMap();
            //自定义查询列名
            Subject subject = SecurityUtils.getSubject();
            ActiveUser user = (ActiveUser) subject.getPrincipal();
            Integer userId = user.getUserid();
            //查询用户角色
            queryFilter.put("isForbidden",0);
            queryFilter.put("isDel",0);
            queryFilter.put("userId",userId);
            List<Map<Object,Object>> roleList = null;
            ResultVo rolesVo = memberService.selectUserRoles(condition);
            if(rolesVo.getResult()== ResultConstant.SYS_REQUIRED_SUCCESS){
                roleList = (List<Map<Object, Object>>) rolesVo.getDataSet();
            }
            queryFilter = null;
            Integer roleInt = -1;
            for (Map<Object, Object> role : roleList) {
                if(StringUtil.trim(role.get("roleName")).equals("内勤业务员")){
                    roleInt = 1;
                    break;
                }
            }
            //普通内勤只能看到自己的订单
            if(roleInt == 1){
                condition.put("standby1",userId);
            }
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("ORDER_CODE orderCode");//订单编号
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_WAY payWay");//支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("PAY_TIME payTime");//支付时间
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("ADDITIONAL_TERMS additionalTerms");//合同附加条款
            queryColumn.add("REMARK remark");//备注
            queryColumn.add("CREATE_TIME createTime");//创建时间
            queryColumn.add("VERSION_NO versionNo");//当前版本
            //交易状态 0:交易中 1:交易成功 2:交易失败
            queryColumn.add("TRADING_STATUS tradingStatus");
            condition.put("queryColumn", queryColumn);
//            condition.put("isCancel", 0);//是否取消0:不取消，1：用户取消 2：业主取消
            condition.put("isDel", 0);//是否删除0:不删除，1：删除
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(vo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return vo;
            }
            List<Integer> memberIds = Lists.newArrayList();
            List<Integer> houseIds = Lists.newArrayList();
            for (Map<Object, Object> order : orderList) {
                int memberId = StringUtil.getAsInt(StringUtil.trim(order.get("memberId")), -1);
                int ownerId = StringUtil.getAsInt(StringUtil.trim(order.get("ownerId")), -1);
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")), -1);
                if(memberId != -1){
                    memberIds.add(memberId);
                }
                if(ownerId != -1){
                    memberIds.add(ownerId);
                }
                if(houseId != -1){
                    houseIds.add(houseId);
                }
            }
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID memberId");//memberID
            queryColumn.add("NICKNAME nickname");//会员昵称
            queryColumn.add("AREA_CODE areaCode");//电话地区号
            queryColumn.add("MEMBER_MOBLE memberMoble");//买家id
            condition.put("queryColumn", queryColumn);
            condition.put("memberIds", memberIds);//是否删除0:不删除，1：删除
            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
            if(memberVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return memberVo;
            }

            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID houseId");//houseId
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("ADDRESS address");//地址
            condition.put("queryColumn", queryColumn);
            condition.put("houseIds", houseIds);//是否删除0:不删除，1：删除
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(houseVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return houseVo;
            }
            List<Map<Object,Object>> memberList = (List<Map<Object, Object>>) memberVo.getDataSet();
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            for (Map<Object, Object> order : orderList) {
                int memberId = StringUtil.getAsInt(StringUtil.trim(order.get("memberId")));
                int ownerId = StringUtil.getAsInt(StringUtil.trim(order.get("ownerId")));
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> member : memberList) {
                    int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                    if( memberId ==_memberId ){
                        order.put("memberInfo",member);
                    }
                    if( ownerId ==_memberId ){
                        order.put("ownerInfo",member);
                    }
                }
                for (Map<Object, Object> house : houseList) {
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    if( houseId == _houseId ){
                        order.putAll(house);
                    }
                }
            }

        } catch (Exception e) {
            logger.warn("Remote call to getRentOrSaleOrderList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 内勤查看出租出售详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getRentOrSaleOrderDetail(Integer id) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            condition.put("id",id);
            return getOrder(condition);
        } catch (Exception e) {
            logger.warn("Remote call to getRentOrSaleOrderDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取房源订单列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOrderList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");//主键ID
            queryColumn.add("ORDER_CODE orderCode");//房源编号
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//业主id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
            queryColumn.add("PAY_WAY payWay");//支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("TRADING_STATUS tradingStatus");//交易状态 0:交易中 1:交易成功 2:交易失败
            queryColumn.add("PAY_TIME payTime");//支付时间
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("ADDITIONAL_TERMS additionalTerms");//合同附加条款
            queryColumn.add("IS_CANCEL isCancel");//是否取消0:不取消，1：用户取消 2：业主取消
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
            queryColumn.add("REMARK remark");//备注
            queryColumn.add("CREATE_TIME createTime");//创建时间
            queryColumn.add("VERSION_NO versionNo");//当前版本
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(vo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return vo;
            }
            List<Integer> houseIds = Lists.newArrayList();
            for (Map<Object, Object> order : orderList) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")), -1);
                if(houseId != -1){
                    houseIds.add(houseId);
                }
            }
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID houseId");//houseId
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("ADDRESS address");//地址
            //房源主图
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
            condition.put("queryColumn", queryColumn);
            condition.put("houseIds", houseIds);//是否删除0:不删除，1：删除
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(houseVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return houseVo;
            }
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            for (Map<Object, Object> order : orderList) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> house : houseList) {
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    if( houseId == _houseId ){
                        String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                        house.put("houseMainImg", ImageUtil.imgResultUrl(houseMainImg));
                        order.putAll(house);
                    }
                }
            }

        } catch (Exception e) {
            logger.warn("Remote call to getOrderList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 获取房源订单详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getOrderDetail(Integer id) {
        ResultVo vo = null;
        Map<Object,Object> result = Maps.newHashMap();
        try {
            //自定义查询列名
            vo = orderService.select(id,new HsHousingOrder());
            if(vo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsHousingOrder houseOrder = (HsHousingOrder) vo.getDataSet();
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> condition = Maps.newHashMap();
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID houseId");//houseId
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("ADDRESS address");//地址
            queryColumn.add("ROOM_NAME roomName");//门牌号
            condition.put("queryColumn", queryColumn);
            condition.put("id", houseOrder.getHouseId());//是否删除0:不删除，1：删除
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(houseVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return houseVo;
            }
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            if(houseList!=null && houseList.size()>0){
                result.put("house",houseList.get(0));
            }else{
                result.put("house",null);
            }
            result.put("order",houseOrder);
            vo.setDataSet(result);

        } catch (Exception e) {
            logger.warn("Remote call to getOrderDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }


    /**
     * 获取最终成单列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getCommissionOrderList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID commissionId");//主键ID
            queryColumn.add("ORDER_ID orderId");//订单id
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("CUSTOMER_SERVICE_FEE customerServiceFee");//客服结佣金额
            queryColumn.add("SELLER_ASSISTANT_FEE sellerAssistantFee");//外获结佣金额
            queryColumn.add("REGION_LEADER_FEE regionLeaderFee");//区域长实勘结佣金额
            queryColumn.add("REGION_LEADER_TAKE_KEY_FEE regionLeaderTakeKeyFee");//区域长送钥匙结佣金额
            queryColumn.add("BUYER_ASSISTANT_FEE buyerAssistantFee");//外看结佣金额
            queryColumn.add("INTERNAL_ASSISTANT_FEE internalAssistantFee");//内勤结佣金额
            queryColumn.add("TRANSFER_FEE transferFee");//中介金额
            queryColumn.add("ELSE_ASSISTANT_AMOUNT elseAssistantAmount");//其它结佣金额
            queryColumn.add("IS_SETTLE_ACCOUNTS isSettleAccounts");//是否完成结算0:未结算，1：已结算
            queryColumn.add("IS_CHECK isCheck");//是否审核0:未审核，1：已审核
            queryColumn.add("REMARK remark");//备注
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsOrderCommissionRecord.class, condition);
            List<Map<Object, Object>> commissionList = new ArrayList<>();
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                //成单列表
                commissionList = (List<Map<Object, Object>>) vo.getDataSet();
                /**
                 * 获取订单列表
                 */
                List<Map<Object,Object>> orderList = new ArrayList<>();
                //订单ids
                List<Integer> orderIds = commissionList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("orderId")))).collect(Collectors.toList());
                if(orderIds.size() > 0){
                    //主键ID
                    queryColumn.clear();
                    queryColumn.add("ID orderId");
                    //交易状态 0:交易中 1:交易成功 2:交易失败
                    queryColumn.add("TRADING_STATUS tradingStatus");
                    //支付时间
                    queryColumn.add("PAY_TIME payTime");
                    //业主id
                    queryColumn.add("OWNER_ID ownerId");
                    //创建时间
                    queryColumn.add("CREATE_TIME orderCreateTime");
                    condition.clear();
                    condition.put("queryColumn", queryColumn);
                    condition.put("orderIds",orderIds);
                    condition.put("isDel",0);
                    condition.put("isCancel",0);
                    ResultVo orderResultVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
                    if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                        orderList = (List<Map<Object,Object>>) orderResultVo.getDataSet();
                    }
                }
                /**
                 * 获取房源列表
                 */
                List<Map<Object, Object>> houseList = new ArrayList<>();
                //房源ids
                List<Integer> houseIds = commissionList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("houseId")))).collect(Collectors.toList());
                if(houseIds.size() > 0){
                    //houseId
                    queryColumn.clear();
                    queryColumn.add("ID houseId");
                    //房源名称
                    queryColumn.add("HOUSE_NAME houseName");
                    //房源编号
                    queryColumn.add("HOUSE_CODE houseCode");
                    //城市
                    queryColumn.add("CITY city");
                    //社区
                    queryColumn.add("COMMUNITY community");
                    //子社区
                    queryColumn.add("SUB_COMMUNITY subCommunity");
                    //地址
                    queryColumn.add("ADDRESS address");
                    //房源主图
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                    condition.clear();
                    condition.put("queryColumn", queryColumn);
                    condition.put("houseIds", houseIds);
                    condition.put("isDel", 0);
//                    condition.put("isLock", 0);
                    ResultVo houseResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                        houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
                    }
                }
                /**
                 * 获取业主信息
                 */
                List<Map<Object, Object>> memberList = new ArrayList<>();
                List<Integer> ownerIds = orderList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("ownerId")))).collect(Collectors.toList());
                if(ownerIds.size() > 0){
                    queryColumn.clear();
                    condition.clear();
                    //memberID
                    queryColumn.add("ID memberId");
                    //电话地区号
                    queryColumn.add("AREA_CODE areaCode");
                    //电话
                    queryColumn.add("MEMBER_MOBLE memberMoble");
                    condition.put("queryColumn", queryColumn);
                    condition.put("memberIds", ownerIds);
                    ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                    if(memberResultVo.getResult()== ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                        memberList = (List<Map<Object, Object>>)memberResultVo.getDataSet();
                    }
                }
                /**
                 * 封装返回值
                 */
                for (Map<Object, Object> commissionMap : commissionList) {
                    int orderId = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("orderId")));
                    int houseId = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("houseId")));
                    //封装订单信息
                    Optional<Map<Object, Object>> orderOptional = orderList.stream().filter(orderMap -> StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId"))) == orderId).findFirst();
                    Map<Object, Object> orderMap = new HashMap<>(16);
                    if(orderOptional.isPresent()){
                        orderMap = orderOptional.get();
                    }
                    //业主id
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("ownerId")));
                    commissionMap.putAll(orderMap);
                    //封装房源信息
                    Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(houseMap -> StringUtil.getAsInt(StringUtil.trim(houseMap.get("houseId"))) == houseId).findFirst();
                    Map<Object, Object> houseMap = new HashMap<>(16);
                    if(houseOptional.isPresent()){
                        houseMap = houseOptional.get();
                        String houseMainImg = StringUtil.trim(houseMap.get("houseMainImg"));
                        houseMap.put("houseMainImg",ImageUtil.imgResultUrl(houseMainImg));
                    }
                    commissionMap.putAll(houseMap);
                    //封装业主信息
                    Map<Object, Object> memberMap = new HashMap<>(16);
                    Optional<Map<Object, Object>> memberOptional = memberList.stream().filter(member -> StringUtil.getAsInt(StringUtil.trim(member.get("memberId"))) == ownerId).findFirst();
                    if(memberOptional.isPresent()){
                        //业主信息
                        memberMap = memberOptional.get();
                    }
                    commissionMap.putAll(memberMap);

                }
            }
            vo.setDataSet(commissionList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Remote call to getCommissionOrderList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取最终成单详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getOrderCommissionRecordDetail(Integer id) {
        ResultVo vo = null;
        try {
            //成单信息
            Map<String,Object> commissionRecordMap = new HashMap<>();
            vo = orderService.select(id,new HsOrderCommissionRecord());
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() !=null){
                HsOrderCommissionRecord commissionRecord = (HsOrderCommissionRecord) vo.getDataSet();
                /**
                 * 获取订单服务费
                 */
                BigDecimal platformServiceAmount = null;
                Integer orderId = commissionRecord.getOrderId();
                ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
                if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                    HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                    platformServiceAmount = order.getPlatformServiceAmount();
                }
                //获取房源code
                String houseCode = "";
                Integer houseId = commissionRecord.getHouseId();
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                    HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                    houseCode = house.getHouseCode();
                }
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(commissionRecord, SerializerFeature.WriteMapNullValue));
                commissionRecordMap = JSON.toJavaObject(jsonObject, Map.class);
                commissionRecordMap.put("houseCode",houseCode);
                commissionRecordMap.put("platformServiceAmount",platformServiceAmount);
            }
            vo.setDataSet(commissionRecordMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Remote call to getOrderCommissionRecordDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 内勤成单结佣详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getInternalOrderCommissionRecordDetail(Integer id){
        ResultVo vo = null;
        try {
            Map<Object,Object> condition = new HashMap<>(16);
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID commissionId");
            //订单id
            queryColumn.add("ORDER_ID orderId");
            //订单编码
            queryColumn.add("ORDER_CODE orderCode");
            //订单类型 0-租房->1-买房
            queryColumn.add("ORDER_TYPE orderType");
            //房源id
            queryColumn.add("HOUSE_ID houseId");
            //内勤结佣金额
            queryColumn.add("INTERNAL_ASSISTANT_FEE internalAssistantFee");
            //是否完成结算0:未结算，1：已结算
            queryColumn.add("IS_SETTLE_ACCOUNTS isSettleAccounts");
            //是否审核0:未审核，1：已审核
            queryColumn.add("IS_CHECK isCheck");
            //备注
            queryColumn.add("REMARK remark");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            condition.put("queryColumn", queryColumn);
            condition.put("isDel",0);
            condition.put("id",id);
            vo = orderService.selectCustomColumnNamesList(HsOrderCommissionRecord.class, condition);
            List<Map<Object, Object>> commissionList = new ArrayList<>();
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                //成单列表
                commissionList = (List<Map<Object, Object>>) vo.getDataSet();
                /**
                 * 获取订单列表
                 */
                List<Map<Object,Object>> orderList = new ArrayList<>();
                //订单ids
                List<Integer> orderIds = commissionList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("orderId")))).collect(Collectors.toList());
                if(orderIds.size() > 0){
                    //主键ID
                    queryColumn.clear();
                    queryColumn.add("ID orderId");
                    //交易状态 0:交易中 1:交易成功 2:交易失败
                    queryColumn.add("TRADING_STATUS tradingStatus");
                    //支付时间
                    queryColumn.add("PAY_TIME payTime");
                    //业主id
                    queryColumn.add("OWNER_ID ownerId");
                    //创建时间
                    queryColumn.add("CREATE_TIME orderCreateTime");
                    condition.clear();
                    condition.put("queryColumn", queryColumn);
                    condition.put("orderIds",orderIds);
                    condition.put("isDel",0);
//                    condition.put("isCancel",0);
                    ResultVo orderResultVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
                    if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                        orderList = (List<Map<Object,Object>>) orderResultVo.getDataSet();
                    }
                }
                /**
                 * 获取房源列表
                 */
                List<Map<Object, Object>> houseList = new ArrayList<>();
                //房源ids
                List<Integer> houseIds = commissionList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("houseId")))).collect(Collectors.toList());
                if(houseIds.size() > 0){
                    //houseId
                    queryColumn.clear();
                    queryColumn.add("ID houseId");
                    //房源名称
                    queryColumn.add("HOUSE_NAME houseName");
                    //房源编号
                    queryColumn.add("HOUSE_CODE houseCode");
                    //城市
                    queryColumn.add("CITY city");
                    //社区
                    queryColumn.add("COMMUNITY community");
                    //子社区
                    queryColumn.add("SUB_COMMUNITY subCommunity");
                    //地址
                    queryColumn.add("ADDRESS address");
                    //房源主图
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                    condition.clear();
                    condition.put("queryColumn", queryColumn);
                    condition.put("houseIds", houseIds);
                    condition.put("isDel", 0);
                    condition.put("isLock", 0);
                    ResultVo houseResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                        houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
                    }
                }
                /**
                 * 获取业主信息
                 */
                List<Map<Object, Object>> memberList = new ArrayList<>();
                List<Integer> ownerIds = orderList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("ownerId")))).collect(Collectors.toList());
                if(ownerIds.size() > 0){
                    queryColumn.clear();
                    condition.clear();
                    //memberID
                    queryColumn.add("ID memberId");
                    //电话地区号
                    queryColumn.add("AREA_CODE areaCode");
                    //电话
                    queryColumn.add("MEMBER_MOBLE memberMoble");
                    condition.put("queryColumn", queryColumn);
                    condition.put("memberIds", ownerIds);
                    ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                    if(memberResultVo.getResult()== ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                        memberList = (List<Map<Object, Object>>)memberResultVo.getDataSet();
                    }
                }
                /**
                 * 封装返回值
                 */
                for (Map<Object, Object> commissionMap : commissionList) {
                    int orderId = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("orderId")));
                    int houseId = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("houseId")));
                    //封装订单信息
                    Optional<Map<Object, Object>> orderOptional = orderList.stream().filter(orderMap -> StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderId"))) == orderId).findFirst();
                    Map<Object, Object> orderMap = new HashMap<>(16);
                    if(orderOptional.isPresent()){
                        orderMap = orderOptional.get();
                    }
                    //业主id
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("ownerId")));
                    commissionMap.putAll(orderMap);
                    //封装房源信息
                    Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(houseMap -> StringUtil.getAsInt(StringUtil.trim(houseMap.get("houseId"))) == houseId).findFirst();
                    Map<Object, Object> houseMap = new HashMap<>(16);
                    if(houseOptional.isPresent()){
                        houseMap = houseOptional.get();
                        String houseMainImg = StringUtil.trim(houseMap.get("houseMainImg"));
                        houseMap.put("houseMainImg",ImageUtil.imgResultUrl(houseMainImg));
                    }
                    commissionMap.putAll(houseMap);
                    //封装业主信息
                    Map<Object, Object> memberMap = new HashMap<>(16);
                    Optional<Map<Object, Object>> memberOptional = memberList.stream().filter(member -> StringUtil.getAsInt(StringUtil.trim(member.get("memberId"))) == ownerId).findFirst();
                    if(memberOptional.isPresent()){
                        //业主信息
                        memberMap = memberOptional.get();
                    }
                    commissionMap.putAll(memberMap);

                }
            }
            vo.setDataSet(commissionList);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改最终成单记录
     * @param commissionRecord
     * @return
     */
    @Override
    public ResultVo updateOrderCommissionRecord(HsOrderCommissionRecord commissionRecord) {
        ResultVo vo = null;
        try {
            //公司服务费
            String company = commissionRecord.getStandby5();
            //重置备用字段
            commissionRecord.setStandby5(null);
            Integer id = commissionRecord.getId();
            //自定义查询列名
            vo = orderService.select(id,commissionRecord);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsOrderCommissionRecord _commissionRecord = (HsOrderCommissionRecord) vo.getDataSet();
            if(_commissionRecord == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"数据异常");
            }
            Integer orderId = commissionRecord.getOrderId();
            vo = orderService.select(orderId,new HsHousingOrder());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsHousingOrder order = (HsHousingOrder) vo.getDataSet();
            if(order== null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"订单数据异常");
            }
            //订单服务费
            BigDecimal platformServiceAmount = order.getPlatformServiceAmount() == null ? new BigDecimal(0) : order.getPlatformServiceAmount();
            //过户费
            BigDecimal transferFee = commissionRecord.getTransferFee() == null ? new BigDecimal(0) : commissionRecord.getTransferFee();
            //客服结佣金额
            BigDecimal customerServiceFee = commissionRecord.getCustomerServiceFee() == null ? new BigDecimal(0) : commissionRecord.getCustomerServiceFee();
            //外获结佣金额
            BigDecimal sellerAssistantFee = commissionRecord.getSellerAssistantFee() == null ? new BigDecimal(0) : commissionRecord.getSellerAssistantFee();
            //区域长实勘结佣金额
            BigDecimal regionLeaderFee = commissionRecord.getRegionLeaderFee() == null ? new BigDecimal(0) : commissionRecord.getRegionLeaderFee();
            //区域长送钥匙结佣金额
            BigDecimal regionLeaderTakeKeyFee = commissionRecord.getRegionLeaderTakeKeyFee() == null ? new BigDecimal(0) : commissionRecord.getRegionLeaderTakeKeyFee();
            //外看结佣金额
            BigDecimal buyerAssistantFee = commissionRecord.getBuyerAssistantFee() == null ? new BigDecimal(0) : commissionRecord.getBuyerAssistantFee();
            //内勤结佣金额
            BigDecimal internalAssistantFee = commissionRecord.getInternalAssistantFee() == null ? new BigDecimal(0) : commissionRecord.getInternalAssistantFee();
            //其他服务费
            BigDecimal elseAssistantAmount = commissionRecord.getElseAssistantAmount() == null ? new BigDecimal(0) : commissionRecord.getElseAssistantAmount();
            //公司服务费
            BigDecimal companyBigDecimal = company == null ? new BigDecimal(0) : new BigDecimal(company);

            BigDecimal totalAmount =  customerServiceFee.add(sellerAssistantFee).add(regionLeaderFee).add(regionLeaderTakeKeyFee)
                .add(buyerAssistantFee).add(internalAssistantFee).add(transferFee).add(elseAssistantAmount).add(companyBigDecimal);
            if(platformServiceAmount.compareTo(totalAmount) != 0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"金额数据异常");
            }
            vo = orderService.update(commissionRecord);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Remote call to updateOrderCommissionRecord fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 成单结佣导出
     * @param condition
     * @return
     */
    @Override
    public XSSFWorkbook orderCommissionExport(Map<Object, Object> condition){
        XSSFWorkbook xssfWorkbook=null;
        try{
            List<OrderCommissionExport> orderCommissionList = new ArrayList<>();
            /**
             * 获取数据
             */
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID commissionId");
            //订单编码
            queryColumn.add("ORDER_CODE orderCode");
            //订单类型 0-租房->1-买房
            queryColumn.add("ORDER_TYPE orderType");
            //客服结佣金额
            queryColumn.add("CUSTOMER_SERVICE_FEE customerServiceFee");
            //外获结佣金额
            queryColumn.add("SELLER_ASSISTANT_FEE sellerAssistantFee");
            //区域长实勘结佣金额
            queryColumn.add("REGION_LEADER_FEE regionLeaderFee");
            //区域长送钥匙结佣金额
            queryColumn.add("REGION_LEADER_TAKE_KEY_FEE regionLeaderTakeKeyFee");
            //外看结佣金额
            queryColumn.add("BUYER_ASSISTANT_FEE buyerAssistantFee");
            //内勤结佣金额
            queryColumn.add("INTERNAL_ASSISTANT_FEE internalAssistantFee");
            //中介金额
            queryColumn.add("TRANSFER_FEE transferFee");
            //其它结佣金额
            queryColumn.add("ELSE_ASSISTANT_AMOUNT elseAssistantAmount");
            //是否完成结算0:未结算，1：已结算
            queryColumn.add("IS_SETTLE_ACCOUNTS isSettleAccounts");
            //是否审核0:未审核，1：已审核
            queryColumn.add("IS_CHECK isCheck");
            //备注
            queryColumn.add("REMARK remark");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            condition.put("queryColumn", queryColumn);
            ResultVo orderCommissionResultVo = orderService.selectCustomColumnNamesList(HsOrderCommissionRecord.class, condition);

            if(orderCommissionResultVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<Map<Object,Object>> commissionList = (List<Map<Object,Object>>) orderCommissionResultVo.getDataSet();
            if(commissionList==null || commissionList.size()==0){
                return null;
            }
            /**
             * 封装信息
             */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<Object,Object> commissionMap : commissionList) {
                OrderCommissionExport orderCommissionExport = new OrderCommissionExport();
                int commissionId = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("commissionId")));
                String orderCode = StringUtil.trim(commissionMap.get("orderCode"));
                int orderType = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("orderType")));
                BigDecimal customerServiceFee = new BigDecimal(StringUtil.trim(commissionMap.get("customerServiceFee"),"0"));
                BigDecimal sellerAssistantFee = new BigDecimal(StringUtil.trim(commissionMap.get("sellerAssistantFee"),"0"));
                BigDecimal regionLeaderFee = new BigDecimal(StringUtil.trim(commissionMap.get("regionLeaderFee"),"0"));
                BigDecimal regionLeaderTakeKeyFee = new BigDecimal(StringUtil.trim(commissionMap.get("regionLeaderTakeKeyFee"),"0"));
                BigDecimal buyerAssistantFee = new BigDecimal(StringUtil.trim(commissionMap.get("buyerAssistantFee"),"0"));
                BigDecimal internalAssistantFee = new BigDecimal(StringUtil.trim(commissionMap.get("internalAssistantFee"),"0"));
                BigDecimal transferFee = new BigDecimal(StringUtil.trim(commissionMap.get("transferFee"),"0"));
                BigDecimal elseAssistantAmount = new BigDecimal(StringUtil.trim(commissionMap.get("elseAssistantAmount"),"0"));
                int isSettleAccounts = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("isSettleAccounts")));
                int isCheck = StringUtil.getAsInt(StringUtil.trim(commissionMap.get("isCheck")));
                String remark = StringUtil.trim(commissionMap.get("remark"));
                Date createTime = null;
                String createTimeStr = StringUtil.trim(commissionMap.get("createTime"));
                if(StringUtil.hasText(createTimeStr)){
                    createTime = sdf.parse(createTimeStr);
                }
                orderCommissionExport.setCommissionId(commissionId);
                orderCommissionExport.setOrderCode(orderCode);
                orderCommissionExport.setOrderType(orderType == 1 ? "买房" : "租房");
                orderCommissionExport.setCustomerServiceFee(customerServiceFee);
                orderCommissionExport.setSellerAssistantFee(sellerAssistantFee);
                orderCommissionExport.setRegionLeaderFee(regionLeaderFee);
                orderCommissionExport.setRegionLeaderTakeKeyFee(regionLeaderTakeKeyFee);
                orderCommissionExport.setBuyerAssistantFee(buyerAssistantFee);
                orderCommissionExport.setInternalAssistantFee(internalAssistantFee);
                orderCommissionExport.setTransferFee(transferFee);
                orderCommissionExport.setElseAssistantAmount(elseAssistantAmount);
                orderCommissionExport.setIsSettleAccounts(isSettleAccounts);
                orderCommissionExport.setIsCheck(isCheck);
                orderCommissionExport.setRemark(remark);
                orderCommissionExport.setCreateTime(createTime);
                orderCommissionList.add(orderCommissionExport);
            }

            List<ExcelBean> excel=new ArrayList<>();
            Map<Integer,List<ExcelBean>> map=new LinkedHashMap<>();
            //设置标题栏
            //注释列
            excel.add(new ExcelBean("ID","commissionId",0));
            excel.add(new ExcelBean("订单编码","orderCode",0));
            excel.add(new ExcelBean("订单类型","orderType",0));
            excel.add(new ExcelBean("客服结佣金额","customerServiceFee",0));
            excel.add(new ExcelBean("外获结佣金额","sellerAssistantFee",0));
            excel.add(new ExcelBean("区域长实勘结佣金额","regionLeaderFee",0));
            excel.add(new ExcelBean("区域长送钥匙结佣金额","regionLeaderTakeKeyFee",0));
            excel.add(new ExcelBean("外看结佣金额","buyerAssistantFee",0));
            excel.add(new ExcelBean("内勤结佣金额","internalAssistantFee",0));
            excel.add(new ExcelBean("中介金额（过户费）","transferFee",0));
            excel.add(new ExcelBean("其它结佣金额","elseAssistantAmount",0));
            excel.add(new ExcelBean("是否完成结算","isSettleAccounts",0));
            excel.add(new ExcelBean("是否审核","isCheck",0));
            excel.add(new ExcelBean("备注","remark",0));
            excel.add(new ExcelBean("创建时间","createTime",0));
            map.put(0, excel);
            String sheetName = "成单结佣信息";
            //调用ExcelUtil的方法
            xssfWorkbook = ExcelUtil.createExcelFile(OrderCommissionExport.class, orderCommissionList, map, sheetName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return xssfWorkbook;
    }

    /**
     * 获取退款列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getRefundList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID refundId");//主键ID
            queryColumn.add("ORDER_ID orderId");//订单id
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单总金额
            queryColumn.add("PLATFORM_SERVICE_AMOUNT platformServiceAmount");//平台服务费
            queryColumn.add("REFUNDABLE_AMOUNT refundableAmount");//应退金额
            queryColumn.add("REFUND_TYPE refundType");//0:业主申请退款，1：买家申请退款
            queryColumn.add("IS_CHECK isCheck");//是否审核0:未审核，1：审核通过 2审核不通过
            queryColumn.add("IS_REFUND isRefund");//是否退款0:未退款，1：已退款
            queryColumn.add("REFUND_TIME refundTime");//退款时间
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1:已删除
            queryColumn.add("REMARK remark");//备注
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn", queryColumn);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrderRefund.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Remote call to getRefundList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取退款详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getRefundDetail(Integer id) {
        ResultVo vo;
        Map resultMap = new HashMap(16);
        try {
            vo = orderService.select(id, new HsHousingOrderRefund());
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                HsHousingOrderRefund orderRefund = (HsHousingOrderRefund) vo.getDataSet();
                /**
                 * 获取订单信息
                 */
                //支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
                Integer payWay = null;
                //房源id
                Integer houseId = -1;
                Integer orderId = orderRefund.getOrderId();
                ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
                if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                    HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                    payWay = order.getPayWay();
                    houseId = order.getHouseId();
                }
                /**
                 * 获取房源信息
                 */
                String houseCode = "";
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                    HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                    houseCode = house.getHouseCode();
                }
                /**
                 * 封装返回结果集
                 */
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(orderRefund, SerializerFeature.WriteMapNullValue));
                resultMap = JSON.toJavaObject(jsonObject, Map.class);
                resultMap.put("payWay",payWay);
                resultMap.put("houseCode",houseCode);
                vo.setDataSet(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 审核退款
     * @param condition
     * @return
     */
    @Override
    public ResultVo checkRefund(Map<Object, Object> condition) {
        ResultVo vo;
        int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
        int id= StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
        int isCheck = StringUtil.getAsInt(StringUtil.trim(condition.get("isCheck")));
        String remark = StringUtil.trim(condition.get("remark"));
        BigDecimal refundableAmount = new BigDecimal(StringUtil.trim(condition.get("refundableAmount")));
        BigDecimal otherHandlingFee = new BigDecimal(StringUtil.trim(condition.get("otherHandlingFee")));
        try {
            vo = orderService.select(id, new HsHousingOrderRefund());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsHousingOrderRefund refundRefund = (HsHousingOrderRefund) vo.getDataSet();
            if(refundRefund == null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "：获取退款信息失败");
            }
            Integer isRefund = refundRefund.getIsRefund();
            if(isRefund == 1){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "：退款已经完成，请勿重复操作");
            }
            //平台服务费
            BigDecimal platformServiceAmount = refundRefund.getPlatformServiceAmount();
            //判断 修改后的手续费加上应退金额是否等于 平台服务费
            BigDecimal add = refundableAmount.add(otherHandlingFee);
            int i = add.compareTo(platformServiceAmount);
            if(i != 0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "金额数据异常");
            }

            HsHousingOrderRefund updateRefund = new HsHousingOrderRefund();
            Date date = new Date();
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            if(isCheck == 1){
                updateRefund.setRefundStatus(2);
                updateRefund.setIsRefund(1);
                updateRefund.setRefundTime(date);
                if(!StringUtil.hasText(remark)){
                    remark = "财务同意退款";
                }
            }else{
                updateRefund.setRefundStatus(0);
                if(!StringUtil.hasText(remark)){
                    remark = "财务拒绝退款";
                }
            }
            updateRefund.setId(refundRefund.getId());
            updateRefund.setRefundableAmount(refundableAmount);
            updateRefund.setOtherHandlingFee(otherHandlingFee);
            updateRefund.setUpdateBy(userId);
            if(StringUtil.hasText(remark)){
                updateRefund.setRemark(remark);
            }
            vo = orderService.update(updateRefund);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //记录日志
                HsHousingOrderRefundLog orderRefundLog = new HsHousingOrderRefundLog();
                orderRefundLog.setCreateBy(userId);
                orderRefundLog.setCreateTime(date);
                //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
                if(isCheck == 1){
                    orderRefundLog.setRefundStatus(2);
                }else{
                    orderRefundLog.setRefundStatus(0);
                }
                orderRefundLog.setRemark(remark);
                orderRefundLog.setRefundId(id);
                orderService.insert(orderRefundLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Remote call to checkRefund fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 导出退款列表
     * @param condition
     * @return
     */
    @Override
    public XSSFWorkbook refundExport(Map<Object, Object> condition){
        XSSFWorkbook xssfWorkbook=null;
        try{
            List<RefundExport> refundExportList = new ArrayList<>();
            /**
             * 获取数据
             */
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID refundId");
            //订单编码
            queryColumn.add("ORDER_CODE orderCode");
            //订单类型 0-租房->1-买房
            queryColumn.add("ORDER_TYPE orderType");
            //订单总金额
            queryColumn.add("ORDER_AMOUNT orderAmount");
            //平台服务费
            queryColumn.add("PLATFORM_SERVICE_AMOUNT platformServiceAmount");
            //应退金额
            queryColumn.add("REFUNDABLE_AMOUNT refundableAmount");
            //是否退款 0:未退款，1：已退款
            queryColumn.add("IS_REFUND isRefund");
            //退款时间
            queryColumn.add("REFUND_TIME refundTime");
            //备注
            queryColumn.add("REMARK remark");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            condition.put("queryColumn", queryColumn);
            ResultVo refundResultVo = orderService.selectCustomColumnNamesList(HsHousingOrderRefund.class, condition);


            if(refundResultVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<Map<Object,Object>> refundList = (List<Map<Object,Object>>) refundResultVo.getDataSet();
            if(refundList==null || refundList.size()==0){
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<Object, Object> refundMap : refundList) {
                RefundExport refundExport = new RefundExport();
                int refundId = StringUtil.getAsInt(StringUtil.trim(refundMap.get("refundId")));
                String orderCode = StringUtil.trim(refundMap.get("orderCode"));
                int orderType = StringUtil.getAsInt(StringUtil.trim(refundMap.get("orderType")));
                BigDecimal orderAmount = new BigDecimal(StringUtil.trim(refundMap.get("orderAmount"), "0"));
                BigDecimal platformServiceAmount = new BigDecimal(StringUtil.trim(refundMap.get("platformServiceAmount"), "0"));
                BigDecimal refundableAmount = new BigDecimal(StringUtil.trim(refundMap.get("refundableAmount"), "0"));
                int isRefund = StringUtil.getAsInt(StringUtil.trim(refundMap.get("isRefund")));
                Date refundTime = null;
                String refundTimeStr = StringUtil.trim(refundMap.get("refundTime"));
                if(StringUtil.hasText(refundTimeStr)){
                    refundTime = sdf.parse(refundTimeStr);
                }
                String remark = StringUtil.trim(refundMap.get("remark"));
                Date createTime = null;
                String createTimeStr = StringUtil.trim(refundMap.get("createTime"));
                if(StringUtil.hasText(createTimeStr)){
                    createTime = sdf.parse(createTimeStr);
                }
                refundExport.setRefundId(refundId);
                refundExport.setOrderCode(orderCode);
                refundExport.setOrderType(orderType);
                refundExport.setOrderAmount(orderAmount);
                refundExport.setPlatformServiceAmount(platformServiceAmount);
                refundExport.setRefundableAmount(refundableAmount);
                refundExport.setIsRefund(isRefund);
                refundExport.setRefundTime(refundTime);
                refundExport.setRemark(remark);
                refundExport.setCreateTime(createTime);
                refundExportList.add(refundExport);
            }


            List<ExcelBean> excel=new ArrayList<>();
            Map<Integer,List<ExcelBean>> map=new LinkedHashMap<>();
            //设置标题栏
            //注释列
            excel.add(new ExcelBean("ID","refundId",0));
            excel.add(new ExcelBean("订单编码","orderCode",0));
            excel.add(new ExcelBean("订单类型","orderType",0));
            excel.add(new ExcelBean("订单总金额","orderAmount",0));
            excel.add(new ExcelBean("平台服务费","platformServiceAmount",0));
            excel.add(new ExcelBean("应退金额","refundableAmount",0));
            excel.add(new ExcelBean("是否退款","isRefund",0));
            excel.add(new ExcelBean("退款时间","refundTime",0));
            excel.add(new ExcelBean("备注","remark",0));
            excel.add(new ExcelBean("创建时间","createTime",0));
            map.put(0, excel);
            String sheetName = "退款信息";
            //调用ExcelUtil的方法
            xssfWorkbook = ExcelUtil.createExcelFile(RefundExport.class, refundExportList, map, sheetName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return xssfWorkbook;
    }

    /**
     * 订单导出excel
     * @param condition
     * @return
     */
    @Override
    public XSSFWorkbook orderExport(Map<Object, Object> condition){
        XSSFWorkbook xssfWorkbook=null;
        try{
            List<OrderExport> orderExportList = new ArrayList<>();
            /**
             * 获取数据
             */
            ResultVo orderResultVo = orderService.selectList(new HsHousingOrder(),condition,1);
            if(orderResultVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<HsHousingOrder> orderList = (List<HsHousingOrder>) orderResultVo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return null;
            }
            List<Integer> houseIds = Lists.newArrayList();
            for (HsHousingOrder order : orderList) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.getHouseId()), -1);
                if(houseId != -1){
                    houseIds.add(houseId);
                }
            }
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            condition.clear();
            queryColumn.add("ID id");
            //房源名称
            queryColumn.add("HOUSE_NAME houseName");
            //房源编号
            queryColumn.add("HOUSE_CODE houseCode");
            //城市
            queryColumn.add("CITY city");
            //社区
            queryColumn.add("COMMUNITY community");
            //子社区
            queryColumn.add("SUB_COMMUNITY subCommunity");
            //地址
            queryColumn.add("ADDRESS address");
            condition.put("queryColumn", queryColumn);
            condition.put("houseIds", houseIds);
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(houseVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            for (HsHousingOrder order : orderList) {
                OrderExport orderExport = new OrderExport();
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.getHouseId()));
                //城市
                String city = "";
                //社区
                String community = "";
                //子社区
                String subCommunity = "";
                //房源所在区域名称
                String address = "";
                //房源名称
                String houseName = "";
                //房源编号
                String houseCode = "";
                Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(house -> StringUtil.getAsInt(StringUtil.trim(house.get("id"))) == houseId).findFirst();
                if(houseOptional.isPresent()){
                    Map<Object, Object> houseMap = houseOptional.get();
                    houseName = StringUtil.trim(houseMap.get("houseName"));
                    houseCode = StringUtil.trim(houseMap.get("houseCode"));
                    city = StringUtil.trim(houseMap.get("city"));
                    community = StringUtil.trim(houseMap.get("community"));
                    subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                    address = StringUtil.trim(houseMap.get("address"));
                }
                ModelMapperUtil.getInstance().map(order,orderExport);

                Map<String, String> orderMap = orderConversion(order);
                orderExport.setOrderStatus(orderMap.get("orderStatusStr"));
                orderExport.setPayWay(orderMap.get("payWayStr"));
                orderExport.setPayStatus(orderMap.get("payStatusStr"));
                orderExport.setTradingStatus(orderMap.get("tradingStatusStr"));
                orderExport.setOrderStatus(orderMap.get("orderTypeStr"));
                orderExport.setHouseName(houseName);
                orderExport.setHouseCode(houseCode);
                orderExport.setCity(city);
                orderExport.setCommunity(community);
                orderExport.setSubCommunity(subCommunity);
                orderExport.setAddress(address);
                orderExportList.add(orderExport);
            }
            List<ExcelBean> excel=new ArrayList<>();
            Map<Integer,List<ExcelBean>> map=new LinkedHashMap<>();
            //设置标题栏
            //注释列
            excel.add(new ExcelBean("订单编码","orderCode",0));
            excel.add(new ExcelBean("订单金额","orderAmount",0));
            excel.add(new ExcelBean("订单状态","orderStatus",0));
            excel.add(new ExcelBean("支付方式","payWay",0));
            excel.add(new ExcelBean("支付状态","payStatus",0));
            excel.add(new ExcelBean("交易状态","tradingStatus",0));
            excel.add(new ExcelBean("支付时间","payTime",0));
            excel.add(new ExcelBean("订单类型","orderType",0));
            excel.add(new ExcelBean("房源名称","houseName",0));
            excel.add(new ExcelBean("房源编号","houseCode",0));
            excel.add(new ExcelBean("城市","city",0));
            excel.add(new ExcelBean("社区","community",0));
            excel.add(new ExcelBean("子社区","subCommunity",0));
            excel.add(new ExcelBean("房源所在区域名称","address",0));
            map.put(0, excel);
            String sheetName = "订单信息";
            //调用ExcelUtil的方法
            xssfWorkbook = ExcelUtil.createExcelFile(OrderExport.class, orderExportList, map, sheetName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return xssfWorkbook;
    }

    /**
     * 修改交易进度（房源进度）
     * @param condition
     * @return
     */
    @Override
    public ResultVo progressUpdate(Map<Object ,Object> condition){
        ResultVo vo = new ResultVo();
        try {
            //进度code
            String progressCode = StringUtil.trim(condition.get("progressCode"));
            //房源id
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            //用户id
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            //判断进度是否存在
            List<Map<String,Object>> progress = housesService.findProgress(condition);
            if(progress == null || progress.size() < 1){
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE + ":进度错误");
                return vo;
            }
            int orderId = StringUtil.getAsInt(StringUtil.trim(condition.get("orderId")));
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || orderResultVo.getDataSet() == null){
                //获取订单信息失败
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":获取订单信息失败");
                return vo;
            }
            HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
            //订单备注
            String remark = StringUtil.trim(condition.get("remark"));

            //交易状态 0:交易中 1:交易成功 2:交易失败
            Integer tradingStatus = order.getTradingStatus();
            Integer isDel = order.getIsDel();
            if(tradingStatus != 0 || isDel == 1){
                //订单已经失败不能进行修改操作
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("The order has failed and cannot be modified");
                return vo;
            }
            /**
             * 根据进度类型更改订单状态
             */
            //订单是否结束
            boolean isEnd = false;
            //订单状态名称
            String orderStatusTitle = "";
            //判断订单状态是否是待支付
            Integer orderStatus = order.getOrderStatus();
            Integer payStatus = order.getPayStatus();
            Integer orderType = order.getOrderType();
            if(payStatus != 1){
                //未支付
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":订单未支付");
            }
            HsHousingOrder orderUpdate = new HsHousingOrder();
            /**
             * 异常情况判断
             */
            //进度 合同签订
            if("104".equals(progressCode) || "114".equals(progressCode)){
                //订单状态 线下合同派单
                if(orderStatus != 4 ){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":当前状态不可操作");
                }
                /*判断合同订单任务是否已经完成*/
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                Map<Object,Object> queryFilter = Maps.newHashMap();
                queryColumn.add("ID poolId");
                queryColumn.add("HOUSE_ID houseId");
                //是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
                queryColumn.add("IS_FINISHED isFinished");
                queryFilter.put("houseId",houseId);
                //订单类型 0外获订单->1-外看订单->2合同订单
                queryFilter.put("orderType",2);
                queryFilter.put("isDel",0);
                queryFilter.put("queryColumn",queryColumn);
                ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, queryFilter);
                if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return orderVo;
                }
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
                if(orderList != null && orderList.size() > 0){
                    Map<Object,Object> orderPool =  orderList.get(orderList.size() - 1);
                    if(orderPool==null){
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                    }
                    int isFinished = StringUtil.getAsInt(StringUtil.trim(orderPool.get("isFinished")));
                    if(isFinished!=5){
                        //合同派单任务未完成
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Contract dispatch task not completed");
                    }
                }

                orderStatusTitle = "已签署合同";
                orderStatus = 5;
            }
            //进度 ejari注册
            if("105".equals(progressCode)){
                //只有出租才有 ejari注册
                if(orderType!=0){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":非法操作");
                }
                //订单状态 已签署合同
                if(orderStatus != 5 ){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":当前状态不可操作");
                }
                orderStatusTitle = "确认Ejari合同签订";
                orderStatus = 6;
                orderUpdate.setCloseBy(userId);
                isEnd = true;
            }
            //进度 卖家NOC/买家贷款
            if("115".equals(progressCode)){
                //只有出售才有 卖家NOC/买家贷款
                if(orderType != 1){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":非法操作");
                }
                //订单状态 已签署合同
                if(orderStatus != 5 ){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":当前状态不可操作");
                }
                orderStatusTitle = "卖家NOC/买家贷款";
                orderStatus = 7;
            }
            //进度 产权变更
            if("116".equals(progressCode)){
                //只有出售才有产权变更
                if(orderType != 1){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":非法操作");
                }
                //订单状态 卖家NOC /买家贷款
                if(orderStatus != 7 ){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE +":当前状态不可操作");
                }
                orderStatusTitle = "完成产权变更";
                orderStatus = 8;
                orderUpdate.setCloseBy(userId);
                isEnd = true;
            }
            /*房屋已出租/出售改变房源状态*/
            if(isEnd ){
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                    if(house != null){
                        house.setHouseStatus(4);
                        ResultVo update = housesService.update(house);
                        if(update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                            return update;
                        }
                    }
                }

            }

            Date nowTime = new Date();
            orderUpdate.setId(orderId);
            orderUpdate.setOrderStatus(orderStatus);
            orderUpdate.setOrderStatusTitle(orderStatusTitle);
            orderUpdate.setUpdateBy(userId);
            orderUpdate.setUpdateTime(nowTime);
            orderUpdate.setVersionNo(order.getVersionNo() + 1);
            if(StringUtil.hasText(remark)){
                //修改订单备注
                orderUpdate.setRemark(remark);
            }
            vo = orderService.update(orderUpdate);

            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"更新订单信息失败");
            }
            //记录订单日志
            HsHousingOrderLog log = new HsHousingOrderLog();
            log.setOrderId(orderId);
            log.setRemarks(orderStatusTitle);
            log.setCreateTime(nowTime);
            log.setPostTime(nowTime);
            log.setCreateBy(userId);
            log.setOperatorUid(userId);
            //操作人类型1:普通会员 2:商家 3:系统管理员
            log.setOperatorType(3);
            ResultVo orderLogInsert = orderService.insert(log);

            //2.封装进度信息
            HsHouseProgress hsHouseProgress = new HsHouseProgress();
            //房源申请ID
            hsHouseProgress.setHouseId(houseId);
            //创建人
            hsHouseProgress.setCreateBy(userId);
            //进度
            hsHouseProgress.setProgressCode(progressCode);
            //创建日期
            hsHouseProgress.setCreateTime(nowTime);
            //3.插入数据
            vo = housesService.insert(hsHouseProgress);

            //添加
            HsOrderCommissionRecord orderCommissionRecord = new HsOrderCommissionRecord();
            orderCommissionRecord.setOrderId(orderId);
            orderCommissionRecord.setOrderCode(order.getOrderCode());
            orderCommissionRecord.setHouseId(order.getHouseId());
            orderCommissionRecord.setPlatformServiceAmount(order.getPlatformServiceAmount());
            orderCommissionRecord.setCreateTime(new Date());
            orderCommissionRecord.setOrderType(order.getOrderType());
            orderCommissionRecord.setUserId5(StringUtil.getAsInt(order.getStandby1()));
            //进度 ejari注册
            if("105".equals(progressCode)){
                orderCommissionRecord.setRemark("ejari完成注册");
                createOrderCommissionRecord(orderCommissionRecord);
            }
            //进度 产权变更
            if("116".equals(progressCode)){ //tusTitle = "完成产权变更";
                orderCommissionRecord.setRemark("产权变更");
                createOrderCommissionRecord(orderCommissionRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 生成结佣记录
     * @return
     */
    public ResultVo createOrderCommissionRecord(HsOrderCommissionRecord orderCommissionRecord){
        ResultVo vo = new ResultVo();
        try {
            Map<Object,Object> platformSetting = RedisUtil.safeGet(RedisConstant.SYS_PLATFORM_SETTING_CACHE_KEY);
            //外获
            String sellerAssistantRatio = StringUtil.trim(platformSetting.get("sellerAssistantRatio"));
            String[] sellerAssistantRatios = sellerAssistantRatio.split("-");
            //区域长
            String regionLeaderRatio = StringUtil.trim(platformSetting.get("regionLeaderRatio"));
            String[] regionLeaderRatios = regionLeaderRatio.split("-");
            //外看
            String buyerAssistantRatio = StringUtil.trim(platformSetting.get("buyerAssistantRatio"));
            String[] buyerAssistantRatios = buyerAssistantRatio.split("-");
            //内勤
            String internalAssistantRatio = StringUtil.trim(platformSetting.get("internalAssistantRatio"));
            String[] internalAssistantRatios = internalAssistantRatio.split("-");
            //公司结佣
            String companyAssistantRatio = StringUtil.trim(platformSetting.get("companyAssistantRatio"));
            String[] companyAssistantRatios = companyAssistantRatio.split("-");
            //其它结佣
            String elseAssistantRatio = StringUtil.trim(platformSetting.get("elseAssistantRatio"));
            String[] elseAssistantRatios = elseAssistantRatio.split("-");

            //总服务费
            BigDecimal platformServiceAmount = orderCommissionRecord.getPlatformServiceAmount();


            Integer houseId = orderCommissionRecord.getHouseId();
            Map<Object,Object> condition = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            condition.put("queryColumn",queryColumn);
            condition.put("houseId",houseId);
            condition.put("isFinished",5);
            List<Integer> orderTypes = Lists.newArrayList();
            orderTypes.add(0);
            orderTypes.add(1);
            orderTypes.add(2);
            condition.put("orderTypes",orderTypes);//0外获订单->1-外看订单->2合同订单
            condition.put("isDel",0);
            ResultVo orderPoolVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
            int keysManager = -1;
            Integer orderType = orderCommissionRecord.getOrderType();
            if(ResultConstant.SYS_REQUIRED_SUCCESS == orderPoolVo.getResult()){



                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderPoolVo.getDataSet();
                if(orderList!=null){
                    List<Integer> poolIds = Lists.newArrayListWithCapacity(orderList.size());
                    for (Map<Object, Object> order : orderList) {
                        poolIds.add(StringUtil.getAsInt(StringUtil.trim(order.get("id"))));
                    }
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("TASK_TYPE taskType");
                    queryColumn.add("USER_ID userId");
                    queryColumn.add("STANDBY1 standby1");
                    queryColumn.add("STANDBY2 standby2");
                    condition.put("poolIds",poolIds);
                    condition.put("isTransferOrder",0);//是否转单 0->未转单，1已转单
                    condition.put("isFinished",1);//是否转单 0->未转单，1已转单
                    condition.put("isDel",0);
                    condition.put("queryColumn",queryColumn);
                    ResultVo taskVo = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == taskVo.getResult()){


                        List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) taskVo.getDataSet();
                        for (Map<Object, Object> task : taskList) {
                            int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType")));
                            int userId = StringUtil.getAsInt(StringUtil.trim(task.get("userId")));//业务员
                            int standby1 = StringUtil.getAsInt(StringUtil.trim(task.get("standby1")));//区域长
                            //任务类型 0外获任务->1-外看任务->2合同任务

                            if(taskType == 0){
                                orderCommissionRecord.setUserId2(userId);//外获业务员id
                                keysManager = standby1;
                                if(orderType == 0){//订单类型 0-租房->1-买房
                                    orderCommissionRecord.setSellerAssistantFee(platformServiceAmount.multiply(new BigDecimal(sellerAssistantRatios[0])));
                                }else{
                                    orderCommissionRecord.setSellerAssistantFee(platformServiceAmount.multiply(new BigDecimal(sellerAssistantRatios[1])));
                                }
                            }else if (taskType == 1){
                                orderCommissionRecord.setUserId4(userId);//外看业务员id
                                if(orderType == 0){//订单类型 0-租房->1-买房
                                    orderCommissionRecord.setSellerAssistantFee(platformServiceAmount.multiply(new BigDecimal(buyerAssistantRatios[0])));
                                }else{
                                    orderCommissionRecord.setSellerAssistantFee(platformServiceAmount.multiply(new BigDecimal(buyerAssistantRatios[1])));
                                }
                            }
                        }
                    }
                }
            }

            //查询房源数据，判断区域长是否完成任务
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID houseId");
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
            condition.put("houseId",houseId);
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == houseVo.getResult()){
                List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
                if(houseList!=null){
                    Map<Object, Object> house = houseList.get(0);
                    if(null != house){
                        if(!StringUtil.hasText(StringUtil.trim(house.get("houseMainImg")))){//区域长
                            orderCommissionRecord.setStandby3(StringUtil.trim(keysManager));
                            if(orderType == 0){//订单类型 0-租房->1-买房
                                orderCommissionRecord.setSellerAssistantFee(platformServiceAmount.multiply(new BigDecimal(regionLeaderRatios[0])));
                            }else{
                                orderCommissionRecord.setRegionLeaderFee(platformServiceAmount.multiply(new BigDecimal(regionLeaderRatios[1])));
                            }
                        }
                    }
                }
            }

            //内勤结佣
            if(orderType == 0){
                orderCommissionRecord.setInternalAssistantFee(platformServiceAmount.multiply(new BigDecimal(internalAssistantRatios[0])));
            }else{
                orderCommissionRecord.setInternalAssistantFee(platformServiceAmount.multiply(new BigDecimal(internalAssistantRatios[1])));
            }
            vo = orderService.insert(orderCommissionRecord);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 退款申请
     * @param condition
     * @return
     */
    @Override
    public ResultVo requestRefund(Map<Object, Object> condition){
        ResultVo vo = new ResultVo();
        try {

            //订单id
            int orderId = StringUtil.getAsInt(StringUtil.trim(condition.get("orderId")));
            //订单类型 0-租房->1-买房
            int orderType = StringUtil.getAsInt(StringUtil.trim(condition.get("orderType")));
            //订单编号
            String orderCode = StringUtil.trim(condition.get("orderCode"));
            //订单金额
            String orderAmount = StringUtil.trim(condition.get("orderAmount"));
            //平台服务费
            String platformServiceAmount = StringUtil.trim(condition.get("platformServiceAmount"));
            //用户id
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            //获取退款申请 判断当前订单是否存在正在处理中的退款流程
            condition.clear();
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            List<Integer> refundStatuss = new ArrayList<>();
            refundStatuss.add(0);
            refundStatuss.add(1);
            queryColumn.add("ID id");
            condition.put("queryColumn",queryColumn);
            condition.put("orderId",orderId);
            condition.put("isDel",0);
            condition.put("refundStatuss",refundStatuss);
            ResultVo refundResultVo = orderService.selectCustomColumnNamesList(HsHousingOrderRefund.class,condition);
            if(refundResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> refundList = (List<Map<Object, Object>>) refundResultVo.getDataSet();
                if(refundList != null && refundList.size() > 0){
                    vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":该订单正在退款处理中，请勿重复操作");
                    return vo;
                }
            }
            /**
             * 插入退款申请
             */
            Date date = new Date();
            HsHousingOrderRefund orderRefund = new HsHousingOrderRefund();
            orderRefund.setOrderId(orderId);
            orderRefund.setOrderCode(orderCode);
            orderRefund.setOrderType(orderType);
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            orderRefund.setRefundStatus(0);
            orderRefund.setOrderAmount(new BigDecimal(orderAmount));
            orderRefund.setPlatformServiceAmount(new BigDecimal(platformServiceAmount));
            orderRefund.setCreateBy(userId);
            orderRefund.setCreateTime(date);
            vo = orderService.insert(orderRefund);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                HsHousingOrderRefund refund = (HsHousingOrderRefund) vo.getDataSet();
                //退款id
                Integer id = refund.getId();
                //插入日志信息
                HsHousingOrderRefundLog orderRefundLog = new HsHousingOrderRefundLog();
                orderRefundLog.setCreateBy(userId);
                orderRefundLog.setCreateTime(date);
                orderRefundLog.setRefundStatus(0);
                orderRefundLog.setRemark("内勤人员发起退款申请");
                orderRefundLog.setRefundId(id);
                vo = orderService.insert(orderRefundLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取退款审核列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo refundReviewList(Map<Object, Object> condition){
        ResultVo vo;
        try {
            /**
             * 获取退款信息
             */
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID refundId");
            queryColumn.add("ORDER_ID orderId");
            //是否退款0:未退款，1：已退款
            queryColumn.add("IS_REFUND isRefund");
            //申请退款时间（退款记录创建日期）
            queryColumn.add("CREATE_TIME refundTime");
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            queryColumn.add("REFUND_STATUS refundStatus");
            condition.put("queryColumn",queryColumn);
            condition.put("isDel",0);
            vo = orderService.selectCustomColumnNamesList(HsHousingOrderRefund.class, condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                //退款列表
                List<Map<Object, Object>> refundList = (List<Map<Object, Object>>) vo.getDataSet();
                //订单ids
                List<Integer> orderIds = refundList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("orderId")))).collect(Collectors.toList());

                /**
                 * 获取订单信息
                 */
                queryColumn.clear();
                //主键ID
                queryColumn.add("ID orderId");
                //订单编号
                queryColumn.add("ORDER_CODE orderCode");
                //房源id
                queryColumn.add("HOUSE_ID houseId");
                //买家id
                queryColumn.add("MEMBER_ID memberId");
                //业主id
                queryColumn.add("OWNER_ID ownerId");
                //订单金额
                queryColumn.add("ORDER_AMOUNT orderAmount");
                //订单状态 0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7账务审核不通过
                queryColumn.add("ORDER_STATUS orderStatus");
                //支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
                queryColumn.add("PAY_WAY payWay");
                //支付状态 0-未付款 1- 已支付
                queryColumn.add("PAY_STATUS payStatus");
                //支付时间
                queryColumn.add("PAY_TIME payTime");
                //订单类型 0-租房->1-买房
                queryColumn.add("ORDER_TYPE orderType");
                //创建时间
                queryColumn.add("CREATE_TIME createTime");
                //交易状态 0:交易中 1:交易成功 2:交易失败
                queryColumn.add("TRADING_STATUS tradingStatus");
                condition.put("queryColumn", queryColumn);
                //是否取消0:不取消，1：用户取消 2：业主取消
//                condition.put("isCancel", 0);
//                //是否删除0:不删除，1：删除
//                condition.put("isDel", 0);
                condition.put("orderIds", orderIds);
                vo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
                if(vo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                    return vo;
                }
                List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) vo.getDataSet();
                if(orderList==null || orderList.size()==0){
                    return vo;
                }
                List<Integer> memberIds = Lists.newArrayList();
                List<Integer> houseIds = Lists.newArrayList();
                for (Map<Object, Object> order : orderList) {
                    int memberId = StringUtil.getAsInt(StringUtil.trim(order.get("memberId")), -1);
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(order.get("ownerId")), -1);
                    int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")), -1);
                    if(memberId != -1){
                        memberIds.add(memberId);
                    }
                    if(ownerId != -1){
                        memberIds.add(ownerId);
                    }
                    if(houseId != -1){
                        houseIds.add(houseId);
                    }
                }
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID memberId");//memberID
                queryColumn.add("NICKNAME nickname");//会员昵称
                queryColumn.add("AREA_CODE areaCode");//电话地区号
                queryColumn.add("MEMBER_MOBLE memberMoble");//买家id
                condition.put("queryColumn", queryColumn);
                condition.put("memberIds", memberIds);//是否删除0:不删除，1：删除
                ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                if(memberVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                    return memberVo;
                }

                queryColumn.clear();
                condition.clear();
                //houseId
                queryColumn.add("ID houseId");
                //房源名称
                queryColumn.add("HOUSE_NAME houseName");
                //房源编号
                queryColumn.add("HOUSE_CODE houseCode");
                //城市
                queryColumn.add("CITY city");
                //社区
                queryColumn.add("COMMUNITY community");
                //子社区
                queryColumn.add("SUB_COMMUNITY subCommunity");
                //地址
                queryColumn.add("ADDRESS address");
                //房源表主图
                queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                condition.put("queryColumn", queryColumn);
                condition.put("houseIds", houseIds);
                ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                if(houseVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                    return houseVo;
                }
                List<Map<Object,Object>> memberList = (List<Map<Object, Object>>) memberVo.getDataSet();
                List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
                List<Map<Object, Object>> resultList = new ArrayList<>();
                for (Map<Object, Object> refundMap : refundList) {
                    int orderId = StringUtil.getAsInt(StringUtil.trim(refundMap.get("orderId")));
                    for (Map<Object, Object> order : orderList) {
                        int id = StringUtil.getAsInt(StringUtil.trim(order.get("orderId")));
                        if(orderId == id){
                            int memberId = StringUtil.getAsInt(StringUtil.trim(order.get("memberId")));
                            int ownerId = StringUtil.getAsInt(StringUtil.trim(order.get("ownerId")));
                            int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                            for (Map<Object, Object> member : memberList) {
                                int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                                if( memberId ==_memberId ){
                                    order.put("memberInfo",member);
                                }
                                if( ownerId ==_memberId ){
                                    order.put("ownerInfo",member);
                                }
                            }
                            for (Map<Object, Object> house : houseList) {
                                int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                                if( houseId == _houseId ){
                                    String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                    house.put("houseMainImg",ImageUtil.imgResultUrl(houseMainImg));
                                    order.putAll(house);
                                }
                            }
                            refundMap.putAll(order);
                            break;
                        }
                    }
                    resultList.add(refundMap);
                }
                vo.setDataSet(resultList);

            }

        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 退款审核详情
     * @param id
     * @return
     */
    @Override
    public ResultVo refundReviewDetail(Integer id){
        ResultVo vo;
        Map<String, Object> orderMap = new HashMap<>(16);
        try {
            vo = orderService.select(id,new HsHousingOrderRefund());
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && vo.getDataSet() != null){
                HsHousingOrderRefund orderRefund = (HsHousingOrderRefund) vo.getDataSet();
                orderRefund.getOrderId();
                Map<Object,Object> condition = Maps.newHashMap();
                condition.put("id",orderRefund.getOrderId());
                ResultVo orderResultVo = getOrder(condition);
                if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                    orderMap = (Map<String, Object>) orderResultVo.getDataSet();
                    orderMap.put("refundId",orderRefund.getId());
                    orderMap.put("remark",orderRefund.getRemark());
                }
            }
            vo.setDataSet(orderMap);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 内勤主管同意退款申请
     * @param condition
     * @return
     */
    @Override
    public ResultVo refundReviewPass(Map<Object, Object> condition){
        ResultVo vo;
        try {
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            String remark = StringUtil.trim(condition.get("remark"));
            vo = orderService.select(id, new HsHousingOrderRefund());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || vo.getDataSet() == null){
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":退款申请不存在");
                return vo;
            }
            HsHousingOrderRefund orderRefund = (HsHousingOrderRefund) vo.getDataSet();
            orderRefund.setUpdateBy(userId);
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            orderRefund.setRefundStatus(1);
            if(StringUtil.hasText(remark)){
                orderRefund.setRemark(remark);
            }
            //更新退款状态
            vo = orderService.update(orderRefund);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //插入日志信息
                HsHousingOrderRefundLog orderRefundLog = new HsHousingOrderRefundLog();
                orderRefundLog.setCreateBy(userId);
                orderRefundLog.setCreateTime(new Date());
                orderRefundLog.setRefundStatus(1);
                if(StringUtil.hasText(remark)){
                    orderRefundLog.setRemark(remark);
                }else{
                    orderRefundLog.setRemark("内勤主同意退款申请");
                }
                orderRefundLog.setRefundId(id);
                orderService.insert(orderRefundLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 内勤主管拒绝退款申请
     * @param condition
     * @return
     */
    @Override
    public ResultVo refundReviewRefuse(Map<Object, Object> condition){
        ResultVo vo;
        try {
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            String remark = StringUtil.trim(condition.get("remark"));
            vo = orderService.select(id, new HsHousingOrderRefund());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || vo.getDataSet() == null){
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":退款申请不存在");
                return vo;
            }
            HsHousingOrderRefund orderRefund = (HsHousingOrderRefund) vo.getDataSet();
            orderRefund.setUpdateBy(userId);
            //退款状态 0申请退款 1主管审核通过 2财务审核通过(退款完成) 3退款失败
            orderRefund.setRefundStatus(3);
            if(StringUtil.hasText(remark)){
                orderRefund.setRemark(remark);
            }
            //更新退款状态
            vo = orderService.update(orderRefund);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //插入日志信息
                HsHousingOrderRefundLog orderRefundLog = new HsHousingOrderRefundLog();
                orderRefundLog.setCreateBy(userId);
                orderRefundLog.setCreateTime(new Date());
                orderRefundLog.setRefundStatus(3);
                if(StringUtil.hasText(remark)){
                    orderRefundLog.setRemark(remark);
                }else{
                    orderRefundLog.setRemark("内勤主管拒绝退款申请");
                }
                orderRefundLog.setRefundId(id);
                orderService.insert(orderRefundLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 生成线下合同
     * @param paperContract
     * @return
     */
    @Override
    public ResultVo generatingContract(PaperContract paperContract){
        ResultVo vo;
        try {
            /*获取订单信息*/
            Integer orderId = paperContract.getOrderId();
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //获取订单失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "Failed to get order");
            }
            //订单信息
            HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
            //合同PDF地址
            String standby2 = order.getStandby2();
            //是否存在合同
            boolean haveContract = false;
            if(StringUtil.hasText(standby2)){
                haveContract = true;
                /*已存在合同，删除已有合同。重新生成*/
                int delete_state = FastDFSClient.deleteFile(standby2);
                if(delete_state != 0){
                    //文件删除失败
//                    return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, "File deletion failed");
                }
            }
            /*根据订单日志获取财务审核通过日期*/
            String day = "";
            String month = "";
            String year = "";
            String dateStr = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> queryFilter = Maps.newHashMap();
            queryColumn.add("ID id");
            queryColumn.add("ORDER_ID orderId");
            queryColumn.add("CREATE_TIME createTime");
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("isDel",0);
            //该日志所关联的订单节点类型（如果没有，则为-1；0-创建订单->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7-已取消）
            queryFilter.put("nodeType",2);
            queryFilter.put("orderId",orderId);
            ResultVo orderLogResultVo = orderService.selectCustomColumnNamesList(HsHousingOrderLog.class, queryFilter);
            if(orderLogResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> orderLogList = (List<Map<Object, Object>>) orderLogResultVo.getDataSet();
                if(orderLogList != null && orderLogList.size() > 0){
                    Map<Object, Object> orderLogMap = orderLogList.get(orderLogList.size() - 1);
                    String createTimeStr = StringUtil.trim(orderLogMap.get("createTime"));
                    if(createTimeStr.endsWith(".0")){
                        createTimeStr = createTimeStr.substring(0,createTimeStr.length() - 2);
                    }
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = sdf2.parse(createTimeStr);
                    dateStr = sdf.format(parse);
                }
            }
            if(dateStr.contains("/")){
                String[] split = dateStr.split("/");
                if(split.length == 3){
                    day = split[0];
                    month = split[1];
                    year = split[2];
                }
            }
            paperContract.setDateStr(dateStr);
            paperContract.setDay(day);
            paperContract.setMonth(month);
            paperContract.setYear(year);
            /*根据订单类型及语言版本选择合同模板*/
            //合同模板路径
            String templatePath = "";
            //语言版本 0中文 1英文 2阿拉伯语
            Integer languageVersion = paperContract.getLanguageVersion();
            //订单类型 0-租房->1-买房
            Integer orderType = order.getOrderType();
            if(languageVersion == 1){
                if(orderType == 0){
                    //英文 出租
                    templatePath = "template/rentalContractTemplate.pdf";
                }else if(orderType == 1){
                    //英文 出售
                    templatePath = "template/saleContractTemplate.pdf";
                }
            }else if(languageVersion == 2){
                if(orderType == 0){
                    //阿拉伯语 出租
                    templatePath = "template/EjariContractTemplate.pdf";
                }else if(orderType == 1){
                    //阿拉伯语 出售
                    templatePath = "template/FormFTemplate.pdf";
                }
            }
            if(!StringUtil.hasText(templatePath)){
                //语言版本或订单类型错误 或模板不存在
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, "Template does not exist");
            }
            //订单code
            String orderCode = order.getOrderCode();
            /*将rentalContract对象转为map*/
            String rentalContractStr = JSONObject.toJSONString(paperContract);
            JSONObject rentalContractJSON = JSON.parseObject(rentalContractStr);
            Map rentalContractMap = JSON.toJavaObject(rentalContractJSON, Map.class);
            rentalContractMap.put("no",orderCode);
            /*对象转换为map后没有泛型，需要将参数转换一次才能生效*/
            Map<Object,Object> parameterMap = new HashMap<>(16);
            parameterMap.putAll(rentalContractMap);
            /*生成pdf*/
            PdfUtil pdfUtil = new PdfUtil();

            byte[] pdfByte = pdfUtil.getPDF(templatePath,parameterMap, "", "");
            if(pdfByte.length < 1){
                //生成PDF失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, "Failed to generate PDF");
            }
            /*上传文件*/
            String fid = FastDFSClient.uploadFile(pdfByte,"paperContractTemplate.pdf",null);
            if( fid == null || "".equals(fid) ){
                //上传失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, "upload failed");
            }
            //PDF地址重新赋值
            order.setStandby2(fid);
            /*是否存在合同订单*/
            boolean itExist = false;
            //房源id
            Integer houseId = order.getHouseId();
            queryColumn.clear();
            queryFilter.clear();
            //主键ID
            queryColumn.add("ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            //是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
            queryColumn.add("IS_FINISHED isFinished");
            queryFilter.put("houseId",houseId);
            //订单类型 0外获订单->1-外看订单->2合同订单
            queryFilter.put("orderType",2);
            //是否删除0:不删除，1：删除
            queryFilter.put("isDel",0);
            List<Integer> isFinisheds = new ArrayList<>();
            isFinisheds.add(0);
            isFinisheds.add(1);
            queryFilter.put("isFinisheds",isFinisheds);
            queryFilter.put("queryColumn",queryColumn);
            ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, queryFilter);
            if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return orderVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
            if(orderList != null && orderList.size() > 0){
                //存在合同订单
                itExist = true;
            }


            //用户id
            Integer userId = paperContract.getUserId();
            Date nowTime = new Date();
            HsHousingOrderLog log = null;
            //是否派送合同订单 0生成 1不生成
            Integer isDelivery = paperContract.getIsDelivery();
            if(isDelivery == 1){
                /*不派送合同单，直接更改订单状态*/
                if(!haveContract){
                    order.setOrderStatus(4);
                }

            }else{
                /*不存在合同订单，生成合同订单*/
                if(!itExist){
                    //修改订单状态
                    order.setOrderStatus(4);
                    //获取房源申请id
                    Integer applyId = -1;
                    ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                    if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                        HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                        applyId = house.getApplyId();
                    }
                    //见面时间
                    Date estimatedTime = paperContract.getEstimatedTime();
                    //见面地点
                    String appointmentMeetPlace = paperContract.getAppointmentMeetPlace();

                    HsSystemOrderPool orderPool = new HsSystemOrderPool();
                    orderPool.setOrderCode("SCO_"+RandomUtils.getRandomCode());
                    orderPool.setHouseId(houseId);
                    orderPool.setApplyId(applyId);
                    //合同订单
                    orderPool.setOrderType(2);
                    orderPool.setCreateBy(userId);
                    orderPool.setCreateTime(nowTime);
                    //开启抢单
                    orderPool.setIsOpenOrder(1);
                    //设置预计订单开始时间
                    orderPool.setEstimatedTime(estimatedTime);
                    //见面地点
                    orderPool.setAppointmentMeetPlace(appointmentMeetPlace);
                    //半个小时
                    long close = 30*60*1000;
                    //关单时间
                    Date closeDate = new Date(nowTime.getTime() + close);
                    orderPool.setOpenOrderCloseTime(closeDate);
                    //2天
                    long time = 48*60*60*1000;
                    //过期时间
                    Date afterDate = new Date(nowTime.getTime() + time);
                    orderPool.setCloseTime(afterDate);
                    orderPool.setRemark("内勤生成合同订单");
                    vo = orderService.insert(orderPool);
                    if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        //插入日志
                        HsSystemOrderPoolLog orderPoollog = new HsSystemOrderPoolLog();
                        orderPoollog.setPoolId(orderPool.getId());
                        //合同订单
                        orderPoollog.setOrderType(2);
                        //加入订单池
                        orderPoollog.setNodeType(0);
                        orderPoollog.setCreateBy(userId);
                        orderPoollog.setRemarks("内勤生成合同订单");
                        orderPoollog.setCreateTime(nowTime);
                        orderPoollog.setPostTime(nowTime);
                        orderPoollog.setOperatorType(1);
                        ResultVo logInsert = orderService.insert(orderPoollog);
                    }
                    /*订单日志*/
                    log = new HsHousingOrderLog();
                    log.setNodeType(4);
                    log.setRemarks("内勤生成合同订单");
                    log.setCreateTime(nowTime);
                    log.setPostTime(nowTime);
                    log.setCreateBy(userId);
                    log.setOperatorUid(userId);
                    //操作人类型1:普通会员 2:商家 3:系统管理员
                    log.setOperatorType(3);
                }

            }

            /*修改订单数据*/
            order.setUpdateBy(userId);
            order.setUpdateTime(nowTime);
            vo = orderService.update(order);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                if(log != null){
                    /*已生成合同订单，插入订单日志*/
                    log.setOrderId(orderId);
                    ResultVo insert = orderService.insert(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 取消订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo cancelOrder(Map<Object, Object> condition){
        ResultVo vo;
        try {
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            int isCancel = StringUtil.getAsInt(StringUtil.trim(condition.get("isCancel")));
            String remark = StringUtil.trim(condition.get("remark"));
            vo = orderService.select(id, new HsHousingOrder());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || vo.getDataSet() == null){
                //订单不存在
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("Order does not exist");
                return vo;
            }
            Date nowTime = new Date();
            HsHousingOrder order = (HsHousingOrder) vo.getDataSet();
            //交易状态 0:交易中 1:交易成功 2:交易失败
            Integer tradingStatus = order.getTradingStatus();
            if(tradingStatus != 0){
                //订单状态错误
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("Order status error");
                return vo;
            }

            order.setIsCancel(isCancel);
            order.setUpdateBy(userId);
            order.setUpdateTime(nowTime);
            if(StringUtil.hasText(remark)){
                order.setRemark(remark);
            }
            //更新订单信息
            vo = orderService.update(order);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Integer houseId = order.getHouseId();
                //插入日志信息
                HsHousingOrderLog orderLog = new HsHousingOrderLog();
                orderLog.setOrderId(id);
                if(StringUtil.hasText(remark)){
                    orderLog.setRemarks(remark);
                }else{
                    orderLog.setRemarks("取消订单");
                }
                orderLog.setCreateTime(nowTime);
                orderLog.setPostTime(nowTime);
                orderLog.setCreateBy(userId);
                orderLog.setOperatorUid(userId);
                //操作人类型1:普通会员 2:商家 3:系统管理员
                orderLog.setOperatorType(3);
                orderService.insert(orderLog);
                /*更新房源进度*/
                //1.封装进度信息
                HsHouseProgress hsHouseProgress = new HsHouseProgress();
                //房源ID
                hsHouseProgress.setHouseId(houseId);
                //创建人
                hsHouseProgress.setCreateBy(userId);
                //进度
                hsHouseProgress.setProgressCode("102");
                //创建日期
                hsHouseProgress.setCreateTime(nowTime);
                //3.插入数据
                ResultVo insert = housesService.insert(hsHouseProgress);
                if (insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    /*删除发布房源之后的进度*/
                    //角色类型，逗号（,）分隔 1 业主；10业主(出租)；11业主(出售)；2 租客 3 买家 40客服(出租) 41客服(出售) 50内勤(出租) 51内勤(出售)
                    String type = "50";
                    //订单类型 0-租房->1-买房
                    Integer orderType = order.getOrderType();
                    if(orderType == 1){
                        type = "51";
                    }
                    //获取进度列表
                    List<Map<String, Object>> progressList = housesService.findProgressList(type);
                    Optional<Map<String, Object>> codeOptional = progressList.stream().filter(map -> "102".equals(StringUtil.trim(map.get("code")))).findFirst();
                    if(codeOptional.isPresent()){
                        Map<String, Object> progressMap = codeOptional.get();
                        //102进度的顺序位置
                        int orderInt = StringUtil.getAsInt(StringUtil.trim(progressMap.get("order")));
                        //大于102进度位置的进度
                        List<String> codes = new ArrayList<>();
                        for (Map<String, Object> map : progressList) {
                            if(StringUtil.getAsInt(StringUtil.trim(map.get("order"))) > orderInt){
                                String code = StringUtil.trim(map.get("code"));
                                codes.add(code);
                            }
                        }
                        condition.clear();
                        List<String> setColumn = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format = sdf.format(nowTime);
                        setColumn.add("DELETE_TIME = '" + format + "'");
                        condition.put("setColumn",setColumn);
                        condition.put("progressCodes",codes);
                        condition.put("houseId",houseId);
                        int i = housesService.updateByCondition(condition);
                    }
                    /*修改房源状态*/
                    ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                    if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
                        if(mainHouse != null){
                            Integer isLock = mainHouse.getIsLock();
                            if(isLock == 1){
                                //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
                                mainHouse.setIsLock(0);
                                housesService.update(mainHouse);
                                //创建索引
                                HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, 0);
                                kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * HsHousingOrder对象状态值转换为文字
     * @param order HsHousingOrder
     * @return
     */
    public Map<String,String> orderConversion(HsHousingOrder order){

        Map<String,String> resultMap = new HashMap<>(5);
        Integer orderStatus = order.getOrderStatus();
        //获取订单状态
        String orderStatusStr = "";
        ResultVo statusResultVo = orderService.getOrderStatus();
        if(statusResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object, Object>> statusList = (List<Map<Object, Object>>) statusResultVo.getDataSet();
            if(statusList != null && statusList.size() > 0){
                Optional<Map<Object, Object>> statusOptional = statusList.stream().filter(map -> StringUtil.getAsInt(StringUtil.trim(map.get("code"))) == orderStatus).findFirst();
                if(statusOptional.isPresent()){
                    Map<Object, Object> map = statusOptional.get();
                    orderStatusStr = StringUtil.trim(map.get("en_us"));
                }

            }
        }

        //支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
        String payWayStr = "";
        Integer payWay = order.getPayWay();
        switch(payWay){
            case 0:
                payWayStr = "未付款";
                break;
            case 1:
                payWayStr = "线上支付";
                break;
            case 2:
                payWayStr = "线下支付";
                break;
            case 3:
                payWayStr = "钱包支付";
                break;
        }
        //支付状态 0-未付款 1- 已支付
        String payStatusStr = order.getPayStatus() == 1 ? "已支付" : "未付款";
        //交易状态 0:交易中 1:交易成功 2:交易失败
        String tradingStatusStr = "";
        Integer tradingStatus = order.getTradingStatus();
        switch(tradingStatus){
            case 0:
                tradingStatusStr = "交易中";
                break;
            case 1:
                tradingStatusStr = "交易成功";
                break;
            case 2:
                tradingStatusStr = "交易失败";
                break;
        }
        //订单类型 0-租房->1-买房
        String orderTypeStr = order.getOrderType() == 1 ? "买房" : "租房";
        resultMap.put("orderStatusStr",orderStatusStr);
        resultMap.put("orderStatusStr","orderStatusStr");
        resultMap.put("payWayStr",payWayStr);
        resultMap.put("payStatusStr",payStatusStr);
        resultMap.put("tradingStatusStr",tradingStatusStr);
        resultMap.put("orderTypeStr",orderTypeStr);
        return resultMap;
    }

}
