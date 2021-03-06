package www.ucforward.com.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.*;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.MsgDetails;
import www.ucforward.com.emchat.api.impl.EasemobChatGroup;
import www.ucforward.com.entity.*;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.umeng.util.UmengUtil;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统分配订单
 * @author wenbn
 * @version 1.0
 * @date 2018/6/11
 */

public class SystemOrderManager {

    @Resource
    private OrderService orderService;
    @Resource
    private HousesService housesService;
    @Resource
    private MemberService memberService;
    @Resource
    private CommonService commonService;

    /**
     * 外获，外看 自动关闭抢单功能
     * @throws Exception
     */
    public void closePanicBuyingOrder()throws Exception {
        if(orderService==null){
            orderService = SpringContextUtils.getBean("orderService");
        }
        Map<Object,Object> condition =  new HashMap<>();
//        long time = 30*60*1000;//30分钟
//        Date date = new Date();
//        Date afterDate = new Date(date.getTime() - time);//过期时间
//        condition.put("closeOpenOrderTime",afterDate);//关闭订单时间
        Date date = new Date();
        condition.put("openOrderCloseTime",date);//关闭订单时间
        condition.put("isFinished",0);//未完成
        condition.put("isOpenOrder",1);//可以抢单
        //condition.put("orderType",0);//外获订单
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID poolId");//主键ID
        queryColumn.add("VERSION_NO versionNo");//版本号
        condition.put("queryColumn",queryColumn);
        ResultVo resultVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
        if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object,Object>> resultList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(resultList == null || resultList.size()==0){
                return ;
            }
            List<HsSystemOrderPool> pools = new ArrayList<>();
            for (Map<Object, Object> result : resultList) {
                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                orderPool.setId(StringUtil.getAsInt(StringUtil.trim(result.get("poolId"))));
                orderPool.setIsOpenOrder(0);//关闭自动抢单功能 ，开始派单流程
                orderPool.setVersionNo(StringUtil.getAsInt(StringUtil.trim(result.get("versionNo"))));
                orderPool.setRemark("抢单时间到，系统关闭抢单功能");
                orderPool.setUpdateTime(date);
                pools.add(orderPool);
            }
            condition.put("data",pools);
            resultVo = orderService.batchUpdate(new HsSystemOrderPool(),condition);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                System.out.println("系统关闭抢单功能");
            }
        }
    }


    /**
     * 外获超过48小时自动关闭订单
     * @throws Exception
     */
    public void closeSysOrder()throws Exception {
        if(orderService==null) {
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null) {
            memberService = SpringContextUtils.getBean("memberService");
        }
//        OrderService orderService = SpringContextUtils.getBean("orderService");
//        MemberService memberService = SpringContextUtils.getBean("memberService");
        Map<Object,Object> condition =  new HashMap<>();
        Date date = new Date();
        condition.put("closeTime",date);//关闭订单时间
//        condition.put("orderType",0);//只有外获才有48小时关单
//        condition.put("isFinished",1);//未完成
        ArrayList<Integer> isFinisheds = Lists.newArrayList();
        isFinisheds.add(0);
        isFinisheds.add(1);
        condition.put("isFinisheds",isFinisheds);//未完成
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID poolId");//主键ID
        queryColumn.add("HOUSE_ID houseId");//主键ID
        queryColumn.add("VERSION_NO versionNo");//版本号
        condition.put("queryColumn",queryColumn);
        ResultVo resultVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
        if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object,Object>> resultList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(resultList == null || resultList.size()==0){
                return ;
            }
            List<HsSystemOrderPool> pools = new ArrayList<>();
            //保存订单池ID
            List<Integer> poolIds = new ArrayList<>();
            for (Map<Object, Object> result : resultList) {
                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                int poolId = StringUtil.getAsInt(StringUtil.trim(result.get("poolId")));
                poolIds.add(poolId);
                orderPool.setId(poolId);
                orderPool.setIsOpenOrder(0);//关闭自动抢单功能 ，开始派单流程
                orderPool.setIsFinished(4);//订单已关闭
                orderPool.setVersionNo(StringUtil.getAsInt(StringUtil.trim(result.get("versionNo"))));
                orderPool.setRemark("此订单已超过48小时，系统关闭订单");
                orderPool.setUpdateTime(date);
                pools.add(orderPool);
            }
            condition.put("data",pools);
            //关闭当前订单池状态
            resultVo = orderService.batchUpdate(new HsSystemOrderPool(),condition);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                condition.clear();
                queryColumn.clear();
                queryColumn.add("ID orderId");//订单ID
                queryColumn.add("VERSION_NO versionNo");//版本号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//删除
                condition.put("poolIds",poolIds);
                //根据订单池查询业务员任务，同步修改任务状态
                ResultVo orderVo = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class, condition);
                List<Map<Object,Object>> orderResultList = (List<Map<Object, Object>>) orderVo.getDataSet();
                if(orderResultList!=null && orderResultList.size()>0){
                    //记录过期的订单池IDs
                    //poolIds.clear();
                    List<HsSystemUserOrderTasks> orderTasks = new ArrayList<>();
                    for (Map<Object, Object> order : orderResultList) {
                        int orderId = StringUtil.getAsInt(StringUtil.trim(order.get("orderId")));
                        //poolIds.add(orderId);//添加到过期订单池中
                        HsSystemUserOrderTasks orderTask = new HsSystemUserOrderTasks();
                        orderTask.setId(orderId);
                        orderTask.setIsFinished(4);
                        orderTask.setRemark("此订单已超过48小时，系统关闭订单");
                        orderTask.setVersionNo(StringUtil.getAsInt(StringUtil.trim(order.get("versionNo"))));
                        orderTasks.add(orderTask);
                    }
                    //关闭任务状态
                    condition.clear();
                    condition.put("tasksList",orderTasks);
                    resultVo = memberService.batchUpdate(new HsSystemUserOrderTasks(),condition);
                    if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        System.out.println("系统关闭业务员任务状态");

                        ////扣除计算业务员的积分
                        condition.clear();
                        condition.put("poolIds",poolIds);
                        //查询过期的任务最后在哪个业务员手里
                        List<HsSystemUserOrderTasks>  userOrderTasks = memberService.selectExpiredTasks(condition);
                        if(userOrderTasks!=null && userOrderTasks.size()>0){
                            //重置积分规则缓存
                            List<Map<Object, Object>> goldList = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                            String cacheKey = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                            List<Map<Object, Object>> goldList = JsonUtil.parseJSON2List(cacheKey);
                            List<Integer> userIds = Lists.newArrayList();//保存用户Ids
                            List<HsUserGoldLog> userGoldLogs = Lists.newArrayList();//保存业务员积分日志
                            //保存用户总共要修改的金额
                            Map<Integer,Integer> user_gold = Maps.newHashMap();
                            HsUserGoldLog userGoldLog = null;
                            Map<Object, Object> waihuoGoldMap = null;//保存外看计算规则
                            Map<Object, Object> waikanGoldMap = null;
                            for (Map<Object, Object> gold : goldList) {
                                if(8==StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))){//外获任务
                                    waihuoGoldMap = gold;
                                }
                                if (9 == StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))) {//外获任务
                                    waikanGoldMap = gold;
                                }
                            }
                            for (HsSystemUserOrderTasks userOrderTask : userOrderTasks) {
                                Integer userId = userOrderTask.getUserId();
                                userIds.add(userId);
                                Integer taskType = userOrderTask.getTaskType();//订单类型 0外获订单->1-外看订单->2合同订单
                                int score = 0;//扣除的积分值
                                int goldRuleId = 0;//积分规则ID
                                if(taskType == 0){
                                    goldRuleId = StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("id")));
                                    score = StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("score")))*-1;
                                }else if(taskType == 1){
                                    goldRuleId = StringUtil.getAsInt(StringUtil.trim(waikanGoldMap.get("id")));
                                    score = StringUtil.getAsInt(StringUtil.trim(waikanGoldMap.get("score")))*-1;
                                }else if(taskType == 2){
                                    goldRuleId = StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("id")));
                                    score = StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("score")))*-1;
                                }

                                //添加日志记录
                                userGoldLog = new HsUserGoldLog();//保存用户积分
                                userGoldLog.setUserId(userId);
                                userGoldLog.setTaskId(userOrderTask.getId());
                                userGoldLog.setGoldRuleId(goldRuleId);
                                userGoldLog.setGold(score);
                                userGoldLog.setCreateTime(date);
                                userGoldLog.setRemark("任务超时,扣除"+score);

                                userGoldLogs.add(userGoldLog);

                                //保存用户总共要修改的金额
                                if(user_gold.containsKey(userId)){
                                    user_gold.put(userId,user_gold.get(userId)+score);
                                }else{
                                    user_gold.put(userId,score);
                                }
                            }
                            //查询用户余额
                            condition.clear();
                            queryColumn.clear();
                            queryColumn.add("ID userId");//用户ID
                            queryColumn.add("GOLD gold");//积分帐户
                            condition.put("queryColumn",queryColumn);
                            condition.put("ids",userIds);
                            ResultVo userVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
                            if(userVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object,Object>> userList = (List<Map<Object, Object>>) userVo.getDataSet();
                                List<HsSysUser> updateUser = Lists.newArrayListWithCapacity(userList.size());
                                HsSysUser _user = null;
                                for (Map<Object, Object> user : userList) {
                                    _user = new HsSysUser();
                                    int _userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                                    //获取总共要修改的积分数
                                    _user.setId(_userId);
                                    _user.setGold(user_gold.get(_userId));
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

                        }
                    }
                }
            }
        }
    }

    /**
     * 系统分配外获订单
     * 处理逻辑，先查询是否有外获订单
     * @throws Exception
     */
    public void systemAllocationOutsideOrder()throws Exception {
//        orderService = SpringContextUtils.getBean("orderService");
//        memberService = SpringContextUtils.getBean("memberService");
//        housesService = SpringContextUtils.getBean("housesService");
        if(orderService==null) {
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null) {
            memberService = SpringContextUtils.getBean("memberService");
        }
        if(housesService==null) {
            housesService = SpringContextUtils.getBean("housesService");
        }
        if(commonService==null){
            commonService = SpringContextUtils.getBean("commonService");
        }
//        OrderService orderService = SpringContextUtils.getBean("orderService");
//        HousesService housesService = SpringContextUtils.getBean("housesService");
//        MemberService memberService = SpringContextUtils.getBean("memberService");
        Map<Object,Object> condition =  new HashMap<>();
        condition.put("isFinished",0);//查询未完成订单
        List<Integer> orderTypes = Lists.newArrayList();
        orderTypes.add(0);
        orderTypes.add(2);
        condition.put("orderTypes",orderTypes);//处理外获的订单
        condition.put("isOpenOrder",0);//是否开启抢单0:未开启
        condition.put("isDel",0);
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID poolId");//主键ID
        queryColumn.add("ORDER_TYPE orderType");//订单类型
        queryColumn.add("HOUSE_ID houseId");//房源ID
        queryColumn.add("APPLY_ID applyId");//申请ID
        queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
        queryColumn.add("VERSION_NO versionNo");//版本号
        condition.put("queryColumn",queryColumn);
        //查询当前符合条件的订单池
        ResultVo resultVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
        if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            Date date = resultVo.getSystemTime();//获取系统当前时间
            List<Map<Object,Object>> resultList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(resultList == null || resultList.size()==0){
                return;
            }
            List<Integer> houseIds = Lists.newArrayList();
            resultList.forEach((result) -> houseIds.add(StringUtil.getAsInt(StringUtil.trim(result.get("houseId")))));

            //判断房源钥匙是否在平台
            List<Map<Object,Object>> houses = new ArrayList<>();
            Map<Object,Object> queryFilter =Maps.newHashMap();
            queryColumn.clear();
            queryColumn.add("ID houseId");//主键ID
            queryColumn.add("HAVE_KEY haveKey");//是否有钥匙：0>无,1有
            //预约类型（0：出租，1：出售）
            queryColumn.add("LEASE_TYPE leaseType");
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("houseIds",houseIds);
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, queryFilter);
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            for (Map<Object, Object> result : resultList) {
                int haveKey = 0;
                int houseId = StringUtil.getAsInt(StringUtil.trim(result.get("houseId")));
                int leaseType = StringUtil.getAsInt(StringUtil.trim(result.get("leaseType")));
                for (Map<Object, Object> house : houseList) {
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    int _haveKey = StringUtil.getAsInt(StringUtil.trim(house.get("haveKey")));
                    if(_haveKey == 1 && houseId == _houseId){//钥匙在平台
                        haveKey = 1;
                        break;
                    }
                }
                result.put("leaseType",leaseType);
                result.put("haveKey",haveKey);
                houses.add(result);
            }

            //查询所有外获业务员的ID
            //获取业务员位置及信息
            condition.clear();
            condition.put("locked",0);
            condition.put("isForbidden",0);
            condition.put("isDel",0);
            List<String> roleIds = new ArrayList<String>();
            roleIds.add("a0a080f42e6f13b3a2df133f073095dd");//外获业务员
            roleIds.add("2ece77221ad0b89f14f35899a8a63886");//钥匙管理员
            condition.put("roleIds",roleIds);
            //获取所有外获业务员
            List<Map<Object,Object>> users = memberService.selectOutsideUser(condition);
            if(users == null){
                System.out.println("当前没有可用的业务员，此次派单失败");
                return ;
            }
            //查询所有用户的订单数
            //钥匙管理员数据
            List<Map<Object, Object>> keymasterUsers = Lists.newArrayList();
            List<Integer> userIds = Lists.newArrayList();
            for(int i = users.size() - 1; i >= 0; i--){
                Map<Object, Object> user = users.get(i);
                int userId  = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                if(StringUtil.trim(user.get("roleId")).equals("2ece77221ad0b89f14f35899a8a63886")){//如果钥匙管理员
                    keymasterUsers.add(user);
                    users.remove(user);
                    continue;
                }
                userIds.add(userId);
            }

            condition.clear();
            condition.put("locked",0);
            condition.put("isTransferOrder",0);//是否转单 0->未转单，1已转单
            condition.put("isFinished",0);//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            condition.put("isDel",0);
            condition.put("userIds",userIds);
            ResultVo userTasksVo = memberService.selectGroupUserTasksByCondition(condition);
            //查询失败此次不做订单分配
            if(userTasksVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return ;
            }
            //保存当前用户的订单数
            List<Map<Object,Object>> userTasks  = (List<Map<Object, Object>>) userTasksVo.getDataSet();
            for (Map<Object, Object> user : users) {
                String userId = StringUtil.trim(user.get("userId"));
                boolean isFind = false;//所有业务员是否有任务
                for (Map<Object, Object> userTask : userTasks) {
                    String _userId = StringUtil.trim(userTask.get("userId"));
                    if(userId.equals(_userId)){
                        isFind = true;
                        user.put("taskCount", userTask.get("count"));
                        break;
                    }
                }
                if(!isFind){
                    user.put("taskCount", 0);
                }
            }

            //符合条件的派单
            List<HsSystemOrderPool> orderPools = new ArrayList<HsSystemOrderPool>();
            List<HsSystemOrderPoolLog> orderPoolLogs = new ArrayList<HsSystemOrderPoolLog>();
            List<HsSystemUserOrderTasks> userOrderTasks = new ArrayList<HsSystemUserOrderTasks>();
            List<HsSystemUserOrderTasksLog> orderTaskLogs = new ArrayList<HsSystemUserOrderTasksLog>();
            /**
             * 推送参数
             */
            //用户id列表
            List<Integer> userIdList = new ArrayList<>();
            //用户手机号  key为用户id  value为用户手机号
            Map<Integer,String> userMobileMap = new HashMap<>(16);
            //订单池id key为用户id  value为用
            Map<Integer,Integer> poolIdMap = new HashMap<>(16);
            //见面地点列表
            Map<Integer,String> appointmentMeetPlaceMap = new HashMap<>(16);
            //见面时间
            Map<Integer,String> estimatedTimeMap = new HashMap<>(16);
            //房源类型列表
            Map<Integer,Integer> leaseTypeMap = new HashMap<>(16);
            //钥匙是否在平台
            Map<Integer,Integer> haveKeyMap = new HashMap<>(16);
            //钥匙管理员id
            Map<Integer,String[]> keyMasterIdMap = new HashMap<>(16);
            //分配派单逻辑

            //系统订单池
            for (Map<Object, Object> map : resultList) {
                //此订单约定的见面地点
                String appointmentMeetPlace = StringUtil.trim(map.get("appointmentMeetPlace"));
                int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")));//房源ID
                int mixTaskCount = 10000;//单量最少的业务员
                int _userId = -1;
                String userMobile = "";

                int userSizeLeng = users.size();//总的用户长度
                int index = 0;//记录当前
                //业务员信息
                for ( int i  = 0 ; i< userSizeLeng ;i++) {
                    Map<Object, Object> user = users.get(i);
                    //判断业务员订单选择最小的
                    int taskCount = StringUtil.getAsInt(StringUtil.trim(user.get("taskCount")));
                    if(taskCount<=mixTaskCount){//选择最小的
                        mixTaskCount = taskCount;
                        _userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                        userMobile = StringUtil.trim("userMobile");
                        index = i;
                    }
                    if(i == userSizeLeng-1 && _userId != -1){//完成了所有业务员的计算 匹配到用户
                        users.get(index).put("taskCount",taskCount+1);
                    }
                }
                if(_userId != -1){//匹配到用户
                    String[] keymasterArr = new String[2];
                    String keymasterId = "-1";
                    String keymasterMobile = "-1";
//                    if(StringUtil.getAsInt(StringUtil.trim(map.get("haveKey"))) == 1){//钥匙在平台
                        //查询所属钥匙管理员信息
                        for ( int i  = 0 ; i< keymasterUsers.size() ;i++) {
                            Map<Object, Object> keymasterUser = keymasterUsers.get(i);
                            //判断钥匙管理员所属位置
                            String userId = StringUtil.trim(keymasterUser.get("userId"));
                            String keyUserMobile = StringUtil.trim(keymasterUser.get("userMobile"));
                            String city = StringUtil.trim(keymasterUser.get("city"));
                            String community = StringUtil.trim(keymasterUser.get("community"));
                            if( -1 != appointmentMeetPlace.indexOf(city) && -1 != appointmentMeetPlace.indexOf(community)){ // 查询所属业务员负责地址
                                keymasterMobile = keyUserMobile;
                                keymasterId = keymasterId;
                                break;
                            }
                        }
//                    }
                    keymasterArr[0] = keymasterId;
                    keymasterArr[1] = keymasterMobile;

                    int poolId  = StringUtil.getAsInt(StringUtil.trim(map.get("poolId")));
                    int orderType = StringUtil.getAsInt(StringUtil.trim(map.get("orderType")));
                    int applyId = StringUtil.getAsInt(StringUtil.trim(map.get("applyId")));
                    String estimatedTime = StringUtil.trim(map.get("estimatedTime"));
                    HsSystemOrderPool orderPool = new HsSystemOrderPool();
                    orderPool.setId(poolId);
                    orderPool.setIsFinished(1);
                    orderPool.setRemark("系统自动派单完成");
                    orderPool.setUpdateTime(date);
                    orderPool.setVersionNo(StringUtil.getAsInt(StringUtil.trim(map.get("versionNo"))));
                    orderPools.add(orderPool);

                    HsSystemOrderPoolLog log = new HsSystemOrderPoolLog();
                    log.setOrderType(orderType);//外获订单
                    log.setNodeType(1);
                    log.setPoolId(poolId);
                    log.setIsDel(0);
                    log.setCreateTime(date);
                    log.setUpdateTime(date);
                    log.setPostTime(date);
                    log.setRemarks("系统自动派单完成--外获业务员接单");
                    orderPoolLogs.add(log);

                    HsSystemUserOrderTasks tasks = new HsSystemUserOrderTasks();
                    tasks.setHouseId(houseId);
                    tasks.setApplyId(applyId);
                    tasks.setUserId(_userId);
                    tasks.setPoolId(poolId);
                    tasks.setTaskType(orderType);//外获任务
                    tasks.setCreateTime(date);
                    tasks.setEstimatedTime((Date) map.get("estimatedTime"));
                    tasks.setRemark("系统自动派单完成--外获业务员接单");
                    tasks.setVersionNo(0);
                    tasks.setStandby1(keymasterId);//钥匙管理员ID
                    tasks.setStandby2("0");//未完成
                    userOrderTasks.add(tasks);

                    HsSystemUserOrderTasksLog tasksLog = new HsSystemUserOrderTasksLog();
                    tasksLog.setNodeType(0);
                    tasksLog.setPoolId(poolId);
                    tasks.setIsDel(0);
                    tasksLog.setCreateTime(date);
                    tasksLog.setRemarks("系统自动派单完成--外获业务员接单");
                    orderTaskLogs.add(tasksLog);

                    //获取订单对应的房源信息
                    Map<Object, Object> house = new HashMap<>(16);
                    Optional<Map<Object, Object>> houseOptional = houses.stream().filter(houseMap -> houseMap.get("houseId").equals(houseId)).findFirst();
                    if(houseOptional.isPresent()){
                        house = houseOptional.get();
                    }
                    Integer leaseType = StringUtil.getAsInt(StringUtil.trim(house.get("leaseType")));
                    Integer haveKey = StringUtil.getAsInt(StringUtil.trim(house.get("haveKey")));
                    //封装推送参数
                    userIdList.add(_userId);
                    //封装用户手机号
                    userMobileMap.put(_userId,userMobile);
                    //封装订单池id
                    poolIdMap.put(_userId,poolId);
                    //封装房源类型
                    leaseTypeMap.put(_userId,leaseType);
                    //封装见面地点
                    appointmentMeetPlaceMap.put(_userId,appointmentMeetPlace);
                    //封装见面时间
                    estimatedTimeMap.put(_userId,estimatedTime);
                    //封装钥匙归属
                    haveKeyMap.put(_userId,haveKey);
                    //封装钥匙管理员id
                    keyMasterIdMap.put(_userId,keymasterArr);
                }
            }

            if(userOrderTasks!=null && userOrderTasks.size()>0){
                condition.put("data",userOrderTasks);
                condition.put("logData",orderTaskLogs);
                resultVo = memberService.batchInsert(new HsSystemUserOrderTasks(),condition);
                if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    condition.clear();
                    condition.put("data",orderPools);
                    condition.put("logData",orderPoolLogs);
                    resultVo = orderService.batchUpdate(new HsSystemOrderPool(),condition);

                    if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //推送消息
                        for (Integer userId : userIdList) {
                            Integer poolId = poolIdMap.get(userId);
                            int leaseType = leaseTypeMap.get(userId);
                            int haveKey = haveKeyMap.get(userId);
                            String userMobile = userMobileMap.get(userId);
                            String appointmentMeetPlace = appointmentMeetPlaceMap.get(userId);
                            String estimatedTime = estimatedTimeMap.get(userId);
                            String[] keymasterArr = keyMasterIdMap.get(userId);
                            String keymasterUserId= keymasterArr[0];
                            String keymasterMobile= keymasterArr[1];

                            //封装消息详情
                            MsgDetails msgDetails = new MsgDetails();
                            //房源类型 预约类型（0：出租，1：出售）
                            msgDetails.setLeaseType(leaseType);
                            //是否系统派单0:自动派单，1：业务员手动抢单
                            msgDetails.setIsSystem(0);
                            //是否有钥匙：0>无,1有
                            msgDetails.setHaveKey(haveKey);
                            //见面地点
                            msgDetails.setAppointmentMeetPlace(appointmentMeetPlace);
                            //见面时间（客户预约时间）
                            msgDetails.setEstimatedTime(estimatedTime);
                            //外获业务员推送消息
                            boolean push = push(msgDetails, userMobile, userId,2,"您有新的外获订单",6);
                            System.out.println("lsq====:systemAllocationOutsideOrder外获业务员推送结果为：" + push);
                            if(!"-1".equals(keymasterMobile)){
                                //钥匙管理员推送消息
                                boolean keyMasterPush = push(msgDetails, keymasterUserId, userId,4,"您有新的外获订单",6);
                                System.out.println("lsq====:systemAllocationOutsideOrder钥匙管理员外外获订单推送结果为：" + keyMasterPush);
                            }
                        }
                    }

                }
            }
        }
    }


    /**
     * 系统分配外看订单
     * 处理逻辑，先查询是否有外看订单
     * @throws Exception
     */
    public void systemAllocationLookOutOrder()throws Exception {
        if(orderService==null){
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null){
            memberService = SpringContextUtils.getBean("memberService");
        }
        if(housesService==null){
            housesService = SpringContextUtils.getBean("housesService");
        }
        if(commonService==null){
            commonService = SpringContextUtils.getBean("commonService");
        }
        Map<Object,Object> condition =  new HashMap<>();
        condition.put("isFinished",0);//查询未完成订单
        condition.put("orderType",1);//处理外获的订单
        condition.put("isOpenOrder",0);//是否开启抢单0:未开启
        condition.put("isDel",0);
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID poolId");//主键ID
        queryColumn.add("ORDER_TYPE orderType");//订单类型
        queryColumn.add("HOUSE_ID houseId");//房源ID
        queryColumn.add("APPLY_ID applyId");//申请ID
        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
        queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
        queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
        queryColumn.add("VERSION_NO versionNo");//版本号
        condition.put("queryColumn",queryColumn);
        condition.put("pageIndex",0);
        condition.put("pageSize",1);
        condition.put("needSort",1);
        condition.put("orderBy","VERSION_NO");
        condition.put("orderDirection","asc");
        //查询当前符合条件的订单池
        ResultVo resultVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
        if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            Date date = resultVo.getSystemTime();//获取系统当前时间
            List<Map<Object,Object>> lists = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(lists == null || lists.size()==0){
                return ;
            }
            Map<Object,Object> resultMap = lists.get(0);

            int houseId = StringUtil.getAsInt(StringUtil.trim(resultMap.get("houseId")));//房源ID
            //判断房源钥匙是否在平台
            Map<Object,Object> queryFilter =Maps.newHashMap();
            queryColumn.clear();
            queryColumn.add("ID houseId");//主键ID
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区城市
            queryColumn.add("HAVE_KEY haveKey");//是否有钥匙：0>无,1有
            queryColumn.add("LEASE_TYPE leaseType");   //预约类型（0：出租，1：出售）
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("id",houseId);
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, queryFilter);
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
            Map<Object,Object> houseMap  = null;
            if(houseList == null || houseList.size() == 0){
                return ;
            }
            houseMap = houseList.get(0);
            //查询所有外获业务员的ID
            //获取业务员位置及信息
            condition.clear();
            condition.put("locked",0);
            condition.put("isForbidden",0);
            condition.put("isDel",0);
            List<String> roleIds = new ArrayList<String>();
            roleIds.add("202cb962ac59075b964b07152d234b70");//外看业务员
            roleIds.add("2ece77221ad0b89f14f35899a8a63886");//钥匙管理员
            condition.put("roleIds",roleIds);
