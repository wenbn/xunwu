package www.ucforward.com.manager.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.json.JSONArray;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.MemberAdminManager;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wenbn
 * @version 1.0
 * @date 2018/6/20
 */
@Service("memberAdminManager")
public class MemberAdminManagerImpl implements MemberAdminManager {

    private static final Logger logger = LoggerFactory.getLogger(MemberAdminManagerImpl.class);
    @Autowired
    private MemberService memberService;
    @Resource
    private HousesService housesService;
    @Resource
    private OrderService orderService;


    /**
     * 业务发送定位信息
     * @param userLocation
     * @return
     */
    @Override
    public ResultVo sendLocation(HsSysUserLocation userLocation) {
        ResultVo vo = new ResultVo();
        try {
            //业务发送定位信息
            return memberService.insert(userLocation);
        } catch (Exception e) {
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 外看业务员已带看房列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo selectLookForHouseFinished(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //查询带看房列表
            vo = memberService.selectList(new HsSystemUserOrderTasks(),condition,1);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//查询成功
                List<HsSystemUserOrderTasks> orderTasks = (List<HsSystemUserOrderTasks>) vo.getDataSet();
                if(orderTasks== null){
                    return vo;
                }
                //返回数据
                if( orderTasks!=null && orderTasks.size()>0 ){
                    //房源Ids
                    List<Integer> houseIds = new ArrayList<>();
                    for (HsSystemUserOrderTasks orderTask : orderTasks) {
                        houseIds.add(orderTask.getHouseId());
                    }
                    if( houseIds!=null && houseIds.size()>0 ){
                        condition.clear();
                        condition.put("houseIds",houseIds);
                        //自定义查询列名
                        List<String> queryColumn = new ArrayList<>();
                        queryColumn.add("ID houseId");//主键ID
                        queryColumn.add("HOUSE_NAME houseName");//房源ID
                        queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                        queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                        queryColumn.add("LEASE_TYPE leaseType");//房屋类型 0:出租，1：出售
                        queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
                        queryColumn.add("CITY city");//城市
                        queryColumn.add("COMMUNITY community");//社区
                        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                        queryColumn.add("ADDRESS address");//所在位置

                        condition.put("queryColumn",queryColumn);
                        ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                        if(ResultConstant.SYS_REQUIRED_SUCCESS == housesVo.getResult()) {//查询成功
                            List<Map<Object,Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
                            if(houses == null){//房源数据异常
                                vo.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
                                vo.setMessage(ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                            }
                            for (Map<Object, Object> house : houses) {
                                String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                if(StringUtil.hasText(houseMainImg)){
                                    house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                                }
                                int houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                                for (HsSystemUserOrderTasks orderTask : orderTasks) {
                                    if (orderTask.getHouseId() == houseId){
                                        house.put("taskId",orderTask.getId());
                                        orderTasks.remove(orderTask);
                                        break;
                                    }
                                }
                            }
                            vo.setDataSet(houses);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 外看业务员已带看房详情
     * @param condition
     * @return
     */
    @Override
    public ResultVo selectLookForHouseFinishedDetail(Map<Object, Object> condition) {
        ResultVo vo = null;
        //返回结果
        Map<Object,Object> map = new HashMap<>();
        try {
            //查询带看房详情
            int taskId = (int) condition.get("taskId");
            int userId = (int) condition.get("userId");
            vo = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(ResultConstant.SYS_REQUIRED_SUCCESS == vo.getResult()){//查询成功
                HsSystemUserOrderTasks orderTasks = (HsSystemUserOrderTasks) vo.getDataSet();
                vo.setDataSet(null);
                if( orderTasks!=null ){
                    //查询房源信息
                    ResultVo housesVo = housesService.select(orderTasks.getHouseId(),new HsMainHouse());
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == housesVo.getResult()) {//查询成功
                        HsMainHouse houses = (HsMainHouse) housesVo.getDataSet();
                        //房源Ids
                        List<Integer> houseIds = new ArrayList<>();
                        houseIds.add(orderTasks.getHouseId());
                        condition.put("houseIds",houseIds);
                        //查询房源图片
                        ResultVo houseImgVo = housesService.selectList(new HsHouseImg(), condition ,0);
                        List<Map<Object,Object>> houseImgList = (List<Map<Object, Object>>) houseImgVo.getDataSet();

                        int houseId = houses.getId();
                        //保存房源图片
                        List<String> housImgs = new ArrayList<>();
                        Map<Object,Object> houseMap = MapClassUtil.beanToMap(houses);
                        if(houseImgList==null ||houseImgList.size()==0){
                            houseMap.put("subImg",housImgs);
                        }
                        for (Map<Object, Object> houseImg : houseImgList) {//保存房源图片
                            int _houseId = StringUtil.getAsInt(StringUtil.trim(houseImg.get("houseId")));
                            for (Map.Entry<Object, Object> entry : houseImg.entrySet()) {
                                Object key = entry.getKey();
                                if(-1 != StringUtil.trim(key).indexOf("houseImg") && StringUtil.hasText(StringUtil.trim(entry.getValue()))){
                                    housImgs.add(ImageUtil.IMG_URL_PREFIX+entry.getValue());
                                }
                            }
                            if(houseId == _houseId){
                                houseMap.put("subImg",housImgs);
                                break;
                            }
                        }
                        houseMap.put("taskId",orderTasks.getId());
                        map.put("houseInfo",houseMap);
                    }
                    //查询房源评价信息
                    condition.clear();
                    condition.put("houseId",orderTasks.getHouseId());
                    condition.put("isCheck",1);//审核状态：0>待审核，1>审核通过，2>审核不通过
                    //自定义查询列名
                    List<String> queryColumn = new ArrayList<>();
                    queryColumn.add("ID evaluationId");//主键ID
                    queryColumn.add("VALUATOR_TYPE valuatorType");//评价人类型
                    queryColumn.add("EVALUATION_TYPE evaluationType");//评价类型
                    queryColumn.add("EVALUATION_EXPLAIN evaluationExplain");//评价说明
                    queryColumn.add("USER_ID userId");//业务员ID
                    queryColumn.add("MEMBER_ID memberId");//看房人ID
                    queryColumn.add("CREATE_TIME createTime");//看房人ID
                    queryColumn.add("HOUSE_ID houseId");//房源ID

                    condition.put("queryColumn",queryColumn);
                    ResultVo housesEvaluationVo = housesService.selectCustomColumnNamesList(HsHouseEvaluation.class, condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == housesEvaluationVo.getResult()) {//查询成功
                        List<Map<Object,Object>> evaluations = (List<Map<Object, Object>>) housesEvaluationVo.getDataSet();

                        List<Integer> memberIds = Lists.newArrayList();
                        List<Integer> userIds = Lists.newArrayList();
                        evaluations.forEach((evaluation)->{
                            int memberId = StringUtil.getAsInt(StringUtil.trim(evaluation.get("memberId")));
                            int _userId = StringUtil.getAsInt(StringUtil.trim(evaluation.get("userId")));
                            if(memberId != -1){
                                memberIds.add(memberId);
                            }
                            if(_userId != -1){
                                userIds.add(_userId);
                            }
                        });

                        //获取评价人名称
                        if(memberIds!=null && memberIds.size()>0){
                            condition.clear();
                            queryColumn.clear();
                            queryColumn.add("ID memberid");//主键ID
                            queryColumn.add("NICKNAME nickname");//会员昵称
                            condition.put("queryColumn",queryColumn);
                            condition.put("memberIds",memberIds);
                            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                            if(memberVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object,Object>> memberList = (List<Map<Object, Object>>) memberVo.getDataSet();
                                for (Map<Object, Object> evaluation : evaluations) {
                                    int memberId = StringUtil.getAsInt(StringUtil.trim(evaluation.get("memberId")));
                                    for (Map<Object, Object> member : memberList) {
                                        int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                                        if(memberId == _memberId){
                                            evaluation.put("name",member.get("nickname"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if(userIds!=null && userIds.size()>0){
                            condition.clear();
                            queryColumn.clear();
                            queryColumn.add("ID userId");//主键ID
                            queryColumn.add("USERNAME username");//会员昵称
                            condition.put("queryColumn",queryColumn);
                            condition.put("memberIds",memberIds);
                            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
                            if(memberVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object,Object>> userList = (List<Map<Object, Object>>) memberVo.getDataSet();
                                for (Map<Object, Object> evaluation : evaluations) {
                                    int userId1 = StringUtil.getAsInt(StringUtil.trim(evaluation.get("userId")));
                                    for (Map<Object, Object> user : userList) {
                                        int _userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                                        if(userId1 == _userId){
                                            evaluation.put("name",user.get("username"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        map.put("housesEvaluation",evaluations);
                    }
                    vo.setDataSet(map);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 查询外获派单列表信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOutGainDispatchOrder(Map<Object, Object> condition) {
        ResultVo result = null;
        Map<Object,Object> query = new HashMap<>();
        try {
            //查询当前业务员的
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("APPLY_ID applyId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            condition.put("queryColumn",queryColumn);
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Date date = new Date();
                condition.remove("queryColumn");
                Object pageInfo = result.getPageInfo();
                List<String> memberIds = new ArrayList<>();
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                List<String> houseApplyIds = new ArrayList<>(); //房源申请ID
                List<Integer> poolIds = new ArrayList<>();//订单池Ids
                if(taskList != null && taskList.size() > 0){
                    String houseApplyId = "";
                    List<Map<Object,Object>> applyHouseList = new ArrayList<>(); //房源申请信息
                    for(Map<Object,Object> task : taskList){
                        houseApplyId = StringUtil.trim(task.get("applyId")); //房源申请ID
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType")));
                        if(taskType == 0 || taskType == 4){
                            houseApplyIds.add(houseApplyId);
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId"))));
                        }
                    }

                    //房源申请ID，外获查询申请表
                    if(houseApplyIds != null && houseApplyIds.size() >0){
                        condition.clear();
                        queryColumn.clear();
                        queryColumn.add("ID poolId");
                        queryColumn.add("ORDER_CODE orderCode");//订单编码
                        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                        queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
                        queryColumn.add("CLOSE_TIME closeTime");//预计完成时间
                        condition.put("queryColumn",queryColumn);
                        condition.put("poolIds",poolIds);
                        ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                        if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//访问失败
                            return orderVo;
                        }

                        //订单池数据
                        List<Map<Object,Object>> ordersList = (List<Map<Object,Object>>) orderVo.getDataSet();
                        if(ordersList!=null && ordersList.size()>0 ){
                            //将系统订单池与业务员任务表进行关联
                            for (Map<Object,Object> orderTask : taskList) {
                                int poolId = StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId")));
                                for (Map<Object, Object> order : ordersList) {
                                    int _poolId = StringUtil.getAsInt(StringUtil.trim(order.get("poolId")));
                                    if(poolId == _poolId){
                                        Date closeTime = (Date) order.get("closeTime");
                                        long time2 = closeTime.getTime() - date.getTime();
                                        order.put("closeTimeCountDown",time2/1000);

                                        Date estimatedTime = (Date) order.get("estimatedTime");
                                        long time1 = estimatedTime.getTime() - date.getTime();
                                        order.put("estimatedTimeCountDown",time1/1000);
                                        orderTask.putAll(order);
                                        break;
                                    }
                                }
                            }
                        }
                        query.clear();
                        queryColumn.clear();
                        queryColumn.add("ID applyId");
                        queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
                        queryColumn.add("CITY city");//'城市'
                        queryColumn.add("COMMUNITY community");//'社区
                        queryColumn.add("SUB_COMMUNITY subCommunity");//'子社区'
                        queryColumn.add("PROPERTY property");//'项目'
                        queryColumn.add("ADDRESS address");//'房源所在区域名称'
                        queryColumn.add("MEMBER_ID memberId"); //用户ID
                        queryColumn.add("APPLICANT_TYPE applicantType"); //申请类型 0：业主 1：POA
                        query.put("queryColumn",queryColumn);
                        query.put("houseApplyIds",houseApplyIds);
                        ResultVo applyResult = housesService.selectCustomColumnNamesList(HsOwnerHousingApplication.class,query); //查询房源申请信息
                        if(applyResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//请求失败
                            return applyResult;
                        }
                        applyHouseList = (List<Map<Object, Object>>)applyResult.getDataSet();
                    }

                    for (Map<Object,Object> task : taskList){
                        houseApplyId = StringUtil.trim(task.get("applyId")); //房源申请ID
                        if(!"-1".equals(houseApplyId)){
                            for (Map<Object,Object> applyHouse : applyHouseList){
                                String _houseApplyId = StringUtil.trim(applyHouse.get("applyId")); // 房源主信息ID
                                if(houseApplyId.equals(_houseApplyId)){
                                    task.put("address",applyHouse.get("address")); //房源位置
                                    task.put("memberId",applyHouse.get("memberId")); //会员ID
                                    task.put("leaseType",applyHouse.get("leaseType")); //房屋类型（0：出租，1：出售）
                                    task.put("applicantType",applyHouse.get("applicantType")); //申请类型（0：业主，1：POA）
                                    memberIds.add(StringUtil.trim(applyHouse.get("memberId")));
                                }
                            }
                        }
                    }

                    if(memberIds.size() > 0){
                        condition.clear();
                        condition.put("memberIds",memberIds);
                    }
                    result = memberService.selectList(new HsMember(),condition,1);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<HsMember> memberList = (List<HsMember>) result.getDataSet();
                        for(HsMember member :memberList){
                            int mid = member.getId();
                            for(Map<Object,Object> task : taskList){
                                int _mid = StringUtil.getAsInt(StringUtil.trim(task.get("memberId")));
                                if(mid == _mid){
                                    task.put("memberName",member.getNickname());
                                    task.put("memberAreaCode",member.getAreaCode());
                                    task.put("memberMoble",member.getMemberMoble());
                                    task.put("languageVersion",member.getLanguageVersion());
                                }
                            }
                        }
                    }
                    result.setDataSet(taskList);
                    result.setPageInfo(pageInfo);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 批量转单
     * @param condition
     * @return
     */
    @Override
    public ResultVo batchDispathOrder(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            String ids = StringUtil.trim(condition.get("ids"));//任务IDs
            int userId = Integer.parseInt(StringUtil.trim(condition.get("userId")));
            int taskType = Integer.parseInt(StringUtil.trim(condition.get("taskType"))); //任务类型 0:外获 1：外看
            condition.remove("taskType");
            String roleName = StringUtil.trim(condition.get("roleName"));
            if("钥匙管理员".equals(roleName)){
                condition.remove("userId");
            }
            Date date = new Date();
            //任务ids
            List<String> taskIds =Arrays.asList(ids.split(","));
            if(taskIds == null || taskIds.size()==0){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return result;
            }
            //根据任务IDS查询
            condition.put("ids",taskIds);
            result = memberService.selectList(new HsSystemUserOrderTasks(),condition,1);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }

            //查询的任务数
            List<HsSystemUserOrderTasks> taskList = (List<HsSystemUserOrderTasks>) result.getDataSet();//业务员任务集合
            if(taskList == null || taskList.size()==0){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
//                result.setMessage("没有指定的任务，无法转单");
                result.setMessage("No assigned task, no order transfer");
                return result;
            }
            if(taskType == 1) {//如果是外看，则计算转单是否提前了40分钟
                List<Integer> _taskIds = Lists.newArrayListWithCapacity(taskList.size());
                Date afterDate = new Date(date.getTime() + 40 * 60 * 1000);//40分钟过期时间
                for (HsSystemUserOrderTasks task : taskList) {
                    //查询预计完成时间
                    Date estimatedTime = task.getEstimatedTime();
                    if(afterDate.getTime() > estimatedTime.getTime()){//如果超过当前时间
                        _taskIds.add(task.getId());
                    }
                }
                if( _taskIds!=null && _taskIds.size()>0){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("Transfer failed, the order has been over 40 minutes");
//                    result.setMessage("转单失败，订单已超过40分钟");
                    result.setDataSet(_taskIds);
                    return result;
                }
            }

            List<HsSystemUserOrderTasksLog> logList = new ArrayList<>(); //任务日志记录
            List<Integer> poolIds = new ArrayList<>(); //订单池ID
            //记录要修改的任务集合
            List<HsSystemUserOrderTasks> updateTasks = Lists.newArrayList();//业务员任务集合
            HsSystemUserOrderTasks updateTask = null;
            for (HsSystemUserOrderTasks map : taskList){
                poolIds.add(map.getPoolId());
                updateTask = new HsSystemUserOrderTasks();
                updateTask.setId(map.getId());
                updateTask.setIsDel(1); //是否删除 0：未删除，1：已删除
                updateTask.setIsTransferOrder(1); //是否转单 0->未转单，1已转单
                //转单理由
                updateTask.setTransferOrderReason(Integer.parseInt(StringUtil.trim(condition.get("transferOrderReason"))));
                updateTask.setRemark(StringUtil.trim(condition.get("remark"))); //转单说明
                updateTask.setVersionNo(map.getVersionNo());
                updateTasks.add(updateTask);

                HsSystemUserOrderTasksLog userOrderTasksLog = new HsSystemUserOrderTasksLog();
                userOrderTasksLog.setPoolId(map.getPoolId()); //订单池ID
                userOrderTasksLog.setTaskId(map.getId()); //任务ID
                userOrderTasksLog.setNodeType(1); //0-创建任务->1-转单->2任务未完成->3任务完成
                userOrderTasksLog.setCreateBy(userId);
                userOrderTasksLog.setCreateTime(date);
                userOrderTasksLog.setUpdateBy(userId);
                userOrderTasksLog.setUpdateTime(date);
                userOrderTasksLog.setRemarks(StringUtil.trim(condition.get("remark")));
                userOrderTasksLog.setTransferOrderReason(Integer.parseInt(StringUtil.trim(condition.get("transferOrderReason"))));
                userOrderTasksLog.setProofPic1(StringUtil.trim(condition.get("proofPic1")));
                userOrderTasksLog.setProofPic2(StringUtil.trim(condition.get("proofPic2")));
                userOrderTasksLog.setProofPic3(StringUtil.trim(condition.get("proofPic3")));
                userOrderTasksLog.setProofPic4(StringUtil.trim(condition.get("proofPic4")));
                userOrderTasksLog.setProofPic5(StringUtil.trim(condition.get("proofPic5")));
                logList.add(userOrderTasksLog);
            }
            //用户任务集合
            if(updateTasks != null && updateTasks.size()>0){
                condition.put("tasksList",updateTasks);
            }
            //更新任务表数据
            result = memberService.batchUpdate(new HsSystemUserOrderTasks(),condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            //任务日志集合，批量新增任务日志数据
            if(logList != null && logList.size()>0){
                condition.put("logData",logList);
                //插入日志信息
                result = memberService.batchInsert(new HsSystemUserOrderTasksLog(),condition);
            }

            //线程池ID
            if(poolIds != null && poolIds.size()>0){
                //查询订单池数据
                condition.put("poolIds",poolIds);
                result = orderService.selectList(new HsSystemOrderPool(),condition,1);
                if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                    return result;
                }
                List<HsSystemOrderPool> poolList = (List<HsSystemOrderPool>) result.getDataSet();
                if(poolList != null && poolList.size()>0){
                    Date closeDate = new Date(date.getTime() + 5 * 60 * 1000);//设置抢单时间
                    //保存要修改的订单池数据
                    List<HsSystemOrderPool> data = new ArrayList<>();
                    HsSystemOrderPool pool = null;
                    //保存订单池日志
                    List<HsSystemOrderPoolLog> logData = new ArrayList<>();
                    for(HsSystemOrderPool orderPool : poolList){
                        Integer poolId = orderPool.getId();
                        pool = new HsSystemOrderPool();
                        pool.setId(poolId);
                        pool.setIsOpenOrder(1); //是否开启自动抢单 0：未开启，1：已开启
                        pool.setIsFinished(0); //是否完成 0：未完成，1：已完成
                        pool.setIsTransferOrder(1); //是否转单 0：未转单，1：已转单
                        if(taskType == 0){ //外获订单
                            //获取只系统派一次单，a彭说的
                            pool.setOpenOrderCloseTime(orderPool.getCloseTime());
//                            pool.setOpenOrderCloseTime(orderPool.getEstimatedTime());
                        }else if(taskType == 1){ //外看任务,抢单时间加五分钟
                            pool.setOpenOrderCloseTime(closeDate);
                        }else{
                            //投诉订单
                            pool.setOpenOrderCloseTime(orderPool.getEstimatedTime());
                        }
                        pool.setVersionNo(orderPool.getVersionNo());
                        data.add(pool);

                        //添加订单池日志
                        HsSystemOrderPoolLog poolLog = new HsSystemOrderPoolLog();
                        poolLog.setPoolId(poolId);
                        poolLog.setOrderType(orderPool.getOrderType()); // 0外获订单->1-外看订单->2合同订单
                        poolLog.setNodeType(2); //0-加入订单池->1-外获业务员接单->2业务员转单->3已完成->3未完成
                        poolLog.setCreateBy(userId);
                        poolLog.setCreateTime(date);
                        poolLog.setUpdateBy(userId);
                        poolLog.setUpdateTime(date);
                        logData.add(poolLog);
                    }
                    condition.put("data",data);
                    condition.put("logData",logData);
                }
                result = orderService.batchUpdate(new HsSystemOrderPool(),condition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }


    /**
     * 批量转单
     * @param condition
     * @return
     */
    public ResultVo batchDispathOrder1(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();

        try {
            String ids = StringUtil.trim(condition.get("ids"));//任务IDs
            int userId = Integer.parseInt(StringUtil.trim(condition.get("userId")));
            int taskType = Integer.parseInt(StringUtil.trim(condition.get("taskType"))); //任务类型 0:外获 1：外看
            Date date = new Date();
            List<String> idList = Lists.newArrayList();
            List<String> timeList =  Lists.newArrayList();//时间集合
            List<HsSystemUserOrderTasks> tasksList = new ArrayList<>(); //业余员任务集合
            List<HsSystemUserOrderTasksLog> logList = new ArrayList<>(); //日志集合
            List<String> poolIds = new ArrayList<>(); //线程池ID
            String [] idArray = ids.split(",");
            idList = Arrays.asList(idArray);
            if(idList == null || idList.size()==0){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return result;
            }

            List<HsSystemUserOrderTasks> taskList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(taskType == 1){//如果是外看，则计算转单是否提前了40分钟
                String times = StringUtil.trim(condition.get("times")); //时间 时间以逗号分隔
                String [] timeArray = times.split(",");
                timeList = Arrays.asList(timeArray);

                //任务开始前四十分钟无法转单
                for (String time :timeList){
                    //转单前四十分钟时间
                    String minusTime =  DateUtil.getTimeByMinute(time,40,0);
                    if(time.equals(minusTime)){
                        result.setResult(ResultConstant.BUS_USER_TRANSFER_ORDER_FAILURE);
                        result.setMessage(ResultConstant.BUS_USER_TRANSFER_ORDER_FAILURE_VALUE);
                        return result;
                    }
                }

            }
            condition.put("ids",idList);

            result = memberService.selectList(new HsSystemUserOrderTasks(),condition,1);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                taskList = (List<HsSystemUserOrderTasks>) result.getDataSet();
                if(taskList != null){
                    for (HsSystemUserOrderTasks map : taskList){
                        poolIds.add(StringUtil.trim(map.getPoolId()));
                        HsSystemUserOrderTasks userOrderTasks = new HsSystemUserOrderTasks();
                        userOrderTasks.setIsDel(1); //是否删除 0：未删除，1：已删除
                        userOrderTasks.setId(map.getId());
                        userOrderTasks.setIsTransferOrder(1); //是否转单 0->未转单，1已转单
                        //转单理由
                        userOrderTasks.setTransferOrderReason(Integer.parseInt(StringUtil.trim(condition.get("transferOrderReason"))));
                        userOrderTasks.setRemark(StringUtil.trim(condition.get("remark"))); //转单说明
                        userOrderTasks.setVersionNo(map.getVersionNo());
                        tasksList.add(userOrderTasks);

                        HsSystemUserOrderTasksLog userOrderTasksLog = new HsSystemUserOrderTasksLog();
                        userOrderTasksLog.setPoolId(map.getPoolId()); //订单池ID
                        userOrderTasksLog.setTaskId(map.getId()); //任务ID
                        userOrderTasksLog.setNodeType(1); //0-创建任务->1-转单->2任务未完成->3任务完成
                        userOrderTasksLog.setCreateBy(userId);
                        userOrderTasksLog.setCreateTime(date);
                        userOrderTasksLog.setUpdateBy(userId);
                        userOrderTasksLog.setUpdateTime(date);
                        userOrderTasksLog.setRemarks(StringUtil.trim(condition.get("remark")));
                        userOrderTasksLog.setTransferOrderReason(Integer.parseInt(StringUtil.trim(condition.get("transferOrderReason"))));
                        userOrderTasksLog.setProofPic1(StringUtil.trim(condition.get("proofPic1")));
                        userOrderTasksLog.setProofPic2(StringUtil.trim(condition.get("proofPic2")));
                        userOrderTasksLog.setProofPic3(StringUtil.trim(condition.get("proofPic3")));
                        userOrderTasksLog.setProofPic4(StringUtil.trim(condition.get("proofPic4")));
                        userOrderTasksLog.setProofPic5(StringUtil.trim(condition.get("proofPic5")));
                        logList.add(userOrderTasksLog);
                    }
                }
            }
            //用户任务集合
            if(tasksList != null){
                condition.put("tasksList",tasksList);
            }

            //更新任务表数据
            result = memberService.batchUpdate(new HsSystemUserOrderTasks(),condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //任务日志集合，批量新增任务日志数据
                if(logList != null){
                    condition.put("logData",logList);
                }
                //插入日志信息
                result = memberService.batchInsert(new HsSystemUserOrderTasksLog(),condition);

            }

            //线程池ID
            if(poolIds != null){
                //查询订单池数据
                condition.put("poolIds",poolIds);
                result = orderService.selectList(new HsSystemOrderPool(),condition,1);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<HsSystemOrderPool> poolList = (List<HsSystemOrderPool>) result.getDataSet();
                    if(poolList != null){
                        List<HsSystemOrderPool> data = new ArrayList<>();
                        List<HsSystemOrderPoolLog> logData = new ArrayList<>();
                        for(HsSystemOrderPool orderPool : poolList){
                            int poolId = orderPool.getId();
                            HsSystemOrderPool pool = new HsSystemOrderPool();

                            for(HsSystemUserOrderTasks map : taskList){
                                int _poolId = map.getPoolId();
                                //判断任务表中的订单池ID与查询的订单池ID是否相同，相同则根据是否自动派单，设置订单池表是否开启自动抢单
                                if(poolId == _poolId){
                                    if(map.getIsSystem() == 1){ //是否自动派单，0：自动派单，1：手动抢单
                                        pool.setIsOpenOrder(1); //是否开启自动抢单 0：未开启，1：已开启
                                        break;
                                    }
                                }
                            }

                            pool.setIsFinished(0); //是否完成 0：未完成，1：已完成
                            pool.setIsTransferOrder(1); //是否转单 0：未转单，1：已转单
                            if(taskType == 0){ //外获订单
                                pool.setOpenOrderCloseTime(date);
                            }else if(taskType == 1){ //外看任务,抢单时间加五分钟
                                String closeTimeStr = DateUtil.getTimeByMinute(sdf.format(new Date()),5,0); //字符串日期抢单时间倒计时
                                pool.setOpenOrderCloseTime( sdf.parse(closeTimeStr));
                            }
                            pool.setId(poolId);
                            pool.setVersionNo(orderPool.getVersionNo());
                            data.add(pool);

                            HsSystemOrderPoolLog poolLog = new HsSystemOrderPoolLog();
                            poolLog.setPoolId(poolId);
                            poolLog.setOrderType(orderPool.getOrderType()); // 0外获订单->1-外看订单->2合同订单
                            poolLog.setNodeType(2); //0-加入订单池->1-外获业务员接单->2业务员转单->3已完成->3未完成
                            poolLog.setCreateBy(userId);
                            poolLog.setCreateTime(date);
                            poolLog.setUpdateBy(userId);
                            poolLog.setUpdateTime(date);
                            logData.add(poolLog);
                        }

                        condition.put("data",data);
                        condition.put("logData",logData);
                    }
                }

                result = orderService.batchUpdate(new HsSystemOrderPool(),condition);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 查询外货任务详情信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo outGainOrderDetail(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int applyId = Integer.parseInt(StringUtil.trim(condition.get("applyId")));
            int poolId = Integer.parseInt(StringUtil.trim(condition.get("poolId"))); //订单池ID
            result = housesService.select(applyId,new HsOwnerHousingApplication());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsOwnerHousingApplication ownerHousingApplication = (HsOwnerHousingApplication) result.getDataSet();
                condition.put("languageVersion",ownerHousingApplication.getLanguageVersion()); //语言
                condition.put("leaseType",ownerHousingApplication.getLeaseType()); //房源属性（0：出租，1：出售）

                ResultVo poolResult = orderService.select(poolId,new HsSystemOrderPool());
                if(poolResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    HsSystemOrderPool pool = (HsSystemOrderPool) poolResult.getDataSet();
                    if(pool != null){
                        Date date = new Date();
                        Date estimatedTime = pool.getEstimatedTime();
                        Date closeTime = pool.getCloseTime();
                        long time2 = closeTime.getTime() - date.getTime();
                        condition.put("closeTimeCountDown",time2/1000);
                        condition.put("houseId",pool.getHouseId());

                        long time1 = estimatedTime.getTime() - date.getTime();
                        condition.put("estimatedTimeCountDown",time1/1000);

                        condition.put("appointmentDoorTime",pool.getEstimatedTime());
                        condition.put("closeTime",closeTime);
                        condition.put("estimatedTime",estimatedTime);
                        condition.put("appointmentMeetPlace",pool.getAppointmentMeetPlace());
                    }
                }
                /*获取任务详情*/
                Integer isArrive = 0;
                int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId")));
                ResultVo taskResultVo = memberService.select(taskId, new HsSystemUserOrderTasks());
                if(taskResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    HsSystemUserOrderTasks orderTasks = (HsSystemUserOrderTasks) taskResultVo.getDataSet();
                    if(orderTasks != null){
                        //是否到达0:未到达，1：已到达
                        isArrive = orderTasks.getIsArrive();
                    }


                }
                condition.put("isArrive",isArrive);

                result = memberService.select(ownerHousingApplication.getMemberId(),new HsMember());
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    HsMember member = (HsMember) result.getDataSet();
                    condition.put("memberName",member.getNickname());
                    condition.put("memberAreaCode",member.getAreaCode());
                    condition.put("memberMoble",member.getMemberMoble());
                }
            }

            result.setDataSet(condition);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 新增房源评论
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo addHouseEvaluation(HsHouseEvaluation evaluation) {
        ResultVo result = null ;
        try {
            result = housesService.insert(evaluation);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setDataSet(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }


    /**
     * 操作用户订单任务（到达客户，完成看房）
     * @param condition
     * @return
     */
    @Override
    public ResultVo operateUserOrderTask(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try {
            Integer id = Integer.parseInt(StringUtil.trim(condition.get("id"))); //任务ID
            int memberId = Integer.parseInt(StringUtil.trim(condition.get("memberId"))); //业务员ID
            String isFinished = StringUtil.trim(condition.get("isFinished")); //是否完成看房
            Date date = new Date();
            result = memberService.select(id,new HsSystemUserOrderTasks());
            HsSystemUserOrderTasks systemUserOrderTasks = (HsSystemUserOrderTasks) result.getDataSet();
            if(systemUserOrderTasks==null){//查询不到任务
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            //获取预计完成时间
            Date estimatedTime = systemUserOrderTasks.getEstimatedTime();
            if(estimatedTime.getTime()<=date.getTime()){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Has exceeded the expected completion time");
//                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"已经超过预计完成时间");
            }
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            HsSystemUserOrderTasks updateUserOrderTasks = new HsSystemUserOrderTasks();
            updateUserOrderTasks.setId(systemUserOrderTasks.getId()); //任务ID
            if(StringUtil.hasText(isFinished)){
                //查询钥匙是否在平台
                Integer houseId = systemUserOrderTasks.getHouseId();
                Map<Object,Object> queryFilter = Maps.newHashMap();
                queryColumn.add("ID houseId");//用户ID
                queryColumn.add("HAVE_KEY haveKey");//是否有钥匙：0>无(在业主),1有(在平台)
                queryFilter.put("queryColumn",queryColumn);
                queryFilter.put("id",houseId);
                ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, queryFilter);
                if(houseVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return houseVo;
                }
                List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
                if(houseList == null || houseList.size() !=1){
                    return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                }
                Map<Object,Object> houseMap = houseList.get(0);
                int haveKey = StringUtil.getAsInt(StringUtil.trim(houseMap.get("haveKey")),0);
                if(haveKey == 1){//如果钥匙在平台，判断区域长是否先完成送钥匙任务，再处理
                    //
                    int asInt = StringUtil.getAsInt(StringUtil.trim(systemUserOrderTasks.getStandby2()), 0);
                    if(asInt == 0){
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Please get the key from the area manager first");
//                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"请先从区域长中获取钥匙");
                    }
                }

                updateUserOrderTasks.setIsFinished(1); //是否完成 0：未完成，1：已完成
                updateUserOrderTasks.setRemark("外看业务员完成看房");
            }
            if(StringUtil.hasText(StringUtil.trim(condition.get("isArrive")))){
                updateUserOrderTasks.setIsArrive(1); //是否到达0:未到达，1：已到达
                updateUserOrderTasks.setRemark("业务员到达预约地点");
            }
            updateUserOrderTasks.setVersionNo(0);
            result = memberService.update(updateUserOrderTasks);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){

                HsSystemUserOrderTasksLog log = new HsSystemUserOrderTasksLog();
                log.setTaskId(systemUserOrderTasks.getId()); //任务ID
                log.setPoolId(systemUserOrderTasks.getPoolId()); //订单池ID
                if(StringUtil.hasText(isFinished)){
                    log.setNodeType(3); //该日志所关联的节点类型（0-创建任务->1-转单->2任务未完成->3任务完成->4到达客户）
                }
                if(StringUtil.hasText(StringUtil.trim(condition.get("isArrive")))){
                    log.setNodeType(4); //该日志所关联的节点类型（0-创建任务->1-转单->2任务未完成->3任务完成->4到达客户）
                }
                log.setCreateBy(memberId);
                log.setCreateTime(date);
                log.setUpdateBy(memberId);
                log.setUpdateTime(date);
                result = memberService.insert(log);
                if("1".equals(isFinished)){//修改会员预约看房表完成

                    int orderPoolId = systemUserOrderTasks.getPoolId();//订单池ID
                    //将订单池状态改成已完成
                    HsSystemOrderPool orderPool = new HsSystemOrderPool();
                    ResultVo orderPoolVo = orderService.select(orderPoolId,orderPool);
                    HsSystemOrderPool oldOrderPool  = (HsSystemOrderPool) orderPoolVo.getDataSet();
                    orderPool.setId(orderPoolId);
                    orderPool.setIsFinished(5);//是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
                    orderPool.setRemark("外看业务员完成看房任务");
                    orderPool.setUpdateTime(date);
                    orderPool.setVersionNo(oldOrderPool.getVersionNo());
                    orderPoolVo =orderService.update(orderPool);
                    if(orderPoolVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//修改成功添加日志信息
                        HsSystemOrderPoolLog orderPoolLog = new HsSystemOrderPoolLog();
                        orderPoolLog.setPoolId(orderPoolId);
                        orderPoolLog.setOrderType(orderPool.getOrderType());
                        orderPoolLog.setOperatorType(5);
                        orderPoolLog.setCreateTime(date);
                        orderPoolLog.setPostTime(date);
                        orderPoolLog.setRemarks("外看业务员完成看房任务");
                        orderService.update(orderPoolLog);
                    }


                    condition.put("poolId",systemUserOrderTasks.getPoolId()); //订单池ID
                    condition.put("isFinish",isFinished); //是否完成l 0：未完成 1：已完成
                    result = memberService.updateHousingApplicationByPoolId(condition);

                    //=========================添加积分=============================
                    //重置积分规则缓存
                    List<Map<Object, Object>> goldList = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                    String cacheKey = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                    List<Map<Object, Object>> goldList = JsonUtil.parseJSON2List(cacheKey);
                    List<Integer> userIds = Lists.newArrayList();//保存用户Ids
                    Integer userId = systemUserOrderTasks.getUserId();
                    userIds.add(userId);
                    List<HsUserGoldLog> userGoldLogs = Lists.newArrayList();//保存业务员积分日志
                    Map<Object, Object> waikanGoldMap = null;//保存外看计算规则
                    for (Map<Object, Object> gold : goldList) {
                        if(3==StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))){//外获任务
                            waikanGoldMap = gold;
                            break;
                        }
                    }
                    //保存用户积分
                    HsUserGoldLog userGoldLog = new HsUserGoldLog();
                    userGoldLog.setUserId(userId);
                    userGoldLog.setTaskId(systemUserOrderTasks.getId());
                    userGoldLog.setGoldRuleId(StringUtil.getAsInt(StringUtil.trim(waikanGoldMap.get("id"))));
                    int score = StringUtil.getAsInt(StringUtil.trim(waikanGoldMap.get("score")));
                    int isAddSubtract = StringUtil.getAsInt(StringUtil.trim(waikanGoldMap.get("isAddSubtract")));//控制加分还是减分 0 ：加分 1减分
                    score = isAddSubtract == 0 ? score : score*-1;
                    userGoldLog.setGold(score);
                    userGoldLog.setCreateTime(date);
                    userGoldLog.setRemark("任务超时,扣除"+score);
                    userGoldLogs.add(userGoldLog);

                    condition.clear();

                    queryColumn.clear();
                    queryColumn.add("ID userId");//用户ID
                    queryColumn.add("GOLD gold");//积分帐户
                    condition.put("queryColumn",queryColumn);
                    condition.put("ids",userIds);
                    ResultVo userVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
                    if(userVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> userList = (List<Map<Object, Object>>) userVo.getDataSet();
                        List<HsSysUser> updateUser = Lists.newArrayListWithCapacity(userList.size());
                        HsSysUser _user = null;
                        for (Map<Object, Object> user : userList) {
                            _user = new HsSysUser();
                            int _userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                            int _gold = StringUtil.getAsInt(StringUtil.trim(user.get("gold")));

                            //获取总共要修改的积分数
                            _user.setId(_userId);
                            _user.setGold(_gold+score);
                            _user.setUpdateTime(date);
                            updateUser.add(_user);
                        }
                        if(updateUser!=null && updateUser.size()>0){
                            condition.clear();
                            condition.put("data",updateUser);
                            ResultVo updateVo = memberService.batchUpdate(_user, condition);
                            if(updateVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                condition.clear();
                                condition.put("data",userGoldLogs);
                                memberService.batchInsert(userGoldLog, condition);
                            }
                        }
                    }
                    //=========================添加积分 end =============================
                }

                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//添加考勤记录
                    if(StringUtil.hasText(StringUtil.trim(condition.get("isArrive")))){
                        HsUserAttendanceSheet hsUserAttendanceSheet = new HsUserAttendanceSheet();
                        hsUserAttendanceSheet.setUserId(memberId);
                        hsUserAttendanceSheet.setPostTime(date);
                        hsUserAttendanceSheet.setLatitude(StringUtil.trim(condition.get("latitude")));//纬度
                        hsUserAttendanceSheet.setLongitude(StringUtil.trim(condition.get("longitude"))); //经度
                        hsUserAttendanceSheet.setPostLocation(StringUtil.trim(condition.get("location"))); //打卡位置
                        ResultVo clockResult = memberService.clockIn(hsUserAttendanceSheet);
                        if(clockResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                            return clockResult;
                        }
                    }
                    result.setDataSet(null);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 取消预约操作
     * @param condition
     * @return
     */
    @Override
    public ResultVo cancelAppointment(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            Integer id = Integer.parseInt(StringUtil.trim(condition.get("taskId"))); //任务ID
            Integer taskType = Integer.parseInt(StringUtil.trim(condition.get("taskType"))); //任务类型
            int memberId = Integer.parseInt(StringUtil.trim(condition.get("memberId"))); //业务员ID
            int cancelType = Integer.parseInt(StringUtil.trim(condition.get("cancelType"))); //取消类型（0：业主取消，1：租客/买家）
            int feedbackType = Integer.parseInt(StringUtil.trim(condition.get("feedbackType"))); //反馈类型
            String feedbackDesc = StringUtil.trim(condition.get("feedbackDesc")); //反馈描述

            Date date = new Date();
            result = memberService.select(id,new HsSystemUserOrderTasks());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemUserOrderTasks systemUserOrderTasks = (HsSystemUserOrderTasks) result.getDataSet();
                int appointmentCount = 0; //预约看房信息条数
                if(taskType == 1){ //外看任务
                    Map<Object,Object> queryFilter = new HashMap<>();
                    queryFilter.put("poolId",systemUserOrderTasks.getPoolId()); //订单池ID
                    queryFilter.put("isFinish",0); //未完成看房
                    queryFilter.put("isCancel",0); //未取消看房
                    // 为外看任务时，查询该订单池对应的数据的条数
                    ResultVo appointmentResult = memberService.selectList(new HsMemberHousingApplication(),queryFilter,0);
                    if(appointmentResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> appointmentList = (List<Map<Object, Object>>) appointmentResult.getDataSet();
                        appointmentCount = appointmentList.size();
                    }
                }

                //为外获任务，或者为外看任务，但是只有一条预约信息时，该任务直接取消关闭,为外看任务时，是业主取消预约，该任务直接关闭
                if(taskType == 0 || (taskType == 1 && appointmentCount == 1) || (taskType == 1 && cancelType == 0)){
                    HsSystemUserOrderTasks updateUserOrderTasks = new HsSystemUserOrderTasks();
                    updateUserOrderTasks.setId(systemUserOrderTasks.getId()); //任务ID
                    if(cancelType == 0){ //业主取消预约
                        updateUserOrderTasks.setIsFinished(2); //是否完成0:未完成，1：已完成，2：业主取消预约，3：租客/卖家取消预约
                    }else{
                        updateUserOrderTasks.setIsFinished(3);
                    }
                    updateUserOrderTasks.setIsDel(1); //是否删除 0：未删除，1：已删除
                    updateUserOrderTasks.setVersionNo(0);
                    updateUserOrderTasks.setTransferOrderReason(feedbackType); //反馈原因
                    updateUserOrderTasks.setRemark(feedbackDesc); //描述
                    result = memberService.update(updateUserOrderTasks); //修改用户任务信息
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsSystemUserOrderTasksLog log = new HsSystemUserOrderTasksLog();
                        log.setTaskId(systemUserOrderTasks.getId()); //任务ID
                        log.setPoolId(systemUserOrderTasks.getPoolId()); //订单池ID
                        if(cancelType == 0){
                            log.setNodeType(5); //该日志所关联的节点类型（0-创建任务->1-转单->2任务未完成->3任务完成->4到达客户->5业主取消预约->6租客/买家取消预约）
                        }else{
                            log.setNodeType(6);
                        }
                        log.setCreateBy(memberId);
                        log.setCreateTime(date);
                        log.setUpdateBy(memberId);
                        log.setUpdateTime(date);
                        log.setTransferOrderReason(feedbackType);
                        log.setRemarks(feedbackDesc);
                        log.setProofPic1(StringUtil.trim(condition.get("proofPic1"))); //凭证图片1
                        log.setProofPic2(StringUtil.trim(condition.get("proofPic2"))); //凭证图片1
                        log.setProofPic3(StringUtil.trim(condition.get("proofPic3"))); //凭证图片1
                        log.setProofPic4(StringUtil.trim(condition.get("proofPic4"))); //凭证图片1
                        log.setProofPic5(StringUtil.trim(condition.get("proofPic5"))); //凭证图片1
                        result = memberService.insert(log); //新增任务日志信息
                        if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            //查询订单池数据
                            ResultVo selectPoolVo = orderService.select(systemUserOrderTasks.getPoolId(), new HsSystemOrderPool());
                            HsSystemOrderPool selectPool = (HsSystemOrderPool) selectPoolVo.getDataSet();

                            HsSystemOrderPool updatePool = new HsSystemOrderPool();
                            updatePool.setId(systemUserOrderTasks.getPoolId());
                            if(cancelType == 0){
                                updatePool.setIsFinished(2); //是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约
                            }else{
                                updatePool.setIsFinished(3);
                            }
                            updatePool.setVersionNo(selectPool.getVersionNo());

                            result = orderService.update(updatePool); //修改订单池
                            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                HsSystemOrderPoolLog poolLog = new HsSystemOrderPoolLog();
                                poolLog.setPoolId(systemUserOrderTasks.getPoolId());
                                poolLog.setOrderType(systemUserOrderTasks.getTaskType()); //订单类型 0外获订单->1-外看订单->2合同订单
                                if(cancelType == 0){
                                    poolLog.setNodeType(3);//（0-加入订单池->1-外获业务员接单->2业务员转单->3业主取消预约->4租客/买家取消预约->6已完成->7未完成）
                                }else{
                                    poolLog.setNodeType(4);
                                }
                                poolLog.setCreateBy(memberId);
                                poolLog.setCreateTime(date);
                                poolLog.setUpdateBy(memberId);
                                poolLog.setUpdateTime(date);

                                result = orderService.insert(poolLog); //新增订单池日志信息

                                if(taskType == 1){ //外看任务，更新预约看房申请表
                                    condition.put("isCancel",1); //是否取消 0：未取消 1：已取消
                                    condition.put("poolId",systemUserOrderTasks.getPoolId()); //订单池ID
                                    result = memberService.updateHousingApplicationByPoolId(condition);
                                }
                            }
                        }

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 查询合同类订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo getContractOrdersList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //查询当前业务员的
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            condition.put("queryColumn",queryColumn);
            //查询业务员任务列表信息
            vo =  memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> orderTaskList = (List<Map<Object,Object>>) vo.getDataSet();
                if(orderTaskList!=null && orderTaskList.size()>0){
                    List<Integer> houseIds = new ArrayList<>();//房源Ids
                    List<Integer> poolIds = new ArrayList<>();//订单池Ids
                    Date date = new Date();
                    for (Map<Object,Object> orderTask : orderTaskList) {
                        houseIds.add(StringUtil.getAsInt(StringUtil.trim(orderTask.get("houseId"))));
                        poolIds.add(StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId"))));
                    }

                    //查询订单池当中上门时间和过期时间
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("ID poolId");
                    queryColumn.add("ORDER_CODE orderCode");//订单编码
                    queryColumn.add("IS_FINISHED isFinished");//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
                    queryColumn.add("IS_OPEN_ORDER isOpenOrder");//是否开启抢单0:未开启，1：开启
                    queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                    queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
                    queryColumn.add("CLOSE_TIME closeTime");//关单时间
                    condition.put("queryColumn",queryColumn);
                    condition.put("poolIds",poolIds);
                    ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == orderVo.getResult()) {//查询成功
                        //订单池数据
                        List<Map<Object,Object>> ordersList = (List<Map<Object,Object>>) orderVo.getDataSet();
                        if(ordersList!=null && ordersList.size()>0 ){
                            //将系统订单池与业务员任务表进行关联
                            for (Map<Object,Object> orderTask : orderTaskList) {
                                int poolId = StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId")));
                                for (Map<Object, Object> order : ordersList) {
                                    int _poolId = StringUtil.getAsInt(StringUtil.trim(order.get("poolId")));
                                    if(poolId == _poolId){
                                        Date estimatedTime = (Date) order.get("estimatedTime");
                                        long time1 = estimatedTime.getTime() - date.getTime();
                                        order.put("estimatedTimeCountDown",time1/1000);
                                        Date closeTime = (Date) order.get("closeTime");
                                        long time2 = closeTime.getTime() - date.getTime();
                                        order.put("closeTimeCountDown",time2/1000);
                                        orderTask.putAll(order);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("ID houseId");
                    queryColumn.add("MEMBER_ID memberId");//业主ID
                    queryColumn.add("APPLY_ID applyId");//业主ID
                    queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
//                    queryColumn.add("CONTACTS contacts");//业主联系人
//                    queryColumn.add("PHONE_NUMBER phoneNumber");//业主手机号
                    condition.put("queryColumn",queryColumn);
                    condition.put("houseIds",houseIds);
                    //查询房源信息
                    ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == housesVo.getResult()) {//查询成功
                        List<Map<Object,Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
                        List<Integer> memberIds = new ArrayList<>();//业主Ids
                        for (Map<Object,Object> house : houses) {
                            memberIds.add(StringUtil.getAsInt(StringUtil.trim(house.get("memberId"))));
                        }
                        if(memberIds.size() > 0){
                            condition.clear();
                            queryColumn.clear();
                            queryColumn.add("ID memberId");
                            queryColumn.add("NICKNAME ownerName");//业主名称
                            queryColumn.add("AREA_CODE areaCode");//手机号
                            queryColumn.add("MEMBER_MOBLE ownerMoble");//业主手机号
                            condition.put("queryColumn",queryColumn);
                            condition.put("memberIds",memberIds);
                            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                            if(ResultConstant.SYS_REQUIRED_SUCCESS == memberVo.getResult()) {//查询成功
                                List<Map<Object,Object>> members = (List<Map<Object, Object>>) memberVo.getDataSet();
                                if(houses!=null){
                                    //将业主手机信息保存到房源信息当中
                                    houses.forEach((house) -> {
                                        int memberId = StringUtil.getAsInt(StringUtil.trim(house.get("memberId")));
                                        members.forEach((member) -> {
                                            int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                                            if(memberId == _memberId){
                                                house.putAll(member);
                                            }
                                        });
                                    });
                                    for (Map<Object,Object> orderTask : orderTaskList) {
                                        int _houseId  = StringUtil.getAsInt(StringUtil.trim(orderTask.get("houseId")));
                                        for (Map<Object, Object> house : houses) {
                                            int houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                                            if (_houseId == houseId){
                                                orderTask.putAll(house);
                                                break;
                                            }
                                        }
                                    }
                                }
                                vo.setDataSet(orderTaskList);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 查询外获业务员合同类派送记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo getContractOrderRecordsList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //查询当前业务员的
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            condition.put("queryColumn",queryColumn);
            //查询业务员任务列表信息
            vo =  memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> orderTaskList = (List<Map<Object,Object>>) vo.getDataSet();
                if(orderTaskList!=null && orderTaskList.size()>0){
                    List<Integer> houseIds = new ArrayList<>();//房源Ids
                    List<Integer> poolIds = new ArrayList<>();//订单池Ids
                    for (Map<Object,Object> orderTask : orderTaskList) {
                        houseIds.add(StringUtil.getAsInt(StringUtil.trim(orderTask.get("houseId"))));
                        poolIds.add(StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId"))));
                    }

                    //查询订单池当中上门时间和过期时间
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("ID poolId");
                    queryColumn.add("ORDER_CODE orderCode");//订单编码
                    queryColumn.add("IS_FINISHED isFinished");//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
                    queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                    queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
                    condition.put("queryColumn",queryColumn);
                    condition.put("poolIds",poolIds);
                    ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == orderVo.getResult()) {//查询成功
                        //订单池数据
                        List<Map<Object,Object>> ordersList = (List<Map<Object,Object>>) orderVo.getDataSet();
                        if(ordersList!=null && ordersList.size()>0 ){
                            //将系统订单池与业务员任务表进行关联
                            for (Map<Object,Object> orderTask : orderTaskList) {
                                int poolId = StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId")));
                                for (Map<Object, Object> order : ordersList) {
                                    int _poolId = StringUtil.getAsInt(StringUtil.trim(order.get("poolId")));
                                    if(poolId == _poolId){
                                        orderTask.putAll(order);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("ID houseId");
                    queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
                    //房源名称
                    queryColumn.add("HOUSE_NAME houseName");
                    //社区
                    queryColumn.add("COMMUNITY community");
//                    queryColumn.add("CONTACTS contacts");//业主联系人
//                    queryColumn.add("PHONE_NUMBER phoneNumber");//业主手机号
                    condition.put("queryColumn",queryColumn);
                    condition.put("houseIds",houseIds);
                    //查询房源信息
                    ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == housesVo.getResult()) {//查询成功
                        List<Map<Object,Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
                        List<Integer> memberIds = new ArrayList<>();//业主Ids
                        for (Map<Object,Object> house : houses) {
                            memberIds.add(StringUtil.getAsInt(StringUtil.trim(house.get("memberId"))));
                        }
                        if(memberIds.size() > 0){
                            condition.clear();
                            queryColumn.clear();
                            queryColumn.add("ID memberId");
                            queryColumn.add("NICKNAME ownerName");//业主名称
                            queryColumn.add("AREA_CODE areaCode");//手机号
                            queryColumn.add("MEMBER_MOBLE ownerMoble");//业主手机号
                            condition.put("queryColumn",queryColumn);
                            condition.put("memberIds",memberIds);
                            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                            if(ResultConstant.SYS_REQUIRED_SUCCESS == memberVo.getResult()) {//查询成功
                                List<Map<Object,Object>> members = (List<Map<Object, Object>>) memberVo.getDataSet();
                                if(houses!=null){
                                    //将业主手机信息保存到房源信息当中
                                    houses.forEach((house) -> {
                                        int memberId = StringUtil.getAsInt(StringUtil.trim(house.get("memberId")));
                                        members.forEach((member) -> {
                                            int _memberId = StringUtil.getAsInt(StringUtil.trim(member.get("memberId")));
                                            if(memberId == _memberId){
                                                house.putAll(member);
                                            }
                                        });
                                    });
                                    for (Map<Object,Object> orderTask : orderTaskList) {
                                        int _houseId  = StringUtil.getAsInt(StringUtil.trim(orderTask.get("houseId")));
                                        for (Map<Object, Object> house : houses) {
                                            int houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                                            if (_houseId == houseId){
                                                orderTask.putAll(house);
                                                break;
                                            }
                                        }
                                    }
                                }
                                vo.setDataSet(orderTaskList);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取已上传房源列表信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getUploadedHousingList(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            //查询未审核通过(未完成)的外获任务
            Map<Object,Object> queryFilter = new HashMap<>();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("USER_ID userId");
            condition.put("queryColumn",queryColumn);
            condition.put("taskType",0); //外获任务
            condition.put("queryCurrentMonth",1); //查询当月的任务数
            condition.put("needSort",1); //需要排序
            condition.put("orderBy","UPDATE_TIME"); //需要排序
            condition.put("orderDirection","desc"); //需要排序
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> poolIds = new ArrayList<>(); //订单池Ids
                List<String> houseIds = new ArrayList<>(); //房源ID
                //获取poolId
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                if(taskList != null && taskList.size() > 0){
                    for (Map<Object,Object> map : taskList){
                        poolIds.add(StringUtil.trim(map.get("poolId")));
                        houseIds.add(StringUtil.trim(map.get("houseId"))); //房源ID
                    }
                    queryColumn.clear();
                    queryColumn.add("ID poolId");
                    queryColumn.add("ORDER_CODE orderCode"); //订单编号
                    queryFilter.put("queryColumn",queryColumn);
                    queryFilter.put("poolIds",poolIds);
                    ResultVo orderPoolResult = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,queryFilter);
                    if(orderPoolResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        return orderPoolResult;
                    }
                    List<Map<Object,Object>> poolList = (List<Map<Object, Object>>) orderPoolResult.getDataSet();
                    if(poolList != null && poolList.size() > 0){
                        for (Map<Object,Object> pool : poolList){
                            int poolId = Integer.parseInt(StringUtil.trim(pool.get("poolId"))); //订单池ID
                            for(Map<Object,Object> task : taskList){
                                int poolId2 = Integer.parseInt(StringUtil.trim(task.get("poolId"))); //订单池ID
                                if(poolId == poolId2){
                                    task.put("orderCode",pool.get("orderCode"));
                                    break;
                                }
                            }
                        }
                    }
                    queryColumn.clear();
                    queryColumn.add("ID houseId");
                    queryColumn.add("HOUSE_NAME houseName");
                    queryColumn.add("CITY city");
                    queryColumn.add("COMMUNITY community");
                    queryColumn.add("SUB_COMMUNITY subCommunity");
                    queryColumn.add("HOUSE_ACREAGE houseAcreage");
                    queryColumn.add("ADDRESS address");
                    queryColumn.add("HOUSE_RENT houseRent");
                    queryColumn.add("IS_CHECK isCheck");
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                    queryColumn.add("LEASE_TYPE leaseType");
                    queryColumn.add("APPLICANT_TYPE applicantType");
                    queryFilter.put("queryColumn",queryColumn);
                    queryFilter.put("houseIds",houseIds);
                    ResultVo houseResult = housesService.selectCustomColumnNamesList(HsMainHouse.class,queryFilter);
                    if(houseResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        return houseResult;
                    }
                    List<Map<Object,Object>> houses = (List<Map<Object, Object>>) houseResult.getDataSet();
                    if(houses!=null && houses.size()>0){
                        for(Map<Object,Object> task : taskList){
                            int houseId2 = Integer.parseInt(StringUtil.trim(task.get("houseId")));
                            for(int i = houses.size()-1 ;i>=0 ;i--){
                                Map<Object,Object> house = houses.get(i);
                                int houseId = Integer.parseInt(StringUtil.trim(house.get("houseId")));
                                if(houseId == houseId2){
//                                    house.put("orderCode",task.get("orderCode"));
                                    task.putAll(house);
                                    break;
                                }
                            }
                            String houseMainImg = StringUtil.trim(task.get("houseMainImg"));
                            if(StringUtil.hasText(houseMainImg)){
                                task.put("houseMainImg",ImageUtil.IMG_URL_PREFIX + houseMainImg);
                            }
                        }
                    }
                    if(taskList == null || taskList.size()==0){
                        return ResultVo.success();
                    }
                    result.setDataSet(taskList);
                }
            }
        } catch (Exception e) {
            logger.error("Error to encode MemberAdminManagerImpl getUploadedHousingList", e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取外获业务员个人绩效
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOutGainPersonalPerformance(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();

        try {
            condition.put("isTransferOrder",0); //未转单
            condition.put("isDel",0); //是否删除 0 ：未删除，1：已删除
            result = memberService.selectPersonalPerformance(condition); //查询上传房源数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("uploadHouseNum",result.getDataSet());
            }

            condition.remove("isDel");
            condition.remove("isTransferOrder");
            condition.put("isArrive",1); //已到达客户
            result = memberService.selectPersonalPerformance(condition); //查询到达客户数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("arriveCustomerNum",result.getDataSet());
            }

            condition.remove("isArrive");
            condition.put("isFinished",1); //是否完成 1：已完成
            result = memberService.selectPersonalPerformance(condition); //查询成功上传数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("uploadSuccessNum",result.getDataSet());
            }

            condition.remove("isFinished");
            condition.put("isTransferOrder",1); //是否转单（0：未转单，1：已转单）
            result = memberService.selectPersonalPerformance(condition); //查询转单数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("transferOrderNum",result.getDataSet());
            }

            if(resultMap != null){
                result.setDataSet(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }


    @Override
    public ResultVo updateSysUser(Map<Object,Object> condition) {
        //根据当前用户信息判断旧密码是否相同，相同则进行修改，否则失败
        ResultVo result = new ResultVo();
        try {
            int userId = Integer.parseInt(StringUtil.trim(condition.get("userId")));
            String oldPassword = StringUtil.trim(condition.get("oldPassword")); //旧密码
            String PASSWORD_HASHITERATIONS = StringUtil.trim(condition.get("PASSWORD_HASHITERATIONS"));

            result = memberService.select(userId,new HsSysUser());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSysUser getUser = (HsSysUser) result.getDataSet();
                String getOldPassword = getUser.getPassword();
                String salt = getUser.getSalt();
                SimpleHash oldMd5 = new SimpleHash("MD5",oldPassword,salt,Integer.parseInt(PASSWORD_HASHITERATIONS));
                //如果旧密码是正确的
                if(getOldPassword.equalsIgnoreCase(oldMd5.toString())){
                    HsSysUser user = new HsSysUser();
                    user.setPassword(StringUtil.trim(condition.get("password"))); //密码
                    user.setId(userId);
                    user.setSalt(StringUtil.trim(condition.get("salt")));
                    result = memberService.update(user);
                }else{
                    result.setDataSet(null);
                    result.setResult(ResultConstant.BUS_USER_PASSWORD_ERROR);
                    result.setMessage(ResultConstant.BUS_USER_PASSWORD_ERROR_VALUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    @Override
    public ResultVo forgetPassword(Map<Object, Object> condition) {
        //根据当前用户信息判断旧密码是否相同，相同则进行修改，否则失败
        ResultVo result = new ResultVo();
        try {
            String password = StringUtil.trim(condition.get("password")); //新密码
            String salt = StringUtil.trim(condition.get("salt")); //新密码
            condition.remove("password");
            condition.remove("salt");
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID userId");
            condition.put("queryColumn",queryColumn);
            condition.put("locked",0); //外获任务
            condition.put("isDel",0); //未完成
            result = memberService.selectCustomColumnNamesList(HsSysUser.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            List<Map<Object,Object>> userList  = (List<Map<Object, Object>>) result.getDataSet();
            if(userList==null || userList.size()==0){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }

            HsSysUser user = new HsSysUser();
            user.setId(StringUtil.getAsInt(StringUtil.trim(userList.get(0).get("userId"))));
            user.setPassword(password);//密码
            user.setSalt(salt);
            result = memberService.update(user);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 检验短信验证码
     * @param condition
     * @return
     */
    @Override
    public ResultVo validateSmsCode(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            String mobile = StringUtil.trim(condition.get("mobile")); //区号
            String nationCode = StringUtil.trim(condition.get("areaCode")); //区号
            String smsCode = StringUtil.trim(condition.get("smsCode")); //验证码
            String ip = StringUtil.trim(condition.get("ip"));
            //查询当前业务员的
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID userId");
            condition.put("queryColumn",queryColumn);
            //查询业务员任务列表信息
            ResultVo userVo = memberService.selectCustomColumnNamesList(HsSysUser.class,condition);
            if(userVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//请求成功
                List<Map<Object, Object>> userList = (List<Map<Object, Object>>) userVo.getDataSet();
                if(userList!=null && userList.size()>0){
                    String cacheKey = RedisConstant.SYS_USER_UPATE_VALIDATE_CODE_KEY + nationCode + mobile + ip;
                    String cacheValidateCode = "";
                    if(!StringUtil.hasText(cacheKey)){
                        result.setResult(ResultConstant.SYS_IMG_CODE_IS_OVERDUE);
                        result.setMessage(ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                        return result;
                    }

                    if(RedisUtil.isExistCache(cacheKey)){
                        cacheValidateCode = RedisUtil.safeGet(cacheKey);
                    }

                    if(!cacheValidateCode.equalsIgnoreCase(smsCode)){
                        result.setResult(-1);
                        result.setMessage("验证码错误");
                        return result;
                    }
                }else{
                    result.setResult(-1);
                    result.setMessage("验证码错误");
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return  result;
    }

    @Override
    public ResultVo updateSysUserMobile(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try {
            String ip = StringUtil.trim(condition.get("ip"));
            String mobile = StringUtil.trim(condition.get("mobile"));//手机号
            String nationCode = StringUtil.trim(condition.get("areaCode")); //区号
            int userId = Integer.parseInt(StringUtil.trim(condition.get("userId")));
            String validateCode = StringUtil.trim(condition.get("validateCode")); //验证码
            String cacheKey = RedisConstant.SYS_USER_UPATE_VALIDATE_CODE_KEY + nationCode + mobile + ip;
            String cacheValidateCode = "";

            if(!StringUtil.hasText(cacheKey)){
                result.setResult(ResultConstant.SYS_IMG_CODE_IS_OVERDUE);
                result.setMessage(ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                return result;
            }

            if(RedisUtil.isExistCache(cacheKey)){
                cacheValidateCode = RedisUtil.safeGet(cacheKey);
            }

            if(!cacheValidateCode.equalsIgnoreCase(validateCode)){
                result.setResult(-1);
//                result.setMessage("验证码错误");
                result.setMessage(ResultConstant.SYS_IMG_CODE_ERROR_VALUE);
                return result;
            }


            condition.clear();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            condition.put("queryColumn",queryColumn);
            condition.put("mobile",mobile);
            ResultVo memberVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
            List<Map<Object,Object>> userList = (List<Map<Object, Object>>) memberVo.getDataSet();
            if(userList!=null || userList.size()>0){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("手机号已存在。");
                return result;
            }

            RedisUtil.safeDel(cacheKey);
            //修改用户手机号
            HsSysUser hsSysUser = new HsSysUser();
            hsSysUser.setId(userId);
            hsSysUser.setMobile(mobile);
            hsSysUser.setUpdateTime(new Date());
            result = memberService.update(hsSysUser);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return  result;
    }

    /**
     * 获取外看派单列表信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOutLookDispatchOrder(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> query = new HashMap<>();
        try {
            String isComplaint = StringUtil.trim(condition.get("isComplaint"));
            //获取钥匙管理员信息
            int keysManagerId = StringUtil.getAsInt(StringUtil.trim(condition.get("keysManagerId")));
            if(keysManagerId > 0 && !StringUtil.hasText(isComplaint)){
                Map<Object,Object> queryMap = new HashMap<>(16);
                queryMap.put("userId",keysManagerId);

                ResultVo memberResultVo = memberService.select(keysManagerId, new HsSysUser());
                if(memberResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memberResultVo.getDataSet() == null){
                    return memberResultVo;
                }
                HsSysUser sysUser = (HsSysUser) memberResultVo.getDataSet();
                String city = sysUser.getCity();
                String community = sysUser.getCommunity();
                //查询所有外获业务员的ID
                queryMap.clear();
                queryMap.put("locked",0);
                queryMap.put("isForbidden",0);
                queryMap.put("isDel",0);
                List<String> roleIds = new ArrayList<String>();
                //外看业务员
                roleIds.add("202cb962ac59075b964b07152d234b70");
                queryMap.put("roleIds",roleIds);
                queryMap.put("city",city);
                queryMap.put("community",community);
                //获取所有外看业务员
                List<Map<Object,Object>> users = memberService.selectOutsideUser(queryMap);
                List<Integer> userIds = users.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("userId")))).collect(Collectors.toList());
                if(userIds.size() < 0){
                    return result;
                }
                condition.remove("keysManagerId");
                condition.put("userIds",userIds);
            }



            //查询业务员任务信息
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("APPLY_ID applyId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            condition.put("queryColumn",queryColumn);
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> memberIds = new ArrayList<>();
                condition.remove("queryColumn");
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                List<String> houseIds = new ArrayList<>(); //房源ID
                List<Integer> poolIds = new ArrayList<>(); //订单池ID
                if(taskList != null && taskList.size() > 0){
                    Date date = new Date();
                    String houseId = "";
                    List<Map<Object,Object>> houseList = new ArrayList<>(); //房源信息
                    List<Map<Object,Object>> appointmentList = new ArrayList<>(); //预约看房信息
                    //获取房源ID、订单池ID
                    for(Map<Object,Object> task : taskList){
                        houseId = StringUtil.trim(task.get("houseId")); //房源ID
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType")));
                        if(taskType == 1 || taskType == 3 || taskType == 5){ //外看或者投诉任务任务或者区域长投诉任务
                            houseIds.add(houseId);
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId"))));
                        }
                    }

                    if(poolIds.size() > 0){
                        //查询订单池数据
                        condition.clear();
                        queryColumn.clear();
                        queryColumn.add("ID poolId");//订单编码
                        queryColumn.add("ORDER_CODE orderCode");//订单编码
                        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                        //预计完成时间
                        queryColumn.add("CLOSE_TIME closeTime");
                        condition.put("queryColumn",queryColumn);
                        condition.put("poolIds",poolIds);
                        ResultVo poolResult = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                        if(poolResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> poolsList = (List<Map<Object, Object>>) poolResult.getDataSet();
                            if(poolsList != null && poolsList.size() > 0){
                                //将系统订单池与业务员任务表进行关联
                                for (Map<Object,Object> orderTask : taskList) {
                                    int poolId = StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId")));
                                    for (Map<Object, Object> pool : poolsList) {
                                        int _poolId = StringUtil.getAsInt(StringUtil.trim(pool.get("poolId")));
                                        if(poolId == _poolId){
                                            Date estimatedTime = (Date) pool.get("estimatedTime");
                                            long time1 = estimatedTime.getTime() - date.getTime();
                                            Date closeTime = (Date) pool.get("closeTime");
                                            long time2 = closeTime.getTime() - date.getTime();
                                            pool.put("estimatedTimeCountDown",time1/1000);
                                            pool.put("closeTimeCountDown",time2/1000);
                                            orderTask.putAll(pool);
                                            break;
                                        }
                                    }
                                }
                            }else{
                                return poolResult;
                            }
                        }
                    }

                    //房源ID，外看查询房源主信息表
                    if(houseIds != null && houseIds.size() >0){
                        query.clear();
                        query.put("houseIds",houseIds);
                        ResultVo houseResult = housesService.selectList(new HsMainHouse(),query,0); //查询房源主信息
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            houseList = (List<Map<Object, Object>>)houseResult.getDataSet();
                        }else{
                            return houseResult;
                        }
                    }

                    //订单池ID,根据订单池ID查询预约看房时间信息
                    if(poolIds != null && houseIds.size() > 0){
                        query.clear();
                        query.put("poolIds",poolIds);
                        query.put("isFinish",0); //是否完成 0：未完成 1：已完成
                        query.put("isCancel",0); //是否取消 0：未取消 1：已取消
//                        query.put("applicationType",0); //预约类型（0：申请预约，1：无需预约，2：让客服联系）
//                        query.put("isCheck",1); //是否审核通过（0>待审核，1>审核通过，2>审核不通过）

                        ResultVo appointmentResult = memberService.selectList(new HsMemberHousingApplication(),query,0);
                        if(appointmentResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            appointmentList = (List<Map<Object, Object>>)appointmentResult.getDataSet();
                        }else{
                            return  appointmentResult;
                        }
                    }

                    //遍历任务集合信息，
                    for (Map<Object,Object> task : taskList){
                        houseId = StringUtil.trim(task.get("houseId")); //房源ID
                        String  poolId = StringUtil.trim(task.get("poolId")); //订单池ID

                        //便利房源主信息与预约申请信息，匹配同一订单池数据
                        if(!"-1".equals(houseId)){
                            //房源主信息
                            for (Map<Object,Object> house : houseList){
                                String _houseApplyId = StringUtil.trim(house.get("id")); // 房源主信息ID
                                if(houseId.equals(_houseApplyId)){
                                    task.put("address",house.get("address")); //房源位置
                                    task.put("leaseType",house.get("leaseType")); //房屋类型（0：出租，1：出售）
                                }
                            }
                        }

                        if(StringUtil.hasText(poolId)){
                            for (Map map : appointmentList){
                                String _poolId = StringUtil.trim(map.get("standby1")); //订单池ID
                                if(_poolId.equals(poolId)){
                                    task.put("memberId",map.get("memberId"));
                                    memberIds.add(StringUtil.trim(map.get("memberId")));
                                }
                            }
                        }
                    }

                    if(memberIds.size() > 0){
                        condition.clear();
                        condition.put("memberIds",memberIds);
                        ResultVo  membersResult = memberService.selectList(new HsMember(),condition,1);
                        //查询会员名称
                        if(membersResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<HsMember> memberList = (List<HsMember>) membersResult.getDataSet();
                            for(HsMember member :memberList){
                                int mid = member.getId();
                                for(Map<Object,Object> task : taskList){
                                    int _mid = StringUtil.getAsInt(StringUtil.trim(task.get("memberId")));
                                    if(mid == _mid){
                                        task.put("memberName",member.getNickname());
                                        task.put("memberId",member.getId());
                                    }
                                }
                            }
                        }else{
                            return membersResult;
                        }
                    }

                    result.setDataSet(taskList);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取外看、外获投诉派单详情
     * @param condition
     * @return
     */
    @Override
    public ResultVo getComplaintDispatchOrderDetail(Map<Object,Object> condition){
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //任务ID
        int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId")));
        try {
            queryColumn.add("ID poolId");

            //订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("ORDER_TYPE orderType");
            //预计完成时间
            queryColumn.add("ESTIMATED_TIME estimatedTime");
            //预计完成时间
            queryColumn.add("CLOSE_TIME closeTime");
            //是否开启抢单0:未开启，1：开启
//            condition.put("isOpenOrder",1);
//            //是否完成0:未完成，1：已完成
//            condition.put("isFinished",0);
            condition.put("queryColumn",queryColumn);
            //查询订单池可抢的订单信息
            vo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //查询成功
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            }
            //订单数据
            List<Map<Object,Object>> orderList = (List<Map<Object,Object>>) vo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return new ResultVo();
            }
            Map<Object,Object> orderMap = orderList.get(0);
            //订单池Id
            int poolId = -1;
            Date date = new Date();
            Date estimatedTime = (Date) orderMap.get("estimatedTime");
            long time1 = estimatedTime.getTime() - date.getTime();
            Date closeTime = (Date) orderMap.get("closeTime");
            long time2 = closeTime.getTime() - date.getTime();
            poolId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("poolId")));

            //投诉信息
            Map<Object, Object> complainMap = new HashMap<>(16);
            condition.clear();
            condition.put("isDel",0);
            condition.put("poolId",poolId);
            ResultVo complainResultVo = housesService.selectList(new HsHouseComplain(), condition, 0);
            if(complainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && complainResultVo.getDataSet() != null){
                List<Map<Object, Object>> complainList = (List<Map<Object, Object>>) complainResultVo.getDataSet();
                if(complainList.size() < 1){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
                }
                complainMap = complainList.get(0);
            }
            orderMap.clear();
            orderMap.put("estimatedTimeCountDown",time1/1000);
            orderMap.put("closeTimeCountDown",time2/1000);
            orderMap.put("taskId",taskId);
            orderMap.putAll(complainMap);
            vo.setDataSet(orderMap);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取外看业务员派单详情
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOutLookDispatchOrderDetail(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> query = new HashMap<>(); //查询条件
        Map<Object,Object> resultMap = new HashMap<>(); //返回结果集合
        try {
            int poolId = StringUtil.getAsInt(StringUtil.trim(condition.get("poolId"))); //订单池ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId"))); //房源ID
            int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId"))); //任务ID

             List<String> memberIds = new ArrayList<>();
            List<String> queryColumn = new ArrayList<>();//自定义查询列名
            //订单池ID,根据订单池ID查询直接申请预约看房时间信息
            query.put("poolId",poolId);
            //lsq 这里需要修改
            query.put("isFinish",0); //是否完成 0：未完成 1：已完成
            query.put("isCancel",0); //是否取消 0：未取消 1：已取消
//            query.put("isCheck",1); //是否审核通过（0>待审核，1>审核通过，2>审核不通过）
//            query.put("applicationType",0); //预约类型（0：申请预约，1：无需预约，2：让客服联系）
            result = memberService.selectList(new HsMemberHousingApplication(),query,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> data = (List<Map<Object, Object>>) result.getDataSet();
                if(data.size() > 0){
                    Map<Object,Object> directAppointment = data.get(0);
                    resultMap.put("poolId",poolId);
                    resultMap.put("houseId",houseId);
                    resultMap.put("taskId",taskId);

                    int memberId = Integer.parseInt(StringUtil.trim(directAppointment.get("memberId"))); //会员ID
                    resultMap.put("startApartmentTime",directAppointment.get("startApartmentTime")); //直接预约看房信息 ，任务开始时间（见面时间）
                    Date estimatedTime = (Date) directAppointment.get("startApartmentTime");
                    Date date = new Date();
                    long time1 = estimatedTime.getTime() - date.getTime();
                    resultMap.put("estimatedTimeCountDown",time1/1000); //任务开始倒计时

                    ResultVo  memberResult = memberService.select(memberId,new HsMember());
                    if(memberResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsMember hsMember = (HsMember) memberResult.getDataSet();
                        resultMap.put("languageVersion",hsMember.getLanguageVersion()); //语言版本
                        resultMap.put("memberName",hsMember.getNickname()); //会员名称
                        resultMap.put("moble",hsMember.getMemberMoble()); //会员手机

                        ResultVo houseResult = housesService.select(houseId,new HsMainHouse());
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            HsMainHouse mainHouse = (HsMainHouse) houseResult.getDataSet();
                            resultMap.put("address",mainHouse.getAddress()); //见面地点
                            String houseMainImg = StringUtil.trim(mainHouse.getHouseMainImg());
                            if(StringUtil.hasText(houseMainImg)){
                                resultMap.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + mainHouse.getHouseMainImg()); //房源主图
                            }else{
                                resultMap.put("houseMainImg", houseMainImg); //房源主图
                            }
                            resultMap.put("houseName",mainHouse.getHouseName()); //房源名称
                            resultMap.put("houseAcreage",mainHouse.getHouseAcreage()); //房屋面积
                            resultMap.put("houseRent",mainHouse.getHouseRent()); //房屋租金/售价
                            resultMap.put("longitude",mainHouse.getLongitude()); //经度
                            resultMap.put("latitude",mainHouse.getLatitude()); //纬度
                            resultMap.put("leaseType",mainHouse.getLeaseType()); //房屋类型
                            resultMap.put("housingStatus",mainHouse.getHousingStatus()); //房屋类型
                            //TODO 根据房源ID查询钥匙归属

                            queryColumn.add("MEMBER_ID memberId");//业主ID
                            queryColumn.add("USER_ID userId");//业务员ID
                            queryColumn.add("IS_EXPIRE isExpire");//是否过期 0：未过期 1：已过期
                            condition.put("queryColumn",queryColumn);
                            condition.put("isExpire",0); //未过期
                            ResultVo keysResult = housesService.selectCustomColumnNamesList(HsHouseKeyCases.class,condition);
                            if(keysResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object,Object>> keysList = (List<Map<Object, Object>>) keysResult.getDataSet();
                                if(keysList.size() > 0){
                                    Map keys = keysList.get(0);
                                    int ownerId = StringUtil.getAsInt(StringUtil.trim(keys.get("memberId"))); //业主ID
                                    int userId = StringUtil.getAsInt(StringUtil.trim(keys.get("userId"))); //业务员ID
                                    int haveKey = 0; //是否有钥匙
                                    if(ownerId != -1){ //钥匙在业主处
                                        haveKey = 0;
                                        ResultVo ownerInfoResult = memberService.select(ownerId,new HsMember());
                                        if(ownerInfoResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                            HsMember member = (HsMember) ownerInfoResult.getDataSet();
                                            if(member != null){
                                                resultMap.put("keyCasesName",member.getNickname()); //业主名称
                                                resultMap.put("keyCasesLanguageVersion",member.getLanguageVersion()); //语言版本
                                                resultMap.put("keyCasesMobile",member.getMemberMoble()); //手机号
                                                resultMap.put("haveKey",haveKey);
                                            }else{
                                                return ownerInfoResult;
                                            }
                                        }else{
                                            return ownerInfoResult;
                                        }
                                    }

                                    if(userId != -1){ //在业务员处
                                        ResultVo salesmanInfoResult = memberService.select(userId,new HsSysUser());
                                        if(salesmanInfoResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                            HsSysUser user = (HsSysUser) salesmanInfoResult.getDataSet();
                                            if(user != null){
                                                resultMap.put("keyCasesName",user.getUsername()); //业主名称
                                                resultMap.put("keyCasesLanguageVersion",1); //语言版本(设置为英文)
                                                resultMap.put("keyCasesMobile",user.getMobile()); //手机号

                                                Map<Object,Object> queryRoleFilter = new HashMap<>();
                                                queryRoleFilter.put("userId",userId);
                                                queryRoleFilter.put("isForbidden",0); //未禁用
                                                queryRoleFilter.put("isDel",0); //未删除
                                                ResultVo roleResult = memberService.selectUserRoles(queryRoleFilter);
                                                if(roleResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                                    List<Map<Object,Object>> roleList = (List<Map<Object, Object>>) roleResult.getDataSet();
                                                    if(roleList.size() > 0){
                                                        for(Map role : roleList){
                                                            if(StringUtil.trim(role.get("roleName")).equals("钥匙管理员")){
                                                                haveKey = 1; //在钥匙管理员处
                                                                break;
                                                            }else if(StringUtil.trim(role.get("roleName")).equals("外看业务员")){
                                                                haveKey = 2; //在外看业务员处
                                                                break;
                                                            }else if(StringUtil.trim(role.get("roleName")).equals("外获业务员")){
                                                                haveKey = 3; //在外获业务员处
                                                                break;
                                                            }
                                                        }
                                                        resultMap.put("haveKey",haveKey);
                                                    }
                                                }
                                            }else{
                                                return salesmanInfoResult;
                                            }
                                        }else{
                                            return salesmanInfoResult;
                                        }
                                    }

                                }
                            }
                            query.put("applicationType",1); //查询参与看房成员信息
                            ResultVo joinResult = memberService.selectList(new HsMemberHousingApplication(),query,0);
                            if(joinResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object,Object>> joinList = (List<Map<Object, Object>>) joinResult.getDataSet();
                                if(joinList.size() > 0){
                                    //根据查询参与看房成员预约信息，获取参与看房成员会员ID
                                    for (Map<Object,Object> join : joinList){
                                        memberIds.add(StringUtil.trim(join.get("memberId")));
                                    }
                                    if(memberIds.size() > 0){
                                        condition.clear();
                                        condition.put("memberIds",memberIds);
                                    }
                                    queryColumn.clear();
                                    queryColumn.add("ID memberId");//主键ID
                                    queryColumn.add("NICKNAME memberName");//会员名称
                                    queryColumn.add("MEMBER_MOBLE mobile");//会员手机号
                                    queryColumn.add("LANGUAGE_VERSION languageVersion");//语言
                                    condition.put("queryColumn",queryColumn);
                                    //查询会员信息
                                    ResultVo membersResult = memberService.selectCustomColumnNamesList(HsMember.class,condition);
                                    if(membersResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                        resultMap.put("members",membersResult.getDataSet());
                                    }
                                }else{
                                    resultMap.put("members",null);
                                }
                            }else{
                                return joinResult;
                            }
                        }else{
                            return houseResult;
                        }
                    }else{
                        return memberResult;
                    }
                }

                result.setDataSet(resultMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 上传房源
     * @param mainHouse
     * @param houseCredentialsData
     * @return
     */
    @Override
    public ResultVo uploadHousing(HsMainHouse mainHouse, HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> queryFilter = Maps.newHashMap();
        try {
            int houseId = mainHouse.getId();
            int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId"))); //任务ID
            String keysManagerId = StringUtil.trim(condition.get("keysManagerId")); //钥匙管理员
            String userId = StringUtil.trim(condition.get("userId")); //钥匙管理员

            ResultVo userOrderTaskResult = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(userOrderTaskResult.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS) {
                return userOrderTaskResult;
            }
            HsSystemUserOrderTasks getUserOrderTask = (HsSystemUserOrderTasks) userOrderTaskResult.getDataSet();
            if(StringUtil.hasText(keysManagerId)) {//如果是钥匙管理员，则判断只修改自己的任务
                if (getUserOrderTask.getIsFinished() == 0) {
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "请等业务员上传完房源信息后，再操作");
                }
            }

            //组装房源日期格式数据
            assembleStringToDate(mainHouse);
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse house = (HsMainHouse) result.getDataSet();
                if(house==null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage(ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                    return result;
                }
                Date date = new Date();
                mainHouse.setId(houseId);
                mainHouse.setUpdateTime(date);
                mainHouse.setUpdateBy(mainHouse.getCreateBy());
                houseCredentialsData.setHouseId(houseId);
                StringBuffer sb = new StringBuffer();
                sb.append(mainHouse.getBedroomNum());
                sb.append(" Available in ");
                sb.append(mainHouse.getCommunity() +" ");
                sb.append(mainHouse.getHouseRent()+"AED");
                if(mainHouse.getLeaseType()==0){//预约类型（0：出租，1：出售）
                    sb.append("/Year ");
                }
                mainHouse.setHouseName(sb.toString());
                mainHouse.setVersionNo(house.getVersionNo());
                result = housesService.update(mainHouse); //修改房源主信息

                if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                    return result;
                }
                //保存自动应答
                String json = StringUtil.trim(condition.get("setting")); //获取json数组
                if(StringUtil.hasText(json)){
                    JSONArray jsonArray = JSONArray.fromObject(json);
                    if(jsonArray!=null && jsonArray.size()>0){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        //将json数组转换为对象集合
                        List<HsHouseAutoReplySetting> replyList = JSONArray.toList(jsonArray, HsHouseAutoReplySetting.class);
                        for (HsHouseAutoReplySetting houseAutoReplySetting : replyList) {
                            String startRentDate = StringUtil.trim(houseAutoReplySetting.getStartRentDate());
                            //当起租日期不为空时，设置起租日期格式
                            if (StringUtil.hasText(startRentDate)) {
                                houseAutoReplySetting.setBeginRentDate(sdf.parse(startRentDate));
                            }
                            houseAutoReplySetting.setHouseId(houseId);
                        }
                        if(replyList!=null && replyList.size()>0){
                            if(replyList.size() > 3){
                                //自动应答最多设置3条
                                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                                result.setMessage("Automatic answer up to 3");
                                return result;
                            }
                            for (HsHouseAutoReplySetting hsHouseAutoReplySetting : replyList) {
                                Integer id = hsHouseAutoReplySetting.getId();
                                BigDecimal houseRentPrice = hsHouseAutoReplySetting.getHouseRentPrice();
                                if(houseRentPrice == null){
                                    //houseRentPrice不能为空
                                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                                    result.setMessage("houseRentPrice cannot be empty");
                                    return result;
                                }
                                if(id == null){
                                    ResultVo insert = housesService.insert(hsHouseAutoReplySetting);
                                    if(insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                                        result = insert;
                                        return result;
                                    }
                                }else{
                                    ResultVo update = housesService.update(hsHouseAutoReplySetting);
                                    if(update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                                        result = update;
                                        return result;
                                    }
                                }
                            }
                            /*condition.put("list", replyList);
                            ResultVo applyResult = housesService.batchInsert(new HsHouseAutoReplySetting(),condition);
                            if(applyResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                                result = applyResult;
                                return result;
                            }*/
                        }
                    }
                }


                //判断是否愿意交钥匙，如果愿意则添加业务员钥匙记录
                if(mainHouse.getHaveKey() !=null && mainHouse.getHaveKey() == 1){
                    if(StringUtil.hasText(userId)){//外获员
                        HsHouseKeyCases keyCases = new HsHouseKeyCases();
                        keyCases.setHouseId(houseId);
                        keyCases.setHouseCode(house.getHouseCode());
                        keyCases.setUserId(StringUtil.getAsInt(userId));//业务员ID
                        keyCases.setCreateTime(new Date());
                        keyCases.setCreateBy(StringUtil.getAsInt(userId));
                        keyCases.setRemarks("业务员当面获取房源钥匙");

                        //业务员获取房源钥匙
                        housesService.getHouseKeys(keyCases);
                    }

                }

                //根据房源ID查询房源证件数据
                condition.clear();
                condition.put("applyId",mainHouse.getApplyId());
                result = housesService.selectDataByCondition(new HsHouseCredentialsData(),condition);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    //如果有数据，根据查询出的ID更新数据
                    HsHouseCredentialsData getHouseCredentials = (HsHouseCredentialsData) result.getDataSet();
                    if(getHouseCredentials != null){
                        houseCredentialsData.setId(getHouseCredentials.getId());
                        houseCredentialsData.setHouseId(mainHouse.getId());
                        result = housesService.update(houseCredentialsData);
                    }else{
                        //新增房源证件信息
                        result = housesService.insert(houseCredentialsData);
                    }
                }

                //修改任务状态
                if(StringUtil.hasText(keysManagerId)){//如果是钥匙管理员，则判断只修改自己的任务
                    //钥匙管理员上传房源图片
                    queryFilter.clear();
                    queryFilter.put("houseId",houseId);
                    ResultVo houseImgVo = housesService.selectList(houseImg, queryFilter, 1);
                    if(null!=houseImgVo.getDataSet()){//该房源已上传的信息
                        List<HsHouseImg> houseImgs = (List<HsHouseImg>) houseImgVo.getDataSet();
                        if(houseImgs!=null && houseImgs.size()>0){
                            HsHouseImg queryHouseImg =houseImgs.get(0);
                            houseImg.setId(queryHouseImg.getId());
                            //执行修改
                            result = housesService.update(houseImg);
                        }else {//执行新增
                            houseImg.setHouseCode(mainHouse.getHouseCode());
                            result = housesService.insert(houseImg);
                        }
                    }
                    if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//上传房源图片成功
                        return result;
                    }
                }

                HsSystemUserOrderTasks systemUserOrderTasks = new HsSystemUserOrderTasks();
                systemUserOrderTasks.setHouseId(houseId); //房源ID
                systemUserOrderTasks.setApplyId(house.getApplyId());
                systemUserOrderTasks.setPoolId(getUserOrderTask.getPoolId()); //订单池ID
                systemUserOrderTasks.setVersionNo(getUserOrderTask.getVersionNo()); //版本号
                systemUserOrderTasks.setId(taskId); //任务ID
                if(StringUtil.hasText(keysManagerId)){//钥匙管理员
                    systemUserOrderTasks.setStandby2("1"); //钥匙管理员完成任务
                    systemUserOrderTasks.setStandby3(StringUtil.trim(condition.get("keysManagerRemark")));//钥匙管理备注(说明)
                }
                systemUserOrderTasks.setIsFinished(1); //任务完成
                systemUserOrderTasks.setUpdateTime(date);
                ResultVo updateTaskResult = memberService.update(systemUserOrderTasks);
                if(updateTaskResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    if(StringUtil.hasText(keysManagerId)) {//钥匙管理员
                        //如果区域长完成任务，将订单池数据设置成已完成
                        ResultVo orderVo = orderService.select(getUserOrderTask.getPoolId(), new HsSystemOrderPool());
                        if (orderVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            HsSystemOrderPool orderPool = (HsSystemOrderPool) orderVo.getDataSet();
                            if (orderPool != null) {
                                HsSystemOrderPool updateOrderPool = new HsSystemOrderPool();
                                updateOrderPool.setId(orderPool.getId());
                                updateOrderPool.setIsFinished(5);//
                                updateOrderPool.setUpdateTime(date);
                                updateOrderPool.setVersionNo(orderPool.getVersionNo());
                                orderService.update(updateOrderPool);
                            }
                        }
                    }
                    HsSystemUserOrderTasksLog hsSystemUserOrderTasksLog = new HsSystemUserOrderTasksLog();
                    hsSystemUserOrderTasksLog.setTaskId(taskId);
                    hsSystemUserOrderTasksLog.setPoolId(getUserOrderTask.getPoolId());
                    hsSystemUserOrderTasksLog.setNodeType(3); //任务完成
                    hsSystemUserOrderTasksLog.setCreateBy(StringUtil.hasText(keysManagerId)? StringUtil.getAsInt(keysManagerId):StringUtil.getAsInt(StringUtil.trim(condition.get("userId"))));
                    hsSystemUserOrderTasksLog.setCreateTime(date);
                    String logRemarks = "";
                    if(StringUtil.hasText(keysManagerId)){//钥匙管理员
                        logRemarks = "钥匙管理员上传房源图片，任务完成";
                    }else{//外获员
                        logRemarks = "外获上传房源信息，任务完成";
                    }
                    hsSystemUserOrderTasksLog.setRemarks(logRemarks);
                    memberService.insert(hsSystemUserOrderTasksLog);
                }else{
                    return updateTaskResult;
                }
            }
        } catch (Exception e) {
            logger.error("error info : " +e);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取申请详细信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getApplicationDetails(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        try {
            int applyId = Integer.parseInt(StringUtil.trim(condition.get("applyId")));

            result =  housesService.selectList(new HsMainHouse(),condition,1);
//            result = housesService.select(applyId,new HsOwnerHousingApplication());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<HsMainHouse> houses = (List<HsMainHouse>) result.getDataSet();
//                HsOwnerHousingApplication house = (HsOwnerHousingApplication) result.getDataSet();
                HsMainHouse house= houses.get(0);
                Integer memberId = house.getMemberId();
                condition.clear();
                List<String> queryColumn = new ArrayList<>();
                queryColumn.add("ID memberId");
                queryColumn.add("MEMBER_MOBLE memberMoble");//手机号
                condition.put("queryColumn",queryColumn);
                condition.put("memberId",memberId);
                //查询订单池可抢的订单信息
                result = memberService.selectCustomColumnNamesList(HsMember.class,condition);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    List<Map<Object,Object>> memberList = (List<Map<Object, Object>>) result.getDataSet();
                    resultMap.put("phoneNumber", memberList.get(0).get("memberMoble"));
                }
                resultMap.put("houses",house);

                condition.clear();
                queryColumn.clear();
                queryColumn.add("HOUSE_SITUATION houseSituation");//'房源概况'
                queryColumn.add("PLOT_NUMBER plotNumber");//地块信息
                queryColumn.add("TYPE_OF_AREA typeOfArea");//区域信息(0:Free Hold 1:Lease Hold)
                queryColumn.add("TITLE_DEED_NUMBER titleDeedNumber");//产权证书编号
                queryColumn.add("PROPERTY_NUMBER propertyNumber");//产权编号
                queryColumn.add("MASTER_DEVELPOER_NAME masterDevelpoerName");//地产开发商
                condition.put("queryColumn",queryColumn);
                condition.put("applyId",applyId);
                //查询订单池可抢的订单信息
                result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                List<Map<Object,Object>> _houseList = (List<Map<Object, Object>>) result.getDataSet();
                if(_houseList!=null && _houseList.size()>0){
                    resultMap.put("houseInfo",_houseList.get(0));
                }else{
                    resultMap.put("houseInfo",null);
                }
                condition.clear();
                condition.put("applyId",applyId);
                ResultVo credentialsResult = housesService.selectDataByCondition(new HsHouseCredentialsData(),condition);
                if(credentialsResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    resultMap.put("credentials",credentialsResult.getDataSet());
                }
                //根据房源信息查询房源图片
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID houseId");
                condition.put("queryColumn",queryColumn);
                condition.put("applyId",applyId);
                //查询订单池可抢的订单信息
                ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                if(houseVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
                    Map<Object,Object> houseMap = null;
                    if(houseList!=null && houseList.size() >0){
                        houseMap = houseList.get(0);
                    }
                    condition.clear();
                    condition.put("applyId",applyId);
                    condition.put("houseId",StringUtil.getAsInt(StringUtil.trim(houseMap.get("houseId"))));
                    ResultVo imgResult = housesService.selectList(new HsHouseImg(),condition,0);
                    if(imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> imgList = (List<Map<Object, Object>>) imgResult.getDataSet();
                        Map<Object,Object> houseImg = new HashMap<>();
                        if(imgList.size() > 0){
                            houseImg = imgList.get(0);
                            houseImg.remove("houseCode");
                            houseImg.remove("houseId");
                            houseImg.remove("id");
                        }
                        resultMap.put("houseSubImg",houseImg.values());
                    }

                    queryColumn.clear();
                    queryColumn.add("ID id");//主键ID
                    queryColumn.add("HOUSE_ID house_id");//房源ID
                    queryColumn.add("APPLY_ID applyId");//房源申请ID
                    queryColumn.add("BEGIN_RENT_DATE begin_rent_date");//起租日期
                    queryColumn.add("RENT_TIME rent_time");//租赁时长
                    queryColumn.add("HOUSE_RENT_PRICE house_rent_price");//最低租金/或出售价
                    queryColumn.add("PAY_NODE payNode");//支付节点 , 1....12/月
                    queryColumn.add("PAY_TYPE payType");//支付方式（0：现金，1：贷款）
                    queryColumn.add("HAS_EXPECT_APPROVE hasExpectApprove");//是否完成预审批（0：是，1：否）
                    queryColumn.add("IS_OPEN isOpen");//是否完成预审批（0：是，1：否）
                    condition.put("queryColumn",queryColumn);
                    //查询订单池可抢的订单信息
                    ResultVo autoReplyResult = housesService.selectList(new HsHouseAutoReplySetting(),condition,1);
                    if(autoReplyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        resultMap.put("autoReplyList",autoReplyResult.getDataSet());
                    }
                }
            }
            result.setDataSet(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo getUploadHouseDetail(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        try {
            int houseId = Integer.parseInt(StringUtil.trim(condition.get("houseId")));
            //根据房源ID查询房源主信息
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){

                HsMainHouse hsMainHouse = (HsMainHouse) result.getDataSet();
                resultMap.put("houses",hsMainHouse);
                //增加houseInfo
                List<String> queryColumn = new ArrayList<>();
                condition.clear();
                queryColumn.clear();
                queryColumn.add("HOUSE_SITUATION houseSituation");//'房源概况'
                queryColumn.add("PLOT_NUMBER plotNumber");//地块信息
                queryColumn.add("TYPE_OF_AREA typeOfArea");//区域信息(0:Free Hold 1:Lease Hold)
                queryColumn.add("TITLE_DEED_NUMBER titleDeedNumber");//产权证书编号
                queryColumn.add("PROPERTY_NUMBER propertyNumber");//产权编号
                queryColumn.add("MASTER_DEVELPOER_NAME masterDevelpoerName");//地产开发商
                condition.put("queryColumn",queryColumn);
                condition.put("applyId",hsMainHouse.getApplyId());
                condition.put("houseId",houseId);
                //查询订单池可抢的订单信息
                result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                List<Map<Object,Object>> _houseList = (List<Map<Object, Object>>) result.getDataSet();
                if(_houseList!=null && _houseList.size()>0){
                    resultMap.put("houseInfo",_houseList.get(0));
                }else{
                    resultMap.put("houseInfo",null);
                }

                //查询房源图片证件信息
                ResultVo credentialsResult = housesService.selectDataByCondition(new HsHouseCredentialsData(),condition);
                if(credentialsResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    resultMap.put("credentials",credentialsResult.getDataSet());
                }
                //根据房源信息查询房源图片
                condition.put("houseId",houseId);
                ResultVo imgResult = housesService.selectList(new HsHouseImg(),condition,0);
                if(imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<Map<Object,Object>> imgList = (List<Map<Object, Object>>) imgResult.getDataSet();
                    Map<Object,Object> houseImg = new HashMap<>();
                    if(imgList.size() > 0){
                        houseImg = imgList.get(0);
                        houseImg.remove("houseCode");
                        houseImg.remove("houseId");
                        houseImg.remove("id");
                    }
                    resultMap.put("houseSubImg",houseImg.values());
                }
                //查询房源自动应答条件信息
                ResultVo replyResult = housesService.selectList(new HsHouseAutoReplySetting(),condition,0);
                if(replyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<Map<Object,Object>> replyList = (List<Map<Object, Object>>) replyResult.getDataSet();
                    resultMap.put("autoReplyList",replyList);
                }
                Integer memberId = hsMainHouse.getMemberId();
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID memberId");
                queryColumn.add("MEMBER_MOBLE memberMoble");//手机号
                condition.put("queryColumn",queryColumn);
                condition.put("memberId",memberId);
                //查询订单池可抢的订单信息
                result = memberService.selectCustomColumnNamesList(HsMember.class,condition);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    List<Map<Object,Object>> memberList = (List<Map<Object, Object>>) result.getDataSet();
                    resultMap.put("phoneNumber", memberList.get(0).get("memberMoble"));
                }
            }
            result.setDataSet(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo updateUploadedHouse(HsMainHouse mainHouse, HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int houseId = mainHouse.getId();
            //根据房源ID查询房源主表中有无记录
            condition.put("houseId",houseId);
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Date date = result.getSystemTime();
                HsMainHouse houseData = (HsMainHouse) result.getDataSet();
                //该ID在主表中有记录，进行修改操作，否则，进行新增操作
                if(houseData != null){
                    assembleStringToDate(mainHouse);

                    mainHouse.setId(houseId);
                    mainHouse.setUpdateTime(date);
                    mainHouse.setUpdateBy(mainHouse.getCreateBy());
                    mainHouse.setCreateBy(null);
                    houseCredentialsData.setHouseId(houseId);

                    StringBuffer sb = new StringBuffer();
                    sb.append(mainHouse.getBedroomNum());
                    sb.append(" Available in ");
                    sb.append(mainHouse.getCommunity());
                    sb.append(mainHouse.getHouseRent()+"AED");
                    if(mainHouse.getLeaseType()==0){//预约类型（0：出租，1：出售）
                        sb.append("/Year ");
                    }
                    mainHouse.setHouseName(sb.toString());
                    mainHouse.setVersionNo(houseData.getVersionNo());
                    result = housesService.update(mainHouse); //修改房源主信息
                    if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                        return result;
                    }
                    //查询图片表里有无该房源的数据，有数据则进行修改操作，否则进行新增操作
                    ResultVo imgResult = housesService.selectList(new HsHouseImg(),condition,1);
                    if(imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<HsHouseImg> imgList = (List<HsHouseImg>) imgResult.getDataSet();
                        if(imgList.size() > 0){
                            HsHouseImg getHouseImg = imgList.get(0);
                            houseImg.setId(getHouseImg.getId());
                            result = housesService.update(houseImg); //更新房源图片表
                        }
                    }
                    //根据房源ID查询房源证件数据
                    Map<Object,Object> queryFilter = Maps.newHashMap();
                    queryFilter.put("applyId",houseData.getApplyId());
                    result = housesService.selectDataByCondition(new HsHouseCredentialsData(),queryFilter);
                    //如果有数据，根据查询出的ID更新数据
                    HsHouseCredentialsData getHouseCredentials = (HsHouseCredentialsData) result.getDataSet();
                    if(getHouseCredentials != null){
                        houseCredentialsData.setId(getHouseCredentials.getId());
                        houseCredentialsData.setHouseId(houseId);
                        houseCredentialsData.setApplyId(houseData.getApplyId());
                        result = housesService.update(houseCredentialsData);
                    }else{
                        //新增房源证件信息
                        result = housesService.insert(houseCredentialsData);
                    }
                }

                //业主是否设置了自动应答
                String json = StringUtil.trim(condition.get("setting")); //获取json数组
                if(!StringUtil.hasText(json)){
                    return result;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                JSONArray jsonArray = JSONArray.fromObject(json);
                //将json数组转换为对象集合
                List<HsHouseAutoReplySetting> list = JSONArray.toList(jsonArray, HsHouseAutoReplySetting.class);
                if(list !=null && list.size() > 0) {
                    for (HsHouseAutoReplySetting houseAutoReplySetting : list) {
                        String startRentDate = StringUtil.trim(houseAutoReplySetting.getStartRentDate());
                        //当起租日期不为空时，设置起租日期格式
                        if (StringUtil.hasText(startRentDate)) {
                            houseAutoReplySetting.setBeginRentDate(sdf.parse(startRentDate));
                        }
                    }
                    condition.put("list", list);
                    ResultVo replyResult = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                    if (replyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> replyList = (List<Map<Object, Object>>) replyResult.getDataSet();
                        if (replyList.size() > 0) {
                            //自动应答设置修改
                            ResultVo updateResult = housesService.batchUpdate(new HsHouseAutoReplySetting(), condition);
                            if (updateResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                                return updateResult;
                            }
                        } else {
                            // 新增
                            ResultVo insertResult = housesService.batchInsert(new HsHouseAutoReplySetting(), condition);
                            if (insertResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                                return insertResult;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 组装房源字符串日期格式化数据
     * @param mainHouse
     * @throws ParseException
     */
    private void assembleStringToDate(HsMainHouse mainHouse) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //起租日期
        if (StringUtil.hasText(mainHouse.getStartRentDate())) {
            String startRentDate = mainHouse.getStartRentDate();
            if (startRentDate.indexOf(" ") == -1) {
                startRentDate = mainHouse.getStartRentDate() + " 00:00:00";
            }
            mainHouse.setBeginRentDate(sdfTime.parse(startRentDate));
        }

        if (StringUtil.hasText(mainHouse.getBargainHouseDate())) { //交房时间
            String bargainHouseDate = mainHouse.getBargainHouseDate();
            if (bargainHouseDate.indexOf(" ") == -1) {
                bargainHouseDate = mainHouse.getBargainHouseDate() + " 00:00:00";
            }
            mainHouse.setExpectBargainHouseDate(sdfTime.parse(bargainHouseDate));
        }

//        if (mainHouse.getHousingStatus() != null) {
//            //（0：空房，1：出租，2：自住，3：准现房）数据字典，20056 空房，20057出租，20058自住，20059准现房
//            if (mainHouse.getHousingStatus() == 20057 || mainHouse.getHousingStatus() == 20058) {
//                //为出租类型，自住时，钥匙不在平台
//                mainHouse.setHaveKey(0);
//            } else if (mainHouse.getHousingStatus() == 20059) {
//                //准现房可随时看房，平台有钥匙
//                mainHouse.setHaveKey(1);
//            }
//        }
    }

    /**
     * 获取外看人员个人绩效
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOutLookPersonalPerformance(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        try {
            condition.put("isArrive",1);
            result = memberService.selectPersonalPerformance(condition); //查询到达客户户数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("arriveCustomerNum",result.getDataSet());
            }

            condition.remove("isArrive");
            condition.put("isFinished",1); //是否完成 1：已完成
            result = memberService.selectPersonalPerformance(condition); //查询已完成带看房数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("lookedHouseNum",result.getDataSet());
            }

            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            condition.put("queryColumn",queryColumn);

            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                if(taskList.size() > 0){
                    List<String> houseIds = new ArrayList<>();
                    for(Map<Object,Object> task : taskList){
                        houseIds.add(StringUtil.trim(task.get("houseId")));
                    }
                    Map<Object,Object> query = new HashMap<>();
                    query.put("houseIds",houseIds);
                    query.put("isCheck",4);
                    //查询主房源信息条数
                    result = housesService.selectHouseCountByCondition(query);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        resultMap.put("successBusinessNum",result.getDataSet()); //成功交易数
                    }
                }else{
                    resultMap.put("successBusinessNum",0); //成功交易数
                }
            }
            condition.remove("queryColumn");
            condition.remove("isFinished");
            condition.put("isTransferOrder",1); //是否转单
            result = memberService.selectPersonalPerformance(condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){ //转单数量
                resultMap.put("transferOrderNum",result.getDataSet());
            }

            if(resultMap != null){
                result.setDataSet(resultMap);
            }
        } catch (Exception e) {

            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;

    }


    /**
     * 业务员查询考勤
     * @param condition
     * @return
     */
    @Override
    public ResultVo queryUserAttendances(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        try {
            List<String> queryColumn = new ArrayList<>();
            //queryColumn.add("ID attendanceId");///考勤表Id
            queryColumn.add("POST_TIME postTime");//打卡时间
            condition.put("queryColumn",queryColumn);
            //查询业务员打卡记录
            result = memberService.selectCustomColumnNamesList(HsUserAttendanceSheet.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //查询业务员考勤记录
                List<Map<Object,Object>> attendanceList = (List<Map<Object, Object>>) result.getDataSet();
                resultMap.put("attendance",attendanceList);//考勤记录
                //查询业务员当月请假记录
                queryColumn.clear();
//                queryColumn.add("ID vacateId");//请假记录Id
//                queryColumn.add("USER_ID userId");//业务员Id
                queryColumn.add("VACATE_TYPE vacateType");//请假类型 0:年假 1：事假 2：病假 3：调休 4：产假
                queryColumn.add("BEGIN_TIME beginTime");//开始时间
                queryColumn.add("END_TIME endTime");//结束时间
                queryColumn.add("DAYS days");//请假天数

                result = memberService.selectCustomColumnNamesList(HsUserVacateSheet.class,condition);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    //查询业务员请假记录
                    resultMap.put("vacates",result.getDataSet());//请假记录
                }
                result.setDataSet(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 房源进度查询
     * @param condition
     * @return
     */
    @Override
    public ResultVo getProgressInfo(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            result = memberService.selectList(new HsHouseLog(),condition,0);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 发送短信验证码
     * @param condition
     * @return
     */
    @Override
    public ResultVo sendSmsValidateCode(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            String nationCode = StringUtil.trim(condition.get("areaCode")); //区号
            String mobile = StringUtil.trim(condition.get("mobile")); //手机号
            String ip = StringUtil.trim(condition.get("ip")); //ip地址
            int randomCode = (int) ((Math.random()*9+1)*100000); //随机数
            int templateId = 166400; //默认为国际短信模板
            String smsSign = "Hi Sandy"; //签名
            String [] phoneNumbers = {mobile}; //手机号
            String[] params = {randomCode+""}; //参数

            if("86".equals(nationCode)){ //如果是中国，发送国内短信
                templateId = 166398; //短信模板
                smsSign = "三迪科技";
            }

            String resultStr = SendSmsUtil.sendSms(nationCode,phoneNumbers,templateId,params,smsSign,null,null);
            if(!"success".equals(resultStr)){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            String cacheKey = RedisConstant.SYS_USER_UPATE_VALIDATE_CODE_KEY + nationCode + mobile + ip;
            RedisUtil.safeSet(cacheKey, randomCode+"", 60*15*1*1);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 修改见面时间
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateMeetTime(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String meetTime = StringUtil.trim(condition.get("meetTime")); //见面时间
            int poolId = Integer.parseInt(StringUtil.trim(condition.get("poolId"))); //订单池ID
            int taskId = Integer.parseInt(StringUtil.trim(condition.get("taskId"))); //任务ID
            HsSystemOrderPool pool = new HsSystemOrderPool();
            pool.setEstimatedTime(sdf.parse(meetTime));
            pool.setId(poolId);

            result = orderService.update(pool);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemUserOrderTasks userOrderTasks = new HsSystemUserOrderTasks();
                userOrderTasks.setId(taskId);
                userOrderTasks.setIsFinished(0); //任务未完成

                result = memberService.update(userOrderTasks); //修改订单任务为未完成
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 修改见面地点
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateMeetPlace(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int poolId = Integer.parseInt(StringUtil.trim(condition.get("poolId"))); //订单池ID
            String meetPlace = StringUtil.trim(condition.get("meetPlace"));
            HsSystemOrderPool pool = new HsSystemOrderPool();
            pool.setAppointmentMeetPlace(meetPlace);
            pool.setId(poolId);
            result = orderService.update(pool);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取业务员信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getUserInfo(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID userId");
            queryColumn.add("USERCODE usercode");
            queryColumn.add("USERNAME username");
            queryColumn.add("MOBILE mobile");
            queryColumn.add("USER_LOGO userLogo");
            queryColumn.add("CITY city");
            queryColumn.add("COMMUNITY community");
            queryColumn.add("GOLD gold");
            condition.put("queryColumn",queryColumn);
            result = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) result.getDataSet();
            if(houseList!=null && houseList.size()>0){
                for (Map<Object, Object> house : houseList) {
                    String userLogo = StringUtil.trim(house.get("userLogo"));
                    if(!StringUtil.hasText(userLogo)){
                        house.put("userLogo", ImageUtil.IMG_URL_PREFIX+userLogo);
                    }else{
                        house.put("userLogo", ImageUtil.IMG_URL_PREFIX+"group1/M01/00/06/rBLBRFtexMeAdM2nAAC12pMJ0vY815.jpg");
                    }
                }
            }
            queryColumn = null;//使用设置为空
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 业务员获取房源钥匙
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseKey(Map<Object, Object> condition) {
        try {
            HsHouseKeyCases key = (HsHouseKeyCases) condition.get("key");
            return housesService.getHouseKeys(key);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
    }

    /**
     * 检查房源钥匙是否过期
     * @param houseId
     * @param userId
     * @return
     */
    @Override
    public ResultVo checkKeyIsExpire(int houseId, int userId) {
        ResultVo resultVo = null;
        try {
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("houseId",houseId);
            condition.put("userId",userId);
            condition.put("isExpire",0);//未过期
            resultVo = housesService.checkKeyIsExpire(condition);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取房源钥匙数据
     * @param condition
     * @return
     */
    @Override
    public ResultVo loadMyHousekeys(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        try {
            resultVo = housesService.loadMyHousekeys(condition);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return resultVo;
    }

    /**
     * 业务员修改见面时间和见面地点
     * @param condition
     * @return
     */
    @Override
    public ResultVo updatePoolInfo(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        try {
            HsSystemOrderPool orderPool = (HsSystemOrderPool) condition.get("orderPool");
            Integer userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            resultVo = orderService.select(orderPool.getId(),orderPool);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//请求成功
                HsSystemOrderPool daOrderPool = (HsSystemOrderPool) resultVo.getDataSet();
                if(daOrderPool!=null){
                    Integer isFinished = daOrderPool.getIsFinished();
//                    if(isFinished!=0){//只有当未完成状态才能修改见面时间和见面地点
//                        resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
//                        resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
//                        return resultVo;
//                    }
                    orderPool.setVersionNo(daOrderPool.getVersionNo());
                    orderPool.setRemark("业务员"+userId+"修改了见面时间或见面地点");
                    orderPool.setUpdateTime(resultVo.getSystemTime());

                    if(orderPool.getEstimatedTime()!=null ){
                        if(daOrderPool.getCloseTime().getTime() <= orderPool.getEstimatedTime().getTime()){
                            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            resultVo.setMessage("The meeting time shall not exceed the system closing time");
//                            resultVo.setMessage("见面时间不能超过系统关单时间");
                            return resultVo;
                        }
                    }

                    resultVo = orderService.update(orderPool);
                    if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//请求成功
                        //插入订单池日志
                        HsSystemOrderPoolLog log = new HsSystemOrderPoolLog();
                        log.setPoolId(orderPool.getId());
                        log.setCreateTime(resultVo.getSystemTime());
                        log.setPostTime(resultVo.getSystemTime());
                        log.setRemarks("业务员"+userId+"修改了见面时间或见面地点");
                        orderService.insert(log);

                        //修改业务员的任务预计完成时间
                        condition.clear();
                        condition.put("userId",userId);//业务员
                        condition.put("poolId",orderPool.getId());//订单池ID
                        condition.put("isTransferOrder",0);//是否转单 0->未转单，1已转单
                        List<String> queryColumn = new ArrayList<>();
                        queryColumn.add("ID taskId");
                        queryColumn.add("VERSION_NO versionNo");
                        condition.put("queryColumn",queryColumn);
                        ResultVo taskVo = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class, condition);
                        if(taskVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//请求成功
                            List<Map<Object, Object>> taskList = (List<Map<Object, Object>>) taskVo.getDataSet();
                            if(taskList!=null && taskList.size()>0){
                                List<HsSystemUserOrderTasks> tasks = Lists.newArrayList();
                                HsSystemUserOrderTasks task = null;
                                for (Map<Object, Object> map : taskList) {
                                    task = new HsSystemUserOrderTasks();
                                    task.setId(StringUtil.getAsInt(StringUtil.trim(map.get("taskId"))));
                                    task.setEstimatedTime(orderPool.getEstimatedTime());
                                    task.setVersionNo(StringUtil.getAsInt(StringUtil.trim(map.get("versionNo"))));
                                    tasks.add(task);
                                }
                                if(tasks!=null && tasks.size()>0){
                                    condition.clear();
                                    condition.put("tasksList",tasks);
                                    memberService.batchUpdate(task,condition);
                                }

                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取钥匙管理员派单任务列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getKeysManagerTaskList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> query = new HashMap<>();
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("APPLY_ID applyId");
            queryColumn.add("TASK_TYPE taskType");
            queryColumn.add("IS_FINISHED isFinished");
            queryColumn.add("IS_TRANSFER_ORDER isTransferOrder");
            condition.put("queryColumn",queryColumn);
            List<Integer> tasksType = new ArrayList<>();
            tasksType.add(0); //外获任务
            tasksType.add(1); //外看任务
            condition.put("tasksType",tasksType);
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Date date = new Date();
                condition.remove("queryColumn");
                Object pageInfo = result.getPageInfo();
                List<String> memberIds = new ArrayList<>();
                List<String> houseApplyIds = new ArrayList<>(); //房源申请ID
                List<String> houseIds = new ArrayList<>(); //主房源信息ID

                List<Integer> poolIds = new ArrayList<>(); //订单池ID
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                if(taskList.size() > 0){
                    List<Map<Object,Object>> appointmentList = new ArrayList<>(); //预约看房信息
                    String houseApplyId = "";
                    String houseId = "";
                    String opoolId = "";
                    List<Map<Object,Object>> applyHouseList = new ArrayList<>(); //房源申请信息
                    List<Map<Object,Object>> houseList = new ArrayList<>(); //主房源信息
                    for(Map<Object,Object> task : taskList){
                        houseApplyId = StringUtil.trim(task.get("applyId")); //房源申请ID
                        houseId = StringUtil.trim(task.get("houseId")); //主房源信息ID
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType")));
                        //根据任务类型，获取申请ID，以及主房源信息ID
                        if(taskType == 0){ //外获任务
                            houseApplyIds.add(houseApplyId);
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId"))));
                        }else if(taskType == 1){ //外看任务
                            houseIds.add(houseId);
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId"))));
                        }
                    }

                    if(houseApplyIds != null && houseApplyIds.size() > 0){
                        condition.clear();
                        queryColumn.clear();
                        queryColumn.add("ID id");
                        queryColumn.add("ORDER_CODE orderCode"); //订单编号
                        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                        queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace"); //见面地点
                        queryColumn.add("CLOSE_TIME closeTime");//预计完成时间
                        condition.put("queryColumn",queryColumn);
                        condition.put("poolIds",poolIds);
                        ResultVo poolVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                        if(poolVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> poolList = (List<Map<Object, Object>>) poolVo.getDataSet();
                            if(poolList != null && poolList.size() > 0){
                                for(Map task : taskList){
                                    int poolId = StringUtil.getAsInt(StringUtil.trim(task.get("poolId")));
                                    for(Map pool : poolList){
                                        int _poolId = StringUtil.getAsInt(StringUtil.trim(pool.get("poolId")));
                                        if(poolId == _poolId){
                                            Date estimatedTime = (Date) pool.get("estimatedTime");
                                            long time1 = estimatedTime.getTime() - date.getTime();
                                            pool.put("estimatedTimeCountDown",time1/1000);

                                            Date closeTime = (Date) pool.get("closeTime");
                                            long time2 = closeTime.getTime() - date.getTime();
                                            pool.put("closeTimeCountDown",time2/1000);
                                            task.putAll(pool);
                                            break;
                                        }
                                    }
                                }
                            }
                        }else{
                            return poolVo;
                        }
                        query.clear();
                        queryColumn.clear();
                        queryColumn.add("ID houseId");
                        queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
                        queryColumn.add("CITY city");//'城市'
                        queryColumn.add("COMMUNITY community");//'社区
                        queryColumn.add("SUB_COMMUNITY subCommunity");//'子社区'
                        queryColumn.add("PROPERTY property");//'项目'
                        queryColumn.add("ADDRESS address");//'房源所在区域名称'
                        queryColumn.add("MEMBER_ID memberId"); //用户ID
                        queryColumn.add("APPLICANT_TYPE applicantType"); //申请类型 0：业主 1：POA
                        query.put("queryColumn",queryColumn);
                        query.put("houseApplyIds",houseApplyIds);
                        ResultVo applyResult = housesService.selectCustomColumnNamesList(HsOwnerHousingApplication.class,query); //查询房源申请信息
                        if(applyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            applyHouseList = (List<Map<Object, Object>>)applyResult.getDataSet();
                        }else{
                            return applyResult;
                        }

                        query.remove("houseApplyIds");
                        query.put("houseIds",houseIds);
                        //查询主房源信息
                        ResultVo houseResult = housesService.selectCustomColumnNamesList(HsMainHouse.class,query);
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            houseList = (List<Map<Object, Object>>) houseResult.getDataSet();
                        }else{
                            return houseResult;
                        }
                    }

                    //订单池ID,根据订单池ID查询预约看房时间信息
                    if(poolIds != null && houseIds.size() > 0){
                        query.clear();
                        query.put("poolIds",poolIds);
                        query.put("isFinish",0); //是否完成 0：未完成 1：已完成
                        query.put("isCancel",0); //是否取消 0：未取消 1：已取消
                        query.put("isCheck",1); //是否审核通过（0>待审核，1>审核通过，2>审核不通过）
                        query.put("applicationType",0); //预约类型（0：申请预约，1：无需预约，2：让客服联系）
                        ResultVo appointmentResult = memberService.selectList(new HsMemberHousingApplication(),query,0);
                        if(appointmentResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            appointmentList = (List<Map<Object, Object>>)appointmentResult.getDataSet();
                        }
                    }

                    for(Map task : taskList){
                        houseApplyId = StringUtil.trim(task.get("applyId")); //房源申请ID
                        houseId = StringUtil.trim(task.get("houseId")); //主房源信息ID
                        opoolId = StringUtil.trim(task.get("poolId")); //订单池ID
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType"))); //任务类型0：外获 1：外看
                        //外获任务
                        if(taskType == 0){
                            for(Map applyHouse : applyHouseList){
                                String _houseApplyId = StringUtil.trim(applyHouse.get("applyId")); //房源申请ID
                                if(houseApplyId.equals(_houseApplyId)){
                                    task.put("leaseType",applyHouse.get("leaseType")); //房屋类型 0：出租，1：出售
                                    task.put("applicantType",applyHouse.get("applicantType")); //申请类型 0：业主，1：POA
                                    task.put("address",applyHouse.get("address")); //房源位置
                                    task.put("memberId",applyHouse.get("memberId")); //会员ID
                                    memberIds.add(StringUtil.trim(applyHouse.get("memberId"))); //会员ID
                                    break;
                                }
                            }
                        }else if(taskType == 1){ //外看任务
                            for(Map house : houseList){
                                String _houseId = StringUtil.trim(house.get("houseId")); //房源ID
                                if(houseId.equals(_houseId)){
                                    task.put("address",house.get("address")); //房源位置
                                    task.put("leaseType",house.get("leaseType")); //房屋类型 0：出租，1：出售
                                    task.put("applicantType",house.get("applicantType")); //申请类型 0
                                    break;
                                }
                            }
                        }

                        if(StringUtil.hasText(opoolId)){
                            for (Map map : appointmentList){
                                String _poolId = StringUtil.trim(map.get("standby1"));
                                if(_poolId.equals(opoolId)){
                                    task.put("memberId",map.get("memberId"));
                                    memberIds.add(StringUtil.trim(map.get("memberId")));
                                    break;
                                }
                            }
                        }
                    }
                    ResultVo  membersResult = memberService.selectList(new HsMember(),condition,1);
                    //查询会员名称
                    if(membersResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<HsMember> memberList = (List<HsMember>) membersResult.getDataSet();
                        for(HsMember member :memberList){
                            int mid = member.getId();
                            for(Map<Object,Object> task : taskList){
                                int _mid = Integer.parseInt(StringUtil.trim(task.get("memberId")));
                                if(mid == _mid){
                                    task.put("memberName",member.getNickname());
                                    task.put("memberId",member.getId());
                                    break;
                                }
                            }
                        }
                    }
                }
                result.setDataSet(taskList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 完成合同单任务
     * @param condition
     * @return
     */
    @Override
    public ResultVo finishedContractTask(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            // 根据任务ID查询任务表，修改任务，并且增加任务日志信息
            int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId")));
            String holdContractPic = StringUtil.trim(condition.get("holdContractPic")); //手持合同半身照
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId"))); //用户ID
            result = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemUserOrderTasks systemUserOrderTasks = (HsSystemUserOrderTasks) result.getDataSet();
                if(systemUserOrderTasks != null){
                    //订单池id
                    Integer poolId = systemUserOrderTasks.getPoolId();
                    Date date = new Date();
                    HsSystemUserOrderTasks updateOrderTasks = new HsSystemUserOrderTasks();
                    updateOrderTasks.setId(systemUserOrderTasks.getId()); //任务ID
                    updateOrderTasks.setVersionNo(systemUserOrderTasks.getVersionNo());
                    updateOrderTasks.setIsFinished(1); //已完成
                    updateOrderTasks.setStandby1(holdContractPic); //手持合同半身照

                    ResultVo updateTaskResult = memberService.update(updateOrderTasks);
                    if(updateTaskResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //新增日志数据
                        HsSystemUserOrderTasksLog log = new HsSystemUserOrderTasksLog();
                        log.setTaskId(systemUserOrderTasks.getId());
                        log.setPoolId(poolId);
                        log.setNodeType(3); //任务完成
                        log.setCreateBy(userId);
                        log.setCreateTime(date);
                        log.setUpdateTime(date);
                        log.setUpdateBy(userId);
                        log.setPostTime(date);
                        log.setOperatorUid(userId);


                        ResultVo insertLogResult = memberService.insert(log);
                        if(insertLogResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            /*修改订单池*/
                            //获取订单池
                            ResultVo orderPollResultVo = orderService.select(poolId, new HsSystemOrderPool());
                            if(orderPollResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderPollResultVo.getDataSet() != null){
                                HsSystemOrderPool orderPool = (HsSystemOrderPool) orderPollResultVo.getDataSet();
                                orderPool.setIsFinished(5);
                                orderPool.setUpdateBy(userId);
                                orderPool.setUpdateTime(date);
                                result = orderService.update(orderPool);
                                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                    /*插入订单池日志*/
                                    HsSystemOrderPoolLog orderPoolLog = new HsSystemOrderPoolLog();
                                    //订单池主键ID
                                    orderPoolLog.setPoolId(poolId);
                                    //外看订单
                                    orderPoolLog.setOrderType(orderPool.getOrderType());
                                    orderPoolLog.setNodeType(5);
                                    orderPoolLog.setCreateBy(userId);
                                    orderPoolLog.setCreateTime(date);
                                    orderPoolLog.setPostTime(date);
                                    //操作人用户ID
                                    orderPoolLog.setOperatorUid(userId);
                                    orderPoolLog.setRemarks("业务员完成订单");
                                    //新增系统订单池日志信息
                                    result = orderService.insert(orderPoolLog);
                                }
                            }
                        }


                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 获取区域长个人绩效信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getKeysManagerPersonalPerformance(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>(); //返回结果集

        try {
            //送钥匙数量，上传房源，带看房源，成功交易，转单数量
            condition.put("isFinishedSendKeys",1); //是否完成送钥匙 （0：未完成，1：已完成）
            condition.put("taskType",1); //外看任务
            result = memberService.selectPersonalPerformance(condition); //查询送钥匙数量
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("sendKeysNum",result.getDataSet()); //送钥匙数量
            }

            condition.remove("isFinishedSendKeys");
            condition.put("isTransferOrder",0); //未转单
            condition.put("isDel",0); //是否删除 0 ：未删除，1：已删除
            condition.put("taskType",0); //外获任务
            result = memberService.selectPersonalPerformance(condition); //查询上传房源数
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("uploadHouseNum",result.getDataSet()); //上传房源数量
            }
            condition.remove("isDel");
            condition.remove("isTransferOrder");
            condition.put("isFinished",1); //已完成看房
            condition.put("userIdNull","null");
            condition.put("taskType",1); //外看任务
            result = memberService.selectPersonalPerformance(condition); //查询已带看房源
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                resultMap.put("lookedHouseNum",result.getDataSet()); //已带看房源数量
            }

            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId");
            queryColumn.add("POOL_ID poolId");
            queryColumn.add("HOUSE_ID houseId");
            condition.put("queryColumn",queryColumn);
            condition.remove("userIdNull");
            condition.remove("taskType"); //外看任务
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                if(taskList.size() > 0){
                    List<String> houseIds = new ArrayList<>();
                    for(Map<Object,Object> task : taskList){
                        houseIds.add(StringUtil.trim(task.get("houseId")));
                    }
                    Map<Object,Object> query = new HashMap<>();
                    query.put("isCheck",4);
                    query.put("houseIds",houseIds);
                    //查询主房源信息条数
                    result = housesService.selectHouseCountByCondition(query);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        resultMap.put("successBusinessNum",result.getDataSet()); //成功交易数
                    }
                }else{
                    resultMap.put("successBusinessNum",0); //成功交易数
                }
            }
            condition.remove("queryColumn");
            condition.remove("isFinished");
            condition.put("isTransferOrder",1); //是否转单
            result = memberService.selectPersonalPerformance(condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){ //转单数量
                resultMap.put("transferOrderNum",result.getDataSet());
            }

            if(resultMap != null){
                result.setDataSet(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }


    /**
     * 检查是否可以显示房源二维码
     * @param houseId
     * @param userId
     * @return
     */
    @Override
    public ResultVo checkShowHouseQrcode(Integer houseId, Integer userId) {
        ResultVo resultVo = null;
        try {
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("houseId",houseId);
            condition.put("userId",userId);
            condition.put("isExpire",0);//未过期
            resultVo = housesService.checkKeyIsExpire(condition);
        }catch (Exception e){
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return resultVo;
    }


    /**
     * 获取送钥匙任务列表信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDeliveryKeysTaskList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> query = new HashMap<>();
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID taskId"); //任务ID
            queryColumn.add("POOL_ID poolId"); //订单池iID
            queryColumn.add("HOUSE_ID houseId"); //房源ID
            queryColumn.add("APPLY_ID applyId"); //预约申请ID
            queryColumn.add("TASK_TYPE taskType"); //任务类型
            queryColumn.add("USER_ID userId"); //业务员ID
            condition.put("queryColumn",queryColumn);
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> userIds = new ArrayList<>();
                condition.remove("queryColumn");
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                List<String> houseIds = new ArrayList<>(); //房源Ids
                List<Integer> poolIds = new ArrayList<>(); //订单池Ids
                if(taskList != null && taskList.size() > 0){
                    Date date = new Date();
                    String houseId = "";
                    List<Map<Object,Object>> houseList = new ArrayList<>(); //房源信息
                    for(Map task : taskList){
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType"))); //任务类型
                        if(taskType == 1){
                            houseId = StringUtil.trim(task.get("houseId")); //房源ID
                            houseIds.add(houseId);
                            userIds.add(StringUtil.trim(task.get("userId"))); //业务员ID
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId")))); //订单池Ids
                        }
                    }

                    //查询订单池数据（订单编码，任务开始时间等）
                    if(poolIds != null && poolIds.size() > 0){
                        condition.clear();
                        queryColumn.clear();
                        queryColumn.add("ORDER_CODE orderCode");//订单编码
                        queryColumn.add("ID poolId");//订单ID
                        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                        condition.put("queryColumn",queryColumn);
                        condition.put("poolIds",poolIds);

                        ResultVo poolResult = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                        if(poolResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> poolsList = (List<Map<Object, Object>>) poolResult.getDataSet();
                            if(poolsList != null && poolsList.size() > 0){
                                //将系统订单池与业务员任务表进行关联
                                for (Map<Object,Object> orderTask : taskList) {
                                    int poolId = StringUtil.getAsInt(StringUtil.trim(orderTask.get("poolId")));
                                    for (Map<Object, Object> pool : poolsList) {
                                        int _poolId = StringUtil.getAsInt(StringUtil.trim(pool.get("poolId")));
                                        if(poolId == _poolId){
                                            Date estimatedTime = (Date) pool.get("estimatedTime");
                                            long time1 = estimatedTime.getTime() - date.getTime();
                                            pool.put("estimatedTimeCountDown",time1/1000);
                                            orderTask.putAll(pool);
                                            break;
                                        }
                                    }
                                }
                            }else{
                                return poolResult;
                            }
                        }
                    }

                    //查询房源主信息
                    if(houseIds != null && houseIds.size() > 0){
                        query.clear();
                        queryColumn.clear();
                        queryColumn.add("ID id"); //房屋类型（0：出租，1：出售）
                        queryColumn.add("LEASE_TYPE leaseType"); //房屋类型（0：出租，1：出售）
                        queryColumn.add("CITY city"); //房源位置
                        queryColumn.add("COMMUNITY community"); //房源位置
                        queryColumn.add("SUB_COMMUNITY subCommunity"); //房源位置
                        queryColumn.add("ADDRESS address"); //房源位置
                        queryColumn.add("HOUSE_CODE houseCode"); //房源编号
                        query.put("queryColumn",queryColumn);
                        query.put("houseIds",houseIds);
                        query.put("haveKey",1);
                        ResultVo houseResult  = housesService.selectCustomColumnNamesList(HsMainHouse.class,query);
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            houseList = (List<Map<Object, Object>>)houseResult.getDataSet();
                        }else{
                            return houseResult;
                        }
                    }

                    //遍历任务集合信息，
                    for (int i = taskList.size()-1 ;i>=0 ;i--) {
                        Map<Object,Object> task = taskList.get(i);
                        houseId = StringUtil.trim(task.get("houseId")); //房源ID
                        if (!"-1".equals(houseId)) {
                            boolean isHouseKeyInManager = false;
                            //房源主信息,将任务信息与房源信息组装起来
                            for (Map<Object, Object> house : houseList) {
                                String _houseApplyId = StringUtil.trim(house.get("id")); // 房源主信息ID
                                if (houseId.equals(_houseApplyId)) {
                                    isHouseKeyInManager = true;
                                    task.putAll(house);
                                    break;
                                }
                            }
                            if(!isHouseKeyInManager){
                                taskList.remove(i);
                            }
                        }
                    }

                    if(userIds.size() > 0){
                        condition.clear();
                        condition.put("userIds",userIds);
                        ResultVo userResult = memberService.selectList(new HsSysUser(),condition,0);
                        if(userResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> userList = (List<Map<Object,Object>>) userResult.getDataSet();
                            for(Map user : userList){
                                int uid = StringUtil.getAsInt(StringUtil.trim(user.get("id")));
                                for(Map<Object,Object> task : taskList){
                                    int _uid = StringUtil.getAsInt(StringUtil.trim(task.get("userId"))); //业务员ID
                                    if(uid == _uid){
                                        task.put("userName",user.get("username")); //业务员姓名
                                        task.put("userMobile",user.get("mobile")); //业务员手机号
                                        break;
                                    }
                                }
                            }
                        }else{
                            return userResult;
                        }
                    }
                    result.setDataSet(taskList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 获取钥匙派送记录信息 （接收项目的这位兄弟,慢慢修bug吧。）
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDeliveredKeysRecordList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> query = new HashMap<>();
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("POOL_ID poolId"); //订单池iID
            queryColumn.add("HOUSE_ID houseId"); //房源ID
            queryColumn.add("APPLY_ID applyId"); //预约申请ID
            queryColumn.add("TASK_TYPE taskType"); //任务类型
            queryColumn.add("USER_ID userId"); //业务员ID
            queryColumn.add("STANDBY2 isFinishedSendKeys"); //送钥匙任务是否完成
            condition.put("queryColumn",queryColumn);
            result = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> userIds = new ArrayList<>();
                List<String> houseIds = new ArrayList<>(); //房源Ids
                List<Integer> poolIds = new ArrayList<>(); //订单池Ids
                condition.remove("queryColumn");
                //任务集合
                List<Map<Object,Object>> taskList = (List<Map<Object, Object>>) result.getDataSet();
                if(taskList != null && taskList.size() > 0){
                    Date date = new Date();
                    List<Map<Object,Object>> houseList = new ArrayList<>(); //房源信息
                    //遍历任务集合，获取出房源Ids,业务员Ids,订单池Ids
                    for(Map task : taskList){
                        int taskType = StringUtil.getAsInt(StringUtil.trim(task.get("taskType"))); //任务类型
                        if(taskType == 1){
                            houseIds.add(StringUtil.trim(task.get("houseId"))); //房源ID
                            userIds.add(StringUtil.trim(task.get("userId"))); //业务员ID
                            poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId")))); //订单池ID
                        }
                    }

                    if(poolIds != null && poolIds.size() > 0){
                        condition.clear();
                        queryColumn.clear();
                        queryColumn.add("ORDER_CODE orderCode");//订单编码
                        queryColumn.add("ID poolId");//订单ID
                        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
                        condition.put("queryColumn",queryColumn);
                        condition.put("poolIds",poolIds);
                        //查询订单池信息
                        ResultVo poolResult = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class,condition);
                        if(poolResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> poolsList = (List<Map<Object, Object>>) poolResult.getDataSet();
                            if(poolsList != null && poolsList.size() > 0){
                                //遍历订单池集合信息，与遍历任务信息，如果订单池ID相等，将数据组成一条
                                for(Map task : taskList){
                                    int poolId = StringUtil.getAsInt(StringUtil.trim(task.get("poolId")));
                                    for(Map pool : poolsList){
                                        int _poolId = StringUtil.getAsInt(StringUtil.trim(pool.get("poolId")));
                                        if(poolId == _poolId){
                                            task.putAll(pool);
                                            break;
                                        }
                                    }
                                }
                            }
                        }else{
                            return poolResult;
                        }
                    }

                    if(houseIds != null && houseIds.size() > 0){
                        query.clear();
                        query.put("houseIds",houseIds);
                        ResultVo houseResult = housesService.selectList(new HsMainHouse(),query,0);
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            houseList = (List<Map<Object, Object>>) houseResult.getDataSet();
                        }else{
                            return houseResult;
                        }
                    }
                    //遍历任务集合信息，
                    for (Map<Object,Object> task : taskList) {
                        String houseId = StringUtil.trim(task.get("houseId")); //房源ID
                        if (!"-1".equals(houseId)) {
                            //房源主信息,将任务信息与房源信息组装起来
                            for (Map<Object, Object> house : houseList) {
                                String _houseApplyId = StringUtil.trim(house.get("id")); // 房源主信息ID
                                if (houseId.equals(_houseApplyId)) {
                                    task.put("leaseType", house.get("leaseType")); //房屋类型（0：出租，1：出售）
                                    task.put("address", house.get("address")); //房源位置
                                }
                            }
                        }
                    }

                    if(userIds.size() > 0){
                        condition.clear();
                        condition.put("userIds",userIds);
                        ResultVo userResult = memberService.selectList(new HsSysUser(),condition,0);
                        if(userResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> userList = (List<Map<Object,Object>>) userResult.getDataSet();
                            for(Map user : userList){
                                int uid = StringUtil.getAsInt(StringUtil.trim(user.get("id")));
                                for(Map<Object,Object> task : taskList){
                                    int _uid = StringUtil.getAsInt(StringUtil.trim(task.get("userId"))); //业务员ID
                                    if(uid == _uid){
                                        task.put("userName",user.get("username")); //业务员姓名
                                        task.put("userMobile",user.get("mobile")); //业务员手机号
                                        break;
                                    }
                                }
                            }
                        }else{
                            return userResult;
                        }
                    }
                    result.setDataSet(taskList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 获取派送钥匙任务详情信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDeliveryKeysTaskDetail(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>(); //返回结果集合
        try {
            int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId"))); //任务ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId"))); //房源ID
            result = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemUserOrderTasks task = (HsSystemUserOrderTasks) result.getDataSet();
                //业务员电话号码，名字，房源属性，房源地点，任务开始时间，任务开始倒计时，房源信息
                if(task != null){
                    int userId = task.getUserId();
                    resultMap.put("estimatedTime",task.getEstimatedTime()); //见面时间
                    Date date = new Date();
                    long time1 = task.getEstimatedTime().getTime() - date.getTime();
                    resultMap.put("estimatedTimeCountDown",time1/1000); //任务开始倒计时

                    ResultVo userResult = memberService.select(userId,new HsSysUser()); //查询业务员信息
                    if(userResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsSysUser user = (HsSysUser) userResult.getDataSet();
                        if(user != null){
                            resultMap.put("userName",user.getUsername()); //业务员姓名
                            resultMap.put("userMobile",user.getMobile()); //手机号
                            resultMap.put("userId",user.getId()); //业务员ID
                        }
                    }else{
                        return userResult;
                    }
                    //查询房源信息
                    ResultVo houseResult = housesService.select(houseId,new HsMainHouse());
                    if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsMainHouse house = (HsMainHouse) houseResult.getDataSet();
                        if(house != null){
                            Map<Object,Object> houseData = new HashMap<>();
                            //房源主图，面积，房屋标题，房屋位置，租金
                            houseData.put("houseId",house.getId()); //房源ID
                            houseData.put("houseCode",house.getHouseCode()); //纬度
                            houseData.put("houseMainImg",house.getHouseMainImg()); //房源主图
                            houseData.put("address",house.getAddress());//房屋地址
                            houseData.put("houseAcreage",house.getHouseAcreage()); //房屋面积
                            houseData.put("houseName",house.getHouseName()); //房源名称
                            houseData.put("houseRent",house.getHouseRent()); //租金/售价
                            houseData.put("longitude",house.getLongitude()); //经度
                            houseData.put("latitude",house.getLatitude()); //纬度

                            resultMap.put("house",houseData);
                            resultMap.put("appointMeetPlace",house.getAddress()); //见面地点
                            resultMap.put("leaseType",house.getLeaseType()); //房屋类型（0：出租，1：出售）
                        }
                    }else{
                        return houseResult;
                    }

                }

                result.setDataSet(resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 完成派送钥匙任务
     * @param condition
     * @return
     */
    @Override
    public ResultVo finishedDeliveryKeys(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int taskId = StringUtil.getAsInt(StringUtil.trim(condition.get("taskId"))); //任务ID
            int userId= StringUtil.getAsInt(StringUtil.trim(condition.get("userId"))); //用户ID
            result = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemUserOrderTasks task = (HsSystemUserOrderTasks) result.getDataSet();

                HsSystemUserOrderTasks updateTask = new HsSystemUserOrderTasks();
                updateTask.setId(task.getId()); //任务ID
                updateTask.setStandby2("1"); //完成钥匙派送
                updateTask.setVersionNo(task.getVersionNo()); //版本号
                //更新任务表
                ResultVo updateTaskResult = memberService.update(updateTask);
                if(updateTaskResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    Date date = new Date();
                    HsSystemUserOrderTasksLog log = new HsSystemUserOrderTasksLog();
                    log.setPoolId(task.getPoolId()); //订单池ID
                    log.setTaskId(task.getId()); //任务ID
                    log.setNodeType(4); //完成派送
                    log.setCreateBy(userId);
                    log.setCreateTime(date);
                    log.setUpdateBy(userId);
                    log.setUpdateTime(date);

                    ResultVo insertLogResult = memberService.insert(log);
                    if(insertLogResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        return insertLogResult;
                    }
                }else{
                    return updateTaskResult;
                }

                result.setDataSet(null);

            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 到达送钥匙低点
     * @param condition
     * @return
     */
    @Override
    public ResultVo arriveDeliveryKeysPlace(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        try {
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")),-1); //房源ID
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("MEMBER_ID memberId");//业主ID
            queryColumn.add("USER_ID userId");//业务员ID
            queryColumn.add("HOUSE_ID houseId");//业务员ID
            queryColumn.add("HOUSE_CODE houseCode"); //房源编码
            queryColumn.add("IS_EXPIRE isExpire");//是否过期 0：未过期 1：已过期
            condition.put("queryColumn",queryColumn);
            condition.put("isExpire",0); //未过期
            //查询房源钥匙信息
            ResultVo keysResult = housesService.selectCustomColumnNamesList(HsHouseKeyCases.class,condition);
            if(keysResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> keysList = (List<Map<Object, Object>>) keysResult.getDataSet();
                if(keysList.size() > 0){
                    Map keys = keysList.get(0);
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(keys.get("memberId"))); //业主ID
                    int userId = StringUtil.getAsInt(StringUtil.trim(keys.get("userId"))); //业务员ID
                    int haveKey = 0; //是否有钥匙
                    if(ownerId != -1){ //钥匙在业主处
                        haveKey = 0;
                        //查询业主信息
                        ResultVo ownerInfoResult = memberService.select(ownerId,new HsMember());
                        if(ownerInfoResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            HsMember member = (HsMember) ownerInfoResult.getDataSet();
                            if(member != null){
                                resultMap.put("haveKey",haveKey);
                            }else{
                                return ownerInfoResult;
                            }
                        }else{
                            return ownerInfoResult;
                        }
                    }

                    if(userId != -1){ //在业务员处
                        //查询业务员信息
                        ResultVo salesmanInfoResult = memberService.select(userId,new HsSysUser());
                        if(salesmanInfoResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            HsSysUser user = (HsSysUser) salesmanInfoResult.getDataSet();
                            if(user != null){
                                Map<Object,Object> queryRoleFilter = new HashMap<>();
                                queryRoleFilter.put("userId",userId);
                                queryRoleFilter.put("isForbidden",0); //未禁用
                                queryRoleFilter.put("isDel",0); //未删除
                                ResultVo roleResult = memberService.selectUserRoles(queryRoleFilter);
                                if(roleResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                    List<Map<Object,Object>> roleList = (List<Map<Object, Object>>) roleResult.getDataSet();
                                    if(roleList.size() > 0){
                                        for(Map role : roleList){
                                            String roleName =StringUtil.trim( role.get("roleName"));
                                            if("钥匙管理员".equals(roleName)){
                                                haveKey = 1; //在钥匙管理员处
                                                break;
                                            }else if("外看业务员".equals(roleName)){
                                                haveKey = 2; //在外看业务员处
                                                break;
                                            }else if("外获业务员".equals(roleName)){
                                                haveKey = 3; //在外获业务员处
                                                break;
                                            }
                                        }
                                        resultMap.put("haveKey",haveKey);
                                    }
                                }
                            }else{
                                return salesmanInfoResult;
                            }
                        }else{
                            return salesmanInfoResult;
                        }
                    }

                    resultMap.put("houseId",keys.get("houseId")); //房源ID
                    resultMap.put("houseCode",keys.get("houseCode")); //房源编码
                    result.setDataSet(resultMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 我的结佣记录表
     * @param condition
     * @return
     */
    @Override
    public ResultVo myBrokerages(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID brokerageId");//结佣记录id
            queryColumn.add("ORDER_ID orderId");//订单id
            queryColumn.add("HOUSE_ID houseId");//房源id
            condition.put("queryColumn",queryColumn);
            //成单结佣记录
            result = orderService.selectCustomColumnNamesList(HsOrderCommissionRecord.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> brokerages = (List<Map<Object, Object>>) result.getDataSet();
                int size = brokerages.size();
                if(brokerages==null || size ==0){
                    return result;
                }
                List<Integer> orderIds = Lists.newArrayListWithCapacity(size);
                List<Integer> houseIds = Lists.newArrayListWithCapacity(size);
                for (Map<Object, Object> brokerage : brokerages) {
                    orderIds.add(StringUtil.getAsInt(StringUtil.trim(brokerage.get("orderId"))));
                    houseIds.add(StringUtil.getAsInt(StringUtil.trim(brokerage.get("houseId"))));
                }
                //查询订单信息
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID orderId");//订单id
                queryColumn.add("ORDER_CODE orderCode");//订单编号
                queryColumn.add("ORDER_STATUS orderStatus");//订单状态
                queryColumn.add("PAY_WAY payWay");//支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
                queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
                queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
                queryColumn.add("REMARK remark"); //备注描述
                queryColumn.add("CREATE_TIME createTime");//创建时间
                queryColumn.add("PAY_TIME payTime");//支付时间
                condition.put("queryColumn",queryColumn);
                condition.put("orderIds",orderIds);
                ResultVo orderVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class,condition);
                if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return orderVo;
                }
                List<Map<Object,Object>> orders = (List<Map<Object, Object>>) orderVo.getDataSet();


                //查询房源信息
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID houseId");//房源id
                queryColumn.add("HOUSE_NAME houseName");//房源名称
                queryColumn.add("HOUSE_CODE houseCode");//房源编号
                condition.put("queryColumn",queryColumn);
                condition.put("houseIds",houseIds);
                ResultVo houseVo  = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                if(houseVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return result;
                }
                List<Map<Object,Object>> houses = (List<Map<Object, Object>>) houseVo.getDataSet();

                for (Map<Object, Object> brokerage : brokerages) {
                    int orderId = StringUtil.getAsInt(StringUtil.trim(brokerage.get("orderId")));
                    int houseId = StringUtil.getAsInt(StringUtil.trim(brokerage.get("houseId")));
                    for (Map<Object, Object> order : orders) {
                        int _orderId = StringUtil.getAsInt(StringUtil.trim(order.get("orderId")));
                        if(orderId == _orderId){
                            brokerage.putAll(order);
                            break;
                        }
                    }
                    for (Map<Object, Object> house : houses) {
                        int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                        if(houseId == _houseId){
                            brokerage.putAll(house);
                            break;
                        }
                    }
                }
                result.setDataSet(brokerages);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }
}
