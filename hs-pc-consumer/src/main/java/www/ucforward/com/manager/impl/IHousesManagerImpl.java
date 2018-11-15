package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.Constant;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.customService.CustomController;
import www.ucforward.com.dto.BuildingMemberExport;
import www.ucforward.com.dto.HousingApplicationDto;
import www.ucforward.com.dto.OrderExport;
import www.ucforward.com.emchat.api.impl.EasemobChatGroup;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.manager.IHousesManager;
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
 * @Auther: lsq
 * @Date: 2018/8/20 19:02
 * @Description:
 */
@Service("housesManager")
public class IHousesManagerImpl implements IHousesManager {

    // 日志记录
    private static Logger logger = LoggerFactory.getLogger(IHousesManagerImpl.class);

    //处理消息
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 房源相关
     */
    @Resource
    private HousesService housesService;
    /**
     * 人员相关
     */
    @Resource
    private MemberService memberService;

    @Resource
    private OrderService orderService;

    /**
     * lsq
     * 获取业主预约获取房源申请
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOwnerHousingApplication(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<Object,Object>> dataSet = new ArrayList<>();
        try {
            //1.获取申请信息
            result = housesService.selectList(new HsOwnerHousingApplication(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                //2.根据房源申请信息获取，房源所属人员memberIds
                List<Integer> memberIds = new ArrayList<>();
                //房源申请ids
                List<Integer> applyIds = new ArrayList<>();
                List<Map<Object,Object>> list = (List<Map<Object,Object>>)result.getDataSet();
                list.forEach(map->{
                    int memberId = StringUtil.getAsInt(StringUtil.trim(map.get("memberId")));
                    int id = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                    memberIds.add(memberId);
                    applyIds.add(id);
                });
                //3.根据获取到的memberIds获取人员手机号map
                //人员手机号map key 人员id value 人员手机号
                Map<Integer,String> mobileMap = new HashMap<>(10);
                condition.clear();
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                //主键ID
                queryColumn.add("ID id");
                //手机号
                queryColumn.add("MEMBER_MOBLE memberMoble");
                condition.put("queryColumn",queryColumn);
                condition.put("memberIds",memberIds);
                ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    List<HsMember> memberList = (List<HsMember>) memberResultVo.getDataSet();
                    memberList.forEach(hsMember->{
                        Integer id = StringUtil.getAsInt(StringUtil.trim(hsMember.getId()));
                        String memberMoble = StringUtil.trim(hsMember.getMemberMoble());
                        mobileMap.put(id,memberMoble);
                    });
                }

                //根据房源申请id获取房产证照片
                Map<String,Map<String,String>> pocImgMap = new HashMap<>(10);
                queryColumn.clear();
                queryColumn.add("POC_IMG1 pocImg1");
                queryColumn.add("POC_IMG2 pocImg2");
                queryColumn.add("POC_IMG3 pocImg3");
                condition.clear();
                condition.put("queryColumn",queryColumn);
                condition.put("applyIds",applyIds);
                ResultVo resultVo = housesService.selectCustomColumnNamesList(HsHouseCredentialsData.class, condition);
                if(resultVo != null && resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                    List<Map<Object, Object>> credentialsList = (List<Map<Object, Object>>) resultVo.getDataSet();
                    credentialsList.forEach(credentials ->{
                        //封装房产证信息
                        Map<String,String> imgMap = new HashMap<>(3);
                        String applyId = StringUtil.trim(credentials.get("applyId"));
                        String pocImg1 = StringUtil.trim(credentials.get("pocImg1"));
                        String pocImg2 = StringUtil.trim(credentials.get("pocImg2"));
                        String pocImg3 = StringUtil.trim(credentials.get("pocImg3"));
                        imgMap.put("pocImg1",pocImg1);
                        imgMap.put("pocImg2",pocImg2);
                        imgMap.put("pocImg3",pocImg3);
                        pocImgMap.put(applyId,imgMap);
                    });
                }
                //封装人员手机号及房产证信息
                list.forEach(map->{
                    String memberIdStr = StringUtil.trim(map.get("memberId"));
                    String applyId = StringUtil.trim(map.get("applyId"));
                    String memberMobile = "";
                    String pocImg1 = "";
                    String pocImg2 = "";
                    String pocImg3 = "";
                    //封装手机号
                    if(StringUtil.hasText(memberIdStr)){
                        Integer memberId = StringUtil.getAsInt(memberIdStr);
                        memberMobile = StringUtil.trim(mobileMap.get(memberId));
                    }
                    //获取房产证信息
                    if(StringUtil.hasText(applyId)){
                        Map<String, String> stringStringMap = pocImgMap.get(applyId);
                        pocImg1 = stringStringMap.get("pocImg1");
                        pocImg2 = stringStringMap.get("pocImg2");
                        pocImg3 = stringStringMap.get("pocImg3");
                    }
                    map.put("memberMobile",memberMobile);
                    //处理图片返回值
                    String houseHoldImg = StringUtil.trim(map.get("houseHoldImg"));
                    map.put("houseHoldImg",splice(houseHoldImg));
                    map.put("pocImg1",splice(pocImg1));
                    map.put("pocImg2",splice(pocImg2));
                    map.put("pocImg3",splice(pocImg3));
                    dataSet.add(map);
                });
            }
            result.setDataSet(dataSet);
        } catch (Exception e) {
            logger.error("remote procedure call getOwnerHousingApplication is error" +e);
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * lsq
     * 获取业主预约获取房源申请详情
     * @param id 房源申请id
     * @return
     */
    @Override
    public ResultVo getOwnerHousingApplicationById(Integer id) {
        ResultVo result = new ResultVo();
        try {
            Map resultMap = new HashMap(2);
            HousingApplicationDto housingApplication = new HousingApplicationDto();
            HsHouseCredentialsData houseCredentials = new HsHouseCredentialsData();
            //1.获取申请信息
            ResultVo appResult = housesService.select(id,new HsOwnerHousingApplication());
            if(appResult != null && appResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && appResult.getDataSet() != null){
                //2.根据房源申请信息获取，房源所属人员memberId
                HsOwnerHousingApplication hsOwnerHousingApplication = (HsOwnerHousingApplication) appResult.getDataSet();
                //3.根据获取到的memberId获取人员手机号map
                Integer memberId = hsOwnerHousingApplication.getMemberId();
                //4.根据上面获取到的人员手机号map,对房源申请信息结果集进行重新封装，将人员手机号封装进去
                HsMember member = null;
                ResultVo memberResultVo = memberService.select(memberId, new HsMember());
                if(memberResultVo != null && memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    member = (HsMember) memberResultVo.getDataSet();
                }
                String memberMobile = "";
                if(member != null){
                    Integer mId = member.getId();
                    String memberMoble = member.getMemberMoble();
                    if(memberId.equals(mId)){
                        memberMobile = memberMoble;
                        ModelMapperUtil.getInstance().map(hsOwnerHousingApplication,housingApplication);
                        //处理图片返回值
                        String houseHoldImg = StringUtil.trim(hsOwnerHousingApplication.getHouseHoldImg());
                        housingApplication.setHouseHoldImg(splice(houseHoldImg));
                        housingApplication.setMemberMobile(memberMobile);
                    }
                }

                //2.获取业主预约获取房源申请信息

                Map<Object,Object> condition = new HashMap<>(16);
                condition.put("applyId",id);
                ResultVo resultVo = housesService.selectDataByCondition(new HsHouseCredentialsData(), condition);
                if(resultVo != null && resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                    houseCredentials = (HsHouseCredentialsData) resultVo.getDataSet();
                    //处理图片返回值
                    houseCredentials = credentialsImg(houseCredentials);
                    //resultMap.put("houseCredentials",houseCredentials);
                }
            }
            resultMap.put("housingApplication",housingApplication);
            resultMap.put("houseCredentials",houseCredentials);
            result.setDataSet(resultMap);
        } catch (Exception e) {
            logger.error("remote procedure call getOwnerHousingApplicationById is error" +e);
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }


    /**
     * 客服审核业主申请房源信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo checkHousingApply(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            vo = housesService.checkHousingApplyTx(condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //加入订单池
                condition.putAll((Map<Object, Object>) vo.getDataSet());
                vo = orderService.addSystemOrderPoolTx(condition);
            }
        } catch (Exception e) {
            logger.error("remote procedure call checkHousingApply is error" +e);
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 客服审核房源信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo checkHouse(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));//房源ID
            //查询该房源对应的任务有没有完成
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> queryFilter = Maps.newHashMap();
            queryColumn.add("ID poolId");//主键ID
            queryColumn.add("HOUSE_ID houseId");//主键ID
            queryColumn.add("IS_FINISHED isFinished");//主键ID
            queryFilter.put("houseId",houseId);
            queryFilter.put("orderType",0);//订单类型 0外获订单->1-外看订单->2合同订单
            queryFilter.put("isDel",0);//是否删除0:不删除，1：删除
            queryFilter.put("queryColumn",queryColumn);
            ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, queryFilter);
            if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return orderVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
            Map<Object,Object> orderPool =  orderList.get(0);
            if(orderPool==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            int isFinished = StringUtil.getAsInt(StringUtil.trim(orderPool.get("isFinished")));
            if(isFinished!=5){
                //外获或区域长的任务还未完成
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"外获或区域长的任务还未完成");
            }

            vo = housesService.checkHouse(condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Integer> poolIds = Lists.newArrayList();
                poolIds.add(StringUtil.getAsInt(StringUtil.trim(orderPool.get("poolId"))));
                condition.clear();
                condition.put("poolIds",poolIds);
                //查询订单池的任务最后在哪个业务员手里
                List<HsSystemUserOrderTasks>  userOrderTasks = memberService.selectExpiredTasks(condition);
                if(userOrderTasks != null && userOrderTasks.size() > 0){
                    HsSystemUserOrderTasks task = userOrderTasks.get(0);
                    //重置积分规则缓存
//                    String cacheKey = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                    List<Map<Object, Object>> goldList = JsonUtil.parseJSON2List(cacheKey);

                    List<Map<Object, Object>> goldList = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);

                    //保存用户总共要修改的金额
                    Map<Integer,Integer> user_gold = Maps.newHashMap();
                    List<Integer> userIds = Lists.newArrayList();//保存用户Ids
                    List<HsUserGoldLog> userGoldLogs = Lists.newArrayList();//保存业务员积分日志
                    HsUserGoldLog userGoldLog = null;

                    Map<Object, Object> yaoshiGoldMap = null;//保存第一个拿到钥匙的人
                    Map<Object, Object> waihuoGoldMap = null;//外获上传任务完成
                    Map<Object, Object> quyuzhangGoldMap = null;//区域长上传任务完成
                    Date date = new Date();
                    for (Map<Object, Object> gold : goldList) {
                        if(1==StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))){//外获上传任务完成
                            waihuoGoldMap = gold;
                        }
                        if(2==StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))){//区域长上传任务完成
                            quyuzhangGoldMap = gold;
                        }
                        if(6==StringUtil.getAsInt(StringUtil.trim(gold.get("goldType")))){//第一个业务员获取钥匙
                            yaoshiGoldMap = gold;
                        }
                    }

                    //添加对应业务员积分
                    Map<Object,Object> houseKeyMap = (Map<Object, Object>) vo.getDataSet();
                    if(houseKeyMap!=null){
                        int userId = StringUtil.getAsInt(StringUtil.trim(houseKeyMap.get("userId")));
                        userIds.add(userId);
                        int goldRuleId = StringUtil.getAsInt(StringUtil.trim(yaoshiGoldMap.get("id")));
                        int score = StringUtil.getAsInt(StringUtil.trim(yaoshiGoldMap.get("score")));

                        userGoldLog = new HsUserGoldLog();//保存用户积分
                        userGoldLog.setUserId(userId);
                        userGoldLog.setTaskId(-1);
                        userGoldLog.setGoldRuleId(goldRuleId);
                        userGoldLog.setGold(score);
                        userGoldLog.setCreateTime(date);
                        userGoldLog.setRemark("第一个业务员获取钥匙+"+score);
                        userGoldLogs.add(userGoldLog);

                        //保存要修改的积分
                        user_gold.put(userId,score);

                    }
                    Integer taskId = task.getId();//任务ID
                    Integer userId1 = task.getUserId();//保存外获业务员ID
                    Integer userId2 = StringUtil.getAsInt(StringUtil.trim(task.getStandby1()));//保存区域长ID

                    //添加外获业务员积分日志记录
                    userGoldLog = new HsUserGoldLog();
                    userGoldLog.setUserId(userId1);
                    userGoldLog.setTaskId(taskId);
                    userGoldLog.setGoldRuleId(StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("id"))));
                    int score1 = StringUtil.getAsInt(StringUtil.trim(waihuoGoldMap.get("score")));
                    userGoldLog.setGold(score1);
                    userGoldLog.setCreateTime(date);
                    userGoldLog.setRemark("外获上传任务完成+"+score1);
                    userGoldLogs.add(userGoldLog);
                    //保存要修改的积分
                    //user_gold.put(userId1,user_gold.get(userId1)+score1);
                    if(user_gold.containsKey(userId1)){
                        user_gold.put(userId1,user_gold.get(userId1)+score1);
                    }else{
                        user_gold.put(userId1,score1);
                    }

                    //添加区域长积分日志记录
                    userGoldLog = new HsUserGoldLog();
                    userGoldLog.setUserId(userId2);
                    userGoldLog.setTaskId(taskId);
                    userGoldLog.setGoldRuleId(StringUtil.getAsInt(StringUtil.trim(quyuzhangGoldMap.get("id"))));
                    int score2 = StringUtil.getAsInt(StringUtil.trim(quyuzhangGoldMap.get("score")));
                    userGoldLog.setGold(score1);
                    userGoldLog.setCreateTime(date);
                    userGoldLog.setRemark("区域长上传任务完成+"+score2);
                    userGoldLogs.add(userGoldLog);
                    //保存要修改的积分
//                user_gold.put(userId2,user_gold.get(userId2)+score2);
                    if(user_gold.containsKey(userId2)){
                        user_gold.put(userId2,user_gold.get(userId2)+score2);
                    }else{
                        user_gold.put(userId2,score2);
                    }


                    userIds.add(userId1);
                    userIds.add(userId2);
                    //查询用户余额
                    condition.clear();
                    queryColumn.clear();
                    queryColumn.add("ID userId");//用户ID
                    queryColumn.add("GOLD gold");//积分帐户
                    condition.put("queryColumn",queryColumn);
                    condition.put("ids",userIds);
                    ResultVo userVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
                    if(userVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> userList = (List<Map<Object, Object>>) userVo.getDataSet();
                        List<HsSysUser> updateUser = Lists.newArrayList();//要修改的用户
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("remote procedure call checkHouse is error" +e);
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        vo.setDataSet(null);
        return vo;
    }

    /**
     * 客服终审失败
     * @param condition
     * @return
     */
    @Override
    public ResultVo auditFailure(Map<Object, Object> condition){
        ResultVo vo = null;
        try {
            vo = housesService.auditFailure(condition);
        } catch (Exception e) {
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        vo.setDataSet(null);
        return vo;
    }

    /**
     * 获取房源列表（终审）
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMainHousingList(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        try {
            /**
             * 获取房源信息
             */
            ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
            if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return houseResultVo;
            }
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
            if(houseList == null || houseList.size() < 1){
                return resultVo;
            }
            /**
             * 获取业主信息
             */
            //业主信息
            List<HsMember> memberList = new ArrayList<>();
            //业主id列表
            List<Integer> memberIds = houseList.stream().map(house -> StringUtil.getAsInt(StringUtil.trim(house.get("memberId")))).collect(Collectors.toList());
            if(memberIds.size() > 0){
                condition.clear();
                condition.put("memberIds",memberIds);
                condition.put("isDel",0);
                ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    memberList = (List<HsMember>) memberResultVo.getDataSet();
                }
            }
            /**
             * 封装结果集
             */
            List<Map<Object, Object>> resultList = new ArrayList<>();
            for (Map<Object, Object> houseMap : houseList) {
                //房源id
                int id = StringUtil.getAsInt(StringUtil.trim(houseMap.get("id")));
                //业主id
                int memberId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("memberId")));
                String memberMobile = "";
                Optional<HsMember> memberOptional = memberList.stream().filter(member -> member.getId() == memberId).findFirst();
                if(memberOptional.isPresent()){
                    HsMember member = memberOptional.get();
                    memberMobile = member.getMemberMoble();
                }
                houseMap.put("memberMobile",memberMobile);
                houseMap.put("houseMainImg",ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg"))));
                resultList.add(houseMap);
            }
            resultVo.setDataSet(resultList);
            resultVo.setPageInfo(houseResultVo.getPageInfo());
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取房源详情（终审）
     * @param id
     * @return
     */
    @Override
    public ResultVo getMainHousingInfo(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            /**
             * 查询外获或区域长任务是否完成
             */
            //任务是否完成 0未完成 1已完成
            int isComplete = 1;
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> queryFilter = Maps.newHashMap();
            //主键ID
            queryColumn.add("ID poolId");
            //房源id
            queryColumn.add("HOUSE_ID houseId");
            //是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
            queryColumn.add("IS_FINISHED isFinished");
            //房源id
            queryFilter.put("houseId",id);
            //订单类型 0外获订单->1-外看订单->2合同订单
            queryFilter.put("orderType",0);
            //是否删除0:不删除，1：删除
            queryFilter.put("isDel",0);
            queryFilter.put("queryColumn",queryColumn);
            ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, queryFilter);
            if(orderVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return orderVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
            if(orderList.size() < 1){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            Map<Object,Object> orderPool =  orderList.get(0);
            int isFinished = StringUtil.getAsInt(StringUtil.trim(orderPool.get("isFinished")));
            if(isFinished!=5){
                //外获或区域长的任务还未完成
                isComplete = 0;
            }
            /**
             * 获取房源信息
             */
            ResultVo houseResultVo = housesService.select(id, new HsMainHouse());
            if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                return houseResultVo;
            }
            HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
            /**
             * 获取房源图片信息
             */
            HsHouseCredentialsData houseCredentials = new HsHouseCredentialsData();
            Map<Object,Object> condition = new HashMap<>(2);
            condition.put("isDel",0);
            condition.put("houseId",id);
            ResultVo credentialsResultVo = housesService.selectDataByCondition(new HsHouseCredentialsData(), condition);
            if(credentialsResultVo.getDataSet() != null && credentialsResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                houseCredentials = (HsHouseCredentialsData) credentialsResultVo.getDataSet();
                //处理图片返回值
                houseCredentials = credentialsImg(houseCredentials);
            }

            /**
             * 房源附图
             */
            HsHouseImg houseImg = new HsHouseImg();
            Map<Object, Object> houseImgMap = new HashMap<>(16);
            ResultVo houseImgResultVo = housesService.selectList(new HsHouseImg(), condition, 0);
            if(houseImgResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> houseImgList = (List<Map<Object, Object>>) houseImgResultVo.getDataSet();
                if(houseImgList != null && houseImgList.size() > 0){
                    Map<Object, Object> map = houseImgList.get(0);
                    map.put("houseImg1",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg1"))));
                    map.put("houseImg2",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg2"))));
                    map.put("houseImg3",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg3"))));
                    map.put("houseImg4",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg4"))));
                    map.put("houseImg5",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg5"))));
                    map.put("houseImg6",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg6"))));
                    map.put("houseImg7",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg7"))));
                    map.put("houseImg8",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg8"))));
                    map.put("houseImg9",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg9"))));
                    map.put("houseImg10",ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseImg10"))));
                    houseImgMap = map;

                }
            }

            /**
             * 获取业主信息
             */
            HsMember member = new HsMember();
            //业主id
            Integer memberId = house.getMemberId();
            condition.put("memberId",memberId);
            ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
            if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                List<HsMember> memberList = (List<HsMember>) memberResultVo.getDataSet();
                member = memberList.get(0);
            }
            /**
             * 封装信息
             */
            Map<String,Object> houseMap = new HashMap<>(16);
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(house, SerializerFeature.WriteMapNullValue));
            houseMap = JSON.toJavaObject(jsonObject, Map.class);
            houseMap.put("memberMobile",StringUtil.trim(member.getMemberMoble()));
            houseMap.put("isComplete",isComplete);

            Map<String,Object> resultMap = new HashMap<>(2);
            resultMap.put("house",houseMap);
            resultMap.put("houseCredentials",houseCredentials);
            resultMap.put("houseImg",houseImgMap);

            resultVo.setDataSet(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("remote procedure call getMainHousingInfo is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 新增人员申购信息
     * @param buildingMemberApply
     * @return
     */
    @Override
    public ResultVo addPurchase(HsHouseNewBuildingMemberApply buildingMemberApply) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = housesService.insert(buildingMemberApply);
            if(resultVo == null || resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || resultVo.getDataSet() == null){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }
            buildingMemberApply = (HsHouseNewBuildingMemberApply)resultVo.getDataSet();
            Integer memberId = buildingMemberApply.getMemberId();
            Integer projectId = buildingMemberApply.getProjectId();
            //插入人员直购申请
            HsMemberDirectPurchaseApply purchaseApply = new HsMemberDirectPurchaseApply();
            purchaseApply.setCode(UUID.randomUUID().toString());
            purchaseApply.setMemberId(memberId);
            purchaseApply.setBuildingId(projectId);
            purchaseApply.setStatus(0);
            purchaseApply.setCreateBy(memberId);
            purchaseApply.setCreateTime(new Date());
            ResultVo insert = memberService.insert(purchaseApply);
            if(insert == null || insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || insert.getDataSet() == null){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }

        }catch (Exception e){
            logger.error("remote procedure call addPurchase is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 新增直售楼盘信息
     * @param hsHouseNewBuilding
     * @return
     */
    @Override
    public ResultVo addDirectSalesProperty(HsHouseNewBuilding hsHouseNewBuilding) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = housesService.insert(hsHouseNewBuilding);
        }catch (Exception e){
            logger.error("remote procedure call addDirectSalesProperty is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取楼盘区域
     * @return
     */
    @Override
    public ResultVo getPropertyArea() {
        ResultVo resultVo = new ResultVo();
        try {
            List<Map<Object,Object>> propertyArea = housesService.getPropertyArea();
            resultVo.setDataSet(propertyArea);
        }catch (Exception e){
            logger.error("remote procedure call getPropertyArea is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取楼盘详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getDirectSalesDetails(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = housesService.select(id,new HsHouseNewBuilding());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseNewBuilding newBuilding = (HsHouseNewBuilding) resultVo.getDataSet();
                if(newBuilding != null){
                    String projectMainImg = ImageUtil.imgResultUrl(newBuilding.getProjectMainImg());
                    String projectMainImg2 = ImageUtil.imgResultUrl(newBuilding.getProjectMainImg2());
                    newBuilding.setProjectMainImg(projectMainImg);
                    newBuilding.setProjectMainImg2(projectMainImg2);
                    resultVo.setDataSet(newBuilding);
                }
            }
        }catch (Exception e){
            logger.error("remote procedure call getDirectSalesDetails is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取直售楼盘列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDirectSalesPropertyList(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        List<Map<Object, Object>> resultList = new ArrayList<>();
        try {
            resultVo = housesService.selectList(new HsHouseNewBuilding(),condition,0);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                List<Map<Object, Object>> buildingList = (List<Map<Object, Object>>) resultVo.getDataSet();
                for (Map<Object, Object> map : buildingList) {
                    String projectMainImg = StringUtil.trim(map.get("projectMainImg"));
                    map.put("projectMainImg",ImageUtil.imgResultUrl(projectMainImg));
                    String projectMainImg2 = StringUtil.trim(map.get("projectMainImg2"));
                    map.put("projectMainImg2",ImageUtil.imgResultUrl(projectMainImg2));
                    resultList.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("remote procedure call getDirectSalesPropertyList is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 修改楼盘信息（新增、修改、删除）
     * @param hsHouseNewBuilding 楼盘信息
     * @return
     */
    @Override
    public ResultVo updateDirectSalesDetails(HsHouseNewBuilding hsHouseNewBuilding){
        ResultVo resultVo = new ResultVo();
        try {
            Integer id = hsHouseNewBuilding.getId();
            Integer userId = hsHouseNewBuilding.getUpdateBy();
            Date date = new Date();

            if(id == null){
               //新增
                hsHouseNewBuilding.setProjectCode(UUID.randomUUID().toString());
                hsHouseNewBuilding.setCreateBy(userId);
                hsHouseNewBuilding.setCreateTime(date);
                resultVo = housesService.insert(hsHouseNewBuilding);
            }else{
                //修改
                String projectMainImg = StringUtil.trim(hsHouseNewBuilding.getProjectMainImg());
                if(projectMainImg.startsWith(ImageUtil.IMG_URL_PREFIX)){
                    projectMainImg = projectMainImg.split(ImageUtil.IMG_URL_PREFIX)[1];
                }
                String projectMainImg2 = StringUtil.trim(hsHouseNewBuilding.getProjectMainImg2());
                if(projectMainImg2.startsWith(ImageUtil.IMG_URL_PREFIX)){
                    projectMainImg2 = projectMainImg2.split(ImageUtil.IMG_URL_PREFIX)[1];
                }
                hsHouseNewBuilding.setProjectMainImg(projectMainImg);
                hsHouseNewBuilding.setProjectMainImg2(projectMainImg2);
                hsHouseNewBuilding.setUpdateTime(date);
                resultVo = housesService.update(hsHouseNewBuilding);
            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 房源投诉列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseComplainList(Map<Object, Object> condition) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = housesService.selectList(new HsHouseComplain(),condition,1);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                List<HsHouseComplain> lists = (List<HsHouseComplain>) resultVo.getDataSet();
                //获取房源信息
                List<Map<Object, Object>> houseList = new ArrayList<>();
                List<Integer> houseIds = lists.stream().map(HsHouseComplain::getHouseId).collect(Collectors.toList());
                if(houseIds.size() > 0){
                    condition.clear();
//                    condition.put("isDel",0);
                    condition.put("houseIds",houseIds);
                    ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
                    if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                        logger.error("lsq:getHouseComplainList获取房源失败");
                        return houseResultVo;
                    }
                    houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
                }
                //封装结果集
                List<Map> resultList = new ArrayList<>();
                for (HsHouseComplain houseComplain : lists) {
                    Map houseComplainMap;
                    int houseId = houseComplain.getHouseId();
                    //预约类型（0：出租，1：出售）
                    Integer leaseType = null;
                    //城市
                    String city = "";
                    //社区
                    String community = "";
                    //子社区
                    String subCommunity = "";
                    //房源所在区域名称
                    String address = "";
                    //房源主图
                    String houseMainImg = "";
                    Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(map -> StringUtil.getAsInt(StringUtil.trim(map.get("id"))) == houseId).findFirst();
                    if(houseOptional.isPresent()){
                        //封装房源信息
                        Map<Object, Object> house = houseOptional.get();
                        leaseType = StringUtil.getAsInt(StringUtil.trim(house.get("leaseType")));
                        city = StringUtil.trim(house.get("city"));
                        community = StringUtil.trim(house.get("community"));
                        subCommunity = StringUtil.trim(house.get("subCommunity"));
                        address = StringUtil.trim(house.get("address"));
                        houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                        if(StringUtil.hasText(houseMainImg)){
                            houseMainImg = ImageUtil.imgResultUrl(houseMainImg);
                        }
                    }
                    //处理图片
                    String complainProofPic1 = houseComplain.getComplainProofPic1();
                    String complainProofPic2 = houseComplain.getComplainProofPic2();
                    String complainProofPic3 = houseComplain.getComplainProofPic3();
                    if(StringUtil.hasText(complainProofPic1)){
                        houseComplain.setComplainProofPic1(ImageUtil.IMG_URL_PREFIX+complainProofPic1);
                    }
                    if(StringUtil.hasText(complainProofPic2)){
                        houseComplain.setComplainProofPic2(ImageUtil.IMG_URL_PREFIX+complainProofPic2);
                    }
                    if(StringUtil.hasText(complainProofPic3)){
                        houseComplain.setComplainProofPic3(ImageUtil.IMG_URL_PREFIX+complainProofPic3);
                    }
                    JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(houseComplain, SerializerFeature.WriteMapNullValue));
                    houseComplainMap = JSON.toJavaObject(jsonObject, Map.class);
                    houseComplainMap.put("leaseType",leaseType);
                    houseComplainMap.put("city",city);
                    houseComplainMap.put("community",community);
                    houseComplainMap.put("subCommunity",subCommunity);
                    houseComplainMap.put("address",address);
                    houseComplainMap.put("houseMainImg",houseMainImg);
                    resultList.add(houseComplainMap);
                }
                resultVo.setDataSet(resultList);
            }

        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 更新投诉订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateComplaintGrabOrder(Map<Object, Object> condition){
        ResultVo vo = null;
        //业务员ID
        int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
        //投诉id
        int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
        //投诉是否属实：0未核实 1情况属实 2情况不属实
        int isVerified = StringUtil.getAsInt(StringUtil.trim(condition.get("isVerified")));
        //人员类型 内勤4 财务5
        int userType = StringUtil.getAsInt(StringUtil.trim(condition.get("userType")));
        //备注
        String remark = StringUtil.trim(condition.get("remark"));
        try {
            Date date = new Date();
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
            if(userType == 5){
                houseComplain.setRemarkFinance(remark);
            }else{
                houseComplain.setRemarkBackOffice(remark);
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
            vo = housesService.insert(hsHouseComplainLog);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增房源投诉（新建单）
     * @param houseComplain 房源投诉表
     * @return
     */
    @Override
    public ResultVo addHouseComplain(HsHouseComplain houseComplain){
        ResultVo resultVo = new ResultVo();
        try {
            //生成投诉code
            String complainCode = "TS_" + RandomUtils.getRandomNumbersAndLetters(32);
            houseComplain.setComplainCode(complainCode);
            //获取房源信息
            //房源编号
            String houseCode = houseComplain.getStandby1();
            Map<Object,Object> condition = new HashMap<>(2);
            condition.put("isDel",0);
            condition.put("houseCode",houseCode);
            ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
            if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //房源信息不存在
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage("Housing does not exist");
                return resultVo;
            }
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
            if(houseList == null || houseList.size() < 1){
                logger.error("lsq:addHouseComplain 房源信息不存在");
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":房源信息不存在");
                return resultVo;
            }
            Map<Object, Object> houseMap = houseList.get(0);
            //封装房源id
            int houseId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("id")));
            houseComplain.setHouseId(houseId);
            //将备用字段清空
            houseComplain.setStandby1("");
            resultVo = housesService.insert(houseComplain);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;

    }

    /**
     * 房源投诉转单
     * @param id 投诉id
     * @param userId 用户id
     * @param type 转单类型 1区域长 2外看 3外获 4内勤 5财务
     * @param remark 客服备注
     * @return
     */
    @Override
    public ResultVo houseComplainTransfer(Integer id,Integer userId,Integer type,String remark){
        ResultVo resultVo = new ResultVo();
        Date date = new Date();
        try {
            //获取投诉订单信息
            resultVo = housesService.select(id, new HsHouseComplain());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                //房源投诉订单
                HsHouseComplain complain = (HsHouseComplain) resultVo.getDataSet();
                Integer status = complain.getStatus();
                if(status == 1 || status == 3){
                    //订单正在处理中。 无法执行转单操作
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":The order is being processed. Cannot perform order transfer operation");
                    return resultVo;
                }
                //房源id
                Integer houseId = complain.getHouseId();
                //获取房源信息
                resultVo = housesService.select(houseId,new HsMainHouse());
                if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                    HsMainHouse house = (HsMainHouse) resultVo.getDataSet();
                    Integer applyId = house.getApplyId();
                    if(type == 1 || type == 2 || type == 3){
                        /**
                         * 插入订单池
                         */
                        HsSystemOrderPool orderPool = new HsSystemOrderPool();
                        orderPool.setOrderCode("SCO_"+ RandomUtils.getRandomCode());
                        orderPool.setHouseId(houseId);
                        orderPool.setApplyId(applyId);
                        //开启抢单
                        orderPool.setIsOpenOrder(1);
                        orderPool.setRemark("客服转单插入投诉订单");
                        orderPool.setCreateBy(userId);
                        orderPool.setCreateTime(date);
                        int orderType = 5;
                        if(type == 1){
                            //区域长不需要抢单 直接派单
//                                orderPool.setIsFinished(1);
                        }
                        if(type == 2){
                            orderType = 3;
                        }
                        if(type == 3){
                            orderType = 4;
                        }
                        orderPool.setOrderType(orderType);

                        //设置预计订单完成时间
                        //1天
                        long estimatedLong = 24*60*60*1000;
                        Date estimatedDate = new Date(date.getTime() + estimatedLong);
                        orderPool.setEstimatedTime(estimatedDate);

                        //半个小时
                        long close = 30*60*1000;
                        //关单时间
                        Date closeDate = new Date(date.getTime() + close);
                        orderPool.setOpenOrderCloseTime(closeDate);
                        //2天
                        long time = 48*60*60*1000;
                        //过期时间
                        Date afterDate = new Date(date.getTime() + time);
                        orderPool.setCloseTime(afterDate);
                        //见面地点
                        orderPool.setAppointmentMeetPlace(house.getCity()+house.getCommunity()+house.getSubCommunity()+house.getAddress());
                        ResultVo orderPoolInsert = orderService.insert(orderPool);
                        HsSystemOrderPool pool = (HsSystemOrderPool) orderPoolInsert.getDataSet();
                        if(orderPoolInsert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            complain.setPoolId(pool.getId());
                            /**
                             * 插入订单池日志
                             */
                            HsSystemOrderPoolLog systemOrderPoolLog = new HsSystemOrderPoolLog();
                            //订单池主键ID
                            systemOrderPoolLog.setPoolId(pool.getId());
                            //投诉订单
                            systemOrderPoolLog.setOrderType(orderType);
                            //加入订单池
                            systemOrderPoolLog.setNodeType(0);
                            systemOrderPoolLog.setCreateBy(pool.getCreateBy());
                            systemOrderPoolLog.setCreateTime(date);
                            systemOrderPoolLog.setPostTime(date);
                            //操作人用户ID
                            systemOrderPoolLog.setOperatorUid(pool.getCreateBy());
                            //操作人类型1:普通会员 2:商家 3:系统管理员
                            systemOrderPoolLog.setOperatorType(3);
                            systemOrderPoolLog.setRemarks("客服转单插入投诉订单");
                            //新增系统订单池日志信息
                            resultVo = orderService.insert(systemOrderPoolLog);
                        }
                        if(type == 1){
                            /**
                             * 订单分配给区域长，不需要抢单，直接将任务分配给这个区域的区域长
                             */
                            //获取区域长
                            Map<Object,Object> condition = new HashMap<>(16);
                            condition.put("locked",0);
                            condition.put("isForbidden",0);
                            condition.put("isDel",0);
                            condition.put("city",StringUtil.trim(house.getCity()));
                            condition.put("community",StringUtil.trim(house.getCommunity()));
                            List<String> roleIds = new ArrayList<String>();
                            //钥匙管理员
                            roleIds.add("2ece77221ad0b89f14f35899a8a63886");
                            condition.put("roleIds",roleIds);
                            //获取所有外获业务员
                            List<Map<Object,Object>> users = memberService.selectOutsideUser(condition);
                            if(users != null && users.size() > 0){
                                /**
                                 * 插入任务信息
                                 */
                                //钥匙管理员
                                Map<Object, Object> keyManager = users.get(0);
                                int _userId = StringUtil.getAsInt(StringUtil.trim(keyManager.get("userId")));
                                HsSystemUserOrderTasks tasks = new HsSystemUserOrderTasks();
                                tasks.setHouseId(houseId);
                                tasks.setApplyId(applyId);
                                tasks.setUserId(_userId);
                                tasks.setPoolId(pool.getId());
                                //区域长投诉任务
                                tasks.setTaskType(orderType);
                                tasks.setCreateTime(date);
                                tasks.setEstimatedTime(estimatedDate);
                                tasks.setRemark("客服转单插入投诉订单");
                                tasks.setVersionNo(0);
                                //钥匙管理员ID
                                tasks.setStandby1(_userId + "");
                                //未完成
                                tasks.setStandby2("0");
                                ResultVo tasksInsert = memberService.insert(tasks);
                                if(tasksInsert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                    /**
                                     * 插入任务日志
                                     */
                                    HsSystemUserOrderTasksLog tasksLog = new HsSystemUserOrderTasksLog();
                                    tasksLog.setNodeType(0);
                                    tasksLog.setPoolId(pool.getId());
                                    tasks.setIsDel(0);
                                    tasksLog.setCreateTime(date);
                                    tasksLog.setRemarks("客服转单插入投诉订单");
                                    ResultVo tasksLogInsert = memberService.insert(tasksLog);
                                }


                            }

                        }
                    }
                    //修改订单状态
                    //投诉状态：0未接单 1业务员处理 2业务员处理完成 3已关单
                    complain.setStatus(1);
                    complain.setTransferType(type);
                    complain.setRemark(remark);
                    complain.setUpdateBy(userId);
                    complain.setUpdateTime(date);
                    ResultVo complainUpdate = housesService.update(complain);
                    if(complainUpdate.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        /**
                         * 插入投诉日志
                         */
                        HsHouseComplainLog houseComplainLog = new HsHouseComplainLog();
                        houseComplainLog.setComplainId(id);
                        houseComplainLog.setStatus(1);
                        houseComplainLog.setCreateBy(userId);
                        houseComplainLog.setCreateTime(date);
                        houseComplainLog.setRemark(remark);
                        ResultVo complainLogInsert = housesService.insert(houseComplainLog);
                    }

                }

            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 房源投诉关单
     * @param id 房源投诉id
     * @return
     */
    @Override
    public ResultVo houseComplainClose(Integer id,Integer userId,String remark){
        ResultVo resultVo = new ResultVo();
        Map houseComplainMap = new HashMap(16);
        try {
            resultVo = housesService.select(id,new HsHouseComplain());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseComplain houseComplain = (HsHouseComplain) resultVo.getDataSet();
                if(houseComplain == null){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "：Complaint order does not exist");
                }
                Integer status = houseComplain.getStatus();
                if(status == 1){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "：Salesperson task not complete");
                }
                //修改投诉订单状态
                //投诉状态：0未接单 1业务员处理 2业务员处理完成 3已关单
                houseComplain.setStatus(3);
                houseComplain.setTransferType(0);
                houseComplain.setCloseBy(userId);
                houseComplain.setUpdateBy(userId);
                houseComplain.setRemark(remark);
                resultVo = housesService.update(houseComplain);
                if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    /**
                     * 插入投诉日志
                     */
                    HsHouseComplainLog houseComplainLog = new HsHouseComplainLog();
                    houseComplainLog.setComplainId(id);
                    houseComplainLog.setStatus(3);
                    houseComplainLog.setCreateBy(userId);
                    houseComplainLog.setCreateTime(new Date());
                    houseComplainLog.setRemark(remark);
                    ResultVo complainLogInsert = housesService.insert(houseComplainLog);
                }
            }
            resultVo.setDataSet(houseComplainMap);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 根据房源code获取房源信息
     * @param houseCode 房源code
     * @return
     */
    @Override
    public ResultVo getHouseByCode(String houseCode){
        ResultVo resultVo = new ResultVo();
        try {
            //获取房源信息
            Map<Object,Object> condition = new HashMap<>(2);
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");
            queryColumn.add("HOUSE_NAME houseName");
            queryColumn.add("LEASE_TYPE leaseType");
            queryColumn.add("HOUSE_CODE houseCode");
            queryColumn.add("CITY city");
            queryColumn.add("COMMUNITY community");
            queryColumn.add("SUB_COMMUNITY subCommunity");
            queryColumn.add("COMMUNITY community");

            condition.put("queryColumn",queryColumn);
            condition.put("isDel",0);
            condition.put("houseCode",houseCode);
            resultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                logger.error("lsq: getHouseByCode 获取房源信息失败");
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage("Failed to get house information");
                return resultVo;
            }
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(houseList == null || houseList.size() < 1){
                logger.error("lsq: getHouseByCode 房源信息不存在");
                resultVo.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
                resultVo.setMessage("Housing does not exist");
                return resultVo;
            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     *  房源投诉详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getHouseComplainDetail(Integer id) {
        ResultVo resultVo = new ResultVo();
        Map houseComplainMap = new HashMap(16);
        try {
            resultVo = housesService.select(id,new HsHouseComplain());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseComplain houseComplain = (HsHouseComplain) resultVo.getDataSet();
                if(houseComplain == null){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                int houseId = houseComplain.getHouseId();

                //封装房源信息
                //获取房源信息
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                //预约类型（0：出租，1：出售）
                Integer leaseType = StringUtil.getAsInt(StringUtil.trim(house.getLeaseType()));
                //城市
                String city = StringUtil.trim(house.getCity());
                //社区
                String community = StringUtil.trim(house.getCommunity());
                //子社区
                String subCommunity = StringUtil.trim(house.getSubCommunity());
                //房源所在区域名称
                String address = StringUtil.trim(house.getAddress());
                //房源主图
                String houseMainImg = StringUtil.trim(house.getHouseMainImg());
                if(StringUtil.hasText(houseMainImg)){
                    houseMainImg = ImageUtil.imgResultUrl(houseMainImg);
                }
                String complainProofPic1 = ImageUtil.imgResultUrl(houseComplain.getComplainProofPic1());
                String complainProofPic2 = ImageUtil.imgResultUrl(houseComplain.getComplainProofPic2());
                String complainProofPic3 = ImageUtil.imgResultUrl(houseComplain.getComplainProofPic3());
                /**
                 * 封装投诉人信息
                 */
                //投诉人姓名
                String name = "";
                Map<Object, Object> condition = new HashMap<>(3);
                List<String> queryColumn = new ArrayList<>();
                //投诉人联系电话
                String mobile = houseComplain.getMobile();
                //投诉来源 (1客户 2外获 3外看 4区域长)
                Integer platform = houseComplain.getPlatform();
                if(platform == 1){
                    //客户 获取 hs_member 表信息
                    //姓氏
                    queryColumn.add("FAMILY_NAME familyName");
                    //名称
                    queryColumn.add("NAME name");
                    condition.put("queryColumn",queryColumn);
                    condition.put("isDel",0);
                    condition.put("memberMoble",mobile);
                    ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                    if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                        List<Map<Object,Object>> memberList = (List<Map<Object,Object>>) memberResultVo.getDataSet();
                        if(memberList.size() > 0){
                            Map<Object, Object> memberMap = memberList.get(0);
                            String familyName = StringUtil.trim(memberMap.get("familyName"));
                            String memberName = StringUtil.trim(memberMap.get("name"));
                            name = familyName + memberName;
                        }
                    }
                }else{
                    //业务员 获取 hs_sys_user 表信息
                    queryColumn.add("USERNAME username");
                    condition.put("queryColumn",queryColumn);
                    condition.put("isDel",0);
                    condition.put("mobile",mobile);
                    ResultVo userResultVo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
                    if(userResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && userResultVo.getDataSet() != null){
                        List<Map<Object, Object>> userList = (List<Map<Object, Object>>) userResultVo.getDataSet();
                        if(userList.size() > 0){
                            Map<Object, Object> userMap = userList.get(0);
                            name = StringUtil.trim(userMap.get("username"));
                        }
                    }
                }

                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(houseComplain, SerializerFeature.WriteMapNullValue));
                houseComplainMap = JSON.toJavaObject(jsonObject, Map.class);
                houseComplainMap.put("leaseType",leaseType);
                houseComplainMap.put("city",city);
                houseComplainMap.put("community",community);
                houseComplainMap.put("subCommunity",subCommunity);
                houseComplainMap.put("address",address);
                houseComplainMap.put("houseMainImg",houseMainImg);
                houseComplainMap.put("complainProofPic1",complainProofPic1);
                houseComplainMap.put("complainProofPic2",complainProofPic2);
                houseComplainMap.put("complainProofPic3",complainProofPic3);
                houseComplainMap.put("name",name);
            }
            resultVo.setDataSet(houseComplainMap);
        }catch (Exception e){
            logger.error("remote procedure call getHouseComplainDetail is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 房源评价表列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseEvaluationList(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        try {
            resultVo = housesService.selectList(new HsHouseEvaluation(),condition,1);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<HsHouseEvaluation> lists = (List<HsHouseEvaluation>) resultVo.getDataSet();
                if(lists!=null && lists.size()>0){
                    List<Integer> houseIds = Lists.newArrayListWithCapacity(lists.size());
                    for (HsHouseEvaluation houseEvaluation : lists) {
                        houseIds.add(houseEvaluation.getHouseId());
                        String evaluationImg1 = houseEvaluation.getEvaluationImg1();
                        String evaluationImg2 = houseEvaluation.getEvaluationImg2();
                        String evaluationImg3 = houseEvaluation.getEvaluationImg3();
                        if(StringUtil.hasText(evaluationImg1)){
                            houseEvaluation.setEvaluationImg1(ImageUtil.IMG_URL_PREFIX+evaluationImg1);
                        }
                        if(StringUtil.hasText(evaluationImg2)){
                            houseEvaluation.setEvaluationImg2(ImageUtil.IMG_URL_PREFIX+evaluationImg2);
                        }
                        if(StringUtil.hasText(evaluationImg3)){
                            houseEvaluation.setEvaluationImg3(ImageUtil.IMG_URL_PREFIX+evaluationImg3);
                        }
                    }
                }
            }

        }catch (Exception e){
            logger.error("remote procedure call getHouseEvaluationList is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 房源评价详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getHouseEvaluationDetail(Integer id) {
        ResultVo resultVo = null;
        try {
            resultVo = housesService.select(id,new HsHouseEvaluation());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseEvaluation houseEvaluation = (HsHouseEvaluation) resultVo.getDataSet();
                if(houseEvaluation == null){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                String evaluationImg1 = houseEvaluation.getEvaluationImg1();
                String evaluationImg2 = houseEvaluation.getEvaluationImg2();
                String evaluationImg3 = houseEvaluation.getEvaluationImg3();
                if(StringUtil.hasText(evaluationImg1)){
                    houseEvaluation.setEvaluationImg1(ImageUtil.IMG_URL_PREFIX+evaluationImg1);
                }
                if(StringUtil.hasText(evaluationImg2)){
                    houseEvaluation.setEvaluationImg2(ImageUtil.IMG_URL_PREFIX+evaluationImg2);
                }
                if(StringUtil.hasText(evaluationImg3)){
                    houseEvaluation.setEvaluationImg3(ImageUtil.IMG_URL_PREFIX+evaluationImg3);
                }
            }
        }catch (Exception e){
            logger.error("remote procedure call getHouseEvaluationDetail is error" +e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 业主联系客服上传
     * @param apply 业主申请表
     * @param houseCredentialsData
     * @param areaCode
     * @param memberMobile
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public ResultVo addOwnerApply(HsOwnerHousingApplication apply, HsHouseCredentialsData houseCredentialsData, String areaCode, String memberMobile,Integer applyId,Integer userid) {
        ResultVo result = new ResultVo();
        try {
            Map<Object,Object> condition = Maps.newHashMap();

            if(applyId != null){
                //修改
                apply.setId(applyId);
                apply.setUpdateBy(userid);
                ResultVo houseUpdate = housesService.update(apply);
                if(houseUpdate.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    Integer credentialsId = houseCredentialsData.getId();
                    //图片id不为null 修改图片
                    if(credentialsId != null){
                        ResultVo update = housesService.update(houseCredentialsData);
                        if(update.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            return update;
                        }
                    }
                    return houseUpdate;
                }
            }
            //TODO 新增人员id
//            apply.setMemberId(userid);
            //申请类型（0：自主完善，1：联系客服上传，2：业务员上传）
            apply.setApplyType(1);
            apply.setIsCustomerServiceRelation(1);
            apply.setCreateBy(userid);


            //生成申请code
            apply.setApplyCode("AC_" + RandomUtils.getRandomNumbersAndLetters(32));
            Date date = new Date();
            apply.setCreateTime(date);
            apply.setUpdateTime(date);
            result = housesService.insert(apply);//保存业主提交的房源信息
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsOwnerHousingApplication dataSet = (HsOwnerHousingApplication) result.getDataSet();
                Integer id = dataSet.getId();
                //申请ID
                houseCredentialsData.setApplyId(id);
                houseCredentialsData.setApplicantType(apply.getApplicantType());
                houseCredentialsData.setLanguageVersion(apply.getLanguageVersion());
                ResultVo credentialsResult = housesService.insert(houseCredentialsData);

                System.out.println(houseCredentialsData.toString());
                if (credentialsResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    HsOwnerHousingApplicationLog ownerHousingApplyLog = new HsOwnerHousingApplicationLog();
                    //房源申请信息的ID
                    ownerHousingApplyLog.setApplyId(id);
                    ownerHousingApplyLog.setNodeType(0);
                    //ownerHousingApplyLog.setIsDel(0);有默认值可不手动设置
                    ownerHousingApplyLog.setCreateBy(apply.getCreateBy());
                    ownerHousingApplyLog.setCreateTime(date);
                    ownerHousingApplyLog.setPostTime(date);
                    ResultVo logVo = housesService.insert(ownerHousingApplyLog);

                    /**
                     * 房源初审
                     */
                    condition.put("isCheck",2);
                    //房源申请ID
                    condition.put("id",id);
                    result = housesService.checkHousingApplyTx(condition);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //加入订单池
                        condition.putAll((Map<Object, Object>) result.getDataSet());
                        result = orderService.addSystemOrderPoolTx(condition);
                    }

                    if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        //记录房源进度 上传房源
                        //2.封装进度信息
                        HsHouseProgress hsHouseProgress = new HsHouseProgress();
                        //房源申请ID
                        hsHouseProgress.setApplyId(id);
                        //创建人
                        hsHouseProgress.setCreateBy(apply.getCreateBy());
                        //进度
                        hsHouseProgress.setProgressCode("101");
                        //创建日期
                        hsHouseProgress.setCreateTime(date);
                        //3.插入数据
                        ResultVo insert = housesService.insert(hsHouseProgress);
                        if (insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            return insert;
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("addOwnerHousingApply Exception message:" + e.getMessage());
        }
        return result;
    }

    /**
     * 房源编辑（终审）
     * @param mainHouse                房源信息
     * @param houseCredentialsData 房源图片信息
     * @return
     */
    @Override
    public ResultVo houseEdit(HsMainHouse mainHouse, HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        Map<Object,Object> queryFilter = Maps.newHashMap();
        try {
            int houseId = mainHouse.getId();
            //组装房源日期格式数据
            assembleStringToDate(mainHouse);
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse house = (HsMainHouse) result.getDataSet();
                Integer isDel = house.getIsDel();
                if(house==null || isDel == 1){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage(ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                    return result;
                }
                Date date = new Date();
                houseCredentialsData.setHouseId(houseId);
                mainHouse.setVersionNo(house.getVersionNo());
                //修改房源主信息
                result = housesService.update(mainHouse);

                if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                    return result;
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


                //房源图片
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
                if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return result;
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
     * 获取房源自动应答
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getAutoReplySetting(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object, Object> resultMap = new HashMap<>(4);
        try {
            String houseIdStr = StringUtil.trim(condition.get("houseId"));
            //房源id不为空，按照房源id查询自动应答信息
            if(StringUtil.hasText(houseIdStr)){
                //房源ID
                int houseId = StringUtil.getAsInt(houseIdStr);
                //根据房源ID查询房源信息
                result = housesService.select(houseId, new HsMainHouse());
                if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    HsMainHouse mainHouse = (HsMainHouse) result.getDataSet();
                    if (mainHouse != null) {
                        int leaseType = mainHouse.getLeaseType();
                        //resultMap.put("isAutoAnswer",isAutoAnswer);
                        resultMap.put("leaseType", leaseType);

                        //根据房源ID查询自动应答设置信息
                        condition.put("isDel", 0);
                        ResultVo replyResult = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                        if (replyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            List<Map<Object, Object>> replyList = (List<Map<Object, Object>>) replyResult.getDataSet();
                            if (replyList.size() > 0) {
                                for (Map<Object, Object> map : replyList) {
                                    map.remove("isDel");
                                }
                                resultMap.put("autoAnswerList", replyList);
                            }
                        }
                    } else {
                        resultMap.put("isAutoAnswer", null);
                        resultMap.put("leaseType", null);
                        resultMap.put("autoAnswerList", null);
                    }
                    result.setDataSet(resultMap);
                    return result;
                }
            }
            String applyIdStr = StringUtil.trim(condition.get("applyId"));
            //房源id 为空申请id不为空按照申请id查询自动应答信息
            if(StringUtil.hasText(applyIdStr)){
                int applyId = StringUtil.getAsInt(applyIdStr);
                //查询申请信息
                result = housesService.select(applyId, new HsOwnerHousingApplication());
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    HsOwnerHousingApplication application = (HsOwnerHousingApplication) result.getDataSet();
                    if(application != null){
                        Integer leaseType = application.getLeaseType();
                        resultMap.put("leaseType", leaseType);

                        //根据房源申请ID查询自动应答设置信息
                        condition.put("isDel", 0);
                        ResultVo replyResult = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                        if (replyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            List<Map<Object, Object>> replyList = (List<Map<Object, Object>>) replyResult.getDataSet();
                            if (replyList.size() > 0) {
                                for (Map<Object, Object> map : replyList) {
                                    map.remove("isDel");
                                }
                                resultMap.put("autoAnswerList", replyList);
                            }
                        }
                    }else{
                        resultMap.put("isAutoAnswer", null);
                        resultMap.put("leaseType", null);
                        resultMap.put("autoAnswerList", null);
                    }
                    result.setDataSet(resultMap);
                    return result;
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
     * 设置自动应答
     *
     * @param houseAutoReplySetting
     * @return
     */
    @Override
    public ResultVo addAutoReplySetting(HsHouseAutoReplySetting houseAutoReplySetting) {
        ResultVo result = new ResultVo();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (houseAutoReplySetting != null) {
                //自动应答ID
                Integer id = houseAutoReplySetting.getId() == null ? 0 : houseAutoReplySetting.getId();
                //房源ID
                Integer houseId = houseAutoReplySetting.getHouseId() == null ? 0 : houseAutoReplySetting.getHouseId();
                String startRentDate = StringUtil.trim(houseAutoReplySetting.getStartRentDate());
                //当起租日期不为空时，设置起租日期格式
                if (StringUtil.hasText(startRentDate)) {
                    houseAutoReplySetting.setBeginRentDate(sdf.parse(startRentDate));
                }
                //根据自动应答ID查询自动应答设置信息
                Map<Object, Object> condition = new HashMap<>(2);
                condition.put("id", id);
                condition.put("isDel", 0);
                ResultVo replyResult = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                if (replyResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    //自动应答信息结果集
                    List<Map<Object, Object>> replyList = (List<Map<Object, Object>>) replyResult.getDataSet();
                    if (replyList.size() > 0) {
                        //数据库中是否存在当前房源信息
                        boolean isExist = false;
                        //遍历判断需要修改得房源信息是否存在数据库中
                        for (Map<Object, Object> objectObjectMap : replyList) {
                            String tId = StringUtil.trim(objectObjectMap.get("id"));
                            if (tId.equals(id.toString())) {
                                //为避免is_del为null设置is_del值
                                String isDel = StringUtil.trim(objectObjectMap.get("isDel"));
                                houseAutoReplySetting.setIsDel(Integer.parseInt(isDel));
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":No listing information！");
                            return result;
                        }
                        //修改数据
                        ResultVo updateSettingResult = housesService.update(houseAutoReplySetting);
                        if (updateSettingResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                            result = updateSettingResult;
                            return result;
                        }
                    } else {
                        //根据房源ID查询自动应答设置信息
                        condition.clear();
                        condition.put("houseId", houseId);
                        condition.put("isDel", 0);
                        ResultVo resultVo = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                        //房源信息结果集
                        List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) resultVo.getDataSet();
                        if (dataSet.size() > 2) {
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":Auto answer can only be set up to 3！");
                            return result;
                        }
                        // 新增
                        ResultVo insertSettingResult = housesService.insert(houseAutoReplySetting);
                        if (insertSettingResult.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                            result = insertSettingResult;
                            return result;
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
     * 删除房源自动应答
     * @param autoReplyId
     * @return
     */
    @Override
    public ResultVo deleteAutoReply(String autoReplyId) {
        ResultVo result = new ResultVo();
        Map<Object, Object> map = new HashMap<>(1);
        List<HsHouseAutoReplySetting> list = new ArrayList<>();
        HsHouseAutoReplySetting hsHouseAutoReplySetting = new HsHouseAutoReplySetting();
        hsHouseAutoReplySetting.setId(Integer.parseInt(autoReplyId));
        hsHouseAutoReplySetting.setIsDel(1);
        list.add(hsHouseAutoReplySetting);
        map.put("list", list);
        result = housesService.batchUpdate(new HsHouseAutoReplySetting(), map);
        return result;
    }

    /**
     * 修改房源租金/预约看房时间
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateHousingInfo(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int memberId = Integer.parseInt(StringUtil.trim(condition.get("memberId")));
            int id = Integer.parseInt(StringUtil.trim(condition.get("id")));

            ResultVo houseResult = housesService.select(id,new HsMainHouse());
            if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse mainHouse = (HsMainHouse) houseResult.getDataSet();
                HsMainHouse hsMainHouse = new HsMainHouse();
                HsHouseLog houseLog = new HsHouseLog();
                hsMainHouse.setId(id);
                String _houseRent = StringUtil.trim(condition.get("houseRent"));
                if(StringUtil.hasText(_houseRent)){
                    BigDecimal houseRent = new BigDecimal(_houseRent);
                    hsMainHouse.setHouseRent(houseRent);
                    StringBuffer sb = new StringBuffer();
                    sb.append(mainHouse.getBedroomNum());
                    sb.append(" Available in ");
                    sb.append(mainHouse.getCommunity() +" ");
                    sb.append(houseRent+"AED");
                    if(mainHouse.getLeaseType()==0){//预约类型（0：出租，1：出售）
                        sb.append("/Year ");
                    }
                    hsMainHouse.setHouseName(sb.toString());
                    //hsMainHouse.setStandby1(mainHouse.getHouseRent().toString());
                    houseLog.setRemarks("修改价格");
                }

                BigDecimal houseRent = new BigDecimal(condition.get("houseRent")+"");
                hsMainHouse.setHouseRent(houseRent);
                houseLog.setRemarks("修改价格");
                //查询房源信息，如果发布价格为空，为第一次修改，设置租金/出售价为发布价格
                if(!StringUtil.hasText(mainHouse.getStandby1())){
                    hsMainHouse.setStandby1(mainHouse.getHouseRent()+"");
                }
                //是否客服联系
                if(StringUtil.hasText(StringUtil.trim(condition.get("isCustomerServiceRelation")))){
                    hsMainHouse.setAppointmentLookTime(StringUtil.trim(condition.get("appointmentLookTime"))); //预约时间
                    hsMainHouse.setIsCustomerServiceRelation(StringUtil.getAsInt(StringUtil.trim(condition.get("isCustomerServiceRelation")))); //是否客服联系
                    hsMainHouse.setAdvanceReservationDay(StringUtil.getAsInt(StringUtil.trim(condition.get("advanceReservationDay")))); //提前几天预约
                    houseLog.setRemarks("修改预约看房时间");
                }

                hsMainHouse.setVersionNo(0);
                result = housesService.update(hsMainHouse);

                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    if(StringUtil.hasText(_houseRent)){
                        //创建索引
                        HouseIndexMessage message = new HouseIndexMessage(id, HouseIndexMessage.INDEX, 0);
                        kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));
                    }

                    //记录操作日志
                    Date date = new Date();
                    houseLog.setNodeType(Constant.PASS_AUDIT_HOUSES_STATUS); //业主申请下架
                    houseLog.setUpdateBy(memberId); //更新人为提交申请的人（当前登录用户）
                    houseLog.setCreateBy(memberId);
                    houseLog.setBid(0);
                    houseLog.setHouseId(hsMainHouse.getId());
                    houseLog.setOperatorUid(memberId);
                    houseLog.setOperatorType(1); //1：普通会员，2：商家，3：系统管理员
                    houseLog.setCreateTime(date);
                    houseLog.setUpdateTime(date);
                    houseLog.setPostTime(date);
                    result = housesService.insert(houseLog);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }


    /**
     * 查询预约看房时间
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseSettingTime(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<Object, Object>> timeList = new ArrayList<>();
        try {
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId"))); //房源ID
            result = housesService.select(houseId, new HsMainHouse());
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsMainHouse mainHouse = (HsMainHouse) result.getDataSet();
                if(null == mainHouse){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"房源数据异常");
                }
                String appointLookTime = mainHouse.getAppointmentLookTime();
                if (StringUtil.hasText(appointLookTime)) {

                    //时间格式 Mon 13:00,19:00,20:00;Tue 13:00,19:00,20:00;Wed 13:00,19:00,20:00;Thur ;Fri ;Sat ;Sun ;
                    //获取每天设置的时间点
                    String[] weeksArray = appointLookTime.split(";");
                    List<String> weeks = Arrays.asList(weeksArray);
                    if (weeks.size() > 0) {
                        for (String week : weeks) {
                            Map<Object, Object> weekMap = new HashMap<>();
                            String[] timeSplit = week.split(" ");
                            if(timeSplit!=null && timeSplit.length>0){
                                if (timeSplit.length > 1) {
                                    String[] times = timeSplit[1].split(",");
                                    if (times.length > 0) {
                                        List<String> timeArray = Arrays.asList(times);
                                        weekMap.put("time", timeArray);
                                    } else {
                                        weekMap.put("time", null);
                                    }
                                } else {
                                    weekMap.put("time", null);
                                }
                                weekMap.put("week", timeSplit[0]);
                            }else{//
                                weekMap.put("week",null);
                                weekMap.put("time", null);
                            }

                            timeList.add(weekMap);
                        }
                    }
                }

                result.setDataSet(timeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 意见反馈列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getFeedbackList(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.selectList(new HsFeedback(),condition,0);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 意见反馈详情
     * @param id 反馈id
     * @return
     */
    @Override
    public ResultVo getFeedbackDetail(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            //反馈人姓名
            String name = "";
            HsFeedback feedback = new HsFeedback();
            ResultVo feedbackResultVo = memberService.select(id, new HsFeedback());
            if(feedbackResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && feedbackResultVo.getDataSet() != null){
                HsFeedback hsFeedback = (HsFeedback) feedbackResultVo.getDataSet();
                Integer memberId = hsFeedback.getCreateBy();
                //查询member表
                ResultVo memberResultVo = memberService.select(memberId, new HsMember());
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    HsMember member = (HsMember) memberResultVo.getDataSet();
                    String familyName = StringUtil.trim(member.getFamilyName());
                    String memberName = StringUtil.trim(member.getName());
                    name = familyName + memberName;
                }
                //member未查询到反馈人姓名，反馈人可能是业务员，查询user表
                if(!StringUtil.hasText(name)){
                    ResultVo userResultVo = memberService.select(memberId, new HsSysUser());
                    if(userResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && userResultVo.getDataSet() != null){
                        HsSysUser user = (HsSysUser) userResultVo.getDataSet();
                        name = StringUtil.trim(user.getUsername());
                    }
                }
                feedback = (HsFeedback) feedbackResultVo.getDataSet();
            }
            //封装返回值
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(feedback, SerializerFeature.WriteMapNullValue));
            Map feedbackMap = JSON.toJavaObject(jsonObject, Map.class);
            feedbackMap.put("name",name);
            resultVo.setDataSet(feedbackMap);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 修改意见反馈（增加备注、处理反馈）
     * @param hsFeedback
     * @return
     */
    @Override
    public ResultVo updateFeedback(HsFeedback hsFeedback){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.update(hsFeedback);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取房源下架详情
     * @param id  下架id
     * @return
     */
    @Override
    public ResultVo getObtained(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            ResultVo obtainedResultVo = housesService.select(id, new HsHouseObtained());
            if(obtainedResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return obtainedResultVo;
            }
            HsHouseObtained obtained = (HsHouseObtained) obtainedResultVo.getDataSet();
            if(obtained == null){
                return resultVo;
            }
            /**
             * 获取房源信息
             */
            HsMainHouse house = new HsMainHouse();
            //房源id
            Integer houseId = obtained.getHouseId();
            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                house = (HsMainHouse) houseResultVo.getDataSet();
            }
            /**
             * 获取业主信息
             */
            HsMember member = new HsMember();
            Integer memberId = StringUtil.getAsInt(StringUtil.trim(house.getMemberId()));
            ResultVo memberResultVo = memberService.select(memberId, new HsMember());
            if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                member = (HsMember)memberResultVo.getDataSet();
            }
            /**
             * 封装返回值
             */
            Map<String,Object> obtainedMap;
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(obtained, SerializerFeature.WriteMapNullValue));
            obtainedMap = JSON.toJavaObject(jsonObject, Map.class);
            //封装房源信息
            //预约类型（0：出租，1：出售）
            obtainedMap.put("leaseType",house.getLeaseType());
            //城市
            obtainedMap.put("city",StringUtil.trim(house.getCity()));
            //社区
            obtainedMap.put("community",StringUtil.trim(house.getCommunity()));
            //子社区
            obtainedMap.put("subCommunity",StringUtil.trim(house.getSubCommunity()));
            //房源所在区域名称
            obtainedMap.put("address",StringUtil.trim(house.getAddress()));
            //房源主图
            obtainedMap.put("houseMainImg",ImageUtil.imgResultUrl(house.getHouseMainImg()));
            //封装业主信息
            obtainedMap.put("memberMobile",StringUtil.trim(member.getMemberMoble()));
            //业主姓名
            obtainedMap.put("nickName",StringUtil.trim(member.getFamilyName()) + StringUtil.trim(member.getName()));
            resultVo.setDataSet(obtainedMap);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 修改房源下架信息（增加备注、取消下架、完成下架）
     * @param hsHouseObtained 房源下架申请
     * @return
     */
    @Override
    public ResultVo updateObtained(HsHouseObtained hsHouseObtained){
        ResultVo resultVo = new ResultVo();
        try {
            //房源id
            Integer houseId = hsHouseObtained.getHouseId();
            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                return houseResultVo;
            }
            HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
            Integer isDel = house.getIsDel();
            Integer houseStatus = house.getHouseStatus();
            if(isDel == 1 || houseStatus == 3){
                //房源不存在
                resultVo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                return resultVo;
            }

            resultVo = housesService.update(hsHouseObtained);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Integer status = hsHouseObtained.getStatus();
                if(status == 2){
                    /**
                     * 完成下架
                     */

                    String remark = hsHouseObtained.getRemark();
                    Integer userId = hsHouseObtained.getUpdateBy();
                    Map<Object, Object> condition = new HashMap<>(3);
                    condition.put("id",houseId);
                    condition.put("remark",remark);
                    condition.put("userId",userId);
                    resultVo = houseObtained(condition);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 房源下架申请列表
     * @param condition houseConfigDictcode
     * @return
     */
    @Override
    public ResultVo getObtainedList(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        List<Map<Object,Object>> resultList = new ArrayList<>();
        try {
            /**
             * 获取房源申请下架申请信息
             */
            resultVo = housesService.selectCustomColumnNamesList(HsHouseObtained.class,condition);
            if(resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return resultVo;
            }
            //房源申请下架申请
            List<Map<Object, Object>> obtainedList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(obtainedList == null || obtainedList.size() < 1){
                return resultVo;
            }
            /**
             * 房源列表
             */
            List<Map<Object, Object>> houseList = new ArrayList<>();
            //房源id列表
            List<Integer> houseIds = obtainedList.stream().map(obtained -> StringUtil.getAsInt(StringUtil.trim(obtained.get("houseId")))).collect(Collectors.toList());
            if(houseIds.size() > 0){
                //获取房源信息
                condition.clear();
                condition.put("houseIds",houseIds);
//                condition.put("isDel",0);
                ResultVo housesResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
                if(housesResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && housesResultVo.getDataSet() != null){
                    houseList = (List<Map<Object, Object>>) housesResultVo.getDataSet();
                }else{
                    logger.error("lsq: getObtainedList 获取房源信息失败");
                }
            }
            /**
             * 业主信息列表
             */
            List<HsMember> memberList = new ArrayList<>();
            //业主id列表
            List<Integer> memberIds = houseList.stream().map(house -> StringUtil.getAsInt(StringUtil.trim(house.get("memberId")))).collect(Collectors.toList());
            if(memberIds.size() > 0){
                //获取业主信息
                condition.clear();
                condition.put("memberIds",memberIds);
                condition.put("isDel",0);
                ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    memberList = (List<HsMember>) memberResultVo.getDataSet();
                }else{
                    logger.error("lsq: getObtainedList 获取业主信息失败");
                }
            }
            /**
             * 封装结果集
             */
            for (Map<Object, Object> obtainedMap : obtainedList) {
                //房源id
                int houseId = StringUtil.getAsInt(StringUtil.trim(obtainedMap.get("houseId")));
                //预约类型（0：出租，1：出售）
                Integer leaseType = null;
                //城市
                String city = "";
                //社区
                String community = "";
                //子社区
                String subCommunity = "";
                //房源所在区域名称
                String address = "";
                //房源主图
                String houseMainImg = "";
                //业主id
                Integer memberId = -1;
                //业主电话
                String memberMobile = "";
                Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(house -> StringUtil.getAsInt(StringUtil.trim(house.get("id"))) == houseId).findFirst();
                if(houseOptional.isPresent()){
                    //房源信息
                    Map<Object, Object> houseMap = houseOptional.get();
                    leaseType = StringUtil.getAsInt(StringUtil.trim(houseMap.get("leaseType")));
                    city = StringUtil.trim(houseMap.get(city));
                    community = StringUtil.trim(houseMap.get("community"));
                    subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                    address = StringUtil.trim(houseMap.get("address"));
                    houseMainImg = ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg")));
                    memberId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("memberId")));
                }

                Integer finalMemberId = memberId;
                Optional<HsMember> memberOptional = memberList.stream().filter(member -> member.getId().equals(finalMemberId)).findFirst();
                if(memberOptional.isPresent()){
                    //业主信息
                    HsMember memberMap = memberOptional.get();
                    memberMobile = StringUtil.trim(memberMap.getMemberMoble());
                }
                //封装房源、业主信息
                obtainedMap.put("leaseType",leaseType);
                obtainedMap.put("city",city);
                obtainedMap.put("community",community);
                obtainedMap.put("subCommunity",subCommunity);
                obtainedMap.put("address",address);
                obtainedMap.put("houseMainImg",houseMainImg);
                obtainedMap.put("memberMobile",memberMobile);
                resultList.add(obtainedMap);
            }
            resultVo.setDataSet(resultList);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取开发商直售项目申购人员列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getBuildingMemberList(Map<Object, Object> condition){
        ResultVo result;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID id");
            //项目id
            queryColumn.add("PROJECT_ID projectId");
            //人员id
            queryColumn.add("MEMBER_ID memberId");
            //姓氏
            queryColumn.add("FAMILY_NAME familyName");
            //名称
            queryColumn.add("NAME name");
            //国籍
            queryColumn.add("NATIONALITY nationality");
            //护照号
            queryColumn.add("PASSPORT_NUMBER passportNumber");
            //联系方式
            queryColumn.add("CONTACT_WAY contactWay");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            condition.put("queryColumn",queryColumn);
            result = housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class,condition);
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 导出开发商直售项目申购人员列表
     * @param condition
     * @return
     */
    @Override
    public XSSFWorkbook buildingMemberExport(Map<Object, Object> condition){
        XSSFWorkbook xssfWorkbook=null;
        try{
            List<BuildingMemberExport> buildingMemberExportList = new ArrayList<>();
            /**
             * 获取数据
             */
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            //姓氏
            queryColumn.add("FAMILY_NAME familyName");
            //名称
            queryColumn.add("NAME name");
            //国籍
            queryColumn.add("NATIONALITY nationality");
            //护照号
            queryColumn.add("PASSPORT_NUMBER passportNumber");
            //联系方式
            queryColumn.add("CONTACT_WAY contactWay");
            condition.put("queryColumn",queryColumn);
            ResultVo memberResultVo = housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class, condition);

            if(memberResultVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<Map<Object,Object>> memberList = (List<Map<Object,Object>>) memberResultVo.getDataSet();
            if(memberList==null || memberList.size()==0){
                return null;
            }

            for (Map<Object, Object> map : memberList) {
                BuildingMemberExport memberExport = new BuildingMemberExport();
                String familyName = StringUtil.trim(map.get("familyName"));
                String name = StringUtil.trim(map.get("name"));
                String nationality = StringUtil.trim(map.get("nationality"));
                String passportNumber = StringUtil.trim(map.get("passportNumber"));
                String contactWay = StringUtil.trim(map.get("contactWay"));
                memberExport.setFamilyName(familyName);
                memberExport.setName(name);
                memberExport.setNationality(nationality);
                memberExport.setPassportNumber(passportNumber);
                memberExport.setContactWay(contactWay);
                buildingMemberExportList.add(memberExport);
            }


            List<ExcelBean> excel=new ArrayList<>();
            Map<Integer,List<ExcelBean>> map=new LinkedHashMap<>();
            //设置标题栏
            //注释列
            excel.add(new ExcelBean("姓氏","familyName",0));
            excel.add(new ExcelBean("名称","name",0));
            excel.add(new ExcelBean("国籍","nationality",0));
            excel.add(new ExcelBean("护照号","passportNumber",0));
            excel.add(new ExcelBean("联系方式","contactWay",0));
            map.put(0, excel);
            String sheetName = "申请人信息";
            //调用ExcelUtil的方法
            xssfWorkbook = ExcelUtil.createExcelFile(BuildingMemberExport.class, buildingMemberExportList, map, sheetName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return xssfWorkbook;
    }

    /**
     * 关单记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo closeOrderList(Map<Object, Object> condition){
        ResultVo result;
        try {
            List<Map<Object, Object>> resultList = new ArrayList<>();
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            //主键ID
            queryColumn.add("ID orderId");
            //订单编号
            queryColumn.add("ORDER_CODE orderCode");
            //房源id
            queryColumn.add("HOUSE_ID houseId");
            //业主id
            queryColumn.add("OWNER_ID ownerId");
            //订单类型 0-租房->1-买房
            queryColumn.add("ORDER_TYPE orderType");
            //创建时间
            queryColumn.add("CREATE_TIME createTime");
            //交易状态 0:交易中 1:交易成功 2:交易失败
            queryColumn.add("TRADING_STATUS tradingStatus");
            condition.put("queryColumn", queryColumn);
            result = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                List<Map<Object, Object>> orderList = (List<Map<Object, Object>>) result.getDataSet();
                /**
                 * 获取房源信息
                 */
                List<Map<Object, Object>> houseList = new ArrayList<>();
                //房源ids
                List<Integer> houseIds = orderList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("houseId")))).collect(Collectors.toList());
                if(houseIds.size() > 0){
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
                    //房源主图
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                    condition.put("queryColumn", queryColumn);
                    condition.put("houseIds", houseIds);
                    ResultVo houseResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    if(houseResultVo.getResult()== ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                        houseList = (List<Map<Object, Object>>)houseResultVo.getDataSet();
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
                for (Map<Object, Object> orderMap : orderList) {
                    //业主id
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("ownerId")));
                    //房源id
                    int houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));
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
                    //房源code
                    String houseCode = "";
                    //房源主图
                    String houseMainImg = "";
                    //电话区号
                    String areaCode = "";
                    //业主电话
                    String memberMobile = "";
                    Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(house -> StringUtil.getAsInt(StringUtil.trim(house.get("houseId"))) == houseId).findFirst();
                    if(houseOptional.isPresent()){
                        //房源信息
                        Map<Object, Object> houseMap = houseOptional.get();
                        city = StringUtil.trim(houseMap.get(city));
                        community = StringUtil.trim(houseMap.get("community"));
                        subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                        address = StringUtil.trim(houseMap.get("address"));
                        houseMainImg = ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg")));
                        houseName = StringUtil.trim(houseMap.get("houseName"));
                        houseCode = StringUtil.trim(houseMap.get("houseCode"));
                    }

                    Optional<Map<Object, Object>> memberOptional = memberList.stream().filter(member -> StringUtil.getAsInt(StringUtil.trim(member.get("memberId"))) == ownerId).findFirst();
                    if(memberOptional.isPresent()){
                        //业主信息
                        Map<Object, Object> memberMap = memberOptional.get();
                        areaCode = StringUtil.trim(memberMap.get("areaCode"));
                        memberMobile = StringUtil.trim(memberMap.get("memberMoble"));
                    }
                    //封装房源、业主信息
                    orderMap.put("city",city);
                    orderMap.put("community",community);
                    orderMap.put("subCommunity",subCommunity);
                    orderMap.put("address",address);
                    orderMap.put("houseMainImg",houseMainImg);
                    orderMap.put("houseName",houseName);
                    orderMap.put("houseCode",houseCode);
                    orderMap.put("areaCode",areaCode);
                    orderMap.put("memberMobile",memberMobile);
                    resultList.add(orderMap);
                }
            }
            result.setDataSet(resultList);
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 搜索房源信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo searchHouse(Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        try {
            List<Map<Object,Object>> resultList = new ArrayList<>();
            result = housesService.selectList(new HsMainHouse(), condition, 0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS ) {
                List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) result.getDataSet();
                if (houseList != null && houseList.size() > 0) {
                    /**
                     * 业主信息列表
                     */
                    List<HsMember> memberList = new ArrayList<>();
                    //业主id列表
                    List<Integer> memberIds = houseList.stream().map(house -> StringUtil.getAsInt(StringUtil.trim(house.get("memberId")))).collect(Collectors.toList());
                    if (memberIds.size() > 0) {
                        //获取业主信息
                        condition.clear();
                        condition.put("memberIds", memberIds);
                        condition.put("isDel", 0);
                        ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
                        if (memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null) {
                            memberList = (List<HsMember>) memberResultVo.getDataSet();
                        }
                    }
                    /**
                     * 封装结果集
                     */
                    for (Map<Object, Object> houseMap : houseList) {
                        //房源id
                        int houseId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("id")));
                        //房源编号
                        String houseCode = StringUtil.trim(houseMap.get("houseCode"));
                        //预约类型（0：出租，1：出售）
                        Integer leaseType = StringUtil.getAsInt(StringUtil.trim(houseMap.get("leaseType")));
                        //城市
                        String city = StringUtil.trim(houseMap.get("city"));
                        //社区
                        String community = StringUtil.trim(houseMap.get("community"));
                        //子社区
                        String subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                        //房源所在区域名称
                        String address = StringUtil.trim(houseMap.get("address"));
                        //房源主图
                        String houseMainImg = ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg")));
                        //房屋创建日期
                        String createTimeStr = StringUtil.trim(houseMap.get("createTime"));
                        if(createTimeStr.endsWith(".0")){
                            createTimeStr = createTimeStr.substring(0,createTimeStr.length() - 2);
                        }
                        String createTime = createTimeStr;
                        //业主id
                        Integer memberId = -1;
                        //业主电话
                        String memberMobile = "";
                        Integer finalMemberId = memberId;
                        Optional<HsMember> memberOptional = memberList.stream().filter(member -> member.getId().equals(finalMemberId)).findFirst();
                        if (memberOptional.isPresent()) {
                            //业主信息
                            HsMember memberMap = memberOptional.get();
                            memberMobile = StringUtil.trim(memberMap.getMemberMoble());
                        }
                        //封装房源、业主信息
                        houseMap.clear();
                        houseMap.put("houseId", houseId);
                        houseMap.put("houseCode", houseCode);
                        houseMap.put("leaseType", leaseType);
                        houseMap.put("city", city);
                        houseMap.put("community", community);
                        houseMap.put("subCommunity", subCommunity);
                        houseMap.put("address", address);
                        houseMap.put("createTime", createTime);
                        houseMap.put("houseMainImg", houseMainImg);
                        houseMap.put("memberMobile", memberMobile);
                        resultList.add(houseMap);
                    }
                }
            }
            result.setDataSet(resultList);
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 下架房源
     * @param condition
     * @return
     */
    @Override
    public ResultVo houseObtained(Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        try {
            //房源id
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            //用户id
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            //备注
            String remark = StringUtil.trim(StringUtil.trim(condition.get("remark")));
            result = housesService.select(id, new HsMainHouse());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                Date date = new Date();
                HsMainHouse house = (HsMainHouse) result.getDataSet();
                Integer isDel = house.getIsDel();
                Integer houseStatus = house.getHouseStatus();
                if(isDel == 1 || houseStatus == 3){
                    //房源不存在
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                    return result;
                }
                /*查询房源是否存在外看任务、合同*/
                condition.clear();
                condition.put("houseId",id);
                condition.put("isFinished",1);
                condition.put("isDel",0);
                List<Integer> orderTypes = new ArrayList<>();
                orderTypes.add(1);
                orderTypes.add(2);
                //订单类型 0外获订单->1-外看订单->2合同订单->3-投诉（外看）->4投诉（外获）->5投诉（区域长）
                condition.put("orderTypes",orderTypes);
                ResultVo orderPoolResultVo = orderService.selectList(new HsSystemOrderPool(), condition, 0);
                if(orderPoolResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
                    return result;
                }
                List<Map<Object, Object>> orderPoolList = (List<Map<Object, Object>>) orderPoolResultVo.getDataSet();
                if(orderPoolList != null && orderPoolList.size() > 0){
                    //有正在进行的任务，无法进行下架操作
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"There are ongoing tasks and cannot be removed");
                    return result;
                }
                /*取消该房源正在进行中的订单*/
                condition.clear();
                condition.put("houseId",id);
                //交易状态 0:交易中 1:交易成功 2:交易失败
                condition.put("tradingStatus",0);
                condition.put("isDel",0);
                ResultVo orderResultVo = orderService.selectList(new HsHousingOrder(), condition, 1);
                if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<HsHousingOrder> orderList = (List<HsHousingOrder>) orderResultVo.getDataSet();
                    if(orderList != null && orderList.size() > 0){
                        HsHousingOrder housingOrder = orderList.get(0);
                        housingOrder.setIsCancel(2);
                        housingOrder.setRemark("业主下架房源，取消订单");
                        housingOrder.setUpdateTime(date);
                        housingOrder.setUpdateBy(userId);
                        ResultVo update = orderService.update(housingOrder);
                        if(update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                            return update;
                        }
                    }
                }
                /*删除正在进行中的议价聊天群*/
                condition.clear();
                condition.put("houseId",id);
                //议价状态（0 议价中 1 议价成功 2 议价失败）
                condition.put("bargainStatus",0);
                condition.put("isDel",0);
                ResultVo bargainResultVo = memberService.selectList(new HsMemberHousingBargain(), condition, 1);
                if(bargainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<HsMemberHousingBargain> bargainsList = (List<HsMemberHousingBargain>) bargainResultVo.getDataSet();
                    if(bargainsList != null && bargainsList.size() >0){
                        List<String> groupIds = bargainsList.stream().map(bargain -> bargain.getGroupId()).collect(Collectors.toList());
                        //删除环信聊天群
                        for (String groupId : groupIds) {
                            EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
                            Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                            JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                            JSONObject resultObjecct = object.getJSONObject("data");
                        }
                        //删除数据库聊天群
                        List<String> setColumn = new ArrayList<>();
                        setColumn.add("IS_DEL = 1");
                        condition.clear();
                        condition.put("setColumn",setColumn);
                        condition.put("groupIds", groupIds);
                        condition.put("isDel", 0);
                        Integer delSize = memberService.updateCustomColumnByCondition(new HsMemberHousingBargain(), condition);
                        if(delSize == null || delSize < 1){
                            //删除议价聊天室失败
                            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Delete bargaining room failed");
                        }
                    }
                }
                /*删除正在进行中的预约看房聊天群*/
                condition.clear();
                condition.put("houseId",id);
                //正在聊天中的预约信息
                condition.put("isChat",0);
                ResultVo applicResultVo = memberService.selectList(new HsMemberHousingApplication(), condition, 1);
                if(applicResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    List<HsMemberHousingApplication> applicationList = (List<HsMemberHousingApplication>) applicResultVo.getDataSet();
                    if(applicationList != null && applicationList.size() > 0){
                        List<String> groupIds = applicationList.stream().map(application -> application.getGroupId()).collect(Collectors.toList());
                        //删除环信聊天群
                        for (String groupId : groupIds) {
                            EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
                            Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                            JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                            JSONObject resultObjecct = object.getJSONObject("data");
                        }
                        //删除数据库聊天群
                        List<String> setColumn = new ArrayList<>();
                        //是否取消0:不取消，1：客户取消 2：业主取消  3:系统取消
                        setColumn.add("IS_CANCEL = 2");
                        setColumn.add("REMARK = '房源下架取消聊天'");
                        condition.clear();
                        condition.put("setColumn",setColumn);
                        condition.put("groupIds", groupIds);
                        condition.put("isDel", 0);
                        Integer delSize = memberService.updateCustomColumnByCondition(new HsMemberHousingApplication(), condition);
                        if(delSize == null || delSize < 1){
                            //删除预约聊天室失败
                            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Delete reservation chat room failure");
                        }
                    }
                }


                /*下架房源*/
                house.setHouseStatus(3);
                house.setIsDel(1);
                house.setUpdateBy(userId);
                ResultVo houseUpdate = housesService.update(house);
                if(houseUpdate.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){

                    HouseIndexMessage message = new HouseIndexMessage(id, HouseIndexMessage.REMOVE, 0);
                    kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));

                    //记录日志
                    HsHouseLog houseLog = new HsHouseLog();
                    houseLog.setHouseId(id);
                    //房源下架
                    houseLog.setNodeType(4);
                    houseLog.setCreateTime(date);
                    houseLog.setCreateBy(userId);
                    if(StringUtil.hasText(remark)){
                        houseLog.setRemarks(remark);
                    }
                    housesService.insert(houseLog);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 房源下架申请
     * @param houseObtained
     * @return
     */
    @Override
    public ResultVo houseApplyWithdraw(HsHouseObtained houseObtained){
        ResultVo result = new ResultVo();
        try {
            int createBy = houseObtained.getCreateBy();
            Integer houseId = houseObtained.getHouseId();
            Map<Object,Object> condition = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                if(house != null){
                    Integer isDel = house.getIsDel();
                    //房源状态：0>已提交 1审核通过 2商家申请下架 3下架 4已出售或出租
                    Integer houseStatus = house.getHouseStatus();
                    if(isDel == 1 || houseStatus > 1){
                        result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
                        result.setMessage(ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                        return result;
                    }
                    Date date = new Date();
                    //修改房源主信息表中的状态
                    HsMainHouse mainHouse = new HsMainHouse();
                    mainHouse.setId(houseId);
                    mainHouse.setHouseStatus(Constant.OWNER_APPLY_WITHDRAW_HOUSES_STATUS);
                    mainHouse.setUpdateTime(date);
                    mainHouse.setVersionNo(StringUtil.getAsInt(StringUtil.trim(house.getVersionNo())));
                    result = housesService.updateRecord(mainHouse);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //插入日志
                        HsHouseLog houseLog = new HsHouseLog();
                        //业主申请下架
                        houseLog.setNodeType(8);
                        houseLog.setUpdateBy(createBy); //更新人为提交申请的人（当前登录用户）
                        houseLog.setBid(0);
                        houseLog.setOperatorUid(createBy);
                        houseLog.setOperatorType(1); //1：普通会员，2：商家，3：系统管理员
                        houseLog.setCreateTime(date);
                        houseLog.setUpdateTime(date);
                        houseLog.setPostTime(date);
                        ResultVo houseLogVo = housesService.insert(houseLog);

                        //插入业主申请下架记录
                        houseObtained.setObtainedCode("XJ_"+RandomUtils.getRandomNumbers(8));
                        houseObtained.setCreateBy(createBy);
                        houseObtained.setCreateTime(date);
                        houseObtained.setPlatform(1);
                        result = housesService.insert(houseObtained);
                        if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            result.setDataSet(null);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 处理图片路径返回值
     * @param str
     * @return
     */
    public String splice(String str){
        if(StringUtil.hasText(str)){
            String[] split = str.split(",");
            String splice = "";
            for (String s : split) {
                splice += ImageUtil.IMG_URL_PREFIX + s + ",";
            }
            if(splice.endsWith(",")){
                splice = splice.substring(0,splice.length() - 1);
            }
            str = splice;
        }
        return str;
    }

    /**
     * 处理'业主提供资料及证件'表图片
     * @param houseCredentials
     * @return
     */
    public HsHouseCredentialsData credentialsImg(HsHouseCredentialsData houseCredentials){
        //POA复印件
        houseCredentials.setMandataryCopiesImg1(splice(houseCredentials.getMandataryCopiesImg1()));
        houseCredentials.setMandataryCopiesImg2(splice(houseCredentials.getMandataryCopiesImg2()));
        houseCredentials.setMandataryCopiesImg3(splice(houseCredentials.getMandataryCopiesImg3()));
        houseCredentials.setMandataryCopiesImg4(splice(houseCredentials.getMandataryCopiesImg4()));
        houseCredentials.setMandataryCopiesImg5(splice(houseCredentials.getMandataryCopiesImg5()));
        houseCredentials.setMandataryCopiesImg6(splice(houseCredentials.getMandataryCopiesImg6()));
        houseCredentials.setMandataryCopiesImg7(splice(houseCredentials.getMandataryCopiesImg7()));
        houseCredentials.setMandataryCopiesImg8(splice(houseCredentials.getMandataryCopiesImg8()));
        houseCredentials.setMandataryCopiesImg9(splice(houseCredentials.getMandataryCopiesImg9()));
        houseCredentials.setMandataryCopiesImg10(splice(houseCredentials.getMandataryCopiesImg10()));

        //被委托人护照照
        houseCredentials.setMandataryPassportImg1(splice(houseCredentials.getMandataryPassportImg1()));
        houseCredentials.setMandataryPassportImg2(splice(houseCredentials.getMandataryPassportImg2()));
        houseCredentials.setMandataryPassportImg3(splice(houseCredentials.getMandataryPassportImg3()));

        //被委托人签证照片
        houseCredentials.setMandataryVisaImg1(splice(houseCredentials.getMandataryVisaImg1()));
        houseCredentials.setMandataryVisaImg2(splice(houseCredentials.getMandataryVisaImg2()));
        houseCredentials.setMandataryVisaImg3(splice(houseCredentials.getMandataryVisaImg3()));

        //被委托人ID卡照片
        houseCredentials.setMandataryIdcardImg1(splice(houseCredentials.getMandataryIdcardImg1()));
        houseCredentials.setMandataryIdcardImg2(splice(houseCredentials.getMandataryIdcardImg2()));
        houseCredentials.setMandataryIdcardImg3(splice(houseCredentials.getMandataryIdcardImg3()));
        houseCredentials.setMandataryIdcardImg4(splice(houseCredentials.getMandataryIdcardImg4()));

        //委托书
        houseCredentials.setLetterAuthorization(splice(houseCredentials.getLetterAuthorization()));
        houseCredentials.setLetterAuthorization2(splice(houseCredentials.getLetterAuthorization2()));

        //房屋租赁委托人签字
        houseCredentials.setRentAuthorizationSignImg1(splice(houseCredentials.getRentAuthorizationSignImg1()));
        houseCredentials.setRentAuthorizationSignImg2(splice(houseCredentials.getRentAuthorizationSignImg2()));
        houseCredentials.setRentAuthorizationSignImg3(splice(houseCredentials.getRentAuthorizationSignImg3()));

        //formA确认
        houseCredentials.setFormaConfirmImg1(splice(houseCredentials.getFormaConfirmImg1()));
        houseCredentials.setFormaConfirmImg2(splice(houseCredentials.getFormaConfirmImg2()));
        houseCredentials.setFormaConfirmImg3(splice(houseCredentials.getFormaConfirmImg3()));

        //房产证照片
        houseCredentials.setPocImg1(splice(houseCredentials.getPocImg1()));
        houseCredentials.setPocImg2(splice(houseCredentials.getPocImg2()));
        houseCredentials.setPocImg3(splice(houseCredentials.getPocImg3()));

        //房屋产权人护照照片
        houseCredentials.setReoPassportImg1(splice(houseCredentials.getReoPassportImg1()));
        houseCredentials.setReoPassportImg2(splice(houseCredentials.getReoPassportImg2()));
        houseCredentials.setReoPassportImg3(splice(houseCredentials.getReoPassportImg3()));

        //房屋租赁合同图片
        houseCredentials.setHouseRentContractImg1(splice(houseCredentials.getHouseRentContractImg1()));
        houseCredentials.setHouseRentContractImg2(splice(houseCredentials.getHouseRentContractImg2()));
        houseCredentials.setHouseRentContractImg3(splice(houseCredentials.getHouseRentContractImg2()));
        houseCredentials.setHouseRentContractImg4(splice(houseCredentials.getHouseRentContractImg4()));

        //房屋户型图
        houseCredentials.setHouseHoldImg1(splice(houseCredentials.getHouseHoldImg1()));
        houseCredentials.setHouseHoldImg2(splice(houseCredentials.getHouseHoldImg2()));
        houseCredentials.setHouseHoldImg3(splice(houseCredentials.getHouseHoldImg3()));
        houseCredentials.setHouseHoldImg4(splice(houseCredentials.getHouseHoldImg4()));
        houseCredentials.setHouseHoldImg5(splice(houseCredentials.getHouseHoldImg5()));
        houseCredentials.setHouseHoldImg6(splice(houseCredentials.getHouseHoldImg6()));
        houseCredentials.setHouseHoldImg7(splice(houseCredentials.getHouseHoldImg7()));
        houseCredentials.setHouseHoldImg8(splice(houseCredentials.getHouseHoldImg8()));
        houseCredentials.setHouseHoldImg9(splice(houseCredentials.getHouseHoldImg9()));
        houseCredentials.setHouseHoldImg10(splice(houseCredentials.getHouseHoldImg10()));
        return houseCredentials;
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
    }
}
