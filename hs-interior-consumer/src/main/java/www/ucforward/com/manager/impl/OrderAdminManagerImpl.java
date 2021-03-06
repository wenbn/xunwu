package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.OrderAdminManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.umeng.util.UmengUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 系统及订单相关管理
 * @author wenbn
 * @version 1.0
 * @date 2018/7/26
 */
@Service("orderAdminManager")
public class OrderAdminManagerImpl implements OrderAdminManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderAdminManagerImpl.class);

    @Resource
    private OrderService orderService;
    @Resource
    private MemberService memberService;
    @Resource
    private HousesService housesService;
    @Resource
    private CommonService commonService;



    //业务员抢单
    @Override
    public ResultVo grabOrdersList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //查询条件
        Map<Object, Object> queryFilter= Maps.newHashMap();
        try {
            int userid = StringUtil.getAsInt(StringUtil.trim(condition.get("userid")));
            queryColumn.clear();
            queryColumn.add("ID poolId");
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("HOUSE_ID houseId");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
            queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
            queryColumn.add("CONTACT_NAME contactName");//联系人
            queryColumn.add("CLOSE_TIME closeTime");//预计完成时间
            queryColumn.add("VERSION_NO versionNo");//当前版本号
            condition.put("queryColumn",queryColumn);
            //查询订单池可抢的订单信息
            vo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//查询成功
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            //订单数据
            List<Map<Object,Object>> orderList = (List<Map<Object,Object>>) vo.getDataSet();
            List<Integer> houseIds = new ArrayList<>();//房源Ids
            List<Integer> poolIds = new ArrayList<>();//订单池Ids
            if(orderList==null || orderList.size()==0) {
                return vo;
            }

            for (Map<Object, Object> order : orderList) {
                poolIds.add(StringUtil.getAsInt(StringUtil.trim(order.get("poolId"))));
            }
            //查询当前业务员的
            queryColumn.clear();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("isTransferOrder",1);//已经转过单
            queryFilter.put("poolIds",poolIds);//订单池ID
            queryFilter.put("userId",userid);//已经转过单
            //查询业务员任务列表信息
            ResultVo taskVo =  memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,queryFilter);
            if(taskVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            //任务数据
            List<Map<Object, Object>> taskList = (List<Map<Object, Object>>) taskVo.getDataSet();
            Date date = new Date();
            for (int i =orderList.size()-1;i>=0; i--) {//过滤掉已经转单的订单池
                Map<Object,Object> order = orderList.get(i);
                int poolId = StringUtil.getAsInt(StringUtil.trim(order.get("poolId")));
                Date estimatedTime = (Date) order.get("estimatedTime");
                long time1 = estimatedTime.getTime() - date.getTime();
                order.put("estimatedTimeCountDown",time1/1000);
                Date closeTime = (Date) order.get("closeTime");
                long time2 = closeTime.getTime() - date.getTime();
                order.put("closeTimeCountDown",time2/1000);
                int state = 0;
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> task : taskList) {//判断业务员是否可抢
                    int _poolId = StringUtil.getAsInt(StringUtil.trim(task.get("poolId")));
                    if(poolId == _poolId){
                        state= 1;
                        orderList.remove(i);
                        break;
                    }
                }
                if(state ==0){
                    houseIds.add(houseId);
                }
            }
            if(orderList==null || orderList.size()==0){
                vo.setDataSet(null);
                return vo;
            }
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID houseId");
            queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本
            queryColumn.add("MEMBER_ID memberId");//业主ID
            queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
            queryColumn.add("HAVE_KEY haveKey");//是否有钥匙：0>无,1有
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_RENT houseRent");//期望租金/或出售价
            queryColumn.add("HOUSE_ACREAGE houseAcreage");//期望租金/或出售价
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//期望租金/或出售价
            condition.put("queryColumn",queryColumn);
            condition.put("houseIds",houseIds);
            //查询房源信息
            ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);

            if(ResultConstant.SYS_REQUIRED_SUCCESS != housesVo.getResult()) {//房源数据异常
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            //房源信息
            List<Map<Object,Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
            List<Integer> memberIds = Lists.newArrayList();
            for (Map<Object, Object> house : houses) {
                memberIds.add(StringUtil.getAsInt(StringUtil.trim(house.get("memberId"))));
            }
            if(memberIds!=null && memberIds.size()>0){
                //判断联系人是否存在，不存在则留业主手机号及姓名
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID memberId");
                queryColumn.add("AREA_CODE areaCode");//电话地区号
                queryColumn.add("MEMBER_MOBLE memberMobile");//业主ID
                queryColumn.add("NICKNAME nickname");//业主ID
                condition.put("queryColumn",queryColumn);
                condition.put("memberIds",memberIds);
                ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                if(ResultConstant.SYS_REQUIRED_SUCCESS == memberVo.getResult()) {
                    List<Map<Object,Object>> memberList = (List<Map<Object,Object>>) memberVo.getDataSet();
                    if(memberList!=null && memberList.size()>0){
                        for (Map<Object, Object> house : houses) {
                            int memberId = StringUtil.getAsInt(StringUtil.trim(house.get("memberId")));
                            for (Map<Object, Object> member : memberList) {
                                int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                                if(memberId==_memberId){
                                    house.putAll(member);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            //将系统订单池与业务员任务表进行关联
            for (int i =orderList.size()-1;i>=0; i--) {
                Map<Object,Object> order = orderList.get(i);
                int canRob = 0 ;
                int houseId  = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> house : houses) {
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    if (_houseId == houseId){
                        house.remove("houseId");
                        order.putAll(house);
                        break;
                    }
                }
                order.put("canRob",canRob);//是否可抢
            }
        } catch (Exception e) {
            logger.error("Error to encode OrderAdminManagerImpl grabOrdersList", e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 业务员抢单详情
     * @param condition
     * @return
     */
    @Override
    public ResultVo getGrabOrderDetail(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //查询条件
        Map<Object, Object> queryFilter= Maps.newHashMap();
        //业务员ID
        int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
        try {
            queryColumn.clear();
            queryColumn.add("ID poolId");
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("HOUSE_ID houseId");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
            queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
            queryColumn.add("CONTACT_NAME contactName");//联系人
            queryColumn.add("CLOSE_TIME closeTime");//预计完成时间
            queryColumn.add("VERSION_NO versionNo");//当前版本号
            condition.put("isOpenOrder",1);//是否开启抢单0:未开启，1：开启
            condition.put("isFinished",0);//是否完成0:未完成，1：已完成
            condition.put("queryColumn",queryColumn);
            //查询订单池可抢的订单信息
            vo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//查询成功
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            //订单数据
            List<Map<Object,Object>> orderList = (List<Map<Object,Object>>) vo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return vo;
            }
            Map<Object,Object> orderMap = orderList.get(0);
            int houseId = -1;//房源Id
            int poolId = -1;//订单池Id
            Date date = new Date();
            Date estimatedTime = (Date) orderMap.get("estimatedTime");
            long time1 = estimatedTime.getTime() - date.getTime();
            Date closeTime = (Date) orderMap.get("closeTime");
            long time2 = closeTime.getTime() - date.getTime();
            houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));
            poolId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("poolId")));
            orderMap.put("estimatedTimeCountDown",time1/1000);
            orderMap.put("closeTimeCountDown",time2/1000);

            //查询当前业务员的
            queryColumn.clear();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("isTransferOrder",1);//已经转过单
            queryFilter.put("poolId",poolId);//订单池ID
            queryFilter.put("userId",userId);//已经转过单
            //查询业务员任务列表信息
            ResultVo taskVo =  memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,queryFilter);
            if(taskVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            //任务数据
            List<Map<Object, Object>> taskList = (List<Map<Object, Object>>) taskVo.getDataSet();
            //订单类型 0外获订单->1-外看订单->2合同订单->3-投诉（外看）->4投诉（外获）->5投诉（区域长）
            int orderType = StringUtil.getAsInt(StringUtil.trim(orderMap.get("orderType")));
            if(orderType > 2){
                /**
                 * 投诉订单，获取投诉信息
                 */
                //投诉信息
                Map<Object, Object> complainMap = new HashMap<>(16);
                condition.clear();
                condition.put("isDel",0);
                condition.put("poolId",poolId);
                ResultVo complainResultVo = housesService.selectList(new HsHouseComplain(), condition, 0);
                if(complainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && complainResultVo.getDataSet() != null){
                    List<Map<Object, Object>> complainList = (List<Map<Object, Object>>) complainResultVo.getDataSet();
                    complainMap = complainList.get(0);
                }
//                orderMap.clear();
                orderMap.putAll(complainMap);
            }else{
                /**
                 * 非投诉订单
                 */
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID houseId");
                queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本
                queryColumn.add("MEMBER_ID memberId");//业主ID
                queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
                queryColumn.add("HAVE_KEY haveKey");//是否有钥匙：0>无,1有
                queryColumn.add("HOUSE_NAME houseName");//房源名称
                queryColumn.add("HOUSE_RENT houseRent");//期望租金/或出售价
                queryColumn.add("HOUSE_ACREAGE houseAcreage");//期望租金/或出售价
                queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//期望租金/或出售价
                queryColumn.add("LONGITUDE longitude");//'经度'
                queryColumn.add("LATITUDE latitude");//纬度
                condition.put("queryColumn",queryColumn);
                condition.put("id",houseId);
                //查询房源信息
                ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);

                if(ResultConstant.SYS_REQUIRED_SUCCESS != housesVo.getResult()) {//房源数据异常
                    return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                }
                //房源信息
                List<Map<Object,Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
                if(houses==null || houses.size()<=0){
                    return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                }
                Map<Object,Object> house = houses.get(0);
                //判断联系人是否存在，不存在则留业主手机号及姓名
                int memberId = StringUtil.getAsInt(StringUtil.trim(house.get("memberId")));
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID memberId");
                queryColumn.add("AREA_CODE areaCode");//电话地区号
                queryColumn.add("MEMBER_MOBLE memberMoble");//业主ID
                queryColumn.add("NICKNAME nickname");//业主ID
                condition.put("queryColumn",queryColumn);
                condition.put("memberId",memberId);
                ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                if(ResultConstant.SYS_REQUIRED_SUCCESS == memberVo.getResult()) {
                    List<Map<Object,Object>> memberList = (List<Map<Object,Object>>) memberVo.getDataSet();
                    if(memberList!=null && memberList.size()>0){
                        orderMap.putAll(memberList.get(0));
                    }
                }
                orderMap.putAll(house);
            }
            //将系统订单池与业务员任务表进行关联
            int canRob = 0 ;
            for (Map<Object, Object> task : taskList) {//判断业务员是否可抢
                int _poolId = StringUtil.getAsInt(StringUtil.trim(task.get("poolId")));
                if (poolId == _poolId) {
                    canRob = 1;
                    break;
                }
            }
            //是否可抢
            orderMap.put("canRob",canRob);
            vo.setDataSet(orderMap);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 更新投诉订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateComplaintGrabOrderDetail(Map<Object, Object> condition){
        ResultVo vo = new ResultVo();
        //业务员ID
        int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
        //投诉id
        int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
        //投诉id
        int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId")));
        //投诉是否属实：0未核实 1情况属实 2情况不属实
        int isVerified = StringUtil.getAsInt(StringUtil.trim(condition.get("isVerified")));
        //外看备注
        String remarkLookOutside = StringUtil.trim(condition.get("remarkLookOutside"));
        //外获备注
        String remarkOutOf = StringUtil.trim(condition.get("remarkOutOf"));
        //区域长备注
        String remarkKeyManager = StringUtil.trim(condition.get("remarkKeyManager"));
        String remark = "";
        if(StringUtil.hasText(remarkLookOutside)){
            remark = remarkLookOutside;
        }else if(StringUtil.hasText(remarkOutOf)){
            remark = remarkOutOf;
        }else if(StringUtil.hasText(remarkKeyManager)){
            remark = remarkKeyManager;
        }
        try {
            Date date = new Date();

            /**
             * 更新任务状态
             */
            ResultVo taskResultVo = memberService.select(taskId, new HsSystemUserOrderTasks());
            if(taskResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || taskResultVo.getDataSet() == null){
                //查询不到任务
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Get task failed");
            }
            HsSystemUserOrderTasks systemUserOrderTasks = (HsSystemUserOrderTasks) taskResultVo.getDataSet();
            //获取预计完成时间
            Date estimatedTime = systemUserOrderTasks.getEstimatedTime();
            if(estimatedTime.getTime()<=date.getTime()){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Has exceeded the expected completion time");
            }
            HsSystemUserOrderTasks updateUserOrderTasks = new HsSystemUserOrderTasks();
            //任务ID
            updateUserOrderTasks.setId(systemUserOrderTasks.getId());
            //是否完成 0：未完成，1：已完成
            updateUserOrderTasks.setIsFinished(1);
            updateUserOrderTasks.setRemark("业务员完成投诉任务");
            //是否到达0:未到达，1：已到达
            updateUserOrderTasks.setIsArrive(1);
            updateUserOrderTasks.setVersionNo(0);
            ResultVo TaskUpdate = memberService.update(updateUserOrderTasks);
            if(TaskUpdate.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                /**
                 * 插入任务日志
                 */
                HsSystemUserOrderTasksLog log = new HsSystemUserOrderTasksLog();
                //任务ID
                log.setTaskId(systemUserOrderTasks.getId());
                //订单池ID
                log.setPoolId(systemUserOrderTasks.getPoolId());
                log.setNodeType(3);
                log.setCreateBy(memberId);
                log.setCreateTime(date);
                log.setUpdateBy(memberId);
                log.setUpdateTime(date);
                ResultVo logInsert = memberService.insert(log);

                /**
                 * 更新订单池状态
                 */
                //订单池ID
                int orderPoolId = systemUserOrderTasks.getPoolId();
                //将订单池状态改成已完成
                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                ResultVo orderPoolVo = orderService.select(orderPoolId,orderPool);
                HsSystemOrderPool oldOrderPool  = (HsSystemOrderPool) orderPoolVo.getDataSet();
                orderPool.setId(orderPoolId);
                //是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
                orderPool.setIsFinished(5);
                orderPool.setRemark("业务员完成投诉任务");
                orderPool.setUpdateTime(date);
                orderPool.setVersionNo(oldOrderPool.getVersionNo());
                orderPoolVo =orderService.update(orderPool);
                //修改成功添加日志信息
                if(orderPoolVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    /**
                     * 插入订单池日志
                     */
                    HsSystemOrderPoolLog orderPoolLog = new HsSystemOrderPoolLog();
                    orderPoolLog.setPoolId(orderPoolId);
                    orderPoolLog.setOrderType(orderPool.getOrderType());
                    orderPoolLog.setOperatorType(5);
                    orderPoolLog.setCreateTime(date);
                    orderPoolLog.setPostTime(date);
                    orderPoolLog.setRemarks("业务员完成投诉任务");
                    ResultVo update = orderService.update(orderPoolLog);

                    /**
                     * 获取投诉信息
                     */
                    ResultVo complainResultVo = housesService.select(id,new HsHouseComplain());
                    if(complainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || complainResultVo.getDataSet() == null){
                        //获取投诉信息失败
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Failed to get complaint information");
                    }
                    HsHouseComplain houseComplain = (HsHouseComplain) complainResultVo.getDataSet();
                    Integer status = houseComplain.getStatus();
                    if(status != 1){
                        //订单状态有误
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Order status is incorrect");
                    }
                    /**
                     * 更新投诉信息
                     */
                    houseComplain.setIsVerified(isVerified);
                    houseComplain.setTransferType(0);
                    houseComplain.setStatus(2);
                    if(StringUtil.hasText(remarkLookOutside)){
                        houseComplain.setRemarkLookOutside(remarkLookOutside);
                    }else if(StringUtil.hasText(remarkOutOf)){
                        houseComplain.setRemarkOutOf(remarkOutOf);
                    }else if(StringUtil.hasText(remarkKeyManager)){
                        houseComplain.setRemarkKeyManager(remarkKeyManager);
                    }
                    ResultVo complainUpdate = housesService.update(houseComplain);
                    if(complainUpdate.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        //更新失败
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Update failed");
                    }
                    /**
                     * 插入投诉日志
                     */
                    HsHouseComplainLog hsHouseComplainLog = new HsHouseComplainLog();
                    hsHouseComplainLog.setComplainId(id);
                    hsHouseComplainLog.setStatus(2);
                    hsHouseComplainLog.setRemark(remark);
                    hsHouseComplainLog.setCreateBy(memberId);
                    hsHouseComplainLog.setCreateTime(date);
                    ResultVo insert = housesService.insert(hsHouseComplainLog);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 业务员抢单动作
     * @param condition
     * @return
     */
    @Override
    public ResultVo grabOrderAction(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //查询条件
        Map<Object, Object> queryFilter= Maps.newHashMap();
        try {
            int poolId  = StringUtil.getAsInt(StringUtil.trim(condition.get("poolId")));//订单池ID
            int userid  = StringUtil.getAsInt(StringUtil.trim(condition.get("userid")));//业务员ID
            int salesmanType  = StringUtil.getAsInt(StringUtil.trim(condition.get("salesmanType")));//业务员类型
            //用户名（手机号）
            String username = StringUtil.trim(condition.get("username"));
            String languageVersion = StringUtil.trim(condition.get("languageVersion"));
            condition.remove("username");
            condition.remove("languageVersion");

            //查询订单池可抢的订单信息
            ResultVo orderVo = orderService.select(poolId,new HsSystemOrderPool());
            if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//查询成功
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            HsSystemOrderPool orderPool = (HsSystemOrderPool) orderVo.getDataSet();
            if(orderPool == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            //是否开启抢单0:未开启，1：开启
            if(orderPool.getIsOpenOrder() == 0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"This order does not open the single function");
//                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"此订单未开启抢单功能");
            }
            if(orderPool.getIsFinished() != 0){//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"This order cannot be purchased");
//                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"此订单不能进行抢单");
            }
            //当前时候已达到预计完成时间
            if(orderPool.getEstimatedTime().getTime() <= orderVo.getSystemTime().getTime()){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"This order cannot be purchased");
//                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"此订单不能进行抢单");
            }

            //查询当前业务员是否对此订单做过转单
            queryColumn.clear();
            queryColumn.add("ID taskId");
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("userId",userid);//已经转过单
            queryFilter.put("poolId",poolId);//此订单
            //查询业务员任务列表信息
            ResultVo taskVo =  memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,queryFilter);
            if(taskVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            List<Map<Object, Object>> userOrderTasks = (List<Map<Object, Object>>) taskVo.getDataSet();
            if(userOrderTasks!=null && userOrderTasks.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"You have not completed the task of this order or you have done a suborder operation");
//                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"您未完成此订单的任务或者已进行过转单操作");
            }

            //查询房源所在地
            queryColumn.clear();
            //房源id
            queryColumn.add("ID houseId");
            //城市
            queryColumn.add("CITY city");
            //社区
            queryColumn.add("COMMUNITY community");
            //子社区
            queryColumn.add("SUB_COMMUNITY subCommunity");
            //预约类型（0：出租，1：出售）
            queryColumn.add("LEASE_TYPE leaseType");
            condition.put("queryColumn",queryColumn);
            condition.put("id",orderPool.getHouseId());
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(houseVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            if(houseList==null ||houseList.size()==0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            Map<Object, Object> house = houseList.get(0);
            if(house==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            //房源所在地址
            String houseCity = StringUtil.trim(house.get("city"));
            String houseCommunity = StringUtil.trim(house.get("community"));
            String leaseType = StringUtil.trim(house.get("leaseType"));

            //自定义查询列名

            condition.clear();
            condition.put("locked",0);
            condition.put("isForbidden",0);
            condition.put("isDel",0);
            List<String> roleIds = new ArrayList<String>();
            roleIds.add("2ece77221ad0b89f14f35899a8a63886");//钥匙管理员
            condition.put("roleIds",roleIds);
            //获取所有钥匙管理员
            List<Map<Object,Object>> keymasterUsers = memberService.selectOutsideUser(condition);
            String keymasterId = "-1";
            for ( int i  = 0 ; i< keymasterUsers.size() ;i++) {
                Map<Object, Object> keymasterUser = keymasterUsers.get(i);
                //判断钥匙管理员所属位置
                String userId = StringUtil.trim(keymasterUser.get("userId"));
                String city = StringUtil.trim(keymasterUser.get("city"));
                String community = StringUtil.trim(keymasterUser.get("community"));
                if( houseCity.equals(city) && houseCommunity.equals(community)){ // 查询所属业务员负责地址
                    keymasterId = userId;
                    break;
                }
            }


            //准备添加任务
            HsSystemUserOrderTasks tasks = new HsSystemUserOrderTasks();
            tasks.setPoolId(poolId);
            tasks.setApplyId(orderPool.getApplyId());
            tasks.setHouseId(orderPool.getHouseId());
            tasks.setUserId(userid);
            tasks.setTaskType(orderPool.getOrderType());//任务类型 0外获任务->1-外看任务->2合同任务
            tasks.setIsSystem(1);//业务员手动抢单
            tasks.setCreateBy(userid);
            tasks.setRemark("业务员手动抢单");
            tasks.setStandby1(keymasterId);//绑定区域长ID
            tasks.setStandby2("0");//绑定区域长ID
            tasks.setCreateTime(taskVo.getSystemTime());
            tasks.setEstimatedTime(orderPool.getEstimatedTime());//预计完成时间
            ResultVo insertTaskVo =  memberService.insert(tasks);
            if(insertTaskVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                //插入成功返回的实体数据
                HsSystemUserOrderTasks inserTasks = (HsSystemUserOrderTasks) insertTaskVo.getDataSet();
                //获取返回的任务ID,并添加日志信息
                int taskId = inserTasks.getId();
                HsSystemUserOrderTasksLog tasksLog = new HsSystemUserOrderTasksLog();
                tasksLog.setTaskId(taskId);
                tasksLog.setPoolId(poolId);
                tasksLog.setNodeType(0);
                tasksLog.setCreateBy(userid);
                tasksLog.setCreateTime(insertTaskVo.getSystemTime());
                tasksLog.setPostTime(insertTaskVo.getSystemTime());
                tasksLog.setRemarks("业务员手动抢单");
                ResultVo insertTaskLogVo =  memberService.insert(tasksLog);

                //修改订单池数据
                HsSystemOrderPool updateOrderPool = new HsSystemOrderPool();
                updateOrderPool.setId(poolId);
                updateOrderPool.setIsFinished(1);
                updateOrderPool.setIsOpenOrder(0);//控制不在订单列表里面展示
                updateOrderPool.setVersionNo(orderPool.getVersionNo());
                ResultVo updateOrderVo =  orderService.update(updateOrderPool);

                //抢单成功
                //lsq 修改预约看房信息
                condition.clear();
                condition.put("houseId",house.get("id"));
                condition.put("estimatedTime",orderPool.getEstimatedTime());
                condition.put("poolId",poolId);
                ResultVo updateResultVo = memberService.updateSeeHouseApply(condition);
                if(updateResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    //获取预约信息
                    condition.clear();
                    condition.put("poolId",poolId);
                    condition.put("isDel",0);
                    ResultVo applicationResultVo = memberService.selectList(new HsMemberHousingApplication(), condition, 0);
                    if(applicationResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        //获取预约看房信息失败
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                    }
                    List<Map<Object, Object>> applicationList = (List<Map<Object, Object>>) applicationResultVo.getDataSet();
                    Map<Object, Object> applicationMap = new HashMap<>(16);
                    if(applicationList != null && applicationList.size() > 0){
                        applicationMap = applicationList.get(0);
                    }

                    //lsq 将推送消息存入数据库
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //平台类型 测试:0 外部:1 外获:2 外看:3
                    //业务员类型 0 外获业务员，1外看业务员
                    Integer platform;
                    if(salesmanType == 0){
                        platform = 2;
                    }else{
                        platform = 3;
                    }
                    String msg = "您有新的订单信息";
                    if("1".equals(languageVersion)){
                        msg = "You have new order information";
                    }
                    Integer msgCode = 7;
                    //任务类型 0外获任务->1-外看任务->2合同任务
                    Integer orderType = orderPool.getOrderType();
                    if(orderType == 0){
                        msgCode = 6;
                    }else if(orderType == 2){
                        msgCode = 5;
                    }

                    Map<String,Object> msgMap = new HashMap<>(5);
                    msgMap.put("leaseType",leaseType);
                    //是否系统派单0:自动派单，1：业务员手动抢单
                    msgMap.put("isSystem",1);
                    msgMap.put("haveKey",StringUtil.getAsInt(StringUtil.trim(house.get("haveKey"))));
                    //见面地点
                    msgMap.put("meetingPlace",orderPool.getAppointmentMeetPlace());
                    //见面时间（客户预约时间）
                    msgMap.put("meetingTime",sdf.format(applicationMap.get("startApartmentTime")));
                    String msgMapJsonStr = JSON.toJSONString(msgMap);

                    HsMsgRecord hsMsgRecord = new HsMsgRecord();
                    hsMsgRecord.setUserName(username);
                    hsMsgRecord.setMsgCode(msgCode);
                    hsMsgRecord.setAlert(msg);
                    hsMsgRecord.setDetails(msgMapJsonStr);
                    hsMsgRecord.setCreateTime(new Date());
                    hsMsgRecord.setCreateBy(userid);
                    ResultVo insert = commonService.insert(hsMsgRecord);
                    if(insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //发送推送消息
                        UmengUtil.aliasSend(StringUtil.getAsInt(languageVersion),username,msg,platform,msgCode);
                    }
                }



                return ResultVo.success();
            }else{//抢单失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * //lsq获取钥匙归属
     * @param houseId
     * @return 0：在业主，1：在钥匙管理员，2：外看业务员，3：外获业务员 null：未获取到归属信息
     * @throws Exception
     */
    public Integer getHaveKey(Integer houseId) throws Exception {
        Integer haveKey = null;
        Map<Object, Object> condition = new HashMap<>(16);
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //业主ID
        queryColumn.add("MEMBER_ID memberId");
        //业务员ID
        queryColumn.add("USER_ID userId");
        //是否过期 0：未过期 1：已过期
        queryColumn.add("IS_EXPIRE isExpire");
        condition.put("queryColumn",queryColumn);
        //未过期
        condition.put("isExpire",0);
        condition.put("houseId",houseId);
        ResultVo keysResult = housesService.selectCustomColumnNamesList(HsHouseKeyCases.class,condition);
        if(keysResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || keysResult.getDataSet() == null){
            return null;
        }
        List<Map<Object,Object>> keysList = (List<Map<Object, Object>>) keysResult.getDataSet();
        if(keysList.size() < 1){
            return null;
        }
        Map keys = keysList.get(0);
        //业主ID
        int ownerId = StringUtil.getAsInt(StringUtil.trim(keys.get("memberId")));
        //业务员ID
        int userId = StringUtil.getAsInt(StringUtil.trim(keys.get("userId")));
        if(ownerId != -1){
            //钥匙在业主处
            haveKey = 0;
        }
        if(userId != -1){
            //在业务员处
            Map<Object,Object> queryRoleFilter = new HashMap<>(3);
            queryRoleFilter.put("userId",userId);
            //未禁用
            queryRoleFilter.put("isForbidden",0);
            //未删除
            queryRoleFilter.put("isDel",0);
            ResultVo roleResult = memberService.selectUserRoles(queryRoleFilter);
            if(roleResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> roleList = (List<Map<Object, Object>>) roleResult.getDataSet();
                if(roleList.size() > 0){
                    for(Map role : roleList){
                        if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                            //在钥匙管理员处
                            haveKey = 1;
                            break;
                        }else if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                            //在外看业务员处
                            haveKey = 2;
                            break;
                        }else if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                            //在外获业务员处
                            haveKey = 3;
                            break;
                        }
                    }
                }
            }
        }
        return haveKey;
    }
}
