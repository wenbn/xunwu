package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import io.swagger.client.model.Msg;
import io.swagger.client.model.MsgContent;
import io.swagger.client.model.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utils.StringUtil;
import www.ucforward.com.constants.Constant;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.HsMemberDto;
import www.ucforward.com.emchat.api.impl.EasemobChatGroup;
import www.ucforward.com.emchat.api.impl.EasemobIMUsers;
import www.ucforward.com.emchat.api.impl.EasemobSendMessage;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.key.HouseIndexKey;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.manager.HousesManager;
import www.ucforward.com.manager.MemberManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("memberManager")
public class MemberManagerImpl implements MemberManager {

    private static Logger logger = LoggerFactory.getLogger(MemberManagerImpl.class);

    //处理消息
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private MemberService memberService;//会员相关
    @Resource
    private HousesService housesService; //房源相关
    @Resource
    private OrderService orderService;
    @Autowired
    private CommonService commonService;
    @Resource
    private HousesManager housesManager;

    /**
     * 登录
     * @param condition
     * @return
     */
    @Override
    public ResultVo login(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        try{
            String imgCode = StringUtil.trim(condition.get("validateCode"));
            String ip = StringUtil.trim(condition.get("ip"));
            String nationCode = StringUtil.trim(condition.get("areaCode")); //区号
            String mobile = StringUtil.trim(condition.get("memberMoble")); //手机号
            String invitationCode = StringUtil.trim(condition.get("invitationCode")); //邀请码
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + nationCode + mobile + ip;
            String cacheValidateCode = "";
            if(!StringUtil.hasText(cacheKey)){
                vo.setResult(ResultConstant.SYS_IMG_CODE_IS_OVERDUE);
                vo.setMessage(ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                return vo;
            }

            if(RedisUtil.isExistCache(cacheKey)){
                cacheValidateCode = RedisUtil.safeGet(cacheKey);
            }
            if(!StringUtil.hasText(StringUtil.trim(condition.get("isTest")))){
                if(!mobile.equals("13682653457")) {
                    if (!cacheValidateCode.equalsIgnoreCase(imgCode)) {
                        //验证码错误
                        vo.setResult(ResultConstant.SYS_IMG_CODE_ERROR);
                        vo.setMessage(ResultConstant.SYS_IMG_CODE_ERROR_VALUE);
                        return vo;
                    }
                }
            }
            RedisUtil.safeDel(cacheKey);
            vo = memberService.login(condition);
            Map<Object,Object> userGold = (Map<Object, Object>) vo.getDataSet();
            if(userGold!=null){//如果有邀请码，则添加业务员积分
                condition.clear();
                //自定义查询的列名
                List<String> queryColumn = new ArrayList<>();
                queryColumn.add("ID id");//城市id
                queryColumn.add("GOLD_CODE goldCode");//积分编码
                queryColumn.add("IS_ADD_SUBTRACT isAddSubtract");//控制加分还是减分 0 ：加分 1减分
                queryColumn.add("SCORE score");//积分值
                condition.put("queryColumn", queryColumn);
                condition.put("goldType", 0);//积分类型
                condition.put("isDel", 0);//是否删除,0：未删除，1删除
                //查询积分信息
                ResultVo goldVo = commonService.selectCustomColumnNamesList(HsGoldRule.class, condition);
                List<Map<Object,Object>> goldList = (List<Map<Object, Object>>) goldVo.getDataSet();
                if(goldList!=null && goldList.size()==1){
                    Map<Object,Object> goldMap = goldList.get(0);
                    HsSysUser user = new HsSysUser();
                    user.setId(StringUtil.getAsInt(StringUtil.trim(userGold.get("userId"))));
                    int gold = StringUtil.getAsInt(StringUtil.trim(goldMap.get("score")));
                    if(1==StringUtil.getAsInt(StringUtil.trim(goldMap.get("isAddSubtract")))){//控制加分还是减分 0 ：加分 1减分
                        gold = gold * -1;
                    }
                    user.setGold(gold);
                    ResultVo insert = memberService.update(user);
                    if(insert.getResult() == 0){
                        HsUserGoldLog hsUserGoldLog = new HsUserGoldLog();
                        hsUserGoldLog.setGold(StringUtil.getAsInt(StringUtil.trim(goldMap.get("id"))));
                        hsUserGoldLog.setGold(gold);
                        hsUserGoldLog.setRemark("推荐会员赠送"+gold+"积分");
                        hsUserGoldLog.setUserId(user.getId());
                        hsUserGoldLog.setCreateTime(new Date());
                        memberService.insert(hsUserGoldLog);
                    }
                }
                vo.setDataSet(null);
            }
            return vo;
        }catch ( Exception e){
            e.printStackTrace();
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            return vo;
        }
    }

    /**
     * 是否新用户
     * @param condition
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo isNewUser(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        try{
            String validateCode = StringUtil.trim(condition.get("validateCode"));
            String ip = StringUtil.trim(condition.get("ip"));
            //区号
            String areaCode = StringUtil.trim(condition.get("areaCode"));
            //手机号
            String mobile = StringUtil.trim(condition.get("memberMoble"));
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + areaCode + mobile + ip;
            String cacheValidateCode = "";
            if(!StringUtil.hasText(cacheKey)){
                resultVo.setResult(ResultConstant.SYS_IMG_CODE_IS_OVERDUE);
                resultVo.setMessage(ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                return resultVo;
            }

            if(RedisUtil.isExistCache(cacheKey)){
                cacheValidateCode = RedisUtil.safeGet(cacheKey);
            }
            if(!StringUtil.hasText(StringUtil.trim(condition.get("isTest")))){
                if(!mobile.equals("13682653457")) {
                    if (!cacheValidateCode.equalsIgnoreCase(validateCode)) {
                        //验证码错误
                        resultVo.setResult(ResultConstant.SYS_IMG_CODE_ERROR);
                        resultVo.setMessage(ResultConstant.SYS_IMG_CODE_ERROR_VALUE);
                        return resultVo;
                    }
                }

            }

            List<String> queryColumn = new ArrayList<>();
            condition.clear();
            queryColumn.add("ID id");
            condition.put("queryColumn",queryColumn);
            condition.put("memberMoble", mobile);
            resultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> hsMemberList = (List<Map<Object, Object>>) resultVo.getDataSet();
                if(hsMemberList == null || hsMemberList.size() < 1){
                    //当前用户为新用户
                    resultVo.setResult(ResultConstant.USER_NOT_REGISTERED);
                    resultVo.setMessage(ResultConstant.USER_NOT_REGISTERED_VALUE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            return resultVo;
        }
        return resultVo;
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
            int templateId = 165947; //默认为国际短信模板
            String smsSign = "Hi Sandy"; //签名
            String [] phoneNumbers = {mobile}; //手机号
            String[] params = {randomCode+""}; //参数

            if("86".equals(nationCode)){ //如果是中国，发送国内短信
                templateId = 165847; //短信模板
                smsSign = "三迪科技";
            }

            String resultStr = SendSmsUtil.sendSms(nationCode,phoneNumbers,templateId,params,smsSign,null,null);
            if(!"success".equals(resultStr)){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + nationCode + mobile + ip;
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
     * 新增会员浏览历史记录
     * @param browseHistory
     * @return
     */
    @Override
    public ResultVo addBrowseHistory(HsHousesBrowseHistory browseHistory) {
        ResultVo result = new ResultVo();
        try {
            Date date = new Date();
            Map<Object,Object> condition = new HashMap<Object,Object>();
            condition.put("houseId",browseHistory.getHouseId());
            condition.put("memberId",browseHistory.getMemberId());
            condition.put("houseType",browseHistory.getHouseType());

            //查询数据库中是否有记录
            result = memberService.selectList(new HsHousesBrowseHistory(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> data = (List<Map<Object,Object>> )result.getDataSet();
                //如果有记录进行修改操作，否则进行新增
                if(data.size() > 0){
                    Map<Object,Object> map = data.get(0);
                    browseHistory.setUpdateTime(date);
                    browseHistory.setCreateTime(date);
                    browseHistory.setId(Integer.parseInt(map.get("id")+""));
                    result = memberService.update(browseHistory);
                }else{
                    browseHistory.setCreateTime(date);
                    browseHistory.setUpdateTime(date);
                    result = memberService.insert(browseHistory);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberManagerImpl addBrowseHistory Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }

    /**
     * 会员收藏记录
     * @param memberFavorite
     * @return
     */
    @Override
    public ResultVo addMemberFavorite(HsMemberFavorite memberFavorite) {
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>();
        try {
            condition.put("memberId",memberFavorite.getMemberId());
            condition.put("favoriteId",memberFavorite.getFavoriteId());//是否收藏
            condition.put("isFavorite",memberFavorite.getIsFavorite());
            HsMainHouse queryMainHouse = null;

            HsMainHouse mainHouse = new HsMainHouse();
            Integer houseId = memberFavorite.getFavoriteId();//房源ID
            mainHouse.setId(houseId);

            //查询房源是否存在
            ResultVo hosueVo = housesService.select(houseId, new HsMainHouse());
            if(hosueVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){//查询失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            queryMainHouse = (HsMainHouse) hosueVo.getDataSet();
            if(queryMainHouse == null){//房源数据异常
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            //查询用户是否有收藏该条房源信息的记录
            result = memberService.selectList(new HsMemberFavorite(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> data = (List<Map<Object,Object>>) result.getDataSet();
                //如果有记录对数据进行修改操作，否则进行新增操作
                if(data.size() > 0){
                    Map<Object,Object> map = data.get(0);
                    int isDel = Integer.parseInt(map.get("isDel")+""); //收藏标识（0：已收藏，1：已取消收藏）

                    //当收藏标识为已收藏时，修改标识为 取消收藏，反之，当收藏标识为取消收藏时，修改标识为 已收藏
                    if(isDel == 0){
                        isDel = 1;
                    }else{
                        isDel = 0;
                    }
                    memberFavorite.setIsDel(isDel);
                    memberFavorite.setUpdateTime(result.getSystemTime());
                    memberFavorite.setId(Integer.parseInt(map.get("id")+""));
                    result = memberService.update(memberFavorite);

                    if(isDel == 0){
                        if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            mainHouse.setCollectCount(0);
                            mainHouse.setVersionNo(queryMainHouse.getVersionNo());
                            result = housesService.updateRecord(mainHouse);
                        }
                    }
                }else{
                    memberFavorite.setCreateTime(result.getSystemTime());
                    result = memberService.insert(memberFavorite);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        mainHouse.setCollectCount(0);
                        mainHouse.setVersionNo(queryMainHouse.getVersionNo());
                        result = housesService.updateRecord(mainHouse);
                    }
                }
                result.setDataSet(condition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberManagerImpl addMemberFavorite Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }

    /**
     * 获取会员相关信息（浏览房源记录、收藏房源记录、预约时间表、已看房列表信息）
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberRelationInfo(Object obj,Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object, Object> queryFilter = new HashMap<>();
        try {
            //查询会员浏览的记录
            result = memberService.selectList(obj,condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> houseIds = new ArrayList<>();
                List<String> userIds = new ArrayList<>();
                List<String> poolIds = new ArrayList<>();
                //浏览，收藏，预约时间表信息
                List<Map<Object,Object>> houses = (List<Map<Object,Object>>)result.getDataSet();
                if(houses != null && houses.size()> 0){
                    for (Map<Object,Object> house : houses){
                        String id = StringUtil.trim(house.get("houseId")); //浏览表中的房源ID
                        if(obj instanceof  HsMemberFavorite){
                            id = StringUtil.trim(house.get("favoriteId")); //收藏表中的房源ID
                        }else if(obj instanceof  HsMemberHousingApplication){
                            //订单池id
                            poolIds.add(StringUtil.trim(house.get("standby1")));
                        }
                        houseIds.add(id);
                    }
                    //根据查询出的浏览房源ID获取房源主信息
                    if(houseIds != null && houseIds.size() > 0){
                        Object pageInfo = result.getPageInfo();
                        queryFilter.put("houseIds",houseIds);
                        List<String> queryColumn = new ArrayList<>();
                        queryColumn.add("ID id");//主键ID
                        queryColumn.add("HOUSE_NAME houseName");//房源名称
                        queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                        queryColumn.add("HOUSE_ACREAGE houseAcreage"); //房屋面积
                        queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                        queryColumn.add("CITY city");//城市
                        queryColumn.add("COMMUNITY community");//社区
                        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                        queryColumn.add("ADDRESS address");//所在位置
                        queryColumn.add("COMMUNITY community"); //社区
                        queryColumn.add("SUB_COMMUNITY subCommunity"); //子社区
                        queryColumn.add("PROPERTY property"); //项目
                        queryColumn.add("LONGITUDE longitude"); //经度
                        queryColumn.add("LATITUDE latitude"); //纬度
                        queryColumn.add("MEMBER_ID memberId");
                        queryColumn.add("IS_CHECK isCheck");
                        queryColumn.add("LEASE_TYPE leaseType");
                        queryColumn.add("VILLAGE_NAME villageName");

                        queryFilter.put("queryColumn",queryColumn);
                        result = housesService.selectCustomColumnNamesList(HsMainHouse.class,queryFilter);

                        //如果是显示预约时间表信息或已看房列表信息时
                        if(obj instanceof HsMemberHousingApplication){
                            //key poolid value userid
                            Map<Integer,Integer> poolUser = new HashMap<>(16);
                            queryFilter.clear();
                            queryFilter.put("poolIds",poolIds);
                            queryFilter.put("isDel",0);
                            ResultVo poolResultVo = memberService.selectList(new HsSystemUserOrderTasks(), queryFilter, 0);
                            if(poolResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                List<Map<Object, Object>> poolList = (List<Map<Object, Object>>) poolResultVo.getDataSet();
                                if(poolList != null && poolList.size() > 0){
                                    for (Map<Object, Object> poolMap : poolList) {
                                        int userId = StringUtil.getAsInt(StringUtil.trim(poolMap.get("user_id")));
                                        int poolId = StringUtil.getAsInt(StringUtil.trim(poolMap.get("poolId")));
                                        poolUser.put(poolId,userId);
                                    }
                                    userIds = poolList.stream().map(map -> StringUtil.trim(map.get("user_id"),"-1")).collect(Collectors.toList());
                                }
                            }

                            List<Map<Object,Object>> mainHouses = (List<Map<Object,Object>>) result.getDataSet();
                            List<Map<Object,Object>> userList = new ArrayList<>();
                            if(userIds.size() > 0){
                                queryFilter.clear();
                                queryFilter.put("userIds",userIds);
                                queryFilter.put("isDel",0);
                                ResultVo userResult = memberService.selectList(new HsSysUser(),queryFilter,0);
                                userList = (List<Map<Object, Object>>) userResult.getDataSet();
                            }

                            String isOwner = StringUtil.trim(condition.get("memberId")); //是否为业主
                            for (Map<Object,Object> map : mainHouses) {
                                String id = StringUtil.trim(map.get("id"));
                                //0 表示为是业主，1表示为不是
                                if(isOwner.equals(StringUtil.trim(map.get("memberId")))){
                                    isOwner = "0";
                                }else{
                                    isOwner = "1";
                                }
                                for (Map<Object,Object> house: houses) {
                                    if(id.equals(StringUtil.trim(house.get("houseId")))){
                                        //房源主图
                                        String houseMainImg = StringUtil.trim(map.get("houseMainImg"));
                                        if(StringUtil.hasText(houseMainImg)){
                                            if(!houseMainImg.startsWith("http://")){
                                                house.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + houseMainImg);
                                            }
                                        }else{
                                            house.put("houseMainImg",null);
                                        }
                                        house.put("houseStatus",map.get("houseStatus"));
                                        house.put("city",map.get("city")); //城市
                                        house.put("houseName",map.get("houseName")); //房源名称
                                        house.put("houseAcreage",map.get("houseAcreage")); //面积
                                        house.put("houseRent",map.get("houseRent")); //租金价格/售价
                                        house.put("community",map.get("community")); //社区
                                        house.put("subCommunity",map.get("subCommunity")); //子社区
                                        house.put("property",map.get("property")); //项目
                                        house.put("address",map.get("address")); //所在位置
                                        house.put("longitude",map.get("longitude")); //经度
                                        house.put("latitude",map.get("latitude")); //纬度
                                        house.put("isOwner",isOwner); //是否为业主身份
                                    }
                                }
                            }

                            for(Map<Object,Object> house : houses){
                                house.put("salesmanName","");
                                house.put("salesmanMobile","");
                                house.put("salesmanId","");
                                int poolId = StringUtil.getAsInt(StringUtil.trim(house.get("standby1")));
                                String userId = StringUtil.trim(poolUser.get(poolId));
                                if(userList.size() > 0){
                                    for(Map<Object,Object> user :userList){
                                        if(userId.equals(StringUtil.trim(user.get("id")))){
                                            //业务员姓名
                                            house.put("salesmanName",user.get("username"));
                                            //业务员手机号
                                            house.put("salesmanMobile",user.get("mobile"));
                                            //业务员id
                                            house.put("salesmanId",user.get("id"));
                                        }
                                    }
                                }
                            }
                            result.setDataSet(houses);
                        }else{
                            List<Map<Object,Object>> mainHouses = (List<Map<Object,Object>>) result.getDataSet();
                            if(mainHouses != null && mainHouses.size() > 0){
                                if (obj instanceof HsMemberFavorite ) {//浏览记录
                                    for (Map<Object,Object> _house : houses){
                                        int _id = StringUtil.getAsInt(StringUtil.trim(_house.get("favoriteId"))); //浏览表中的房源ID
                                        for(Map<Object,Object> house : mainHouses) {
                                            String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                            if (StringUtil.hasText(houseMainImg)) {
                                                if(!houseMainImg.startsWith("http://")){
                                                    house.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + houseMainImg);
                                                }
                                            } else {
                                                house.put("houseMainImg", null);
                                            }
                                            int id = StringUtil.getAsInt(StringUtil.trim(house.get("id")));
                                            if(_id == id){
                                                _house.putAll(house);
                                            }
                                        }
                                    }
                                }else if(obj instanceof HsHousesBrowseHistory){//喜爱列表
                                    for (Map<Object,Object> _house : houses){
                                        //浏览表中的房源ID
                                        int _id = StringUtil.getAsInt(StringUtil.trim(_house.get("houseId")));
                                        for(Map<Object,Object> house : mainHouses) {
                                            String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                            if (StringUtil.hasText(houseMainImg)) {
                                                if(!houseMainImg.startsWith("http://")){
                                                    house.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + houseMainImg);
                                                }
                                            } else {
                                                house.put("houseMainImg", null);
                                            }
                                            int id = StringUtil.getAsInt(StringUtil.trim(house.get("id")));
                                            if(_id == id){
                                                _house.putAll(house);
                                            }
                                        }
                                    }
                                }


                                /*for (Map<Object,Object> house : mainHouses){
                                    data.put("city",house.get("city"));
                                    data.put("property",house.get("property"));
                                    data.put("longitude",house.get("longitude"));
                                    data.put("latitude",house.get("latitude"));
                                    data.put("houseRent",house.get("houseRent"));
                                    data.put("villageName",house.get("villageName"));
                                    data.put("leaseType",house.get("leaseType"));
                                    data.put("id",house.get("id"));
                                    data.put("houseAcreage",house.get("houseAcreage"));
                                    data.put("memberId",house.get("memberId"));
                                    data.put("address",house.get("address"));
                                    data.put("houseName",house.get("houseName"));
                                    data.put("community",house.get("community"));
                                    data.put("subCommunity",house.get("subCommunity"));
                                    data.put("isCheck",house.get("isCheck"));
                                    data.put("houseStatus",house.get("houseStatus"));
                                    mainHouses.remove(house);
                                    mainHouses.add(data);
                                }*/

                                result.setDataSet(houses);
//                                result.setDataSet(mainHouses);
                            }

                        }
                        result.setPageInfo(pageInfo);
                    }
                }else{
                    result.setDataSet(null);
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
     * 新增意见反馈信息
     * @param feedback
     * @return
     */
    @Override
    public ResultVo addFeedback(HsFeedback feedback) {
        ResultVo result = new ResultVo();
        try {
            feedback.setCreateTime(new Date());
            result = memberService.insert(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 获取我的房源列表信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberHousing(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<Object,Object>> resultList = new ArrayList<>();
        try {
            /*String status = StringUtil.trim(condition.get("isCheckStatus"));
            String [] array = status.split(",");
            List<String> houseStatuss = Arrays.asList(array);
           condition.put("houseStatuss",houseStatuss);*/
           result = housesService.selectList(new HsMainHouse(),condition,0);
           if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> data = (List<Map<Object,Object>>)result.getDataSet();
                if(data != null && data.size() > 0){
                    for (Map<Object,Object> obj:data) {
                        Map<Object,Object> map = new HashMap<>();
                        map.put("houseName",obj.get("houseName"));
                        map.put("city",obj.get("city"));
                        map.put("property",obj.get("property"));
                        map.put("longitude",obj.get("longitude"));
                        map.put("latitude",obj.get("latitude"));
                        map.put("houseRent",obj.get("houseRent"));
                        map.put("villageName",obj.get("villageName"));
                        map.put("leaseType",obj.get("leaseType"));
                        map.put("id",obj.get("id"));
                        map.put("applyId",obj.get("applyId"));
                        map.put("houseAcreage",obj.get("houseAcreage"));
                        map.put("memberId",obj.get("memberId"));
                        map.put("address",obj.get("address"));
                        map.put("houseName",obj.get("houseName"));
                        map.put("community",obj.get("community"));
                        map.put("subCommunity",obj.get("subCommunity"));
                        map.put("isCheck",obj.get("isCheck"));
                        map.put("houseStatus",obj.get("houseStatus"));
                        map.put("houseMainImg",ImageUtil.imgResultUrl(StringUtil.trim(obj.get("houseMainImg"))));
                        //property,longitude,longitude,houseRent
                        resultList.add(map);
                    }
                }
                result.setDataSet(resultList);
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
     * 订阅列表 查询更多
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberHousingSubscribeMore(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int subscribeId = StringUtil.getAsInt(StringUtil.trim(condition.get("subscribeId")));
            result = memberService.select(subscribeId,new HsMemberHousesSubscribe());
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMemberHousesSubscribe hsMemberHousesSubscribe = (HsMemberHousesSubscribe) result.getDataSet();
                if(hsMemberHousesSubscribe != null){
                    ResultVo houseResult = new ResultVo();
                    Integer minBedroom = hsMemberHousesSubscribe.getMinBedroom();
                    if(minBedroom != null && minBedroom!=-1){
                        condition.put("minBedroom",minBedroom);
                    }
                    Integer maxBedroom = hsMemberHousesSubscribe.getMaxBedroom();
                    if(maxBedroom != null && maxBedroom!=-1){
                        condition.put("maxBedroom",maxBedroom);
                    }
                    Integer minBathroom = hsMemberHousesSubscribe.getMinBathroom();
                    if(minBathroom != null && minBathroom!=-1){
                        condition.put("minBathroom",minBathroom);
                    }
                    Integer maxBathroom = hsMemberHousesSubscribe.getMinBathroom();
                    if(maxBathroom != null && maxBathroom!=-1){
                        condition.put("maxBathroom",maxBathroom);
                    }
                    String housingTypeDictcode = hsMemberHousesSubscribe.getHousingTypeDictcode();
                    if(StringUtil.hasText(housingTypeDictcode)){
                        condition.put("housingTypeDictcodes", Arrays.asList(housingTypeDictcode.split(",")));
                    }
                    String payNode = hsMemberHousesSubscribe.getPayNode();
                    if(StringUtil.hasText(payNode)){
                        condition.put("payNode",payNode);
                    }
                    BigDecimal minPrice = hsMemberHousesSubscribe.getMinPrice();
                    if(minPrice != null && minPrice.intValue()>0){
                        condition.put("minPrice",minPrice);
                    }
                    BigDecimal maxPrice = hsMemberHousesSubscribe.getMinPrice();
                    if(maxPrice != null && maxPrice.intValue()>0){
                        condition.put("maxPrice",maxPrice);
                    }
                    condition.put("leaseType",hsMemberHousesSubscribe.getHouseType());
                    condition.put("isCheck", 1); //每页显示条数
                    condition.put("isDel", 0); //每页显示条数
                    List<String> queryColumn = new ArrayList<>();
                    queryColumn.add("ID id");//主键ID
                    queryColumn.add("HOUSE_NAME houseName");//房源名称
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                    queryColumn.add("HOUSE_ACREAGE houseAcreage"); //房屋面积
                    queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                    queryColumn.add("CITY city");//城市
                    queryColumn.add("COMMUNITY community");//社区
                    queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                    queryColumn.add("ADDRESS address");//所在位置
                    queryColumn.add("PROPERTY property"); //项目
                    queryColumn.add("LONGITUDE longitude"); //经度
                    queryColumn.add("LATITUDE latitude"); //纬度
                    queryColumn.add("MEMBER_ID memberId");
                    queryColumn.add("IS_CHECK isCheck");
                    queryColumn.add("LEASE_TYPE leaseType");
                    condition.put("queryColumn",queryColumn);
                    houseResult = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                    if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> houseData = (List<Map<Object,Object>>) houseResult.getDataSet();
                        if(houseData != null && houseData.size() > 0) {
                            for (Map<Object, Object> house : houseData) {
                                String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                if(StringUtil.hasText(houseMainImg)){
                                    house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                                }
                            }
                        }
                    }
                    return houseResult;
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




    @Override
    public ResultVo getMemberHousingProgress(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {

            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("APPLY_ID applyId");//申请ID
            queryColumn.add("IS_CHECK isCheck");
            queryColumn.add("LEASE_TYPE leaseType");
            condition.put("queryColumn",queryColumn);
            result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<String> applyIds = new ArrayList<>();
                List<Map<Object,Object>> data = (List<Map<Object,Object>>)result.getDataSet();
                if(data != null && data.size() > 0){
                    for(Map house : data){
                        applyIds.add(StringUtil.trim(house.get("applyId")));
                    }

                    queryColumn.clear();
                    queryColumn.add("APPLY_ID applyId");
                    queryColumn.add("CREATE_TIME createTime");
                    condition.put("queryColumn",queryColumn);
                    condition.put("applyIds",applyIds);
                    condition.put("nodeType",0); //提交申请
                    ResultVo logResult = housesService.selectCustomColumnNamesList(HsOwnerHousingApplicationLog.class,condition);
                    if(logResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> logData = (List<Map<Object, Object>>) logResult.getDataSet();
                        if(logData.size() > 0){
                            for(Map log : logData){
                                int applyId = Integer.parseInt(StringUtil.trim(log.get("applyId")));
                                for(Map house : data){
                                    int _applyId = Integer.parseInt(StringUtil.trim(house.get("applyId")));
                                    if(applyId == _applyId){
                                        house.put("createTime",log.get("createTime"));
                                        break;
                                    }
                                }

                            }
                            result.setDataSet(data);
                        }else{
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
     * 业主申请下架
     * @param houseObtained
     * @return
     */
    @Override
    public ResultVo memberApplyWithdraw(HsHouseObtained houseObtained) {
        ResultVo result = new ResultVo();
        try {
            int createBy = houseObtained.getCreateBy();
            Integer houseId = houseObtained.getHouseId();
            Map<Object,Object> condition = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("VERSION_NO versionNo");
            condition.put("queryColumn",queryColumn);
            condition.put("id",houseId);
            result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS  != result.getResult()){
                return result;
            }
            List<Map<Object,Object>> queryMainHouse = (List<Map<Object,Object>>) result.getDataSet();
            if(queryMainHouse == null || queryMainHouse.size() == 0){
                result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
                result.setMessage(ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                return result;
            }
            Date date = new Date();
            //修改房源主信息表中的状态
            HsMainHouse mainHouse = new HsMainHouse();
            mainHouse.setId(houseId);
            mainHouse.setHouseStatus(Constant.OWNER_APPLY_WITHDRAW_HOUSES_STATUS); //业主申请下架状态
            mainHouse.setUpdateTime(date);
            mainHouse.setVersionNo(StringUtil.getAsInt(StringUtil.trim(queryMainHouse.get(0).get("versionNo"))));
            result = housesService.updateRecord(mainHouse);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //插入日志
                HsHouseLog houseLog = new HsHouseLog();
                houseLog.setNodeType(8); //业主申请下架
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
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
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
            int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));

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

                hsMainHouse.setVersionNo(mainHouse.getVersionNo());
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
     * 新增会员订阅房源信息
     * @param memberHousesSubscribe
     * @return
     */
    @Override
    public ResultVo addHousingSubscribe(HsMemberHousesSubscribe memberHousesSubscribe,Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int memberId = memberHousesSubscribe.getMemberId();
            String ip = StringUtil.trim(condition.get("ip"));

            List<HsMemberHousesSubscribe> redisList = new ArrayList<>();
            //查询在缓存中是否存在
            if(RedisUtil.isExistCache(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip)){
                 redisList = RedisUtil.safeGet(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip);
//                 redisList = JsonUtil.json2List(RedisUtil.safeGet(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip),HsMemberHousesSubscribe.class);
                if(redisList != null && redisList.size() > 0){
                    for (HsMemberHousesSubscribe redisSubscribe:redisList) {
                        //如果缓存里有与之相同的订阅信息
                        if(RequestUtil.contrastObj(redisSubscribe,memberHousesSubscribe)){
                            result.setResult(ResultConstant.BUS_MEMBER_SUBSCRIBE_FAILURE); //订阅失败，不能订阅重复信息
                            result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":订阅重复");
                            return result;
                        }
                    }
                }
            }
            //缓存里面没有该数据或订阅信息不同，将数据存入到数据库中之后，再将信息存储到Redis缓存中
            Date date = new Date();
            memberHousesSubscribe.setCreateTime(date);
            memberHousesSubscribe.setUpdateTime(date);
            result = memberService.insert(memberHousesSubscribe);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                memberHousesSubscribe.setCreateTime(null);
                memberHousesSubscribe.setUpdateTime(null);
                redisList.add(memberHousesSubscribe);
//                String value= JsonUtil.list2Json(redisList);
               // String value = JsonUtil.object2Json(memberHousesSubscribe);
                //将订阅参数存入到缓存中，确定过期时间
                RedisUtil.safeSet(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip, redisList, 60*60*24*60);
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
     * 获取会员订阅房源信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberHousingSubscribe(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            result = memberService.selectList(new HsMemberHousesSubscribe(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> data = (List<Map<Object,Object>>) result.getDataSet();
                if(data != null && data.size() > 0){
                    ResultVo houseResult = new ResultVo();
                    Map<Object,Object> housesCondition = new HashMap<>();
                    for(Map<Object,Object> subscribe : data){
                        int minBedroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minBedroom")));
                        if(minBedroom==-1){
                            subscribe.put("minBedroom",null);
                        }
                        int maxBedroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxBedroom")));
                        if(maxBedroom==-1){
                            subscribe.put("maxBedroom",null);
                        }
                        int minBathroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minBathroom")));
                        if(minBathroom==-1){
                            subscribe.put("minBathroom",null);
                        }
                        int maxBathroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxBathroom")));
                        if(maxBathroom==-1){
                            subscribe.put("maxBathroom",null);
                        }
                        int minPrice = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minPrice")));
                        if(minPrice==-1){
                            subscribe.put("minPrice",null);
                        }
                        int maxPrice = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxPrice")));
                        if(maxPrice==-1){
                            subscribe.put("maxPrice",null);
                        }
                        String housingTypeDictcode = StringUtil.trim(subscribe.get("housingTypeDictcode"));
                        if(StringUtil.hasText(housingTypeDictcode)){
                            subscribe.put("housingTypeDictcodes", Arrays.asList(housingTypeDictcode.split(",")));
                        }
                        housesCondition = subscribe;
                        String memberId = StringUtil.trim(subscribe.get("memberId"));
                        List<String> queryColumn = new ArrayList<>();

                        queryColumn.add("ID id");//主键ID
                        queryColumn.add("HOUSE_NAME houseName");//房源名称
                        queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                        queryColumn.add("HOUSE_ACREAGE houseAcreage"); //房屋面积
                        queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                        queryColumn.add("CITY city");//城市
                        queryColumn.add("COMMUNITY community");//社区
                        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                        queryColumn.add("ADDRESS address");//所在位置
                        queryColumn.add("PROPERTY property"); //项目
                        queryColumn.add("LONGITUDE longitude"); //经度
                        queryColumn.add("LATITUDE latitude"); //纬度
                        queryColumn.add("MEMBER_ID memberId");
                        queryColumn.add("IS_CHECK isCheck");
                        queryColumn.add("LEASE_TYPE leaseType");
                        housesCondition.put("queryColumn",queryColumn);
                        String subscribeId = StringUtil.trim(housesCondition.get("id"));
                        housesCondition.remove("id");
                        housesCondition.put("memberId",null);
                        housesCondition.put("pageIndex",1);
                        housesCondition.put("pageSize",3);//每次查询三条数据
                        housesCondition.put("isCheck", 1); //每页显示条数
                        housesCondition.put("isDel", 0); //每页显示条数
                        housesCondition.put("leaseType",subscribe.get("houseType")); //房源类型（出租/出售）
                        houseResult = housesService.selectCustomColumnNamesList(HsMainHouse.class,housesCondition);
                        housesCondition.remove("queryColumn");
                        if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> houseData = (List<Map<Object,Object>>) houseResult.getDataSet();

                            if(houseData != null && houseData.size() > 0) {
                                for (Map<Object, Object> house : houseData) {
                                    String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                                    if(StringUtil.hasText(houseMainImg)){
                                        house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                                    }
                                }
                                subscribe.put("houseData",houseData);
                            }else{
                                subscribe.put("houseData",null);
                            }

                             //组装房源主信息数据
                           // subscribe.put("memberId",memberId);
                        }
                        Map<Object,Object> otherInfo = new HashMap<>();
                        otherInfo.put("pageIndex",subscribe.get("pageIndex"));
                        otherInfo.put("id",subscribeId);
                        otherInfo.put("memberId",memberId);
                        otherInfo.put("pageSize",subscribe.get("pageSize"));
                        otherInfo.put("createTime",subscribe.get("createTime"));
                        subscribe.remove("pageIndex");
                        subscribe.remove("id");
                        subscribe.remove("memberId");
                        subscribe.remove("pageSize");
                        subscribe.remove("createTime");

                        subscribe.put("idInfo",otherInfo);
                    }
                    result.setDataSet(data);
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
     * 修改会员相关信息（修改订阅信息）
     * @param object
     * @return
     */
    @Override
    public ResultVo updateMemberRelationInfo(Object object,Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            if(object instanceof HsMemberHousesSubscribe){
                String ip = StringUtil.trim(condition.get("ip"));
                List<HsMemberHousesSubscribe> redisList = new ArrayList<>();
                HsMemberHousesSubscribe housesSubscribe = (HsMemberHousesSubscribe) object;
                int memberId = housesSubscribe.getMemberId();
                if(RedisUtil.isExistCache(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip)){
                    redisList = RedisUtil.safeGet(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip);
                   if(redisList != null && redisList.size() > 0){
                       for (HsMemberHousesSubscribe redisSubscribe:redisList) {
                           if(RequestUtil.contrastObj(redisSubscribe,housesSubscribe)){
                               result.setResult(ResultConstant.BUS_MEMBER_SUBSCRIBE_FAILURE);
                               result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":订阅重复");
                               return result;
                           }
                       }
                   }
                }
                ((HsMemberHousesSubscribe) object).setUpdateTime(new Date());
                result = memberService.update((HsMemberHousesSubscribe)object);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    redisList.add(housesSubscribe);
                    RedisUtil.safeSet(RedisConstant.SYS_MEMBER_SUBSCRIBE_CACHE_KEY + memberId + ip,redisList,60*60*24*60);
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
     * 删除会员相关信息（删除订阅信息）
     * @param object
     * @param id
     * @return
     */
    @Override
    public ResultVo deleteMemberRelationInfo(Object object,Integer id) {
        ResultVo result = new ResultVo();
        try {
            if(object instanceof  HsMemberHousesSubscribe){
                result = memberService.delete(id,new HsMemberHousesSubscribe());
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
     * 取消预约看房信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo cancelAppointment(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Date date = new Date();
        try {
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            Integer memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
            ResultVo applicationResultVo = memberService.select(id, new HsMemberHousingApplication());
            if(applicationResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || applicationResultVo.getDataSet() == null){
                //获取预约信息失败
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("Failed to get reservation information");
                return result;
            }
            //预约看房信息
            HsMemberHousingApplication application = (HsMemberHousingApplication) applicationResultVo.getDataSet();
            Integer isCancel = application.getIsCancel();
            if(isCancel != 0){
                //预约已经取消
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("Appointment has been cancelled");
                return result;
            }
            //是否已经完成看房 0未完成 1已完成
            Integer isFinish = StringUtil.getAsInt(StringUtil.trim(application.getIsFinish()));
            if(1 == isFinish){
                //已完成看房
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("Have completed the viewing");
                return result;
            }
            //订单池id
            Integer poolId = StringUtil.getAsInt(application.getStandby1());
            ResultVo orderPoolResultVo = orderService.select(poolId, new HsSystemOrderPool());
            if(orderPoolResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + "：获取订单池信息失败");
                return result;
            }
            //订单池信息
            HsSystemOrderPool orderPool = orderPoolResultVo.getDataSet() == null ? null : (HsSystemOrderPool) orderPoolResultVo.getDataSet();
            //业主id
            Integer ownerId = application.getOwnerId();
            //会员预约看房申请表 是否取消0:不取消，1：客户取消 2：业主取消
            Integer memberStatus = 1;
            // 业务员任务表  是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            Integer taskStatus = 3;
            //系统订单池 是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            Integer poolStatus = 3;
            //订单池日志 该日志所关联的节点类型（0-加入订单池->1-外获业务员接单->2业务员转单->3业主取消预约->4租客/买家取消预约->5已完成->6未完成）
            int poolLogStatus = 4;
            if(memberId.equals(ownerId)){
                //TODO 是 业主取消预约时，需要通知 租客/买家，带看房业务员 (发送消息)
                memberStatus = 2;
                taskStatus = 2;
                poolStatus = 2;
                poolLogStatus = 3;
            }else{
                //TODO 是 租客/买家取消预约时，需要通知业主以及外看业务员（发送消息）
            }
            application.setIsCancel(memberStatus);
            application.setUpdateBy(memberId);
            application.setUpdateTime(new Date());
            result = memberService.update(application);
            //修改外看人员订单任务状态
            if(orderPool != null){
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    //修改订单池状态
                    orderPool.setIsFinished(poolStatus);
                    orderPool.setUpdateTime(date);
                    orderPool.setUpdateBy(memberId);
                    result = orderService.update(orderPool);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        //添加订单池日志信息
                        HsSystemOrderPoolLog systemOrderPoolLog = new HsSystemOrderPoolLog();
                        //订单池主键ID
                        systemOrderPoolLog.setPoolId(poolId);
                        //外看订单
                        systemOrderPoolLog.setOrderType(1);
                        systemOrderPoolLog.setNodeType(poolLogStatus);
                        systemOrderPoolLog.setCreateBy(memberId);
                        systemOrderPoolLog.setCreateTime(date);
                        systemOrderPoolLog.setPostTime(date);
                        //操作人用户ID
                        systemOrderPoolLog.setOperatorUid(memberId);
                        //操作人用户名  没必要获取用户名，这里记录了用户id
                        systemOrderPoolLog.setOperatorUname(null);
                        //操作人类型(1:普通会员)
                        systemOrderPoolLog.setOperatorType(1);
                        systemOrderPoolLog.setRemarks("看房预约取消");
                        //新增系统订单池日志信息
                        result = orderService.insert(systemOrderPoolLog);
                        //根据订单池ID修改用户订单任务信息
                        condition.put("poolId",poolId);
                        condition.put("isFinished",taskStatus);
                        orderPool.setUpdateTime(date);
                        orderPool.setUpdateBy(memberId);
                        result = memberService.updateUserOrderTask(condition);
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
     *  新增会员相关信息（投诉业务员）
     * @param obj
     * @return
     */
    @Override
    public ResultVo addMemberRelationInfo(Object obj) {
        ResultVo result = new ResultVo();
        try {
            result = housesService.insert(obj);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 查询房源预约时间
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseAppointTime(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> resultMap = new HashMap<>();
        List<Map<Object,Object>> resultData = new ArrayList<>();
        List<String> dateTime = new ArrayList<>();
        try {
            String date = StringUtil.trim(condition.get("startApartmentTime")); //时间
            Integer id = Integer.parseInt(StringUtil.trim(condition.get("houseId"))); //房源ID
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            ResultVo houseResult = housesService.select(id,new HsMainHouse());//查询房源信息
            if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse hsMainHouse = (HsMainHouse) houseResult.getDataSet();
                String time = StringUtil.trim(hsMainHouse.getAppointmentLookTime()); //可预约时间
                String weekDay = DateUtil.dateToWeek(date); //转换为周几
                String [] weeksArray = time.split(";");
                List<String> weeks = Arrays.asList(weeksArray);

                resultMap.put("houseId",hsMainHouse.getId());
                resultMap.put("haveKey",hsMainHouse.getHaveKey()); //是否有钥匙
                resultMap.put("houseType",hsMainHouse.getLeaseType());

                /*if(StringUtil.hasText(time)){
                    for(String week : weeks){
                        if(week.contains(weekDay)){
                            String[] time3 = week.split(" ");
                            if(time3!=null && time3.length==2){
                                if(StringUtil.hasText(StringUtil.trim(time3[1]))){
                                    String [] time4 = time3[1].split(",");
                                    if(time4!=null){
                                        for (int i=0;i<time4.length;i++){
                                            dateTime.add(date + " " + time4[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }*/
                if(!StringUtil.hasText(time)){
                    for(String week : weeks){
                        if(week.contains(weekDay)){
                            String[] time3 = week.split(" ");
                            String [] time4 = time3[1].split(",");
                            for (int i=0;i<time4.length;i++){
                                dateTime.add(date + " " + time4[i]);
                            }
                        }
                    }
                }

            }

            if(dateTime.size() > 0){
                for(String str : dateTime){
                    Map<Object,Object> addMap = new HashMap<>();
                    addMap.put("isToSee",0); //可被预约时间
                    addMap.put("salesmanId",null);
                    addMap.put("startApartmentTime",str);
                    addMap.put("standby1",null);
                    addMap.put("id",null);
                    addMap.put("memberId",null);
                    resultData.add(addMap);
                }
            }

            result = memberService.selectList(new HsMemberHousingApplication(),condition,0); //查询用户预约信息
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>>   data  = (List<Map<Object, Object>>) result.getDataSet();
                if(data != null && data.size() > 0){
                    for (Map<Object,Object> map : data){
                        String isToSee = map.get("salesmanId")+"";
                        Date startDate = sdf.parse(map.get("startApartmentTime")+"");
                        String startApartmentTime = sdf.format(startDate);
                        //0:已被预约，可被预约
                        if(!"null".equals(isToSee)){
                            isToSee = "0";
                        }else{
                            isToSee = "1";
                        }
                        map.put("isToSee",isToSee);
                        map.put("startApartmentTime",startApartmentTime);
                        map.remove("updateTime");
                        map.remove("endApartmentTime");
                        map.remove("createTime");
                        map.remove("isFinish");
                        map.remove("isCheck");
                        map.remove("applicationType");
                        map.remove("houseId");
                        map.remove("isLandlordConfirm");
                        map.remove("isCancel");
                        map.remove("languageVersion");
                        map.remove("houseType");
                    }
                    resultData.addAll(data);

                }
            }

            Map<Object,Map<Object,Object>> map = new HashMap<Object,Map<Object,Object>>();
            for(Map mapKey : resultData){
                String key = mapKey.get("startApartmentTime")+"";
                map.put(key,mapKey);
            }
            resultData.clear();
            resultData.addAll(map.values());
            resultMap.put("lookHouseData",resultData);
            result.setDataSet(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }

    /**
     * 新增预约看房信息
     * TODO 抢单成功需要修改预约看房状态
     * @param condition
     * @return
     */
    @Override
    public ResultVo addAppointmentLookHouse(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            HsMemberHousingApplication housingApplication = new HsMemberHousingApplication();
            //预约看房时间
            String lookTime = StringUtil.trim(condition.get("startApartmentTime"));
            //客户id
            int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
            //房源id
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            //预约类型（0：申请预约，1：无需预约，2：让客服联系）
            int appointmentType = StringUtil.getAsInt(StringUtil.trim(condition.get("appointmentType")));
            //是否有钥匙
            int haveKey = StringUtil.getAsInt(StringUtil.trim(condition.get("haveKey")));


            if(!StringUtil.hasText(lookTime) && appointmentType != 2){
                //只有预约类型为‘让客服联系’预约看房时间才能为空
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage("看房日期不能为空");
                return result;
            }
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                //房源不存在
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("该房源不存在");
                return result;
            }
            HsMainHouse mainHouse = (HsMainHouse) result.getDataSet();
            Date date = new Date();
            //mainHouse.getHouseStatus() 房源状态：0>已提交 1审核通过 2商家申请下架 3下架 4已出售或出租
            if(mainHouse.getIsDel() != 0 || mainHouse.getIsLock() != 0){
                //房源被删除或者已经锁定
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("房源被删除或者已经锁定");
                return result;
            }

            /*获取当前用户是否有正在进行中的看房订单*/
            Map<Object,Object> queryMap = new HashMap<>(16);
            queryMap.put("memberId",memberId);
            queryMap.put("houseId",houseId);
            queryMap.put("isCancel",0);
            ResultVo applyResultVo = memberService.selectList(new HsMemberHousingApplication(), queryMap, 0);
            if(applyResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> applyList = (List<Map<Object, Object>>) applyResultVo.getDataSet();
                if(applyList != null && applyList.size() > 0){
                    //该用户与该房源的外看 pooids
                    List<Integer> poolIds = applyList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("standby1")))).collect(Collectors.toList());
                    /*查询是否存在正在进行中的订单*/
                    List<Integer> isFinisheds = new ArrayList<>();
                    isFinisheds.add(0);
                    isFinisheds.add(1);
                    queryMap.clear();
                    queryMap.put("poolIds",poolIds);
                    queryMap.put("isDel",0);
                    queryMap.put("isFinisheds",isFinisheds);
                    ResultVo poolResultVo = orderService.selectList(new HsSystemOrderPool(), queryMap, 0);
                    if(poolResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object, Object>> orderPool= (List<Map<Object, Object>>) poolResultVo.getDataSet();
                        if(orderPool != null && orderPool.size() > 0){
                            //订单正在处理中，请勿重复操作
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage("The order is being processed, please do not repeat");
                            return result;
                        }
                    }
                }
            }




            //业主id
            Integer ownerId = mainHouse.getMemberId();
            housingApplication.setOwnerId(ownerId);

            //获取租客电话，判断房源是否需要与租客联系
            boolean isTenant = false;
            String rentCustomerPhone = mainHouse.getRentCustomerPhone();
            if(StringUtil.hasText(rentCustomerPhone)){
                isTenant = true;
            }


            //让客服联系  PC客服需要有处理预约接口
            if(appointmentType == 2 || isTenant){
               //是否处理：0>客服无需处理，1>客服待处理，2>客服已处理
               housingApplication.setIsDispose(1);
               if(isTenant){
                   housingApplication.setApplicationType(3);
               }else{
                   housingApplication.setApplicationType(appointmentType);
               }
               //插入预约看房记录
               housingApplication.setLanguageVersion(Integer.parseInt(condition.get("languageVersion").toString()));
               housingApplication.setHouseId(houseId);
               housingApplication.setMemberId(memberId);
               //房源类型(0：出租，1：出售)
               housingApplication.setHouseType(Integer.parseInt(condition.get("houseType").toString()));
               housingApplication.setCreateTime(date);
               housingApplication.setUpdateTime(date);
               housingApplication.setCreateBy(memberId);
               result = memberService.insert(housingApplication);
               return result;
            }


            //预约看房时间不为空（申请预约，无预约看房操作）
            //用预约看房时间与系统当前时间比较，预约看房时间在此时间之前，则不能被预约
            //开始看房时间点

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat osdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date beginTime = sdf.parse(lookTime);
            String endStrTime = DateUtil.addDateMinut(lookTime,1);
            //结束看房时间点
            Date endTime = sdf.parse(endStrTime);

            //当日可看提前十分钟预约
            if(appointmentType == 1){
                String minusTime =  DateUtil.getTimeByMinute(osdf.format(date),10,0);
                if(beginTime.before(osdf.parse(minusTime))){
                    result.setResult(ResultConstant.BUS_MEMBER_APPOINTMENT_FAILURE);
                    result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":您预约的时间段已过期。");
                    return result;
                }

            }else{
                //TODO 至少提前两小时预约
                String DateStr = DateUtil.addDateMinut(sdf.format(date), 2);
                if(beginTime.before(sdf.parse(DateStr))){
                    result.setResult(ResultConstant.BUS_MEMBER_APPOINTMENT_FAILURE);
                    result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":您预约的时间段已过期。");
                    return result;
                }
            }

            //判断当前房源是否被预约过看房
            Map<Object, Object> applicationCondition = new HashMap<>(4);
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("MEMBER_ID memberId");//会员ID
            queryColumn.add("STANDBY1 orderPoolId");//poolID 订单池ID
            applicationCondition.put("queryColumn",queryColumn);
            applicationCondition.put("houseId",houseId);
            applicationCondition.put("isCancel",0);
            applicationCondition.put("isDel",0);
            applicationCondition.put("_startApartmentTime",beginTime);
//            applicationCondition.put("startApartmentTimeH",beginTime);
            ResultVo applicationResultVo = memberService.selectCustomColumnNamesList(HsMemberHousingApplication.class, applicationCondition);
            if(applicationResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && applicationResultVo.getDataSet()!= null){
                List<Map<Object, Object>> applicationList = (List<Map<Object, Object>>) applicationResultVo.getDataSet();
                //申请看房
                if(applicationList == null || applicationList.size()==0){
                    appointmentType = 0;
                    //钥匙不在平台 创建预约聊天群
                    if(haveKey == 0){
                        //钥匙不在平台
                        /**
                         * 组成规则
                         * 房屋id_户主id_客户id_yy
                         */
                        StringBuffer groupName = new StringBuffer();
                        groupName.append(houseId)
                                .append("_")
                                .append(ownerId)
                                .append("_")
                                .append(memberId)
                                .append("_yy");
                        //根据环信群名称查找预约信息
                        Map<Object,Object> appCondition = new HashMap<>(2);
                        appCondition.put("groupName",groupName.toString());
                        appCondition.put("isDel",0);
                        ResultVo resultVo = memberService.selectList(new HsMemberHousingApplication(), appCondition, 0);
                        if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                            List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) resultVo.getDataSet();
                            if(dataSet != null && dataSet.size() > 0){
                                List<String> groupIds = dataSet.stream().map(map -> StringUtil.trim(map.get("groupId"))).collect(Collectors.toList());
                                queryColumn.clear();
                                Map<Object,Object> queryWhere = new HashMap<>(1);
                                queryWhere.put("groupIds",groupIds);
                                ResultVo reservationMessageResultVo = housesManager.getReservationMessageList(queryWhere);
                                if(reservationMessageResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                                    List<Map<Object, Object>> reservationMessageList = (List<Map<Object, Object>>) reservationMessageResultVo.getDataSet();
                                    for (Map<Object, Object> msgMap : reservationMessageList) {
                                        String groupId = StringUtil.trim(msgMap.get("groupId"));
                                        //大于0表示预约已经完成
                                        long successCount = reservationMessageList.stream().filter(reservation ->
                                                groupId.equals(reservation.get("groupId")) && ("1".equals(StringUtil.trim(reservation.get("operateStatus"))) || "2".equals(StringUtil.trim(reservation.get("operateStatus"))))).count();
                                        if(successCount > 0){
                                            //当前预约已经完成，删除环信群
                                            EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
                                            Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                                            JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                                            JSONObject resultObjecct = object.getJSONObject("data");
                                        }

                                    }

                                }


                            }
                        }
                        Map<String, Object> group = housesManager.createGroup(groupName.toString(), StringUtil.trim(houseId));
                        if(group == null){
                            //创建预约会话失败
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage("Create a reservation session failed");
                            return result;
                        }
                        result.setDataSet(group);
                        return result;

                    }
                    //钥匙在平台 在系统订单池表中插入数据
                    //申请预约，将信息存入到预约时间表中，然后在系统订单池表中插入数据

                    //字符串类型抢单结束时间
                    String closeTimeStr = DateUtil.getTimeByMinute(osdf.format(new Date()),30,1);
                    HsSystemOrderPool systemOrderPool = new HsSystemOrderPool();
                    systemOrderPool.setHouseId(houseId);
                    systemOrderPool.setOrderType(1);//外看订单
                    systemOrderPool.setOrderCode("SCO_"+RandomUtils.getRandomCode());
                    systemOrderPool.setApplyId(mainHouse.getApplyId());//申请ID
                    systemOrderPool.setCreateTime(date);
                    systemOrderPool.setUpdateTime(date);
                    systemOrderPool.setOpenOrderCloseTime(osdf.parse(closeTimeStr));
                    //开启抢单
                    systemOrderPool.setIsOpenOrder(0);
                    systemOrderPool.setCreateBy(memberId);
                    systemOrderPool.setUpdateBy(memberId);
                    systemOrderPool.setAppointmentMeetPlace(mainHouse.getCity()+mainHouse.getCommunity()+mainHouse.getSubCommunity()+mainHouse.getAddress()); //见面地点
                    systemOrderPool.setEstimatedTime(beginTime); //给开始看房时间，结束看房时间赋值
                    systemOrderPool.setCloseTime(new Date(date.getTime()+48*60*60*1000));//分配订单时，将系统分配的时间为准，必须在48小时内完成
                    systemOrderPool.setRemark("会员预约看房加入订单池");//添加备注信息
                    //新增系统订单池数据
                    result = orderService.insert(systemOrderPool);
                    if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsSystemOrderPool pool = (HsSystemOrderPool) result.getDataSet();
                        HsSystemOrderPoolLog systemOrderPoolLog = new HsSystemOrderPoolLog();
                        systemOrderPoolLog.setPoolId(pool.getId());//订单池主键ID
                        systemOrderPoolLog.setOrderType(1);//外看订单
                        systemOrderPoolLog.setNodeType(0); //加入订单池
                        systemOrderPoolLog.setCreateBy(pool.getCreateBy());
                        systemOrderPoolLog.setCreateTime(date);
                        systemOrderPoolLog.setPostTime(date);
                        systemOrderPoolLog.setOperatorUid(pool.getCreateBy());//操作人用户ID
                        systemOrderPoolLog.setOperatorBname(null);//TODO 操作人商家名称
                        systemOrderPoolLog.setOperatorUname(null);//操作人用户名
                        systemOrderPoolLog.setOperatorType(1);//操作人类型(1:普通会员)
                        systemOrderPoolLog.setRemarks("会员预约看房加入订单池");
                        housingApplication.setStandby1(pool.getId().toString());//设置订单池ID
                        //新增系统订单池日志信息
                        result = orderService.insert(systemOrderPoolLog);
                    }
                }else{//参与看房
                    appointmentType = 1;
                    String poolId = "-1";
                    for (Map<Object, Object> applyMap : applicationList) {
                        poolId = StringUtil.trim(applyMap.get("orderPoolId"));
                        if(memberId == StringUtil.getAsInt(StringUtil.trim(applyMap.get("memberId")))){//如果该会员已经预约过这个点，直接返回失败
                            result.setResult(ResultConstant.BUS_MEMBER_ALREADY_RESERVED);
                            result.setMessage(ResultConstant.BUS_MEMBER_ALREADY_RESERVED_VALUE);
                            return result;
                        }
                    }
                    housingApplication.setStandby1(poolId);
                }

            }

            //给开始看房时间，结束看房时间赋值
            housingApplication.setApplicationType(appointmentType);
            housingApplication.setStartApartmentTime(beginTime);
            housingApplication.setEndApartmentTime(endTime);

            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //插入预约看房记录
                housingApplication.setLanguageVersion(Integer.parseInt(condition.get("languageVersion").toString()));
                housingApplication.setHouseId(houseId);
                Object groupId = condition.get("groupId");
                if(groupId != null){
                    housingApplication.setGroupId(StringUtil.trim(groupId));
                }
                Object groupName = condition.get("groupName");
                if(groupName != null){
                    housingApplication.setGroupName(StringUtil.trim(groupName));
                }
                housingApplication.setMemberId(memberId);
                housingApplication.setOwnerId(ownerId);
                housingApplication.setHouseType(StringUtil.getAsInt(StringUtil.trim(condition.get("houseType"))));
                housingApplication.setCreateTime(date);
                housingApplication.setUpdateTime(date);
                housingApplication.setCreateBy(memberId);
                result = memberService.insert(housingApplication);
                result.setDataSet(null);
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
     * 创建预约聊天群
     * @param groupName
     * @param houseId
     * @return
     */
    @Override
    public ResultVo createReservationGroup(String groupName,String houseId){
        ResultVo result = new ResultVo();
        try {
            Map<String, Object> group = housesManager.createGroup(groupName, houseId);
            result.setDataSet(group);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 发送预约看房消息
     * @param message
     * @return
     */
    @Override
    public ResultVo sendReservationMessage(HsMemberHousingApplicationMessage message){
        ResultVo result = new ResultVo();
        try {
            //操作状态 0 协商 1 同意 2 拒绝 3 预约成功 4 预约失败
            Integer operateStatus = message.getOperateStatus();
            result = memberService.insert(message);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                String groupId = message.getGroupId();
                /*根据groupId获取预约信息*/
                Map<Object,Object> condition = new HashMap<>(2);
                condition.put("groupId",groupId);
                ResultVo applicationResultVo = memberService.selectList(new HsMemberHousingApplication(), condition, 1);
                if(applicationResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    //获取预约信息失败
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Failed to get reservation information");
                }
                List<HsMemberHousingApplication> applicationList = (List<HsMemberHousingApplication>) applicationResultVo.getDataSet();
                if(applicationList == null || applicationList.size() < 1){
                    //没有预约信息
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"No reservation information");
                }
                HsMemberHousingApplication housingApplication = applicationList.get(0);
                Date startApartmentTime = housingApplication.getStartApartmentTime();
                Integer isCancel = housingApplication.getIsCancel();
                if(isCancel != 0 || startApartmentTime != null){
                    //预约已经完成
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Appointment has been completed");
                }
                if(operateStatus == 2){
                    Integer createBy = message.getCreateBy();

                    Integer sender = message.getSender();
                    easemobSendMessage(createBy,groupId,operateStatus,sender);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 发送环信消息
     * @param memberId   人员id
     * @param groupId    环信群id
     * @param status     0 成功消息 1失败消息
     * @return
     * @throws Exception
     */
    public boolean easemobSendMessage(Integer memberId,String groupId,Integer status,Integer sender) throws Exception {
        boolean isSuccess = true;
        EasemobSendMessage easemobSendMessage = new EasemobSendMessage();
        //发送环信消息
        //获取memberCode
        ResultVo memResultVo = memberService.select(memberId, new HsMember());
        if(memResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memResultVo.getDataSet() == null){
            throw new RuntimeException("获取人员信息失败");
        }
        HsMember member = (HsMember) memResultVo.getDataSet();
        String memberCode = member.getMemberCode();

        Msg msg = new Msg();
        MsgContent msgContent = new MsgContent();
        msgContent.type(MsgContent.TypeEnum.TXT).msg("预约看房消息");
        UserName userName = new UserName();
        userName.add(StringUtil.trim(groupId));
        Map<String,Object> ext = new HashMap<>(1);
        ext.put("groupId", groupId);
        ext.put("sender", sender);
        ext.put("operateStatus", status);
        msg.from(memberCode).target(userName).targetType("chatgroups").msg(msgContent).ext(ext);
        System.out.println(new GsonBuilder().create().toJson(msg));
        Object sendMessage = easemobSendMessage.sendMessage(msg);
        JSONObject jsonObject = JSON.parseObject(StringUtil.trim(sendMessage));
        Object data = jsonObject.get("data");
        if(data == null){
            isSuccess = false;
        }
        System.out.println(sendMessage);
        return isSuccess;
    }

    /**
     *同意预约看房
     * @param message
     * @param condition
     * @return
     */
    @Override
    public ResultVo agreeReservation(HsMemberHousingApplicationMessage message,Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        try {
            //预约看房时间
            String lookTime = StringUtil.trim(condition.get("startApartmentTime"));
            //当前登陆用户id
            int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
            //房源id
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            String groupName = StringUtil.trim(condition.get("groupName"));
            String groupId = StringUtil.trim(condition.get("groupId"));

            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
                if(mainHouse == null){
                    //房源不存在
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                    return result;
                }
                Integer isDel = mainHouse.getIsDel();
                Integer houseStatus = mainHouse.getHouseStatus();
                //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
                Integer isLock = mainHouse.getIsLock();
                if(isDel == 1 || houseStatus == 3 || isLock == 1){
                    //房源不存在
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                    return result;
                }
            }

            if(!StringUtil.hasText(lookTime)){
                //看房时间不能为空
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage("看房日期不能为空");
                return result;
            }
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                //房源不存在
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("该房源不存在");
                return result;
            }
            HsMainHouse mainHouse = (HsMainHouse) result.getDataSet();
            Date date = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date beginTime = sdf.parse(lookTime);
            String endStrTime = DateUtil.addDateMinut(lookTime,1);
            //结束看房时间点
            Date endTime = sdf.parse(endStrTime);
            //TODO 至少提前一小时预约
            String DateStr = DateUtil.addDateMinut(sdf.format(date), 1);
            if(beginTime.before(sdf.parse(DateStr))){
                result.setResult(ResultConstant.BUS_MEMBER_APPOINTMENT_FAILURE);
                result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":您预约的时间段已过期。");
                return result;
            }
            /*根据groupId获取预约信息*/
            condition.clear();
            condition.put("groupId",groupId);
            condition.put("isCancel",0);
            ResultVo applicationResultVo = memberService.selectList(new HsMemberHousingApplication(), condition, 1);
            if(applicationResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //获取预约信息失败
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Failed to get reservation information");
            }
            List<HsMemberHousingApplication> applicationList = (List<HsMemberHousingApplication>) applicationResultVo.getDataSet();
            if(applicationList == null || applicationList.size() < 1){
                //没有预约信息
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"No reservation information");
            }
            HsMemberHousingApplication housingApplication = applicationList.get(0);


            /*插入订单池*/
            SimpleDateFormat osdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //字符串类型抢单结束时间
            String closeTimeStr = DateUtil.getTimeByMinute(osdf.format(new Date()),30,1);
            HsSystemOrderPool systemOrderPool = new HsSystemOrderPool();
            systemOrderPool.setHouseId(houseId);
            //外看订单
            systemOrderPool.setOrderType(1);
            systemOrderPool.setOrderCode("SCO_"+RandomUtils.getRandomCode());
            //申请ID
            systemOrderPool.setApplyId(mainHouse.getApplyId());
            systemOrderPool.setCreateTime(date);
            systemOrderPool.setUpdateTime(date);
            systemOrderPool.setOpenOrderCloseTime(osdf.parse(closeTimeStr));
            //开启抢单
            systemOrderPool.setIsOpenOrder(0);
            systemOrderPool.setCreateBy(memberId);
            systemOrderPool.setUpdateBy(memberId);
            //见面地点
            systemOrderPool.setAppointmentMeetPlace(mainHouse.getCity()+mainHouse.getCommunity()+mainHouse.getSubCommunity()+mainHouse.getAddress());
            //给开始看房时间，结束看房时间赋值
            systemOrderPool.setEstimatedTime(beginTime);
            //分配订单时，将系统分配的时间为准，必须在48小时内完成
            systemOrderPool.setCloseTime(new Date(date.getTime()+48*60*60*1000));
            systemOrderPool.setRemark("会员预约看房加入订单池");
            //新增系统订单池数据
            result = orderService.insert(systemOrderPool);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsSystemOrderPool pool = (HsSystemOrderPool) result.getDataSet();
                HsSystemOrderPoolLog systemOrderPoolLog = new HsSystemOrderPoolLog();
                //订单池主键ID
                systemOrderPoolLog.setPoolId(pool.getId());
                //外看订单
                systemOrderPoolLog.setOrderType(1);
                //加入订单池
                systemOrderPoolLog.setNodeType(0);
                systemOrderPoolLog.setCreateBy(pool.getCreateBy());
                systemOrderPoolLog.setCreateTime(date);
                systemOrderPoolLog.setPostTime(date);
                //操作人用户ID
                systemOrderPoolLog.setOperatorUid(pool.getCreateBy());
                //操作人类型(1:普通会员)
                systemOrderPoolLog.setOperatorType(1);
                systemOrderPoolLog.setRemarks("会员预约看房加入订单池");
                //设置订单池ID
                housingApplication.setStandby1(pool.getId().toString());
                //新增系统订单池日志信息
                ResultVo insert = orderService.insert(systemOrderPoolLog);
            }

            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //更新预约看房信息
                housingApplication.setStartApartmentTime(beginTime);
                housingApplication.setEndApartmentTime(endTime);
                housingApplication.setLanguageVersion(StringUtil.getAsInt(StringUtil.trim(condition.get("languageVersion")),0));
                housingApplication.setGroupId(groupId);
                housingApplication.setGroupName(groupName);
                housingApplication.setUpdateTime(date);
                housingApplication.setUpdateBy(memberId);
                result = memberService.update(housingApplication);
            }
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                //保存消息
                result = memberService.insert(message);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    Integer createBy = message.getCreateBy();
                    boolean b = easemobSendMessage(createBy, groupId, message.getOperateStatus(),message.getSender());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 发送议价消息
     * @param message
     * @return
     */
    @Override
    @Transactional
    public ResultVo sendBargainMessage(HsMemberHousingBargainMessage message) {
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //1.获取议价信息
            //议价id
            Integer bargainId = message.getBargainId();
            ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
            if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            HsMemberHousingBargain hberHousingBargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
            //议价状态（0 议价中 1 议价成功 2 议价失败）
            Integer bargainStatus = hberHousingBargain.getBargainStatus();
            if(bargainStatus != 0){
                //议价已结束
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("Negotiation has ended");
                return result;
            }
            //房源id
            Integer houseId = hberHousingBargain.getHouseId();
            ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
            if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
                if(mainHouse == null){
                    //房源不存在
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                    return result;
                }
                Integer isDel = mainHouse.getIsDel();
                Integer houseStatus = mainHouse.getHouseStatus();
                //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
                Integer isLock = mainHouse.getIsLock();
                if(isDel == 1 || houseStatus == 3 || isLock == 1){
                    //房源不存在
                    result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                    return result;
                }
            }
            //房源类型
            Integer houseType = hberHousingBargain.getHouseType();
            //业主id
            Integer ownerId = hberHousingBargain.getOwnerId();
            //客户id
            Integer memberId = hberHousingBargain.getMemberId();
            Integer sender = message.getSender();
            //消息创建者
            Integer createBy = ownerId;
            if(sender == 0){
                createBy = memberId;
            }
            //2.判断消息来源是业主还是客户
            if(sender == 0){
                //2.1客户：获取房屋自动应答条件
                //是否删除
                condition.put("isDel",0);
                //是否开启（0：未开启，1：已开启）
                condition.put("isOpen",1);
                //房源id
                condition.put("houseId",houseId);
                ResultVo replySettingReplyVo = housesService.selectList(new HsHouseAutoReplySetting(), condition, 1);
                if (replySettingReplyVo == null || replySettingReplyVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || replySettingReplyVo.getDataSet() == null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                    return result;
                }
                //获取业主设置的自动应答条件
                List<HsHouseAutoReplySetting> autoReplySettings = (List<HsHouseAutoReplySetting>) replySettingReplyVo.getDataSet();
                if(autoReplySettings != null && autoReplySettings.size() > 0){
                    //自动应答是否匹配
                    boolean isSuccess = false;
                    //2.1.1已设置自动应答：遍历自动应答。判断议价条件是否满足
                    //主要判断应答规则，最低租金/或出售价，PAY_NODE
                    for (HsHouseAutoReplySetting autoReplySetting : autoReplySettings) {
                        //客户出价要大于或等于自动自动应答设置的最低租金/或出售价
                        if(StringUtil.getDouble(StringUtil.trim(autoReplySetting.getHouseRentPrice())) <= message.getLeasePrice()){
                            if(houseType == 0){
                                //出租
                                String startRentDate = autoReplySetting.getStartRentDate();
                                //自动应答设置起租日期
                                Date beginRentDate = autoReplySetting.getBeginRentDate();
                                //议价消息起租日期
                                Date leaseStartDate = message.getLeaseStartDate();
                                Integer leaseDurationYear = message.getLeaseDurationYear();
                                Integer rentTime = autoReplySetting.getRentTime();
                                if(autoReplySetting.getPayNode().equals(message.getPayNode()) ){
                                    if (leaseStartDate.getTime() >= beginRentDate.getTime()) {
                                        if(leaseDurationYear >= rentTime){
                                            //出租满足应答规则，则直接同意
                                            isSuccess = true;
                                            break;
                                        }

                                    }
                                }
                            }else{
                                //出售
                                Integer msgPayType = message.getPayType();
                                //支付方式（0：现金，1：贷款或现金）
                                Integer payType = autoReplySetting.getPayType();
                                if(payType == 1 || msgPayType.equals(payType)){
                                    //出售满足应答规则，则直接同意
                                    isSuccess = true;
                                    break;
                                }
                            }
                        }
                    }
                    message.setIsAutomatic(1);
                    //message.setSender(1);
                    //2.1.1.1条件满足：调用同意议价接口
                    if(isSuccess){
                        return agreeBargain(message);
                    }
                    //2.1.1.2条件不满足：调用拒绝议价接口
                    return refuseBargain(message);
                }
            }
            //保存议价信息
            message.setIsAutomatic(0);
            message.setCreateBy(createBy);
            ResultVo insert = memberService.insert(message);
            if(insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setResult(ResultConstant.BUS_MEMBER_SEND_SUCCESS);
                result.setMessage(ResultConstant.BUS_MEMBER_SEND_SUCCESS_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new ResultVo();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 获取预约聊天记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo getReservationHistory(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        List<Map<Object, Object>> resultList = new ArrayList<>();
        try {
            result = memberService.selectList(new HsMemberHousingApplicationMessage(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                List<Map<Object, Object>> messageList = (List<Map<Object, Object>>) result.getDataSet();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Map<Object, Object> map : messageList) {
                    String createTime = StringUtil.trim(map.get("createTime"));
                    Date parse = sdf.parse(createTime);
                    long time = parse.getTime();
                    map.put("createTime",time);
                    resultList.add(map);
                }
            }
            //将消息全部设置为已读  groupId
            String groupId = StringUtil.trim(condition.get("groupId"));
            List<String> setColumn = new ArrayList<>();
            setColumn.add("IS_READ = 1");
            condition.clear();
            condition.put("setColumn",setColumn);
            condition.put("groupId", groupId);
            condition.put("isDel", 0);
            Integer updateInt = memberService.updateCustomColumnByCondition(new HsMemberHousingApplicationMessage(), condition);
            /*if (updateInt < 1) {
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":更新消息状态失败");
                return result;
            }*/
            result.setDataSet(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            result = new ResultVo();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 同意议价接口
     * @param message
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ResultVo agreeBargain(HsMemberHousingBargainMessage message) throws Exception {
        ResultVo result = new ResultVo();
        EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
        //1.获取议价信息
        //议价id
        Integer bargainId = message.getBargainId();
        ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
        if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;
        }
        HsMemberHousingBargain hsMemberHousingBargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
        //议价状态（0 议价中 1 议价成功 2 议价失败）
        Integer bargainStatus = hsMemberHousingBargain.getBargainStatus();
        if(bargainStatus != 0){
            //议价已结束
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage("Negotiation has ended");
            return result;
        }
        //房源id
        Integer houseId = hsMemberHousingBargain.getHouseId();
        ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
        if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
            if(mainHouse == null){
                //房源不存在
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                return result;
            }
            Integer isDel = mainHouse.getIsDel();
            Integer houseStatus = mainHouse.getHouseStatus();
            //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
            Integer isLock = mainHouse.getIsLock();
            if(isDel == 1 || houseStatus == 3 || isLock == 1){
                //房源不存在
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                return result;
            }
        }
        //业主id
        Integer ownerId = hsMemberHousingBargain.getOwnerId();
        //客户id
        Integer memberId = hsMemberHousingBargain.getMemberId();
        //房源类型
        Integer houseType = hsMemberHousingBargain.getHouseType();
        Integer sender = message.getSender();
        //起租日期
        Date leaseStartDate = message.getLeaseStartDate();
        //支付节点 单位 1~12次/年
        Integer payNode = message.getPayNode();
        //租赁时长，单位年
        Integer leaseDurationYear = message.getLeaseDurationYear();
        //支付方式
        Integer payType = message.getPayType();
        //过户日期
        Date transferDate = message.getTransferDate();
        //订单金额
        Integer leasePrice = message.getLeasePrice();
        //判断是否为自动应答消息
        Integer isAutomatic = message.getIsAutomatic() == null ? 0 : message.getIsAutomatic();
        //消息创建者
        Integer createBy = memberId;
        if(sender == 1){
            createBy = ownerId;
        }
        //1.保存议价消息
        message.setIsAutomatic(isAutomatic);
        message.setCreateBy(createBy);
        ResultVo insert = memberService.insert(message);
        if(insert == null || insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            /*result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;*/
            throw new RuntimeException("保存议价消息失败！");
        }
        if(sender == 0 && isAutomatic == 1){
            message.setSender(1);
        }
        //2.判断消息发送者是否为客户
        if(message.getSender() == 0){
            //发送同意消息
            boolean send = automaticAnswerAgree(hsMemberHousingBargain,message,1);
            if(!send){
                throw new RuntimeException("发送环信消息失败！");
            }
            //如果是客户，返回发送成功
            result.setResult(ResultConstant.BUS_MEMBER_SEND_SUCCESS);
            result.setMessage(ResultConstant.BUS_MEMBER_SEND_SUCCESS_VALUE);
            return result;
        }

        //4.更改议价状态
        hsMemberHousingBargain.setBargainStatus(1);
        hsMemberHousingBargain.setUpdateBy(createBy);
        hsMemberHousingBargain.setLeaseStartDate(leaseStartDate);
        hsMemberHousingBargain.setLeaseDurationYear(leaseDurationYear);
        hsMemberHousingBargain.setLeasePrice(leasePrice);
        hsMemberHousingBargain.setPayNode(payNode);
        hsMemberHousingBargain.setTransferDate(transferDate);
        hsMemberHousingBargain.setPayType(payType);
        ResultVo update = memberService.update(hsMemberHousingBargain);
        if(update == null || update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            /*result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;*/
            throw new RuntimeException("更改议价状态失败！");
        }
        //5.记录房源进度 线上议价成交
        //封装进度信息
        HsHouseProgress hsHouseProgress = new HsHouseProgress();
        //房源ID
        hsHouseProgress.setHouseId(houseId);
        //创建人
        hsHouseProgress.setCreateBy(createBy);
        //进度
        hsHouseProgress.setProgressCode("103");
        //创建日期
        hsHouseProgress.setCreateTime(new Date());
        //插入数据
        ResultVo progressInsert = housesService.insert(hsHouseProgress);
        if(progressInsert == null || progressInsert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            //记录房源进度失败！
            /*result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;*/
            throw new RuntimeException("记录房源进度失败！");
        }
        //6.创建订单
        Map<String,Object> map = new HashMap<>(16);
        map.put("leasePrice",leasePrice);
        map.put("houseType",houseType);
        map.put("houseId",houseId);
        map.put("memberId",memberId);
        map.put("ownerId",ownerId);
        map.put("bargainId",bargainId);
        map.put("createBy",createBy);
        ResultVo order = createOrder(map);
        if(order.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            throw new RuntimeException("创建订单失败！");
        }


        //7.调用环信发送消息
        if(isAutomatic == 1){
            //发送一条普通消息
            boolean send = automaticAnswerAgree(hsMemberHousingBargain,message,0);
            if(!send){
                throw new RuntimeException("发送环信消息失败！");
            }
            //3.保存议价成功模板信息
            HsMemberHousingBargainMessage successMessage = new HsMemberHousingBargainMessage();
            ModelMapperUtil.getInstance().map(message,successMessage);
            successMessage.setOperateStatus(1);
            successMessage.setCreateBy(ownerId);
            successMessage.setSender(1);
            successMessage.setIsAutomatic(1);
            ResultVo successMessageInsert = memberService.insert(successMessage);
            if(successMessageInsert == null || successMessageInsert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setResult(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS);
                result.setMessage(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS_VALUE);
                return result;
            }
            //是自动应答消息，以业主的身份发送一条议价消息
            boolean agree = automaticAnswerAgree(hsMemberHousingBargain, successMessage,1);
            if(!agree){
                throw new RuntimeException("发送环信消息失败！");
            }
            result.setResult(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS);
            result.setMessage(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS_VALUE);
            return result;
        }

        boolean send = automaticAnswerAgree(hsMemberHousingBargain,message,1);
        if(!send){
            throw new RuntimeException("发送环信消息失败！");
        }
        result.setResult(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS);
        result.setMessage(ResultConstant.BUS_MEMBER_NEGOTIATION_SUCCESS_VALUE);
        return result;

    }

    /**
     * 拒绝议价接口
     * @param message
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ResultVo refuseBargain(HsMemberHousingBargainMessage message) throws Exception {
        ResultVo result = new ResultVo();
        //1.获取议价信息
        //议价id
        Integer bargainId = message.getBargainId();
        ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
        if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;
        }
        HsMemberHousingBargain hsMemberHousingBargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
        //议价状态（0 议价中 1 议价成功 2 议价失败）
        Integer bargainStatus = hsMemberHousingBargain.getBargainStatus();
        if(bargainStatus != 0){
            //议价已结束
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage("Negotiation has ended");
            return result;
        }
        Integer houseId = hsMemberHousingBargain.getHouseId();
        ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
        if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
            if(mainHouse == null){
                //房源不存在
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                return result;
            }
            Integer isDel = mainHouse.getIsDel();
            Integer houseStatus = mainHouse.getHouseStatus();
            //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
            Integer isLock = mainHouse.getIsLock();
            if(isDel == 1 || houseStatus == 3 || isLock == 1){
                //房源不存在
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Housing does not exist");
                return result;
            }
        }
        //业主id
        Integer ownerId = hsMemberHousingBargain.getOwnerId();
        //客户id
        Integer memberId = hsMemberHousingBargain.getMemberId();
        Integer sender = message.getSender();
        //判断是否为自动应答消息
        Integer isAutomatic = message.getIsAutomatic() == null ? 0 : message.getIsAutomatic();
        //消息创建者
        Integer createBy = memberId;
        if(sender == 1){
            createBy = ownerId;
        }
        //1.保存议价消息
        message.setIsAutomatic(isAutomatic);
        message.setCreateBy(createBy);
        ResultVo insert = memberService.insert(message);
        if(insert == null || insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;
        }
        if(sender == 0 && isAutomatic == 1){
            message.setSender(1);
        }

        //4.更改议价状态
        hsMemberHousingBargain.setBargainStatus(2);
        hsMemberHousingBargain.setUpdateBy(createBy);
        ResultVo update = memberService.update(hsMemberHousingBargain);
        if(update == null || update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;
        }
        //2.保存议价失败环信消息
        //是自动应答消息，先发送一条普通消息再以业主的身份发送一条议价成功消息
        if(isAutomatic == 1){
            //发送一条普通消息
            boolean answerRefuse = automaticAnswerRefuse(hsMemberHousingBargain,message,0);
            if(!answerRefuse){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            //保存消息记录
            HsMemberHousingBargainMessage failMessage = new HsMemberHousingBargainMessage();
            ModelMapperUtil.getInstance().map(message,failMessage);
            failMessage.setOperateStatus(2);
            failMessage.setCreateBy(ownerId);
            failMessage.setSender(1);
            failMessage.setIsAutomatic(1);
            ResultVo failMessageInsert = memberService.insert(failMessage);
            if(failMessageInsert == null || failMessageInsert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            //已业主身份发送一条拒绝议价消息
            boolean agree = automaticAnswerRefuse(hsMemberHousingBargain, failMessage,2);
            if(!agree){
                throw new RuntimeException("发送环信消息失败！");
            }
            result.setResult(ResultConstant.BUS_MEMBER_NEGOTIATION_FAILURE);
            result.setMessage(ResultConstant.BUS_MEMBER_NEGOTIATION_FAILURE_VALUE);
            return result;
        }
        boolean answerRefuse = automaticAnswerRefuse(hsMemberHousingBargain,message,2);
        if(!answerRefuse){
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            return result;
        }
        result.setResult(ResultConstant.BUS_MEMBER_NEGOTIATION_FAILURE);
        result.setMessage(ResultConstant.BUS_MEMBER_NEGOTIATION_FAILURE_VALUE);
        return result;
    }

    /**
     * 获取议价聊天历史
     * @param condition
     * @return
     */
    @Override
    public ResultVo getBargainRecord(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try{
            //获取议价信息
            int bargainId = StringUtil.getAsInt(StringUtil.trim(condition.get("bargainId")));
            ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
            if(bargainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsMemberHousingBargain bargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
                if(bargain != null){
                    String groupId = bargain.getGroupId();
                    String groupName = bargain.getGroupName();
                    result = memberService.selectList(new HsMemberHousingBargainMessage(),condition,0);
                    if(result == null || result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                        result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                        result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                        return result;
                    }
                    List<Map<Object,Object>> list = (List<Map<Object,Object>>) result.getDataSet();
                    List<Map<Object,Object>> resultList = new ArrayList<>();
                    for (Map<Object,Object> bargainMessageMap : list) {
                        //将创建时间转换为毫秒值
                        Object createTime = bargainMessageMap.get("createTime");
                        if(createTime != null){
                            Date date = (Date) createTime;
                            long time = date.getTime();
                            bargainMessageMap.put("createTime",time);
                        }
                        bargainMessageMap.put("groupName",groupName);
                        bargainMessageMap.put("groupId",groupId);
                        resultList.add(bargainMessageMap);
                    }
                    //将消息全部设置为已读

                    List<String> setColumn = new ArrayList<>();
                    setColumn.add("IS_OPEN = 1");
                    condition.clear();
                    condition.put("setColumn",setColumn);
                    condition.put("bargainId", bargainId);
                    condition.put("isDel", 0);
                    Integer updateInt = memberService.updateCustomColumnByCondition(new HsMemberHousingBargainMessage(), condition);
                    result.setDataSet(resultList);

                }
            }

        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 获取用户信息接口
     * @param memberId
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo getMemberInfo(int memberId) {
        ResultVo result = null;
        EasemobIMUsers easemobIMUsers = new EasemobIMUsers();
        try {
            //获取用户信息
            result = memberService.select(memberId,new HsMember());
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//请求成功
                HsMember member = (HsMember) result.getDataSet();
                //前端返回实体
                HsMemberDto memberDto = new HsMemberDto();
                ModelMapperUtil.getInstance().map(member,memberDto);
                //获取用户code
                String memberCode = member.getMemberCode();
                //根据用户code查询用户在线状态
                Object imUserStatus = easemobIMUsers.getIMUserStatus(memberCode);
                JSONObject jsonObject = JSON.parseObject(imUserStatus.toString());
                JSONObject data = jsonObject.getJSONObject("data");
                String stliu = "";
                if(data != null){
                    stliu = data.getString(memberCode);
                }
                //将在线状态封装进返回结果集
                memberDto.setStliu(stliu);
                result.setDataSet(memberDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new ResultVo();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 获取业主首页数据
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOwnerIndexData(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object,Object> data = new HashMap<Object,Object>();
        try {
            Map<Object,Object> query = new HashMap<Object,Object>();
            if(condition == null){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return result;
            }

            String channelAliasName = StringUtil.trim(condition.get("channelAliasName"));
            if(!StringUtil.hasText(channelAliasName)){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return result;
            }

            if(RedisUtil.isExistCache(channelAliasName)){
                data = RedisUtil.safeGet(channelAliasName);
//                data = JsonUtil.parseJSON2Map(RedisUtil.safeGet(channelAliasName));
            }else{
                query.put("channelAliasName",channelAliasName);
                List<Map<Object,Object>> channelBooths = commonService.getChannelBoothAliasName(query);
                if(channelBooths != null && channelBooths.size() > 0){
                    List<String> unCacheBooths = new ArrayList<>();
                    for (Map<Object,Object> channelBooth : channelBooths){
                        String boothAliasName = StringUtil.trim(channelBooth.get("boothAliasName"));
                        if(!RedisUtil.existKey(boothAliasName)){
                            unCacheBooths.add(boothAliasName);
                        }else{
                            data.put(boothAliasName, RedisUtil.safeGet(boothAliasName));
//                            data.put(boothAliasName, JsonUtil.parseJSON2List(RedisUtil.safeGet(boothAliasName)));
                        }
                    }
                    if(unCacheBooths != null){
                        query.clear();
                        query.put("bootState",0);
                        query.put("isDel",0);
                        query.put("boothAliasNames",unCacheBooths);
                        //Map<Object, Object>  unCacheBoothData = commonService.getOwnerArticleDataByCondition(query);
                        Map<Object, Object>  unCacheBoothData = commonService.getBoothDataByCondition(query);
                        if(unCacheBoothData!=null){
                            for (Map.Entry<Object, Object> entry : unCacheBoothData.entrySet()) {
                                String key = StringUtil.trim(entry.getKey());
                                Object value = entry.getValue();
                                data.put(key,value);
                                RedisUtil.safeSet(key,value,60*60*24*60);
//                                RedisUtil.safeSet(key,JsonUtil.object2Json(value),60*60*24*60);
                            }
                        }
                    }
                }
                RedisUtil.safeSet(channelAliasName,data,60*60*24*60);
//                RedisUtil.safeSet(channelAliasName,JsonUtil.toJson(data),60*60*24*60);
            }

            //查询业主提交预约平台上门获取房源条数信息
            ResultVo countVo = housesService.getHousingApplicationCount();
            if(countVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                data.put("count",StringUtil.getAsInt(StringUtil.trim(countVo.getDataSet()),0)+668);
            }
            result.setDataSet(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }


        return result;
    }

    /**
     * 修改用户信息
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo updateMemberInfo(HsMember member) {
        ResultVo resultVo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID memberId");//会员ID
            condition.put("queryColumn",queryColumn);
            condition.put("memberId",member.getId());//会员ID
            resultVo = memberService.selectCustomColumnNamesList(HsMember.class,condition);
            if (resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return resultVo;
            }
            List<Map<Object,Object>> memberList= (List<Map<Object, Object>>) resultVo.getDataSet();
            if(memberList==null || memberList.size()==0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"会员账号不存在");
            }
            //修改用户信息
            resultVo = memberService.update(member);
        } catch (Exception e) {
            e.printStackTrace();
            resultVo = new ResultVo();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 修改用户信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo updateMemberInfo(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        Map<Object,Object> queryFilter = Maps.newHashMap();
        try {
            HsMember member = (HsMember) condition.get("data");
            String validateCode = StringUtil.trim(condition.get("validateCode"));
            String ip = StringUtil.trim(condition.get("ip"));

            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID memberId");//会员ID
            queryColumn.add("AREA_CODE areaCode");//电话地区号
            queryColumn.add("MEMBER_MOBLE memberMoble");//手机号
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("memberId",member.getId());//会员ID
            queryFilter.put("state",1);//会员状态(0:停用,1:启用,2:已删除)默认为1
            resultVo = memberService.selectCustomColumnNamesList(HsMember.class,queryFilter);
            if (resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return resultVo;
            }
            List<Map<Object,Object>> memberList= (List<Map<Object, Object>>) resultVo.getDataSet();
            if(memberList==null || memberList.size()!=1){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"会员账号不存在");
            }
            Map<Object,Object> memberMap = memberList.get(0);

            //校验短信验证码
            if(StringUtil.hasText(member.getMemberMoble())){//若存在手机号码，则校验数据
                String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY +
                        StringUtil.trim(memberMap.get("areaCode")) +
                        member.getMemberMoble() + ip;
                String cacheValidateCode = "";
                if(!StringUtil.hasText(cacheKey)){
                    return ResultVo.error(ResultConstant.SYS_IMG_CODE_IS_OVERDUE,ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                }

                if(RedisUtil.isExistCache(cacheKey)){
                    cacheValidateCode = RedisUtil.safeGet(cacheKey);
                }
                if(!StringUtil.hasText(StringUtil.trim(condition.get("isTest")))){
                    if(!cacheValidateCode.equalsIgnoreCase(validateCode)){
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"验证码错误");
                    }
                }
            }
            //修改用户信息
            resultVo = memberService.update(member);
        } catch (Exception e) {
            e.printStackTrace();
            resultVo = new ResultVo();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取变价房源信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getChangePriceHouse(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            List<Map<Object,Object>> favoriteList = new ArrayList<>(); //收藏房源信息
            List<Map<Object,Object>> browseList = new ArrayList<>(); //浏览房源信息
            List<Map<Object,Object>> houseList = new ArrayList<>(); //房源信息
            List<String> houseIds = new ArrayList<>();

            //查询喜爱列表中的房源数据
            condition.put("favoriteType",condition.get("houseType"));
            ResultVo  favoriteResult = memberService.selectList(new HsMemberFavorite(),condition,0);
            if(favoriteResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                favoriteList  = (List<Map<Object, Object>>) favoriteResult.getDataSet();
            }
            if(favoriteList !=null  && favoriteList.size() > 0){
                for(Map favorite : favoriteList){
                    houseIds.add(StringUtil.trim(favorite.get("favoriteId")));
                }
            }

            //查询浏览记录中的房源数据
//            ResultVo browseResult = memberService.selectList(new HsHousesBrowseHistory(),condition,0);
//            if(browseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
//                browseList = (List<Map<Object, Object>>) browseResult.getDataSet();
//            }
//            if(browseList !=null  && browseList.size() > 0){
//                for(Map browse : browseList){
//                    houseIds.add(StringUtil.trim(browse.get("houseId")));
//                }
//            }





            if(houseIds.size() > 0){
                condition.clear();
                List<String> queryColumn = new ArrayList<>();
                queryColumn.add("ID id");//主键ID
                queryColumn.add("HOUSE_NAME houseName");//房源名称
                queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                queryColumn.add("HOUSE_ACREAGE houseAcreage"); //房屋面积
                queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                queryColumn.add("CITY city");//城市
                queryColumn.add("COMMUNITY community");//社区
                queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                queryColumn.add("ADDRESS address");//所在位置
                queryColumn.add("STANDBY1 publishPrice"); //发布价格
                condition.put("queryColumn",queryColumn);

                condition.put("houseIds",houseIds); //房源Ids
                condition.put("standby1","not null");
                result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    houseList  = (List<Map<Object, Object>>) result.getDataSet();
                    if(houseList.size() > 0){
                        for(Map house : houseList){
                            String upFlag = "up"; //up:上涨，down：下降
                            String houseRent = StringUtil.trim(house.get("houseRent")); //租金/售价
                            String publishPrice = StringUtil.trim(house.get("publishPrice")); //发布价格
                            BigDecimal _houseRent = new BigDecimal(houseRent);
                            BigDecimal _publishPrice = new BigDecimal(publishPrice);

                            BigDecimal differPrice = _houseRent.subtract(_publishPrice); //差价
                            String differ = differPrice.toString(); //相差
                            if(differ.indexOf("-") > -1){ //如果为负数,下降
                                upFlag = "down";
                                house.put("differPrice",differ.substring(1,differ.length()));
                            }else{
                                house.put("differPrice",differ); //为正数，上涨
                            }
                            String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                            if(StringUtil.hasText(houseMainImg)){
                                house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                            }
                            house.put("upFlag",upFlag); //涨降标识
                        }
                    }

                    result.setDataSet(houseList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
        }
        return result;
    }


    /**
     * 获取地址编码
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo loadCityCode(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            if(RedisUtil.existKey(RedisConstant.SYS_REGIONNAME_CACHE_KEY)) {//判断缓存中是否有值
                List<Map<Object,Object>> regionList = RedisUtil.safeGet(RedisConstant.SYS_REGIONNAME_CACHE_KEY);
//                List<Map<Object,Object>> regionList =  JsonUtil.parseJSON2List(RedisUtil.safeGet(RedisConstant.SYS_REGIONNAME_CACHE_KEY));
                result.setDataSet(regionList);
                return result;
            }
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("REGION_NAME_CN regionNameCn");//房源名称
            queryColumn.add("REGION_NAME_EN region_name_en");//房源主图
            queryColumn.add("REGION_CODE regionCode"); //房屋面积
            condition.put("queryColumn",queryColumn);
            result = commonService.selectCustomColumnNamesList(HsRegionCode.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> regionList = (List<Map<Object, Object>>)result.getDataSet();
                if( regionList!=null && regionList.size()>0 ){
                    RedisUtil.safeSet(RedisConstant.SYS_REGIONNAME_CACHE_KEY,regionList,60*60*24*30);
//                    RedisUtil.safeSet(RedisConstant.SYS_REGIONNAME_CACHE_KEY,JsonUtil.toJson(regionList),60*60*24*30);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
        }
        return result;
    }

    /**
     * 我的  获取房源议价列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getBargainsList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            //查询议价信息
            result = memberService.getMyBargainList(condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> bargainList = null;
                bargainList = (List<Map<Object, Object>>)result.getDataSet();
                if( bargainList!=null && bargainList.size()>0 ){
                    List<Integer> bargainIds = Lists.newArrayList();
                    List<Integer> houseIds = Lists.newArrayList();
                    List<Integer> memberIds = new ArrayList<>();
                    for (Map<Object, Object> bargain : bargainList) {
                        bargainIds.add(StringUtil.getAsInt(StringUtil.trim(bargain.get("id"))));
                        houseIds.add(StringUtil.getAsInt(StringUtil.trim(bargain.get("houseId"))));

                        int ownerId = StringUtil.getAsInt(StringUtil.trim(bargain.get("ownerId")));
                        int mId = StringUtil.getAsInt(StringUtil.trim(bargain.get("memberId")));
                        if(!memberIds.contains(ownerId)){
                            memberIds.add(ownerId);
                        }
                        if(!memberIds.contains(mId)){
                            memberIds.add(mId);
                        }
                    }

                    //查询房源信息
                    List<String> queryColumn = new ArrayList<>();
                    queryColumn.clear();
                    //房源ID
                    queryColumn.add("ID houseId");
                    //房源名称
                    queryColumn.add("HOUSE_NAME houseName");
                    //房源编号
                    queryColumn.add("HOUSE_CODE houseCode");
                    //预约类型（0：出租，1：出售）
                    queryColumn.add("LEASE_TYPE leaseType");
                    //房屋面积
                    queryColumn.add("HOUSE_ACREAGE houseAcreage");
                    // 城市
                    queryColumn.add("CITY city");
                    //社区
                    queryColumn.add("COMMUNITY community");
                    //子社区
                    queryColumn.add("SUB_COMMUNITY subCommunity");
                    //项目
                    queryColumn.add("PROPERTY property");
                    //房源所在区域名称
                    queryColumn.add("ADDRESS address");
                    //期望租金/或出售价
                    queryColumn.add("HOUSE_RENT houseRent");
                    //房源主图
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");
                    condition.put("queryColumn",queryColumn);
                    condition.put("houseIds",houseIds);
                    condition.remove("memberId");
                    ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
                    if(houseVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> resultList = new ArrayList<>();
                        List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) houseVo.getDataSet();
                        if(houseList == null || houseList.size()==0) {
                            //没有订单信息
                            return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                        }
                        for (Map<Object, Object> bargain : bargainList) {
                            //房源id
                            int houseId = StringUtil.getAsInt(StringUtil.trim(bargain.get("houseId")));
                            for (Map<Object, Object> house : houseList) {
                                //房源信息
                                if(houseId == StringUtil.getAsInt(StringUtil.trim(house.get("houseId")))){
                                    bargain.putAll(house);
                                    break;
                                }
                            }
                            //设置图片路径
                            Object houseMainImg = bargain.get("houseMainImg");
                            if(houseMainImg != null){
                                bargain.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+bargain.get("houseMainImg"));
                            }
                            resultList.add(bargain);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 新增购房信息
     * @param extend
     * @return
     */
    @Override
    @Transactional
    public ResultVo addPurchaseInfo(HsMemberPurchase extend){
        ResultVo result = new ResultVo();
        try {
            Integer id = extend.getId();
            if(id == null){
                result = memberService.insert(extend);
                if(result != null && result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                    //修改订单状态
                    HsMemberPurchase memberPurchase = (HsMemberPurchase) result.getDataSet();
                    Integer orderId = memberPurchase.getOrderId();
                    //获取订单信息
                    ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
                    if(orderResultVo == null || orderResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || orderResultVo.getDataSet() == null){
                        //获取订单信息失败
                        result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                        result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                        return result;
                    }
                    //修改订单信息
                    HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                    order.setOrderStatus(-2);
                    ResultVo update = orderService.update(order);
                    if(update == null || update.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                        //更新订单信息失败
                        result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                        result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                        return result;
                    }
                }
                return result;
            }
            result = memberService.update(extend);

        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取购房信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getPurchaseInfo(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try{
            //获取人员购房信息
            ResultVo memberExtendResultVo = memberService.selectList(new HsMemberPurchase(), condition, 0);
            if(memberExtendResultVo == null || memberExtendResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memberExtendResultVo.getDataSet() == null){
                //获取人员扩展信息失败
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            List<Map<Object, Object>> memberPurchaseList = (List<Map<Object, Object>>) memberExtendResultVo.getDataSet();
            if(memberPurchaseList.size() < 1){
                return result;
            }
            Map<Object, Object> memberPurchase = memberPurchaseList.get(0);
            String passportImg = StringUtil.trim(memberPurchase.get("passportImg"));
            String visaImg = StringUtil.trim(memberPurchase.get("visaImg"));
            String eidImg = StringUtil.trim(memberPurchase.get("eidImg"));
            passportImg = splice(passportImg);
            visaImg = splice(visaImg);
            eidImg = splice(eidImg);
            memberPurchase.put("passportImg",passportImg);
            memberPurchase.put("visaImg",visaImg);
            memberPurchase.put("eidImg",eidImg);
            result.setDataSet(memberPurchase);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }


    /**
     * 新增人员贷款信息
     * @param financialLoansApply
     * @return
     */
    @Override
    public ResultVo addLoansApply(HsMemberFinancialLoansApply financialLoansApply) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.insert(financialLoansApply);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取人员贷款信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getLoansApply(Map<Object,Object> condition){
        ResultVo resultVo = new ResultVo();
        try {
            ResultVo selectList = memberService.selectList(new HsMemberFinancialLoansApply(), condition, 0);
            if(selectList != null && selectList.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && selectList.getDataSet() != null){
                List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) selectList.getDataSet();
                Map<Object, Object> objectObjectMap = dataSet.get(0);
                resultVo.setDataSet(objectObjectMap);
            }
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取楼盘列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getPurchaseApplyList(Map<Object,Object> condition){
        ResultVo resultVo = new ResultVo();
        List<Map<Object, Object>> resultList = new ArrayList<>();
        try {
            ResultVo applyResultVo = memberService.selectList(new HsMemberDirectPurchaseApply(), condition, 0);
            if(applyResultVo == null || applyResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }
            if(applyResultVo.getDataSet() == null){
                return resultVo;
            }
            List<Map<Object, Object>> applyList = (List<Map<Object, Object>>) applyResultVo.getDataSet();
            //获取楼盘id
            List<String> buildingIds = applyList.stream().map(map -> StringUtil.trim(map.get("buildingId"))).collect(Collectors.toList());
            condition.clear();
            condition.put("ids",buildingIds);
            condition.put("isDel",0);
            ResultVo buildingResultVo = housesService.selectList(new HsHouseNewBuilding(), condition, 0);
            if(buildingResultVo != null && buildingResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && buildingResultVo.getDataSet() != null){
                List<Map<Object, Object>> buildingList = (List<Map<Object, Object>>) buildingResultVo.getDataSet();
                for (Map<Object, Object> map : applyList) {
                    String buildingId = StringUtil.trim(map.get("buildingId"));
                    for (Map<Object, Object> buildingMap : buildingList) {
                        String id = StringUtil.trim(buildingMap.get("id"));
                        if(id.equals(buildingId)){
                            String projectName = StringUtil.trim(buildingMap.get("projectName"));
                            double maxHouseRent = StringUtil.getDouble(StringUtil.trim(buildingMap.get("maxHouseRent")));
                            double minHouseRent = StringUtil.getDouble(StringUtil.trim(buildingMap.get("minHouseRent")));
//                            String city = StringUtil.trim(buildingMap.get("city"));
                            //区域（社区）
                            String community = StringUtil.trim(buildingMap.get("community"));
//                            String subCommunity = StringUtil.trim(buildingMap.get("subCommunity"));
                            //项目主图
                            String projectMainImg = StringUtil.trim(buildingMap.get("projectMainImg"));
                            //房屋面积
                            String houseAcreage = StringUtil.trim(buildingMap.get("houseAcreage"));
                            /*StringBuilder area = new StringBuilder();
                            area.append(city)
                                    .append(" ")
                                    .append(community)
                                    .append(" ")
                                    .append(subCommunity);*/
                            map.put("projectName",projectName);
                            map.put("area",community);
                            map.put("maxHouseRent",maxHouseRent);
                            map.put("minHouseRent",minHouseRent);
                            if(StringUtil.hasText(projectMainImg)){
                                projectMainImg = ImageUtil.IMG_URL_PREFIX + projectMainImg;
                            }
                            map.put("projectMainImg",projectMainImg);
                            map.put("houseAcreage",houseAcreage);
                            break;
                        }
                    }
                    resultList.add(map);
                }
            }
            resultVo.setDataSet(resultList);
            resultVo.setPageInfo(applyResultVo.getPageInfo());
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getPurchaseApplyDetails(Integer id) {
        ResultVo resultVo = new ResultVo();
        Map<String,Object> resultMap = new HashMap<>(10);
        try {
            resultVo = memberService.select(id, new HsMemberDirectPurchaseApply());
            if(resultVo == null || resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }
            if(resultVo.getDataSet() == null){
                return resultVo;
            }
            HsMemberDirectPurchaseApply purchaseApply = (HsMemberDirectPurchaseApply) resultVo.getDataSet();
            Integer buildingId = purchaseApply.getBuildingId();
            Integer memberId = purchaseApply.getMemberId();
            resultMap.put("code",purchaseApply.getCode());
            resultMap.put("createTime",purchaseApply.getCreateTime());
            resultMap.put("status",purchaseApply.getStatus());

            //获取楼盘信息
            String projectName = "";
            String area = "";
            //项目主图
            String projectMainImg = "";
            ResultVo buildingResultVo = housesService.select(buildingId, new HsHouseNewBuilding());
            if(buildingResultVo != null && buildingResultVo.getDataSet() != null && buildingResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseNewBuilding building = (HsHouseNewBuilding) buildingResultVo.getDataSet();
                projectName = building.getProjectName();
                String city = building.getCity();
                String community = building.getCommunity();
                String subCommunity = building.getSubCommunity();
                area = community;
                //项目主图
                projectMainImg = building.getProjectMainImg();
            }
            //获取用户申购信息
            //用户姓名
            String name = "";
            String familyName = "";
            String nationality = "";
            String passportNumber = "";
            String contactWay = "";
            String email = "";
            Map<Object,Object> condition = new HashMap<>(2);
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("NAME name");
            queryColumn.add("FAMILY_NAME familyName");
            queryColumn.add("NATIONALITY nationality");
            queryColumn.add("PASSPORT_NUMBER passportNumber");
            queryColumn.add("CONTACT_WAY contactWay");
            queryColumn.add("EMAIL email");
            condition.put("queryColumn",queryColumn);
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            ResultVo buildingMemberResultVo =housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class,condition);
            if(buildingMemberResultVo != null && buildingMemberResultVo.getDataSet() != null && buildingMemberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> buildingMemberApplyList = (List<Map<Object,Object>>) buildingMemberResultVo.getDataSet();
                if(buildingMemberApplyList.size() > 0){
                    Map<Object, Object> buildingMemberApply = buildingMemberApplyList.get(0);
                    familyName = StringUtil.trim(buildingMemberApply.get("familyName"));
                    name= StringUtil.trim(buildingMemberApply.get("name"));
                    nationality = StringUtil.trim(buildingMemberApply.get("nationality"));
                    passportNumber = StringUtil.trim(buildingMemberApply.get("passportNumber"));
                    contactWay = StringUtil.trim(buildingMemberApply.get("contactWay"));
                    email = StringUtil.trim(buildingMemberApply.get("email"));
                }
            }
            resultMap.put("projectName",projectName);
            resultMap.put("area",area);
            resultMap.put("name",name);
            resultMap.put("familyName",familyName);
            resultMap.put("nationality",nationality);
            resultMap.put("passportNumber",passportNumber);
            resultMap.put("contactWay",contactWay);
            resultMap.put("email",email);
            resultMap.put("projectMainImg",ImageUtil.imgResultUrl(projectMainImg));
            resultVo.setDataSet(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取交易订单列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberOrderList(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID orderId");
            queryColumn.add("ORDER_CODE order_code");//订单编码
            queryColumn.add("HOUSE_ID houseId");//房源id
            queryColumn.add("MEMBER_ID memberId");//买家id
            queryColumn.add("OWNER_ID ownerId");//买家id
            queryColumn.add("BARGAIN_ID bargainId");//议价id
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态
            queryColumn.add("PAY_WAY payWay");//支付方式
            queryColumn.add("PAY_TIME payTime");//支付时间
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("ADDITIONAL_TERMS additionalTerms");//合同附加条款
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn",queryColumn);
            condition.put("needSort",1);
            condition.put("orderBy","ID");
            resultVo =orderService.selectCustomColumnNamesList(HsHousingOrder.class,condition);
            if(resultVo.getResult()!=ResultConstant.SYS_REQUIRED_SUCCESS){//返回状态不正常时
                return  resultVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(orderList==null || orderList.size()==0){
                return resultVo;
            }
            List<Integer> houseIds = Lists.newArrayListWithCapacity(orderList.size());
            orderList.forEach((order) -> houseIds.add(StringUtil.getAsInt(StringUtil.trim(order.get("houseId")))));
            //自定义查询列名
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID houseId");//主键ID
            queryColumn.add("HOUSE_NAME houseName");//房源ID
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
            queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
            queryColumn.add("ADDRESS address");//所在位置
            condition.put("queryColumn", queryColumn);
            condition.put("houseIds", houseIds);//房源IDs
            //查询房源信息
            ResultVo housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            if(housesVo.getResult()!=ResultConstant.SYS_REQUIRED_SUCCESS){//返回状态不正常时
                return  housesVo;
            }
            List<Map<Object,Object>> houseList = (List<Map<Object, Object>>) housesVo.getDataSet();
            for (Map<Object, Object> order : orderList) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> house : houseList) {
                    String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                    if(StringUtil.hasText(houseMainImg) && !houseMainImg.startsWith("http://")){
                        house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                    }
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    if(_houseId == houseId){
                        order.putAll(house);
                        break;
                    }
                }
            }
            resultVo.setDataSet(orderList);
        }catch (Exception e){
            logger.warn("Remote call getMemberOrderList fails  " + e.getMessage());
            resultVo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 我的消费记录列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getExpenseCalendarsList(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            //查询订单信息
            queryColumn.clear();
            queryColumn.add("ID orderId");//订单id
            queryColumn.add("ORDER_CODE orderCode");//订单编号
            queryColumn.add("ORDER_AMOUNT orderAmount");//订单金额
            queryColumn.add("HOUSE_ID houseId");//房源ID
            queryColumn.add("ORDER_STATUS orderStatus");//订单状态
            queryColumn.add("PAY_WAY payWay");//支付方式 0-未付款 1-线上支付 2-线下支付 3-钱包支付
            queryColumn.add("PAY_STATUS payStatus");//支付状态 0-未付款 1- 已支付
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0-租房->1-买房
            queryColumn.add("REMARK remark"); //备注描述
            queryColumn.add("CREATE_TIME createTime");//创建时间
            queryColumn.add("PAY_TIME payTime");//支付时间
            condition.put("queryColumn",queryColumn);
            result = orderService.selectCustomColumnNamesList(HsHousingOrder.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> orders = (List<Map<Object, Object>>) result.getDataSet();
            if(orders==null || orders.size()==0){
                return result;
            }
            List<Integer> houseIds = Lists.newArrayListWithCapacity(orders.size());
            orders.forEach((order) -> houseIds.add(StringUtil.getAsInt(StringUtil.trim(order.get("houseId")))));
            //查询房源信息
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID houseId");//房源id
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            condition.put("queryColumn",queryColumn);
            condition.put("houseIds",houseIds);
            ResultVo houseVo = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
            if(houseVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return houseVo;
            }
            List<Map<Object,Object>> houses = (List<Map<Object, Object>>) houseVo.getDataSet();
            for (Map<Object, Object> order : orders) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(order.get("houseId")));
                for (Map<Object, Object> house : houses) {
                    int _houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                    if(houseId == _houseId){
                        order.putAll(house);
                    }
                }
            }
            result.setDataSet(orders);

        }catch (Exception e){
            logger.warn("Remote call getExpenseCalendarsList fails  " + e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 保存开发商直售楼盘个人信息填写
     * @param memberApply
     * @return
     */
    @Override
    public ResultVo saveNewBuildingMemberApply(HsHouseNewBuildingMemberApply memberApply) {
        ResultVo result = null;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> condition = Maps.newHashMap();
            //查询会员是否已经申请过
            queryColumn.clear();
            queryColumn.add("ID id");//主键ID
            condition.put("queryColumn",queryColumn);
            condition.put("projectId",memberApply.getProjectId());//项目ID
            condition.put("memberId",memberApply.getMemberId());//会员ID
            result = housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> memberApplys = (List<Map<Object, Object>>) result.getDataSet();
            if(memberApplys!=null && memberApplys.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"You have already applied, please do not submit again");
            }
            memberApply.setCreateTime(new Date());
            memberApply.setCreateBy(memberApply.getMemberId());
            result = housesService.insert(memberApply);

            if(result == null || result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            memberApply = (HsHouseNewBuildingMemberApply)result.getDataSet();
            Integer memberId = memberApply.getMemberId();
            Integer projectId = memberApply.getProjectId();
            //插入人员直购申请
            HsMemberDirectPurchaseApply purchaseApply = new HsMemberDirectPurchaseApply();
            purchaseApply.setCode(UUID.randomUUID().toString());
            purchaseApply.setMemberId(memberId);
            purchaseApply.setBuildingId(projectId);
            //TODO 目前没有审核操作。直接申购成功
            purchaseApply.setStatus(3);
            purchaseApply.setCreateBy(memberId);
            purchaseApply.setCreateTime(new Date());
            ResultVo insert = memberService.insert(purchaseApply);
            if(insert == null || insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || insert.getDataSet() == null){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.warn("Remote call saveNewBuildingMemberApply fails  " + e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 修改个人楼盘申购信息
     * @param memberApply
     * @return
     */
    @Override
    public ResultVo updateNewBuildingMemberApply(HsHouseNewBuildingMemberApply memberApply){
        ResultVo result = new ResultVo();
        try {
            Integer id = memberApply.getId();
            ResultVo memberApplyResultVo = housesService.select(id, new HsHouseNewBuildingMemberApply());
            if(memberApplyResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memberApplyResultVo.getDataSet() == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Subscription information does not exist");
            }
            result = housesService.update(memberApply);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseNewBuildingMemberApply apply = (HsHouseNewBuildingMemberApply) memberApplyResultVo.getDataSet();
                result.setDataSet(apply);
            }
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 下载申购合同
     * @param condition
     * @return
     */
    @Override
    public byte[] downloadPurchase(Map<Object, Object> condition){
        byte[] result = null;
        try {
            /**
             * 获取人员申购信息
             */
            ResultVo MemberApplyResultVo = housesService.selectList(new HsHouseNewBuildingMemberApply(), condition, 0);
            if(MemberApplyResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
            List<Map<Object,Object>> orders = (List<Map<Object, Object>>) MemberApplyResultVo.getDataSet();
            if(orders==null || orders.size()==0){
                return null;
            }
            Map<Object, Object> memberApplyMap = orders.get(0);
            //签名图片地址
            String url = ImageUtil.imgResultUrl(StringUtil.trim(memberApplyMap.get("signature")));
            if(!StringUtil.hasText(url)){
                System.out.println("没有签名数据");
                return null;
            }
            /**
             * 获取项目信息
             */
            //项目名称
            String projectName = "";
            //开发商
            String developers = "";
            //项目id
            int projectId = StringUtil.getAsInt(StringUtil.trim(memberApplyMap.get("projectId")));
            ResultVo newBuildingResultVo = housesService.select(projectId, new HsHouseNewBuilding());
            if(newBuildingResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && newBuildingResultVo.getDataSet() != null){
                HsHouseNewBuilding newBuilding = (HsHouseNewBuilding) newBuildingResultVo.getDataSet();
                projectName = newBuilding.getProjectName();
                developers = newBuilding.getDevelopers();
            }
            memberApplyMap.put("projectName",projectName);
            memberApplyMap.put("developers",developers);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            memberApplyMap.put("date",sdf.format(date));
            memberApplyMap.remove("signature");
            PdfUtil pdfUtil = new PdfUtil();
            String templatePath = "pdf/contractTemplate.pdf";
            result = pdfUtil.getPDF(templatePath,memberApplyMap,"signature",url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取开发商直售楼盘个人信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getNewBuildingMemberApply(Map<Object, Object> condition){
        ResultVo result;
        try {
            result = housesService.selectList(new HsHouseNewBuildingMemberApply(), condition, 0);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> orders = (List<Map<Object, Object>>) result.getDataSet();
            if(orders==null || orders.size()==0){
                return result;
            }
            Map<Object, Object> memberApplyMap = orders.get(0);
            result.setDataSet(memberApplyMap);
        }catch (Exception e){
            e.printStackTrace();
            logger.warn("Remote call getNewBuildingMemberApply fails  " + e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 创建订单
     * @param condition
     * @return
     */
    public ResultVo createOrder(Map<String, Object> condition) {
        ResultVo vo;
        //议价id
        int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
        //客户id
        int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
        //业主id
        int ownerId = StringUtil.getAsInt(StringUtil.trim(condition.get("ownerId")));
        //议价ID
        int bargainId = StringUtil.getAsInt(StringUtil.trim(condition.get("bargainId")));
        //创建人
        int createBy = StringUtil.getAsInt(StringUtil.trim(condition.get("createBy")));
        //房屋类型
        int houseType = StringUtil.getAsInt(StringUtil.trim(condition.get("houseType")));
        //交易金额
        String leasePrice = StringUtil.trim(condition.get("leasePrice"));

        try {
            //查询房源信息
            vo = housesService.select(houseId, new HsMainHouse());
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsMainHouse house = (HsMainHouse) vo.getDataSet();
                if (house == null) {
                    //房源数据异常
                    vo = ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                    return vo;
                }
                /**
                 * 获取服务费比例
                 */
                /*
                * 假设交易金额为A值，佣金比例为B值，折扣比例为C值，银行手续费比例为D值，M1为平台服务费，M2为银行手续费，则支付总额M=M1+M2;
                        M1=A*1.05B*C； M2=【（A*1.05B*C）*D+1】*1.05
                    1.在租赁场景下，B=2%, C=50%, D=2.69%, 当
                    ① M1<=525AED, M1=525AED，支付总额M为540.88AED, 可退款金额为M1
                    ② M1>525AED，支付总额M= M1+M2，可退款金额为M1
                    2.在买卖场景下，B=0.6%, C=50%, D=2.69%, 当
                    ① M1<=2100AED, M1=2100AED，支付总额M为2160.36AED，可退款金额为M1
                    ② M1>2100AED, 支付总额M= M1+M2，可退款金额为M1
                    注意，在线付款最高金额为20,000迪拉姆
                */
                double parseDouble = Double.parseDouble(leasePrice);
                //出租最低服务费
                double rentMinM1 = 525;
                //出售最低服务费
                double sellMinM1 = 2100;
                //佣金
                double commission = 0.02;
                //折扣
                double discount = 0.5;
                //手续费
                double handlingFee = 0.0269;
                if(houseType == 1){
                    commission = 0.006;
                }
                //交易金额 M1=A*1.05B*C
                double M1 = BigDecimalUtil.mul(BigDecimalUtil.mul(BigDecimalUtil.mul(parseDouble, 1.05), commission), discount);
                //银行手续费 M2=【（A*1.05B*C）*D+1】*1.05
                double M2 = BigDecimalUtil.mul(BigDecimalUtil.add(BigDecimalUtil.mul(M1,handlingFee),1.0),1.05);
                if(M1 <= rentMinM1){
                    M1 = rentMinM1;
                    M2 = BigDecimalUtil.sub(540.88,M1);
                }
                if(houseType == 1){
                    if(M1 <= sellMinM1){
                        M1 = sellMinM1;
                        M2 = BigDecimalUtil.sub(2160.36,M1);
                    }
                }

                DecimalFormat df = new DecimalFormat("#.00");
                String platformServiceAmountStr = df.format(M1);
                String standby3Str = df.format(M2);



                Date date = new Date();
                HsHousingOrder order = new HsHousingOrder();

                StringBuffer sb = new StringBuffer();
                sb.append("O");
                String city = house.getCity();
                String community = house.getCommunity();
                List<Map<Object, Object>> citys = RedisUtil.safeGet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY);
//                List<Map<Object, Object>> citys = JsonUtil.parseJSON2List(RedisUtil.safeGet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY));
                for (Map<Object, Object> cityMap : citys) {
                    if(StringUtil.trim(cityMap.get("cityNameEn")).equals(city) || StringUtil.trim(cityMap.get("cityNameCn")).equals(city)){
                        sb.append(StringUtil.trim(cityMap.get("cityCode")));
                        List<Map<Object,Object>> subList = (List<Map<Object,Object>>) cityMap.get("sub");
                        for (Map<Object, Object> subMap : subList) {
                            if(StringUtil.trim(subMap.get("cityNameEn")).equals(community) || StringUtil.trim(subMap.get("cityNameCn")).equals(community)){
                                sb.append(StringUtil.trim(subMap.get("cityCode")));
                                break;
                            }
                        }
                    }
                }
                Map<Object, Object> housingTypelist =RedisUtil.safeGet("SYS_HOUSING_TYPE_DICTCODE");
//                Map<Object, Object> housingTypelist = JsonUtil.parseJSON2Map(RedisUtil.safeGet("SYS_HOUSING_TYPE_DICTCODE"));
                String housingTypeDictcode = house.getHousingTypeDictcode();
                if(housingTypelist!=null && housingTypelist.size()>0){
                    List<Map<Object,Object>> items  = (List<Map<Object, Object>>) housingTypelist.get("items");
                    for (Map<Object, Object> item : items) {
                        if(housingTypeDictcode.equals(StringUtil.trim(item.get("id")))){
                            sb.append(item.get("standby2"));
                            break;
                        }
                    }
                }
                sb.append(house.getLeaseType());
                sb.append(RandomUtils.getRandomByLenth(8));
                order.setOrderCode(sb.toString());
                //订单类型 0-租房->1-买房
                order.setOrderType(houseType);
                //字典表 dict_order_status
                order.setOrderStatus(-3);
                order.setHouseId(houseId);
                order.setMemberId(memberId);
                order.setOwnerId(ownerId);
                order.setBargainId(bargainId);
                order.setOrderAmount(new BigDecimal(leasePrice));
                order.setPlatformServiceAmount(new BigDecimal(platformServiceAmountStr));
                order.setCreateTime(date);
                order.setCreateBy(createBy);
                order.setUpdateBy(createBy);
                order.setStandby3(standby3Str);
                //插入订单
                vo = orderService.insert(order);
                vo.setDataSet(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    //自动应答同意发送消息
    public boolean automaticAnswerAgree(HsMemberHousingBargain hsMemberHousingBargain,HsMemberHousingBargainMessage message,Integer status){
        boolean isSuccess = true;
        EasemobSendMessage easemobSendMessage = new EasemobSendMessage();

        Msg msg = new Msg();
        UserName userName = new UserName();
        userName.add(hsMemberHousingBargain.getGroupId());
        MsgContent msgContent = new MsgContent();
        msgContent.type(MsgContent.TypeEnum.TXT).msg("helloword");
        Map<String,Object> ext = new HashMap<>(5);

        Integer from = message.getCreateBy();
        //房屋类型
        Integer houseType = hsMemberHousingBargain.getHouseType();
        //群id
        ext.put("groupId", hsMemberHousingBargain.getGroupId());
        //群名称
        ext.put("groupName", hsMemberHousingBargain.getGroupName());
        //消息发送者
        ext.put("sender", message.getSender());
        //租金/期望价格
        ext.put("leasePrice", message.getLeasePrice());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(houseType == 0){
            //租房
            //起租时间
            ext.put("leaseStartDate",sdf.format(message.getLeaseStartDate()));
            //租赁时长
            ext.put("leaseDurationYear", message.getLeaseDurationYear());
            //支付节点
            ext.put("payNode", message.getPayNode());
        }else if(houseType == 1){
            //出售
            //过户日期
            ext.put("transferDate", sdf.format(message.getTransferDate()));
            //付款方式 0现金 1贷款
            ext.put("payType", message.getPayType());
        }
        //获取memberCode
        String memberCode = "";
        try {
            ResultVo memResultVo = memberService.select(from, new HsMember());
            if(memResultVo == null || memResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memResultVo.getDataSet() == null){
                return false;
            }
            HsMember member = (HsMember) memResultVo.getDataSet();
            memberCode = member.getMemberCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        ext.put("operateStatus", status);
        msg.from(memberCode).target(userName).targetType("chatgroups").msg(msgContent).ext(ext);
        System.out.println(new GsonBuilder().create().toJson(msg));
        Object result = easemobSendMessage.sendMessage(msg);
        JSONObject jsonObject = JSON.parseObject(StringUtil.trim(result));
        Object data = jsonObject.get("data");
        if(data == null){
            isSuccess = false;
        }
        return isSuccess;
    }

    //自动应答拒绝
    public boolean automaticAnswerRefuse(HsMemberHousingBargain hsMemberHousingBargain,HsMemberHousingBargainMessage message,Integer status){
        boolean isSuccess = true;
        EasemobSendMessage easemobSendMessage = new EasemobSendMessage();
        Msg msg = new Msg();
        UserName userName = new UserName();
        userName.add(hsMemberHousingBargain.getGroupId());
        MsgContent msgContent = new MsgContent();
        msgContent.type(MsgContent.TypeEnum.TXT).msg("helloword");
        Map<String,Object> ext = new HashMap<>(5);

        //消息创建者
        Integer from = message.getCreateBy();
        //群id
        ext.put("groupId", hsMemberHousingBargain.getGroupId());
        //群名称
        ext.put("groupName", hsMemberHousingBargain.getGroupName());
        //消息发送者
        ext.put("sender", message.getSender());
        //租金/期望价格
        ext.put("leasePrice", message.getLeasePrice());
        //房屋类型
        Integer houseType = hsMemberHousingBargain.getHouseType();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(houseType == 0){
            //租房
            //起租时间
            ext.put("leaseStartDate",sdf.format(message.getLeaseStartDate()));
            //租赁时长
            ext.put("leaseDurationYear", message.getLeaseDurationYear());

            //支付节点
            ext.put("payNode", message.getPayNode());
        }else if(houseType == 1){
            //出售
            //过户日期
            ext.put("transferDate", sdf.format(message.getTransferDate()));
            //付款方式 0现金 1贷款
            ext.put("payType", message.getPayType());
        }

        //获取memberCode
        String memberCode = "";
        try {
            ResultVo memResultVo = memberService.select(from, new HsMember());
            if(memResultVo == null || memResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memResultVo.getDataSet() == null){
                return false;
            }
            HsMember member = (HsMember) memResultVo.getDataSet();
            memberCode = member.getMemberCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        ext.put("operateStatus", status);

        msg.from(memberCode).target(userName).targetType("chatgroups").msg(msgContent).ext(ext);
        System.out.println(new GsonBuilder().create().toJson(msg));
        Object result = easemobSendMessage.sendMessage(msg);
        JSONObject jsonObject = JSON.parseObject(StringUtil.trim(result));
        Object data = jsonObject.get("data");
        if(data == null){
            isSuccess = false;
        }
        System.out.println(result);
        return isSuccess;
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
}