//            condition.put("city",houseMap.get("city"));
//            condition.put("community",houseMap.get("community"));
            //获取所有外看业务员
            List<Map<Object,Object>> users = memberService.selectOutsideUser(condition);
            //钥匙管理员数据
            List<Map<Object, Object>> keymasterUsers = Lists.newArrayList();
            List<Integer> userIds = Lists.newArrayList();
            for(int i = users.size() - 1; i >= 0; i--){
                Map<Object, Object> user = users.get(i);
                int userId  = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                if(StringUtil.trim(user.get("roleId")).equals("2ece77221ad0b89f14f35899a8a63886")){//如果钥匙管理员
                    keymasterUsers.add(user);
                    users.remove(user);
                    continue;
                }
                userIds.add(userId);
            }
            if(users == null){
                System.out.println("当前没有可用的业务员，此次派单失败");
                return ;
            }
            if(userIds == null || userIds.size()==0){
                System.out.println("当前没有可用的业务员，此次派单失败");
                return ;
            }

            condition.clear();
            condition.put("isFinished",0);//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            condition.put("taskType",1);//外看订单
            condition.put("isDel",0);
            long time = 30*60*1000;//60分钟
            Date beforeEstimatedTime = new Date(date.getTime() - time);//任务前1小时*/
            Date afterEstimatedTime = new Date(date.getTime() + time);//任务后1小时*/
            condition.put("beforeEstimatedTime",beforeEstimatedTime);
            condition.put("afterEstimatedTime",afterEstimatedTime);

            //自定义查询列名
            queryColumn.clear();
            queryColumn.add("USER_ID userId");//业务员ID
            condition.put("queryColumn",queryColumn);
            //排除前1小时和后1小时的
            ResultVo userTasksVo = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class, condition);
            //查询失败此次不做订单分配
            if(resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return ;
            }
            //非空闲业务员

            List<Integer> busyUsers = new ArrayList<>();
            List<Map<Object,Object>> busyUserList = (List<Map<Object, Object>>) userTasksVo.getDataSet();
            if(busyUserList!=null && busyUserList.size()>0){
                for (Map<Object, Object> busyUser : busyUserList) {
                    //业务员ID
                    int busyUserId = StringUtil.getAsInt(StringUtil.trim(busyUser.get("userId")));
                    for (Map<Object, Object> user : users) {
                        if(busyUserId == StringUtil.getAsInt(StringUtil.trim(user.get("userId")))){
                            user.remove(user);//将业务员排除此次派单过程
                            userIds.remove((Integer)busyUserId);
                            break;
                        }
                    }
                }
            }

            //统计业务员今天的任务数
            condition.clear();
            condition.put("locked",0);
            condition.put("isTransferOrder",0);//是否转单 0->未转单，1已转单
            condition.put("isFinished",0);//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            condition.put("isDel",0);
           // condition.put("toDays",1);//只查询今天的任务数
            condition.put("userIds",userIds);
            //统计今天的单量
            ResultVo userTasksGroupVo = memberService.selectGroupUserTasksByCondition(condition);
            List<Map<Object,Object>> userTasks  = (List<Map<Object, Object>>) userTasksGroupVo.getDataSet();
            for (Map<Object, Object> user : users) {
                String userId = StringUtil.trim(user.get("userId"));
                boolean isFind = false;//所有业务员是否有任务
                for (Map<Object, Object> userTask : userTasks) {
                    String _userId = StringUtil.trim(userTask.get("userId"));
                    if(userId.equals(_userId)){
                        isFind = true;
                        user.put("taskCount", userTask.get("count"));
                        break;
                    }
                }
                if(!isFind){
                    user.put("taskCount", 0);
                }
            }

            //符合条件的派单
            List<HsSystemOrderPool> orderPools = new ArrayList<HsSystemOrderPool>();
            List<HsSystemOrderPoolLog> orderPoolLogs = new ArrayList<HsSystemOrderPoolLog>();
            List<HsSystemUserOrderTasks> userOrderTasks = new ArrayList<HsSystemUserOrderTasks>();
            List<HsSystemUserOrderTasksLog> orderTaskLogs = new ArrayList<HsSystemUserOrderTasksLog>();
            //分配派单逻辑
            //系统订单池
            //此订单约定的见面地点
            String appointmentMeetPlace = StringUtil.trim(resultMap.get("appointmentMeetPlace"));
            int mixTaskCount = 10000;//单量最少的业务员
            int _userId = -1;
            //业务员用户手机号
            String userMobile = "";
            int index = 0;//记录当前
            //业务员信息
            for ( int i  = 0 ; i< users.size() ;i++) {
                Map<Object, Object> user = users.get(i);
                //判断业务员所属位置
                String city = StringUtil.trim(user.get("city"));
                String community = StringUtil.trim(user.get("community"));
                int taskCount = StringUtil.getAsInt(StringUtil.trim(user.get("taskCount")));
                if( -1 != appointmentMeetPlace.indexOf(city) && -1 != appointmentMeetPlace.indexOf(community)){ // 查询所属业务员负责地址
                    if(taskCount<=mixTaskCount){//选择最小的
                        mixTaskCount = taskCount;
                        _userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                        userMobile = StringUtil.trim(user.get("mobile"));
                        index = i;
                    }
                }else{
                    users.remove(user);
                }
                if(i == users.size()-1 && _userId != -1){//完成了所有业务员的计算 匹配到用户
                    users.get(index).put("taskCount",taskCount+1);
                }
            }


            //钥匙管理员ID
            String keyMasterId = "-1";
            //钥匙管理员手机号
            String keyMasterMobile = "";
            //匹配到用户
            if(_userId != -1){
                int poolId  = StringUtil.getAsInt(StringUtil.trim(resultMap.get("poolId")));
                int orderType = StringUtil.getAsInt(StringUtil.trim(resultMap.get("orderType")));
                int applyId = StringUtil.getAsInt(StringUtil.trim(resultMap.get("applyId")));//申请ID
                if(houseList!=null && houseList.size()>0){

//                    if(StringUtil.getAsInt(StringUtil.trim(houseMap.get("haveKey"))) == 1){//钥匙在平台
                        //查询所属钥匙管理员信息
                        for ( int i  = 0 ; i< keymasterUsers.size() ;i++) {
                            Map<Object, Object> keymasterUser = keymasterUsers.get(i);
                            //判断钥匙管理员所属位置
                            String userId = StringUtil.trim(keymasterUser.get("userId"));
                            String city = StringUtil.trim(keymasterUser.get("city"));
                            String community = StringUtil.trim(keymasterUser.get("community"));
                            if( -1 != appointmentMeetPlace.indexOf(city) && -1 != appointmentMeetPlace.indexOf(community)){ // 查询所属业务员负责地址
                                keyMasterId = userId;
                                keyMasterMobile = StringUtil.trim(keymasterUser.get("mobile"));
                                break;
                            }
                        }
//                    }
                }

                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                orderPool.setId(poolId);
                orderPool.setIsFinished(1);
                orderPool.setRemark("系统自动派单完成");
                orderPool.setUpdateTime(date);
                orderPool.setVersionNo(StringUtil.getAsInt(StringUtil.trim(resultMap.get("versionNo"))));
                orderPools.add(orderPool);

                HsSystemOrderPoolLog log = new HsSystemOrderPoolLog();
                log.setOrderType(orderType);//外获订单
                log.setNodeType(1);
                log.setPoolId(poolId);
                log.setIsDel(0);
                log.setCreateTime(date);
                log.setUpdateTime(date);
                log.setPostTime(date);
                log.setRemarks("系统自动派单完成--外看业务员分发单");
                orderPoolLogs.add(log);

                HsSystemUserOrderTasks tasks = new HsSystemUserOrderTasks();
                tasks.setHouseId(houseId);
                tasks.setApplyId(applyId);
                tasks.setUserId(_userId);
                tasks.setPoolId(poolId);
                tasks.setTaskType(orderType);//外获任务
                tasks.setCreateTime(date);
                tasks.setEstimatedTime((Date) resultMap.get("estimatedTime"));
                tasks.setRemark("系统自动派单完成--外看业务员分发单");
                tasks.setVersionNo(0);
                tasks.setStandby1(keyMasterId);//钥匙管理员ID
                tasks.setStandby2("0");//未完成
                userOrderTasks.add(tasks);

                HsSystemUserOrderTasksLog tasksLog = new HsSystemUserOrderTasksLog();
                tasksLog.setNodeType(0);
                tasksLog.setPoolId(poolId);
                tasks.setIsDel(0);
                tasksLog.setCreateTime(date);
                tasksLog.setRemarks("系统自动派单完成--外看业务员分发单");
                orderTaskLogs.add(tasksLog);
            }else{
                HsSystemOrderPool orderPool = new HsSystemOrderPool();
                orderPool.setId(StringUtil.getAsInt(StringUtil.trim(resultMap.get("poolId"))));
                orderPool.setUpdateTime(date);
                orderPool.setVersionNo(StringUtil.getAsInt(StringUtil.trim(resultMap.get("versionNo"))));
                orderService.update(orderPool);
            }

            if(userOrderTasks!=null && userOrderTasks.size()>0){
                condition.put("data",userOrderTasks);
                condition.put("logData",orderTaskLogs);
                resultVo = memberService.batchInsert(new HsSystemUserOrderTasks(),condition);
                if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    condition.clear();
                    condition.put("data",orderPools);
                    condition.put("logData",orderPoolLogs);
                    resultVo = orderService.batchUpdate(new HsSystemOrderPool(),condition);

                    ResultVo seeHouseVo = memberService.updateSeeHouseApply(resultMap);
                    if(seeHouseVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        int poolId = StringUtil.getAsInt(StringUtil.trim(resultMap.get("poolId")));
                        int leaseType = StringUtil.getAsInt(StringUtil.trim(houseMap.get("leaseType")));
                        int haveKey = StringUtil.getAsInt(StringUtil.trim(houseMap.get("haveKey")));
                        String estimatedTime = StringUtil.trim(resultMap.get("estimatedTime"));
                        //封装消息详情
                        MsgDetails msgDetails = new MsgDetails();
                        //房源类型 预约类型（0：出租，1：出售）
                        msgDetails.setLeaseType(leaseType);
                        //是否系统派单0:自动派单，1：业务员手动抢单
                        msgDetails.setIsSystem(0);
                        //是否有钥匙：0>无,1有
                        msgDetails.setHaveKey(haveKey);
                        //见面地点
                        msgDetails.setAppointmentMeetPlace(appointmentMeetPlace);
                        //见面时间（客户预约时间）
                        msgDetails.setEstimatedTime(estimatedTime);
                        //外看业务员推送消息
                        boolean push = push(msgDetails, userMobile, _userId,3,"您有新的外看订单",7);
                        System.out.println("lsq====:systemAllocationLookOutOrder外看业务员推送结果为：" + push);
                        if(!"-1".equals(keyMasterId)){
                            //钥匙管理员推送消息
                            boolean keyMasterPush = push(msgDetails, keyMasterMobile, _userId,4,"您有新的外看订单",7);
                            System.out.println("lsq====:systemAllocationLookOutOrder钥匙管理员外看订单推送结果为：" + keyMasterPush);
                        }

                    }
                }
            }
        }
    }




    public void systemDistributionOrder(){

        OrderService orderService = SpringContextUtils.getBean("orderService");
        MemberService memberService = SpringContextUtils.getBean("memberService");
        HousesService housesService = SpringContextUtils.getBean("housesService");
        Map<Object,Object> condition = new HashMap<Object,Object>();
        condition.put("isFinished",0);//查询未完成订单
        condition.put("orderType",0);//处理外获的订单
        condition.put("isDel",0);
        try {
            //查询外获的订单
            ResultVo resultVo = orderService.selectList(new HsSystemOrderPool(), condition, 0);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> orders = (List<Map<Object,Object>>) resultVo.getDataSet();
                if(orders==null || orders.size()==0) return;
                //0外获订单->1-外看订单->2合同订单
                List<Integer> houseIds = new ArrayList<Integer>();//房源ids
                List<Integer> str1 = new ArrayList<Integer>();//外获订单
                List<Integer> str2 = new ArrayList<Integer>();//外看订单
                List<Integer> str3 = new ArrayList<Integer>();//合同订单
                for (Map<Object,Object> order : orders) {
                    Integer orderType = StringUtil.getAsInt(StringUtil.trim(order.get("orderType")));
                    Integer houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                    houseIds.add(houseId);
                    if(orderType == 0){
                        str1.add(houseId);
                    }else if(orderType == 1){
                        str2.add(houseId);
                    }else if(orderType == 2){
                        str3.add(houseId);
                    }
                }
                if(houseIds==null || houseIds.size()==0) return;
                //去重
                HashSet h = new HashSet(houseIds);
                houseIds.clear();
                houseIds.addAll(h);
                condition.clear();
                condition.put("houseIds",houseIds);
                //获取房源位置
                List<Map<Object,Object>> houses = housesService.loadHousesPosition(condition);
                if(houses == null){
                    System.out.println("房源信息获取异常");
                    return ;
                }
                //获取业务员位置及信息
                condition.clear();
                condition.put("locked",0);
                condition.put("isForbidden",0);
                condition.put("isDel",0);
                List<String> roleIds = new ArrayList<String>();
                if(str1.size()>0){
                    roleIds.add("a0a080f42e6f13b3a2df133f073095dd");//外获业务员
                }
                if(str2.size()>0 || str3.size()>0){
                    roleIds.add("202cb962ac59075b964b07152d234b70");//外看业务员
                }
                condition.put("roleIds",roleIds);
                //计算可用的业务员
                List<Map<Object,Object>> users = memberService.allotUsableUsers(condition);
                if(users == null){
                    System.out.println("当前没有可用的业务员，此次派单失败");
                    return ;
                }
                int size  = houses.size();
                List<LatLng> waihuo_destinationLists = new ArrayList<>();//外获业务员
                List<LatLng> waikan_destinationLists = new ArrayList<>();//外看业务员
                for (int j=0;j<size;j++ ) {
                    Map<Object, Object> house = houses.get(j);
                    double longitude = StringUtil.getDouble(StringUtil.trim(house.get("longitude")));
                    double latitude = StringUtil.getDouble(StringUtil.trim(house.get("latitude")));
                    Integer _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("id")));
                    LatLng LatLng = new LatLng(latitude,longitude);
                    if(str1.contains(_houseId)){
                        waihuo_destinationLists.add(LatLng);
                    }else{
                        waikan_destinationLists.add(LatLng);
                    }
                }
                LatLng[] waihuo_destinations = new LatLng[waihuo_destinationLists.size()];//外获
                LatLng[] waikan_destinations = new LatLng[waikan_destinationLists.size()];//外看
                waihuo_destinations = (LatLng[]) waihuo_destinationLists.toArray(waihuo_destinations);
                waikan_destinations = (LatLng[]) waikan_destinationLists.toArray(waikan_destinations);
                //房源与订单关联
                for(int i=0;i<users.size();i++){
                    Map<Object,Object> suitable = new HashMap<Object,Object>();
                    Map<Object,Object> user = users.get(i);
                    double _longitude = (double) user.get("longitude");
                    double _latitude = (double) user.get("latitude");
                    LatLng origins = new LatLng(_latitude,_longitude);//地点
                    String roleId = StringUtil.trim(user.get("roleId"));//角色类型
                    DistanceMatrix await = null;
                    if(roleId.equals("a0a080f42e6f13b3a2df133f073095dd")){//外获业务员
                        await = DistanceMatrixApi.newRequest(GoogleMapUtil.context).origins(origins).destinations(waihuo_destinations).mode(TravelMode.DRIVING).await();
                    }else if(roleId.equals("202cb962ac59075b964b07152d234b70")){//外看业务员
                        await = DistanceMatrixApi.newRequest(GoogleMapUtil.context).origins(origins).destinations(waikan_destinations).mode(TravelMode.DRIVING).await();
                    }
                    System.out.println(JsonUtil.toJson(await));
                    int optimalUserIndex = -1;
                    for (DistanceMatrixRow row : await.rows) {
                        DistanceMatrixElement[] elements = row.elements;
                        if(elements[0].duration==null) break; ;
                        long min = elements[0].duration.inSeconds;
                        for (DistanceMatrixElement element : elements) {
                            optimalUserIndex ++;
                            if(element.status==DistanceMatrixElementStatus.OK){
                                Distance distance = element.distance;
                                Duration duration = element.duration;
                                if(min>duration.inSeconds){
                                    min = duration.inSeconds;
                                    suitable.put("distance",distance.inMeters);// 距离
                                    suitable.put("distanceValue",distance.humanReadable);
                                    suitable.put("duration",duration.inSeconds);// 持续的时间
                                    suitable.put("durationValue",duration.humanReadable);
                                }
                            }
                        }
                    }
                    Integer houseId = (Integer) houses.get(optimalUserIndex).get("id");
                    if(str1.contains(houseId)){
                        waihuo_destinationLists.remove(optimalUserIndex);
                        waihuo_destinations = (LatLng[]) waihuo_destinationLists.toArray(waihuo_destinations);
                    }else{
                        waikan_destinationLists.remove(optimalUserIndex);
                        waikan_destinations = (LatLng[]) waikan_destinationLists.toArray(waikan_destinations);
                    }
                    suitable.put("houseId",houseId);
                    user.putAll(suitable);
                    suitable.clear();
                }
                //符合条件的派单
                List<HsSystemOrderPool> orderPools = new ArrayList<HsSystemOrderPool>();
                List<HsSystemOrderPoolLog> orderPoolLogs = new ArrayList<HsSystemOrderPoolLog>();
                List<HsSystemUserOrderTasks> orderTasks = new ArrayList<HsSystemUserOrderTasks>();
                List<HsSystemUserOrderTasksLog> orderTaskLogs = new ArrayList<HsSystemUserOrderTasksLog>();
                Date now_date = new Date();
                for (Map<Object,Object> order : orders) {
                    Integer _houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                    Integer id = StringUtil.getAsInt(StringUtil.trim(order.get("id")));
                    Integer orderType = StringUtil.getAsInt(StringUtil.trim(order.get("orderType")));
                    for (Map<Object, Object> user : users) {
                        Integer houseId = StringUtil.getAsInt(StringUtil.trim(user.get("houseId")), -1);
                        if (houseId == _houseId) {
                            Integer userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")), -1);
                            HsSystemOrderPool orderPool = new HsSystemOrderPool();
                            orderPool.setId(id);
                            orderPool.setIsFinished(1);
                            orderPool.setRemark("系统自动派单完成");
                            orderPool.setUpdateTime(now_date);
                            orderPools.add(orderPool);

                            HsSystemOrderPoolLog log = new HsSystemOrderPoolLog();
                            log.setOrderType(orderType);
                            log.setNodeType(1);
                            log.setPoolId(id);
                            log.setIsDel(0);
                            log.setCreateTime(now_date);
                            log.setUpdateTime(now_date);
                            log.setPostTime(now_date);
                            log.setRemarks("系统自动派单完成--外获业务员接单");
                            orderPoolLogs.add(log);

                            HsSystemUserOrderTasks tasks = new HsSystemUserOrderTasks();
                            tasks.setHouseId(houseId);
                            tasks.setUserId(userId);
                            tasks.setPoolId(id);
                            tasks.setTaskType(orderType);
                            tasks.setCreateTime(now_date);
                            tasks.setRemark("distance :"+user.get("distanceValue") +"  duration :" +user.get("durationValue") );
                            orderTasks.add(tasks);

                            HsSystemUserOrderTasksLog tasksLog = new HsSystemUserOrderTasksLog();
                            tasksLog.setNodeType(0);
                            tasksLog.setPoolId(id);
                            tasks.setIsDel(0);
                            tasksLog.setCreateTime(now_date);
                            tasksLog.setRemarks("系统派单创建任务");
                            orderTaskLogs.add(tasksLog);
                        }
                    }
                }
                if(orderPools!=null &&orderPools.size()>0){
                    condition.clear();
                    condition.put("data",orderPools);
                    condition.put("logData",orderPoolLogs);
                    resultVo = orderService.batchUpdate(new HsSystemOrderPool(),condition);
                    if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        condition.put("data",orderTasks);
                        condition.put("logData",orderTaskLogs);
                        resultVo = memberService.batchInsert(new HsSystemUserOrderTasks(),condition);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息推送
     * @param msgDetails            消息详情
     * @param userMobile            用户手机号
     * @param userId                用户id
     * @param platform              平台类型 外部:1 外获:2 外看:3 区域长:4
     * @param msg                   推送内容
     * @param msgCode               消息类型code 字典表dict_msg
     * @return
     * @throws Exception
     */
    public boolean push(MsgDetails msgDetails,String userMobile,Integer userId,Integer platform,String msg,Integer msgCode) throws Exception {
        //lsq 将推送消息存入数据库
        //将消息详情转换为json字符串
        String msgMapJsonStr = JSON.toJSONString(msgDetails);
        //插入消息记录
        HsMsgRecord hsMsgRecord = new HsMsgRecord();
        hsMsgRecord.setUserName(userMobile);
        hsMsgRecord.setPlatform(platform);
        hsMsgRecord.setMsgCode(msgCode);
        hsMsgRecord.setAlert(msg);
        hsMsgRecord.setDetails(msgMapJsonStr);
        hsMsgRecord.setCreateTime(new Date());
        hsMsgRecord.setCreateBy(userId);
        hsMsgRecord.setIsRead(0);
        ResultVo insert = commonService.insert(hsMsgRecord);
        if(insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            //发送推送消息
            return UmengUtil.aliasSend(0, userMobile, msg, platform, msgCode);
        }
        return false;
    }

    /**
     * 系统分配内勤人员
     * @throws Exception
     */
    public void systemAllocationOfficeStaff()throws Exception {
        if(orderService==null){
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null){
            memberService = SpringContextUtils.getBean("memberService");
        }
        if(commonService==null){
            commonService = SpringContextUtils.getBean("commonService");
        }
        /**
         * 获取未分配内勤人员的订单
         */
        //自定义查询的列名
        Map<Object,Object> condition = new HashMap<>(16);
        condition.put("isDel", 0);
        condition.put("tradingStatus", 0);
        //订单状态 2:财务已审核（待确认正式合同）
        condition.put("orderStatus", 2);
        condition.put("standby1Null", "null");
        ResultVo orderResultVo = orderService.selectList(new HsHousingOrder(),condition,1);
        if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            //订单列表
            List<HsHousingOrder> orderList = (List<HsHousingOrder>) orderResultVo.getDataSet();
            if(orderList == null || orderList.size() < 1){
                return ;
            }
            /**
             * 获取内勤人员列表
             */
            //查询所有外获业务员的ID
            //获取业务员位置及信息
            condition.clear();
            condition.put("locked",0);
            condition.put("isForbidden",0);
            condition.put("isDel",0);
            List<String> roleIds = new ArrayList<String>();
            //内勤人员
            roleIds.add("40e66a433e3a701bb138a7bbe839f42e");
            condition.put("roleIds",roleIds);
            //获取所有外看业务员
            List<Map<Object,Object>> users = memberService.selectOutsideUser(condition);
            List<Integer> userIds = Lists.newArrayList();
            for(int i = users.size() - 1; i >= 0; i--){
                Map<Object, Object> user = users.get(i);
                int userId  = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                if(userId > 0){
                    userIds.add(userId);
                }
            }
            if(users == null || users.size() < 1){
                System.out.println("systemAllocationOfficeStaff 当前没有可用的业务员，此次派单失败");
                return ;
            }

            /**
             * 获取内勤人员正在进行的任务数量
             */
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            //订单数量
            queryColumn.add("count(0) as `count`");
            //内勤人员id
            queryColumn.add("STANDBY1 standby1");
            condition.clear();
            condition.put("queryColumn", queryColumn);
            condition.put("isDel", 0);
            condition.put("tradingStatus", 0);
            condition.put("groupBy", "standby1");
            condition.put("orderBy", "count");
            ResultVo countResultVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(countResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                System.out.println("获取任务数量失败");
                return ;
            }
            List<Map<Object, Object>> countList = (List<Map<Object, Object>>) countResultVo.getDataSet();
            /**
             * 获取内勤人员任务数量
             */
            //内勤人员任务数量 key人员id value任务数量
            Map<Integer,Integer> userCountMap = new HashMap<>(16);
            for (Map<Object, Object> userMap : users) {
                int userId = StringUtil.getAsInt(StringUtil.trim(userMap.get("userId")));
                //任务数量
                int count = 0;
                Optional<Map<Object, Object>> countOptional = countList.stream().filter(map -> StringUtil.getAsInt(StringUtil.trim(map.get("standby1"))) == userId).findFirst();
                if(countOptional.isPresent()){
                    Map<Object, Object> countMap = countOptional.get();
                    count = StringUtil.getAsInt(StringUtil.trim(countMap.get("count")),0);
                }
                userCountMap.put(userId,count);
            }

            Date nowTime = new Date();
            //需要更新的订单列表
            List<HsHousingOrder> updateOrderList = new ArrayList<>();
            for (HsHousingOrder housingOrder : orderList) {
                List<Map.Entry<Integer,Integer>> list = new ArrayList(userCountMap.entrySet());
                if(list.size() < 1){
                    System.out.println("systemAllocationOfficeStaff 没有匹配到内勤人员");
                    return;
                }
                Collections.sort(list, (o1, o2) -> (o1.getValue() - o2.getValue()));
                //任务最少的内勤人员id
                Integer userId = list.get(0).getKey();
                housingOrder.setStandby1(StringUtil.trim(userId));
                updateOrderList.add(housingOrder);
                //累加当前内勤人员次数
                Integer integer = userCountMap.get(userId) + 1;
                userCountMap.put(userId,integer);
            }

            /**
             * 更新订单及日志
             */
            if(updateOrderList.size() > 0){
                condition.clear();
                condition.put("data",updateOrderList);
                ResultVo batchUpdateResultVo = orderService.batchUpdate(new HsHousingOrder(), condition);
                if(batchUpdateResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    System.out.println("systemAllocationOfficeStaff 更新订单失败");
                }

            }

        }

    }


    /**
     * 关闭预约看房聊天
     * 距离当前时间还有一小时的预约聊天群需要关闭，同时推送消息给聊天双方
     * @throws Exception
     */
    public void closeLookHouseConversation()throws Exception {
        if(orderService==null){
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null){
            memberService = SpringContextUtils.getBean("memberService");
        }
        if(commonService==null){
            commonService = SpringContextUtils.getBean("commonService");
        }
        /*获取正在预约中的聊天群*/
        Map<Object,Object> condition = new HashMap<>(16);
        //自定义查询的列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");
        //客户ID
        queryColumn.add("MEMBER_ID memberId");
        //业主ID
        queryColumn.add("OWNER_ID ownerId");
        //环信群ID
        queryColumn.add("GROUP_ID groupId");
        //房屋类型（0 房屋出租 1房屋出售）
        queryColumn.add("HOUSE_TYPE houseType");
        //是否取消进行过提醒 1 已进行过提醒
        queryColumn.add("STANDBY2 standby2");
        condition.put("queryColumn", queryColumn);
        condition.put("isDel", 0);
        //是否取消0:不取消，1：客户取消 2：业主取消
        condition.put("isCancel", 0);
        //预约类型（0：申请预约，1：无需预约，参与看房，2：让客服联系 3:联系租客）
        condition.put("applicationType", 0);
        //订单池id为null
        condition.put("poolIdIsNull",0);
        //standby2为null
        condition.put("standby2IsNull",1);
        ResultVo applicationResultVo = memberService.selectCustomColumnNamesList(HsMemberHousingApplication.class, condition);
        if(applicationResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object, Object>> applicationList = (List<Map<Object, Object>>) applicationResultVo.getDataSet();
            if(applicationList != null && applicationList.size() > 0){
                /*获取最后一条消息*/
                //正在预约中的id集合
                List<String> groupIds = applicationList.stream().map(map -> StringUtil.trim(map.get("groupId"))).collect(Collectors.toList());
                condition.clear();
                queryColumn.clear();
                //环信群ID
                queryColumn.add("GROUP_ID groupId");
                //预约看房日期
                queryColumn.add("START_APARTMENT_TIME startApartmentTime");
                condition.put("queryColumn",queryColumn);
                condition.put("groupIds",groupIds);
                condition.put("isDel",0);
                List<Map<Object, Object>> msgList = memberService.getLastMsg(new HsMemberHousingApplicationMessage(), condition);
                if(msgList != null && msgList.size() > 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    //需要删除的环信群
                    List<String> delGroupIdList = new ArrayList<>();
                    //需要提醒的环信群
                    List<String> pushGroupIdList = new ArrayList<>();
                    for (Map<Object, Object> msgMap : msgList) {
                        String startApartmentTime = StringUtil.trim(msgMap.get("startApartmentTime"));
                        if(startApartmentTime != null && startApartmentTime.endsWith(".0")){
                            //记录聊天群id
                            String groupId = StringUtil.trim(msgMap.get("groupId"));
                            //是否已经提醒 1 已进行过提醒
                            String standby2 = StringUtil.trim(msgMap.get("standby2"));
                            startApartmentTime = startApartmentTime.substring(0,startApartmentTime.length() - 2);
                            //预约时间
                            Date parse = sdf.parse(startApartmentTime);
                            //预约时间的前一个小时
                            long apartmentTime = parse.getTime() - 60 * 60 * 1000;
                            //预约时间的前90分钟
                            long apartmentTime90 = parse.getTime() - 90 * 60 * 1000;
                            long nowTime = date.getTime();
                            //当前时间小于预约时间的前一个小时，此时需要推送消息并删除聊天群
                            if(apartmentTime < nowTime){
                                if(StringUtil.hasText(groupId)){
                                    delGroupIdList.add(groupId);
                                }
                            }else if(apartmentTime90 < nowTime && !"1".equals(standby2)){
                                if(StringUtil.hasText(groupId)){
                                    pushGroupIdList.add(groupId);
                                }
                            }
                        }
                    }
                    //需要提醒或删除的groupId集合
                    List<Map<Object, Object>> delList = applicationList.stream().filter(map -> delGroupIdList.contains(StringUtil.trim(map.get("groupId")))
                        || pushGroupIdList.contains(StringUtil.trim(map.get("groupId")))).collect(Collectors.toList());
                    //人员id （业主及客户id）
                    List<Integer> memberIds = new ArrayList<>();
                    //预约id
                    List<Integer> applyIds = new ArrayList<>();
                    //提醒过的预约信息
                    List<String> groupList = new ArrayList<>();
                    for (Map<Object, Object> map : delList) {
                        int id = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                        //买家id
                        int memberId = StringUtil.getAsInt(StringUtil.trim(map.get("memberId")));
                        //业主id
                        int ownerId = StringUtil.getAsInt(StringUtil.trim(map.get("ownerId")));
                        String groupId = StringUtil.trim(map.get("groupId"));
                        if(pushGroupIdList.contains(groupId)){
                            memberIds.add(memberId);
                            memberIds.add(ownerId);
                            groupList.add(groupId);
                            continue;
                        }
                        if(delGroupIdList.contains(groupId)){
                            applyIds.add(id);
                            //删除环信群
                            EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
                            Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                            JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                            JSONObject resultObjecct = object.getJSONObject("data");
                        }


                    }
                    /*删除预约信息*/
                    if(applyIds.size() > 0){
                        List<String> setColumn = new ArrayList<>();
                        //是否取消0:不取消，1：客户取消 2：业主取消 3:系统取消
                        setColumn.add("IS_CANCEL = 3");
                        condition.clear();
                        condition.put("setColumn",setColumn);
                        condition.put("ids", applyIds);
                        condition.put("IS_CANCEL", 0);
                        Integer updateSize = memberService.updateCustomColumnByCondition(new HsMemberHousingApplication(), condition);
                        if(updateSize == null || updateSize != applyIds.size()){
                            System.out.println("closeLookHouseConversation更新预约信息失败=============");
                        }
                    }
                    if(memberIds.size() > 0){
                        /*获取人员信息*/
                        condition.clear();
                        queryColumn.clear();
                        //环信群ID
                        queryColumn.add("ID id");
                        //预约看房日期
                        queryColumn.add("MEMBER_MOBLE memberMoble");
                        condition.put("queryColumn",queryColumn);
                        condition.put("memberIds",memberIds);
                        condition.put("isDel",0);
                        ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                        if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object, Object>> memberList = (List<Map<Object, Object>>) memberResultVo.getDataSet();
                            if(memberList != null && memberList.size() > 0){
                                for (Map<Object, Object> memberMap : memberList) {
                                    String memberMoble = StringUtil.trim(memberMap.get("memberMoble"));
                                    int id = StringUtil.getAsInt(StringUtil.trim(memberMap.get("id")));
                                    /*推送消息*/
                                    MsgDetails msgDetails = new MsgDetails();
                                    boolean push = push(msgDetails, memberMoble, id, 1,"您有一条预约看房信息将在半小时后过期",1);
                                }
                            }
                        }
                    }
                    /*更新提醒信息*/
                    if(groupList.size() > 0){
                        List<String> setColumn = new ArrayList<>();
                        //是否取消进行过提醒 1 已进行过提醒
                        setColumn.add("STANDBY2 = 1");
                        condition.clear();
                        condition.put("setColumn",setColumn);
                        condition.put("groupIds", groupList);
                        Integer updateSize = memberService.updateCustomColumnByCondition(new HsMemberHousingApplication(), condition);
                        if(updateSize == null || updateSize != groupList.size()){
                            System.out.println("closeLookHouseConversation更新预约提醒状态失败=============");
                        }
                    }

                }

            }
        }


    }

    /**
     * 关闭议价聊天
     * 2小时推送
     * 4小时短信
     * 2天删除
     * @throws Exception
     */
    public void closeBargainConversation()throws Exception{
        if(orderService==null){
            orderService = SpringContextUtils.getBean("orderService");
        }
        if(memberService==null){
            memberService = SpringContextUtils.getBean("memberService");
        }
        if(commonService==null){
            commonService = SpringContextUtils.getBean("commonService");
        }
        /*获取议价中的信息*/
        Map<Object,Object> condition = new HashMap<>(16);
        //自定义查询的列名
        List<String> queryColumn = new ArrayList<>();
        condition.put("isDel", 0);
        //议价状态（0 议价中 1 议价成功 2 议价失败）
        condition.put("bargainStatus", 0);
        ResultVo bargainResultVo = memberService.selectList(new HsMemberHousingBargain(), condition,0);
        if(bargainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object, Object>> bargainList = (List<Map<Object, Object>>) bargainResultVo.getDataSet();
            if(bargainList != null && bargainList.size() > 0){
                /*获取正在议价中的最后一条消息*/
                List<Integer> bargainIds = bargainList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("id")))).collect(Collectors.toList());
                condition.clear();
                queryColumn.add("ID id");
                queryColumn.add("BARGAIN_ID bargainId");
                queryColumn.add("SENDER sender");
                queryColumn.add("CREATE_TIME createTime");
                condition.put("queryColumn",queryColumn);
                condition.put("bargainIds",bargainIds);
                List<Map<Object, Object>> bargainMessageList = memberService.getLastMsg(new HsMemberHousingBargainMessage(), condition);
                if(bargainMessageList != null && bargainMessageList.size() > 0){
                    List<String> setColumn = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    //需要推送提醒的议价
                    Map<Integer,Integer> pushMap = new HashMap<>(16);
                    //需要短信提醒的议价
                    //key 议价id；value 最后一条消息创建时间 及发送者
                    Map<Integer,String[]> smsMap = new HashMap<>(16);
                    //需要删除的议价
                    Map<Integer,Integer> delMap = new HashMap<>(16);
                    for (Map<Object, Object> msgMap : bargainMessageList) {
                        String createTime = StringUtil.trim(msgMap.get("createTime"));
                        //记录聊天群id
                        Integer bargainId = StringUtil.getAsInt(StringUtil.trim(msgMap.get("bargainId")));
                        //消息发送者 0 客户 1 业主
                        Integer sender = StringUtil.getAsInt(StringUtil.trim(msgMap.get("sender")));
                        if(createTime != null && createTime.endsWith(".0")){
                            createTime = createTime.substring(0,createTime.length() - 2);
                            //最后一条消息创建时间
                            Date parse = sdf.parse(createTime);
                            //预约时间的前一个小时
                            long lastMsgTime = parse.getTime();
                            //当前时间的前2小时
                            long nowTime2Hour = date.getTime() - 2 * 60 * 60 * 1000;
                            //当前时间前4个小时
                            long nowTime4Hour = date.getTime() - 4 * 60 * 60 * 1000;
                            //当前时间前2天
                            long nowTime2Day = date.getTime() - 2 * 24 * 60 * 60 * 1000;
                            if(nowTime2Day > lastMsgTime){
                                //删除议价
                                delMap.put(bargainId,sender);
                            }else if(nowTime4Hour > lastMsgTime){
                                //短信提示
                                //0 最后一条议价消息创建时间 HH:mm；1 消息发送者 0 客户 1 业主
                                String[] smsArr = new String[2];
                                smsArr[0] = createTime;
                                smsArr[1] = sender.toString();
                                smsMap.put(bargainId,smsArr);
                            }else if(nowTime2Hour > lastMsgTime){
                                //推送消息提醒
                                pushMap.put(bargainId,sender);
                            }
                        }
                    }
                    /*删除议价*/
                    List<Integer> delIdList = delMap.entrySet().stream().map(map -> map.getKey()).collect(Collectors.toList());
                    if(delIdList.size() > 0){
                        //删除环信群信息
                        List<Map<Object, Object>> delList = bargainList.stream().filter(map -> delIdList.contains(StringUtil.getAsInt(StringUtil.trim(map.get("id"))))
                                && StringUtil.getAsInt(StringUtil.trim(map.get("isDel"))) != 1).collect(Collectors.toList());
                        delIdList.clear();
                        for (Map<Object, Object> map : delList) {
                            int id = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                            delIdList.add(id);
                            String groupId = StringUtil.trim(map.get("groupId"));
                            //删除环信群
                            EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
                            Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                            JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                            JSONObject resultObjecct = object.getJSONObject("data");
                        }
                        //删除数据库数据
                        setColumn.add("IS_DEL = 1");
                        condition.clear();
                        condition.put("setColumn",setColumn);
                        condition.put("ids", delIdList);
                        condition.put("isDel", 0);
                        Integer delSize = memberService.updateCustomColumnByCondition(new HsMemberHousingBargain(), condition);
                        if(delSize == null || delSize != delIdList.size()){
                            System.out.println("删除过期议价信息失败=============");
                        }
                    }

                    /*获取需要推送或者短信提醒的议价人员信息*/
                    //需要短信提醒的议价
                    List<Integer> smsIdList = smsMap.entrySet().stream().map(map -> map.getKey()).collect(Collectors.toList());
                    //需要推送提醒的议价列表
                    List<Integer> pushIdList = pushMap.entrySet().stream().map(map -> map.getKey()).collect(Collectors.toList());
                    //需要短信提醒的人员 key 人员id；value 最后一条消息时间
                    Map<Integer,String> msMemberMap = new HashMap<>(16);
                    //需要推送提醒的人员id
                    List<Integer> pushMemberIdList = new ArrayList<>();
                    //需要修改推送提醒状态的议价ids
                    List<Integer> pushBargainIdList = new ArrayList<>();
                    //需要修改短信提醒状态的议价ids
                    List<Integer> msmBargainIdList = new ArrayList<>();
                    for (Map<Object, Object> bargainMap : bargainList) {
                        int id = StringUtil.getAsInt(StringUtil.trim(bargainMap.get("id")));
                        //买家id
                        int memberId = StringUtil.getAsInt(StringUtil.trim(bargainMap.get("memberId")));
                        //业主id
                        int ownerId = StringUtil.getAsInt(StringUtil.trim(bargainMap.get("ownerId")));
                        // 是否已进行提醒 1:已进行推送提醒 2:已进行短信提醒 promptStatus
                        int promptStatus = StringUtil.getAsInt(StringUtil.trim(bargainMap.get("promptStatus")));
                        if(smsIdList.contains(id) && promptStatus != 2){
                            String[] arr = smsMap.get(id);
                            //最后一条消息的时间
                            String timeStr = arr[0];
                            //消息发送者 0 客户 1 业主
                            int sender = StringUtil.getAsInt(arr[1]);
                            if(sender == 0){
                                msMemberMap.put(ownerId,timeStr);
                            }else{
                                msMemberMap.put(memberId,timeStr);
                            }
                            msmBargainIdList.add(id);
                            continue;
                        }
                        if(pushIdList.contains(id) && promptStatus != 1){
                            Integer sender = pushMap.get(id);
                            if(sender == 0){
                                pushMemberIdList.add(ownerId);
                            }else{
                                pushMemberIdList.add(memberId);
                            }
                            pushBargainIdList.add(id);
                        }
                    }

                    /*获取人员信息*/
                    //需要短信提醒的人员ids
                    List<Integer> smsMemberIdList = msMemberMap.entrySet().stream().map(map -> map.getKey()).collect(Collectors.toList());
                    //需要推送的手机号及模板参数
                    Map<String,String> mobileMap = new HashMap<>(1);
                    //key区号 value mobileMap
                    Map<String,Map<String,String>> sendSmsMap = new HashMap<>(16);
                    //需要短信或推送提醒的人员ids
                    List<Integer> memberIds = new ArrayList<>();
                    memberIds.addAll(smsMemberIdList);
                    memberIds.addAll(pushMemberIdList);
                    if(memberIds.size() > 0){
                        condition.clear();
                        queryColumn.clear();
                        //环信群ID
                        queryColumn.add("ID id");
                        //预约看房日期
                        queryColumn.add("MEMBER_MOBLE memberMoble");
                        condition.put("queryColumn",queryColumn);
                        condition.put("memberIds",memberIds);
                        condition.put("isDel",0);
                        ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                        if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object, Object>> memberList = (List<Map<Object, Object>>) memberResultVo.getDataSet();
                            if(memberList != null && memberList.size() > 0){
                                for (Map<Object, Object> memberMap : memberList) {
                                    String areaCode = StringUtil.trim(memberMap.get("areaCode"), "86");
                                    String memberMoble = StringUtil.trim(memberMap.get("memberMoble"));
                                    int id = StringUtil.getAsInt(StringUtil.trim(memberMap.get("id")));
                                    if(smsMemberIdList.contains(id)){
                                        /*封装短信推送的手机号及参数*/
                                        String timeStr = msMemberMap.get(id);

                                        mobileMap.put(memberMoble,timeStr);
                                        sendSmsMap.put(areaCode,mobileMap);
                                    }
                                    if(pushMemberIdList.contains(id)){
                                        /*推送消息*/
                                        MsgDetails msgDetails = new MsgDetails();
                                        boolean push = push(msgDetails, memberMoble, id, 1,"您有新的议价消息",1);
                                    }

                                }
                            }
                        }
                    }

                    /*发送短信提醒用户*/
                    //需要短信提醒的手机号
//                    List<String> mobileList = new ArrayList<>();
                    //短信参数
//                    List<String> timeList = new ArrayList<>();

                    if(sendSmsMap.size() > 0){
                        for (Map.Entry<String, Map<String, String>> mapEntry : sendSmsMap.entrySet()) {
                            //区号
                            String key = mapEntry.getKey();
                            Map<String, String> moblieMap = mapEntry.getValue();
                            if(moblieMap.size() > 0){
                                for (Map.Entry<String, String> mobileEntry : moblieMap.entrySet()) {
                                    //手机号
                                    String mobile = mobileEntry.getKey();
                                    //参数
                                    String timeStr = mobileEntry.getValue();
                                    //区号
                                    String nationCode = key;
                                    //默认为国际短信模板
                                    int templateId = 216393;
                                    //签名
                                    String smsSign = "Hi Sandy";

                                    String[] phoneNumbers = {mobile};

                                    String[] params = {timeStr};
                                    //如果是中国，发送国内短信
                                    /*if("86".equals(nationCode)){
                                        //短信模板
                                        templateId = 165847;
                                        smsSign = "三迪科技";
                                    }*/
                                    String resultStr = SendSmsUtil.sendSms(nationCode,phoneNumbers,templateId,params,smsSign,null,null);
                                    if(!"success".equals(resultStr)){
                                        System.out.println("closeBargainConversation 发送短信失败============");
                                    }
                                }
                            }

                        }

                    }
                    /*修改议价提醒状态*/
                    if(pushBargainIdList.size() > 0){
                        setColumn.clear();
                        condition.clear();
                        setColumn.add("PROMPT_STATUS = 1");
                        condition.put("setColumn",setColumn);
                        condition.put("ids", pushBargainIdList);
                        Integer delSize = memberService.updateCustomColumnByCondition(new HsMemberHousingBargain(), condition);
                        if(delSize == null || delSize != delIdList.size()){
                            System.out.println("删除过期议价信息失败=============");
                        }
                    }

                    if(msmBargainIdList.size() > 0){
                        setColumn.clear();
                        condition.clear();
                        setColumn.add("PROMPT_STATUS = 2");
                        condition.put("setColumn",setColumn);
                        condition.put("ids", msmBargainIdList);
                        Integer delSize = memberService.updateCustomColumnByCondition(new HsMemberHousingBargain(), condition);
                        if(delSize == null || delSize != delIdList.size()){
                            System.out.println("删除过期议价信息失败=============");
                        }
                    }

                }
            }
        }

    }
}
