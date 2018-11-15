package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.client.model.Group;
import io.swagger.client.model.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.HousesController;
import www.ucforward.com.emchat.api.impl.EasemobChatGroup;
import www.ucforward.com.emchat.api.impl.EasemobIMUsers;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.entity.HouseBucketDTO;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.form.MapSearch;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.manager.HousesManager;
import www.ucforward.com.serviceInter.*;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("housesManager")
public class HousesManagerImpl implements HousesManager {

    private static Logger logger = LoggerFactory.getLogger(HousesController.class); // 日志记录

    @Resource
    private HousesService housesService;//房源相关
    @Resource
    private CommonService commonService;
    @Resource
    private MemberService memberService;
    @Resource
    private OrderService orderService;
    @Resource
    private SearchService searchService;
    @Resource
    private CommonManager commonManager;
    @Resource
    private MemberManagerImpl memberManager;

    /**
     * 业主提交预约获取房源申请
     *
     * @param apply
     * @return
     */
    @Override
    public ResultVo addOwnerHousingApply(HsOwnerHousingApplication apply, HsHouseCredentialsData houseCredentialsData) {
        ResultVo result = new ResultVo();
        try {
            Date date = result.getSystemTime();
            apply.setCreateTime(date);
            apply.setUpdateTime(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //起租日期
            if (StringUtil.hasText(apply.getStartRentDate())) {
                String startRentDate = apply.getStartRentDate();
                if (startRentDate.indexOf(" ") == -1) {
                    startRentDate = apply.getStartRentDate() + " 00:00:00";
                }
                apply.setBeginRentDate(sdf.parse(startRentDate));
            }

            if (StringUtil.hasText(apply.getBargainHouseDate())) { //交房时间
                String bargainHouseDate = apply.getBargainHouseDate();
                if (bargainHouseDate.indexOf(" ") == -1) {
                    bargainHouseDate = apply.getBargainHouseDate() + " 00:00:00";
                }
                apply.setExpectBargainHouseDate(sdf.parse(bargainHouseDate));
            }

//            if (apply.getHousingStatus() != null) {
//                //（0：空房，1：出租，2：自住，3：准现房）数据字典，20057出租，20058出租
//                if (apply.getHousingStatus() == 20057 || apply.getHousingStatus() == 20058) {
//                    //为出租类型，自住时，钥匙不在平台
//                    apply.setHaveKey(0);
//                } else if (apply.getHousingStatus() == 20059) {
//                    //准现房可随时看房，平台有钥匙
//                    apply.setHaveKey(1);
//                }
//            }
            apply.setApplyCode("AC_" + RandomUtils.getRandomNumbersAndLetters(32));
            ResultVo insertVo = housesService.insert(apply);//保存业主提交的房源信息
            if (insertVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsOwnerHousingApplication dataSet = (HsOwnerHousingApplication) insertVo.getDataSet();
                houseCredentialsData.setApplyId(dataSet.getId()); //申请ID
                houseCredentialsData.setApplicantType(apply.getApplicantType());
                houseCredentialsData.setLanguageVersion(apply.getLanguageVersion());
                ResultVo credentialsResult = housesService.insert(houseCredentialsData);

                System.out.println(houseCredentialsData.toString());
                if (credentialsResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    HsOwnerHousingApplicationLog ownerHousingApplyLog = new HsOwnerHousingApplicationLog();
                    //房源申请信息的ID
                    ownerHousingApplyLog.setApplyId(dataSet.getId());
                    ownerHousingApplyLog.setNodeType(0);
                    //ownerHousingApplyLog.setIsDel(0);有默认值可不手动设置
                    ownerHousingApplyLog.setCreateBy(apply.getCreateBy());
                    ownerHousingApplyLog.setCreateTime(date);
                    ownerHousingApplyLog.setPostTime(date);
                    ResultVo logVo = housesService.insert(ownerHousingApplyLog);
                    if (logVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        //记录房源进度 上传房源
                        //2.封装进度信息
                        HsHouseProgress hsHouseProgress = new HsHouseProgress();
                        //房源申请ID
                        hsHouseProgress.setApplyId(dataSet.getId());
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
            return insertVo;
        } catch (Exception e) {
            logger.error("HousesManagerImpl addOwnerHousingApply Exception message:"+e.getMessage());
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("addOwnerHousingApply Exception message:" + e.getMessage());
        }
        return result;

    }


    /**
     * 查询房源数据字典数据
     *
     * @return
     */
    @Override
    public ResultVo getHouseDictcode() {
        ResultVo result = new ResultVo();
        Map<Object, Object> data = new HashMap<Object, Object>();
        Map<Object, Object> queryFilter = new HashMap<Object, Object>();
        List<String> dictCode = new ArrayList<String>();
        List<String> groupIds = new ArrayList<>();

        try {
            //查询在缓存中是否存在
            if (RedisUtil.isExistCache(RedisConstant.SYS_DICTCODE_CACHE_KEY)) {
                dictCode =RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY);
//                dictCode = JsonUtil.json2List(RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY), String.class);
                if (dictCode.size() > 0) {
                    Map<Object, Object> dictCodeMap = new HashMap<Object, Object>();
                    for (String dictcodeCache : dictCode) {
                        dictCodeMap = RedisUtil.safeGet(dictcodeCache);
//                        dictCodeMap = JsonUtil.parseJSON2Map(RedisUtil.safeGet(dictcodeCache));
                        data.put(dictcodeCache, dictCodeMap);
                    }
                }
            } else {
                //从数据库中查询房源数据字典的数据
                ResultVo vo = commonService.selectList(new HsSysDictcodeGroup(), queryFilter, 1);
                if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    List<HsSysDictcodeGroup> groupList = (List<HsSysDictcodeGroup>) vo.getDataSet();
                    if (groupList.size() > 0) {
                        for (HsSysDictcodeGroup obj : groupList) {
                            groupIds.add(obj.getId() + "");
                            queryFilter.put("groupId", obj.getId());
                            ResultVo itemVo = commonService.selectList(new HsSysDictcodeItem(), queryFilter, 0);
                            List<Map<Object, Object>> mapList = (List<Map<Object, Object>>) itemVo.getDataSet();

                            Map<Object, Object> groupMap = new HashMap<>();
                            groupMap.put("groupName", obj.getGroupName());
                            groupMap.put("groupNameEn", obj.getGroupNameEn());
                            groupMap.put("id", obj.getId());
                            for (Map<Object, Object> map : mapList) {
                                map.remove("createTime");
                                map.remove("updateTime");
                                map.remove("isDel");
                            }
                            groupMap.put("items", mapList);
                            data.put(obj.getGroupCode(), groupMap);
                            if (!RedisUtil.existKey(obj.getGroupCode())) {

                            }
                        }
                    }
                }
            }
            result.setDataSet(data);
        } catch (Exception e) {
            logger.error("HousesManagerImpl getHouseDictcode Exception message:"+e.getMessage());
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("getHouseDictcode Exception message:" + e.getMessage());
        }

        return result;
    }


    /**
     * 查詢租客/買家房源列表信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getRenterOrBuyersHousing(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            //自定义查询列名
            List<String> houseIds = new ArrayList<>();
            result = housesService.selectList(new HsMainHouse(), condition, 0);
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) result.getDataSet();
                if (houseList != null && houseList.size() > 0) {
                    for (Map<Object, Object> house : houseList) {
                        house.put("todayToSee",1); //是否今日可看：0：是，1，否
                        houseIds.add(StringUtil.trim(house.get("id")));
                       /* String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                        if(StringUtil.hasText(houseMainImg)){
                            house.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + house.get("houseMainImg"));
                        }else{
                            house.put("houseMainImg",null);
                        }*/
                    }
                    condition.put("houseIds", houseIds);
                    ResultVo imgResult = housesService.selectList(new HsHouseImg(), condition, 0);
                    if (imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> imgData = (List<Map<Object, Object>>) imgResult.getDataSet();
                        if (imgData != null && imgData.size() > 0) {
                            for (Map img : imgData) {
                                int houseId = Integer.parseInt(StringUtil.trim(img.get("houseId")));
                                for (Map house : houseList) {
                                    int id = Integer.parseInt(StringUtil.trim(house.get("id")));
                                    if (houseId == id) {
                                        img.remove("houseId");
                                        img.remove("houseCode");
                                        img.remove("id");
                                        house.put("houseSubImg", img.values());
                                        house.put("advertUrl", null);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    Map<Object,Object> queryFilter = new HashMap<>();

                    List<String> queryColumn = new ArrayList<>();
                    queryColumn.add("ID id");//主键ID
                    queryColumn.add("HOUSE_ID houseId");//房源名称

                    queryFilter.put("houseIds",houseIds);
                    queryFilter.put("groupBy","HOUSE_ID");
                    queryFilter.put("now","today"); //今日
                    queryFilter.put("isCancel",0); //未取消预约
                    queryFilter.put("isCheck",1); //审核通过
                    queryFilter.put("queryColumn",queryColumn);
                    ResultVo applicationResult = memberService.selectCustomColumnNamesList(HsMemberHousingApplication.class,queryFilter);
                    if(applicationResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        List<Map<Object,Object>> applicationList = (List<Map<Object, Object>>) applicationResult.getDataSet();
                        if(applicationList.size() > 0){
                            for(Map application : applicationList){
                                int ahouseId = StringUtil.getAsInt(StringUtil.trim(application.get("houseId"))); //房源ID
                                for(Map house : houseList){
                                    int houseId = StringUtil.getAsInt(StringUtil.trim(house.get("id"))); //房源ID
                                    if(ahouseId == houseId){
                                        house.put("todayToSee",0); //今日可看
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    result.setDataSet(houseList);
                }
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getRenterOrBuyersHousing Exception message:"+e.getMessage());
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("getRenterOrBuyersHousing Exception message:" + e.getMessage());
        }
        return result;
    }

    /**
     * 查询房源详情信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHouseDetail(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object, Object> resultMap = new HashMap<>();
        Map<Object, Object> houseImg = new HashMap<>();
        try {
            int id = Integer.parseInt(condition.get("id").toString());
            String houseType = StringUtil.trim(condition.get("houseType"));
            int articleType = 0; //0 买家出租 1 买家出售 2 卖家出租 3 卖家出售
            String token = StringUtil.trim(condition.get("token"));
            String memberId = "";
            if (StringUtil.hasText(token)) {
                memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            }
            //修改该房源浏览次数
            ResultVo houseResult = housesService.select(id, new HsMainHouse());
            if (houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsMainHouse mainHouse = (HsMainHouse) houseResult.getDataSet();
                if (mainHouse == null) {
                    result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
                    result.setMessage("房源数据异常");
                    return result;
                }
                Map<Object, Object> queryFilter = new HashMap<>();
                queryFilter.put("houseId", id);
                ResultVo imgResult = housesService.selectList(new HsHouseImg(), queryFilter, 0);
                List<Map<Object, Object>> imgList = (List<Map<Object, Object>>) imgResult.getDataSet();
                if (imgList.size() > 0) {
                    houseImg = imgList.get(0);
                    houseImg.remove("houseId");
                    houseImg.remove("houseCode");
                    houseImg.remove("id");
                    for (Map.Entry<Object, Object> entry : houseImg.entrySet()) {
                        Object key = entry.getKey();
                        if(-1 != StringUtil.trim(key).indexOf("houseImg") && StringUtil.hasText(StringUtil.trim(entry.getValue()))){
                            houseImg.put(entry.getKey(),ImageUtil.IMG_URL_PREFIX+entry.getValue());
                        }
                    }
                }
                String houseMainImg = mainHouse.getHouseMainImg();
                if(StringUtil.hasText(houseMainImg)){
                    mainHouse.setHouseMainImg(ImageUtil.IMG_URL_PREFIX+houseMainImg);
                }
                HsMainHouse updateHouse = new HsMainHouse();
                updateHouse.setId(id);
                updateHouse.setBrowseCount(mainHouse.getBrowseCount() + 1);
                updateHouse.setVersionNo(mainHouse.getVersionNo());
                result = housesService.updateRecord(updateHouse);
                if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    if (StringUtil.hasText(memberId)) {
                        HsHousesBrowseHistory browseHistory = new HsHousesBrowseHistory();
                        browseHistory.setMemberId(Integer.parseInt(memberId));
                        browseHistory.setHouseId(id); //房源ID
                        browseHistory.setHouseType(mainHouse.getLeaseType()); //房屋类型（0 房屋出租 1房屋出售 2新楼盘）
                        memberManager.addBrowseHistory(browseHistory);
//                        //当前登录用户ID不为空，新增记录到浏览记录表中
//                        Date date = new Date();
//                        browseHistory.setCreateTime(date);
//                        browseHistory.setUpdateTime(date);
//                        ResultVo browseResult = memberService.insert(browseHistory);
                        //如果当前登录用户与房源业主ID相同，为业主，否则不是
                        if (Integer.parseInt(memberId) == mainHouse.getMemberId()) {
                            mainHouse.setIsOwner("0");
                        } else {
                            mainHouse.setIsOwner("1");
                        }

                        Map<Object, Object> queryFavorite = new HashMap<>(3);
                        queryFavorite.put("memberId", memberId);
                        queryFavorite.put("isDel", 0);
                        queryFavorite.put("favoriteId", id);
                        ResultVo favoriteResult = memberService.selectList(new HsMemberFavorite(), queryFavorite, 0);
                        if (favoriteResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            List<Map<Object, Object>> data = (List<Map<Object, Object>>) favoriteResult.getDataSet();
                            if (data.size() > 0) {
                                mainHouse.setIsFavorite(0);
                            } else {
                                mainHouse.setIsFavorite(1);
                            }
                        }
                    } else {
                        mainHouse.setIsOwner("1");
                        mainHouse.setIsFavorite(1); //未收藏
                    }
                    resultMap.put("houseSubImg", houseImg.values());
                    resultMap.put("houses", mainHouse);
//                    if ("0".equals(houseType)) { //出租
//                        articleType = 0;
//                    } else {
//                        articleType = 1;
//                    }
//                    condition.put("articleType", articleType);
//                    ResultVo articleResult = commonService.selectAdvertDataByCondition(condition);
//                    resultMap.put("articleData", articleResult.getDataSet());

                    Map<String, Object> recommendHouses = commonManager.recommendHouses(token);
                    resultMap.put("recommendHouses", recommendHouses); //推荐房源

                    //返回FAQ
                    String channelAliasName = StringUtil.trim(condition.get("channelAliasName"));
                    Map<Object,Object> data = new HashMap<Object,Object>();
                    if(RedisUtil.isExistCache(channelAliasName)){
                        data = RedisUtil.safeGet(channelAliasName);
//                        data = JsonUtil.parseJSON2Map(RedisUtil.safeGet(channelAliasName));
                    }else{
                        queryFilter.clear();
                        queryFilter.put("channelAliasName",channelAliasName);
                        List<Map<Object,Object>> channelBooths = commonService.getChannelBoothAliasName(queryFilter);
                        if(channelBooths != null && channelBooths.size() > 0){
                            List<String> unCacheBooths = new ArrayList<>();
                            for (Map<Object,Object> channelBooth : channelBooths){
                                String boothAliasName = StringUtil.trim(channelBooth.get("boothAliasName"));
                                if(!RedisUtil.existKey(boothAliasName)){
                                    unCacheBooths.add(boothAliasName);
                                }else{
                                    data.put(boothAliasName, RedisUtil.safeGet(boothAliasName));
//                                    data.put(boothAliasName, JsonUtil.parseJSON2List(RedisUtil.safeGet(boothAliasName)));
                                }
                            }
                            if (unCacheBooths != null) {
                                //获取展位下的
                                queryFilter.clear();
                                queryFilter.put("boothState", 0);//是否启用
                                queryFilter.put("isDel", 0);//是否删除
                                queryFilter.put("boothAliasNames", unCacheBooths);
                                Map<Object, Object> unCacheBoothData = commonService.getBoothDataByCondition(queryFilter);
                                if (unCacheBoothData != null) {
                                    for (Map.Entry<Object, Object> entry : unCacheBoothData.entrySet()) {
                                        String key = StringUtil.trim(entry.getKey());
                                        Object value = entry.getValue();
                                        data.put(key, value);
                                        RedisUtil.safeSet(key,value,60*60*24*60);
//                                        RedisUtil.safeSet(key,JsonUtil.object2Json(value),60*60*24*60);
                                    }
                                }
                            }
                        }
                        RedisUtil.safeSet(channelAliasName,data,60*60*24*60);
//                        RedisUtil.safeSet(channelAliasName,JsonUtil.toJson(data),60*60*24*60);
                    }
                    resultMap.put("faq",data);
                    result.setDataSet(resultMap);
                }
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getHouseDetail Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("getHouseDetail Exception message:" + e.getMessage());
        }
        return result;
    }


    /**
     * 查询房源图片信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHousesImg(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            result = housesService.selectList(new HsHouseImg(), condition, 0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> houseImgList = (List<Map<Object, Object>>) result.getDataSet();
                if(houseImgList != null && houseImgList.size() >0){
                    for (Map<Object, Object> houseImg : houseImgList) {//保存房源图片
                        for (Map.Entry<Object, Object> entry : houseImg.entrySet()) {
                            Object key = entry.getKey();
                            if(-1 != StringUtil.trim(key).indexOf("houseImg") && StringUtil.hasText(StringUtil.trim(entry.getValue()))){
                                houseImg.put(key,ImageUtil.IMG_URL_PREFIX+entry.getValue());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getHousesImg Exception message:"+e.getMessage());
            result.setResult(ResultConstant.HOUSES_DATA_EXCEPTION);
            result.setMessage("getHousesImg Exception message:" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取房源对比信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHousingCompare(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Integer> houseIds = new ArrayList<>();
        try {
            houseIds.add(Integer.parseInt(condition.get("houseId1").toString()));
            houseIds.add(Integer.parseInt(condition.get("houseId2").toString()));
            condition.put("houseIds", houseIds);
            result = housesService.selectList(new HsMainHouse(), condition, 0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) result.getDataSet();
                if(houseList != null && houseList.size() > 0){
                    List<Map<Object, Object>> resultList = houseList.stream().map(map -> {
                        map.put("houseMainImg", ImageUtil.imgResultUrl(StringUtil.trim(map.get("houseMainImg"))));
                        return map;
                    }).collect(Collectors.toList());
                    result.setDataSet(resultList);
                }
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getHousingCompare Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo addHousingRelationInfo(Object object) {
        ResultVo result = null;
        try {
            if (object instanceof HsHouseComplain) {
                //先查询房源是否存在
                ResultVo houseVo = housesService.select(((HsHouseComplain) object).getHouseId(), new HsMainHouse());
                HsMainHouse oldHouse = null;
                if (houseVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    oldHouse = (HsMainHouse) houseVo.getDataSet();//从数据库查询的房源数据
                }
                if (oldHouse == null) {//房源数据异常
                    return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                }
                result = housesService.insert(object);
                if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    ((HsHouseComplain) object).setCreateTime(new Date());
                    HsMainHouse mainHouse = new HsMainHouse();
                    mainHouse.setId(((HsHouseComplain) object).getHouseId());
                    mainHouse.setBeReportedCount(StringUtil.getAsInt(StringUtil.trim(oldHouse.getBeReportedCount()),0) + 1);
                    mainHouse.setVersionNo(oldHouse.getVersionNo());
                    result = housesService.updateRecord(mainHouse);
                }

            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl addHousingRelationInfo Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 创建订单
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo createOrder(Map<String, Object> condition) {
        ResultVo vo = null;
        int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
        int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
        int bargainId = StringUtil.getAsInt(StringUtil.trim(condition.get("bargainId")), -1);//议价ID
        Map<Object, Object> data = new HashMap<>();//返回数据
        try {
            vo = housesService.select(houseId, new HsMainHouse());//查询房源信息
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                HsMainHouse house = (HsMainHouse) vo.getDataSet();
                if (house == null) {//房源数据异常
                    vo = ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
                    return vo;
                }
                HsHousingOrder order = new HsHousingOrder();
                BigDecimal orderAmount = null;
                /*HsMemberHousingBargainMessage _bargainMessage = null;//修改议价信息
                ResultVo bargainVo = memberService.select(bargainId, new HsMemberHousingBargainMessage());//查询议价信息
                if (bargainVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    HsMemberHousingBargainMessage bargainMessage = (HsMemberHousingBargainMessage) bargainVo.getDataSet();
                    int isDel = bargainMessage.getIsDel();//
                    int status = bargainMessage.getBargainStatus();//议价状态
                    int affirm = bargainMessage.getIsAffirmContract();//买家是否确认在线合同
                    if (status == 1 && isDel == 0 && affirm == 1) {//议价状态为房东确认并且未删除才能生成订单
                        order.setBargainId(bargainId);
                        orderAmount = BigDecimal.valueOf(bargainMessage.getLeasePrice());
                    } else {
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "待房东确认");
                        //return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                    }
                    order.setBargainId(bargainId);
                    orderAmount = BigDecimal.valueOf(bargainMessage.getLeasePrice());
                    _bargainMessage = new HsMemberHousingBargainMessage();
                    _bargainMessage.setId(bargainId);
                    _bargainMessage.setBargainStatus(2);
                } else {
                    return bargainVo;
                }*/
                Date date = new Date();
                order.setOrderType(house.getLeaseType());//订单类型 0-租房->1-买房
                String orderCode = "OC_" + RandomUtils.getRandomNumbersAndLetters(18);
                order.setOrderCode(orderCode);
                order.setHouseId(houseId);
                order.setMemberId(memberId);
                order.setOwnerId(house.getMemberId());
                order.setOrderAmount(orderAmount);
                order.setPayStatusTitle("待付款");
                order.setOrderStatusTitle("待付款");
                order.setCreateTime(date);
                order.setCreateBy(memberId);
                order.setUpdateBy(memberId);
                vo = orderService.insert(order);//插入订单
                if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    /*if (_bargainMessage != null) {
                        ResultVo update = memberService.update(_bargainMessage);
                        if (update.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            System.out.println("修改议价状态成功");
                        }
                    }*/
                    HsHousingOrder _order = (HsHousingOrder) vo.getDataSet();
                    //插入日志
                    HsHousingOrderLog orderLog = new HsHousingOrderLog();
                    orderLog.setOrderId(_order.getId());
                    orderLog.setNodeType(0);//订单节点类型0-待付款
                    orderLog.setOperatorUid(memberId);
                    orderLog.setOperatorType(1);
                    orderLog.setCreateTime(date);
                    orderLog.setCreateBy(memberId);
                    orderLog.setPostTime(date);
                    orderLog.setRemarks("用户创建订单");
                    vo = orderService.insert(orderLog);//插入订单日志
                }
                vo.setDataSet(orderCode);
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl createOrder Exception message:"+e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取进度流程
     *
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> findProgressList(String type) {
        return housesService.findProgressList(type);
    }

    /**
     * 获取房源状态信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getHousingStatusInfo(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            Map<Object, Object> resultMap = new HashMap<>();

            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("APPLY_ID applyId");//房源申请id
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
            condition.put("queryColumn", queryColumn);
            int applyId = 0;
            Map<Object, Object> house = new HashMap<>(16);
            result = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition); //根据申请ID查询房源主信息
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) result.getDataSet();
                if (houseList.size() > 0) {
                    house = houseList.get(0);
                    String houseMainImg = StringUtil.trim(house.get("houseMainImg"));
                    if (StringUtil.hasText(houseMainImg)) {
                        house.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + houseMainImg);
                    } else {
                        house.put("houseMainImg", null);
                    }
                    applyId = StringUtil.getAsInt(StringUtil.trim(house.get("applyId")),0);

                }
            }
            resultMap.put("houses", house);

            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            condition.clear();
            condition.put("houseId",houseId);
            condition.put("applyId",applyId);
            condition.put("isDel",0);
            List<Map<Object,Object>> progress = housesService.getProgress(condition);
            resultMap.put("logData", progress);
            result.setDataSet(resultMap);
            result.setDataSet(resultMap);
        } catch (Exception e) {
            logger.error("HousesManagerImpl getHousingStatusInfo Exception message:"+e.getMessage());
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
                        /**
                         * 获取房源信息
                         */
                        ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                        if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":Failed to get listing information！");
                            return result;
                        }
                        HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
                        //设置申请id
                        houseAutoReplySetting.setApplyId(mainHouse.getApplyId());
                        //根据房源ID查询自动应答设置信息
                        condition.clear();
                        condition.put("houseId", houseId);
                        condition.put("isDel", 0);
                        ResultVo resultVo = housesService.selectList(new HsHouseAutoReplySetting(), condition, 0);
                        //房源信息结果集
                        List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) resultVo.getDataSet();
                        if (dataSet.size() > 2) {
                            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE + ":自动应答最多只能设置3条！");
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
            logger.error("HousesManagerImpl addAutoReplySetting Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

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

    @Override
    public ResultVo checkKeyIsExpire(int houseId, String memberId) {
        ResultVo resultVo = null;
        try {
            Map<Object, Object> condition = Maps.newHashMap();
            condition.put("houseId", houseId);
            condition.put("memberId", memberId);
            condition.put("isExpire", 0);//已过期
            resultVo = housesService.checkKeyIsExpire(condition);
        } catch (Exception e) {
            logger.error("HousesManagerImpl checkKeyIsExpire Exception message:"+e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return resultVo;
    }

    @Override
    @Transactional
    public ResultVo bargain(String groupName, String houseId,Integer languageVersion) {
        ResultVo resultVo = new ResultVo();
        Map<Object,Object> condition = Maps.newHashMap();
        EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
        //根据群名称获取户主及客户id
        String[] split = groupName.split("_");
        if (split.length < 2) {
            throw new RuntimeException("参数错误");
        }
        //户主id
        String ownerId = split[1];
        //客户id
        String clientId = split[2];

        /**
         * 获取订单，判断当前客户是否有与这套房源有未处理的订单
         */
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");
        condition.put("queryColumn",queryColumn);
        condition.put("memberId",StringUtil.getAsInt(clientId));
        condition.put("houseId",StringUtil.getAsInt(houseId));
        condition.put("isDel",0);
        condition.put("tradingStatus",0);
        try {
            ResultVo orderResultVo = orderService.selectCustomColumnNamesList(HsHousingOrder.class, condition);
            if(orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> orderList = (List<Map<Object, Object>>) orderResultVo.getDataSet();
                if(orderList != null && orderList.size() > 0){
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    //订单正在处理中，请勿重复操作
                    resultVo.setMessage("The order is being processed, please do not repeat");
                    return resultVo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //房源信息
        Map<Object, Object> house;
        try {
            house = getHouseInfo(StringUtil.getAsInt(houseId));
        } catch (Exception e) {
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            resultVo.setMessage(e.getMessage());
            return resultVo;
        }

        Map<String, Object> resultMap;
        try {
            resultMap = createGroup(groupName, houseId);
            //获取议价信息
            String groupId = StringUtil.trim(resultMap.get("groupId"));
            condition.clear();
            condition.put("groupId",groupId);
            condition.put("isDel",0);
            condition.put("bargainStatus",0);
            ResultVo bargainResultVo;
            bargainResultVo = memberService.selectList(new HsMemberHousingBargain(), condition, 1);
            if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage("获取议价信息失败");
                return resultVo;
            }
            List<HsMemberHousingBargain> bargainList = (List<HsMemberHousingBargain>) bargainResultVo.getDataSet();
            Integer bargainId;
            if(bargainList.size() < 1){
                //5.2.2插入房源议价信息 （如果插入房源议价信息失败，要删除刚刚创建好的环信群组）
                String leaseType = StringUtil.trim(house.get("leaseType"));
                HsMemberHousingBargain hsMemberHousingBargain = new HsMemberHousingBargain();
                hsMemberHousingBargain.setLanguageVersion(languageVersion);
                hsMemberHousingBargain.setGroupId(groupId);
                hsMemberHousingBargain.setGroupName(groupName);
                hsMemberHousingBargain.setHouseId(Integer.parseInt(houseId));
                hsMemberHousingBargain.setOwnerId(Integer.parseInt(ownerId));
                hsMemberHousingBargain.setMemberId(Integer.parseInt(clientId));
                hsMemberHousingBargain.setHouseType(Integer.parseInt(leaseType));
                hsMemberHousingBargain.setBargainStatus(0);
                hsMemberHousingBargain.setCreateBy(Integer.parseInt(clientId));
                hsMemberHousingBargain.setCreateTime(new Date());
                ResultVo insert = null;
                //插入房源议价信息是否成功
                boolean bool = true;
                try {
                    insert  = memberService.insert(hsMemberHousingBargain);
                } catch (Exception e) {
                    e.printStackTrace();
                    bool = false;
                }
                if (insert == null || insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    bool = false;
                }
                if(!bool){
                    //插入失败要将刚刚新建好的 环信群删除掉
                    Object deleteChatGroup = easemobChatGroup.deleteChatGroup(groupId);
                    JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                    JSONObject resultObjecct = object.getJSONObject("data");
                    if (resultObjecct == null) {
                        //TODO 删除环信群失败！这里需要处理，可能会遗留一些无法删除的环信群组
                        resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                        resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                        return resultVo;
                    }
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                    return resultVo;
                }
                HsMemberHousingBargain bargain = (HsMemberHousingBargain) insert.getDataSet();
                bargainId = bargain.getId();
            }else{
                HsMemberHousingBargain hsMemberHousingBargain = bargainList.get(0);
                bargainId = hsMemberHousingBargain.getId();
            }
            resultMap.put("groupId", groupId);
            resultMap.put("groupName", groupName);
            resultMap.put("bargainId", bargainId);
        } catch (Exception e) {
            logger.error("HousesManagerImpl bargain Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            resultVo.setMessage(e.getMessage());
            return resultVo;
        }
        resultVo.setDataSet(resultMap);
        return resultVo;
    }

    /**
     * 创建环信群
     * 如果环信群存在 直接返回群信息
     * @param groupName 群名称
     * @param houseId   房源id
     * @return
     * @throws Exception  异常信息
     */
    @Override
    public Map<String, Object> createGroup(String groupName, String houseId) throws Exception {
        EasemobChatGroup easemobChatGroup = new EasemobChatGroup();
        Group group = new Group();
        Map<String, Object> resultMap = new HashMap<>(16);
        //1.根据群名称获取户主及客户id
        if("".equals(StringUtil.trim(groupName)) || "".equals(StringUtil.trim(houseId))){
            throw new RuntimeException("参数错误");
        }
        String[] split = groupName.split("_");
        if (split.length < 2) {
            throw new RuntimeException("参数错误");
        }
        //户主id
        String ownerId = split[1];
        //客户id
        String clientId = split[2];
        Map<Object, Object> houseInfo;
        try {
            houseInfo = getHouseInfo(StringUtil.getAsInt(houseId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        //3.获取户主及客户code
        Map<Object,Object> condition = Maps.newHashMap();
        List<String> queryColumn = new ArrayList<>();
        //3.1根据户主及客户id获取户主及客户人员信息
        List<Integer> memberIds = new ArrayList<>();
        memberIds.add(Integer.parseInt(ownerId));
        memberIds.add(Integer.parseInt(clientId));
        //自定义查询列名
        queryColumn.clear();
        queryColumn.add("ID id");
        queryColumn.add("MEMBER_CODE memberCode");
        condition.put("memberIds", memberIds);
        condition.put("queryColumn",queryColumn);
        ResultVo memberResultVo = null;
        memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);

        if (memberResultVo == null || memberResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || memberResultVo.getDataSet() == null) {
            //获取人员信息失败
            throw new RuntimeException("获取人员信息失败");
        }

        List<Map<Object,Object>> dataSet = (List<Map<Object,Object>>) memberResultVo.getDataSet();
        if (dataSet.size() < 1) {
            //人员信息不存在
            throw new RuntimeException("人员信息不存在");
        }
        //3.2根据人员id获取人员code
        String ownerCode = "";
        String clientCode = "";
        for (Map<Object,Object> hsMember : dataSet) {
            if (hsMember == null) {
                throw new RuntimeException("人员信息为空");
            }
            String id = StringUtil.trim(hsMember.get("id"));
            String memberCode = StringUtil.trim(hsMember.get("memberCode"));
            if (id.equals(ownerId)) {
                ownerCode = memberCode;
            }
            if (id.equals(clientId)) {
                clientCode = memberCode;
            }
        }
        //查询当前客户是否有议价完成的群，如果有，进行删除
        condition.clear();
        condition.put("memberId",clientId);
        condition.put("houseId",houseId);
        condition.put("isDel",0);
        ResultVo bargainResultVo = memberService.selectList(new HsMemberHousingBargain(), condition, 1);
        if(bargainResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<HsMemberHousingBargain> bargainList = (List<HsMemberHousingBargain>) bargainResultVo.getDataSet();
            if(bargainList.size() > 0){
                for (HsMemberHousingBargain bargainMap : bargainList) {
                    //已存在议价群 如果数据库中有议价状态不为议价中的数据。说明议价已经完成 需要删除环信群
                    //议价状态（0 议价中 1 议价成功 2 议价失败）
                    Integer bargainStatus = bargainMap.getBargainStatus();
                    //如果议价状态不为议价中，说明该议价已经完成，但是环信群并没有删除。在此处删除环信群
                    if(bargainStatus != 0){
                        //删除环信群
                        Object deleteChatGroup = easemobChatGroup.deleteChatGroup(bargainMap.getGroupId());
                        JSONObject object = JSON.parseObject(StringUtil.trim(deleteChatGroup));
                        JSONObject resultObjecct = object.getJSONObject("data");
                    }
                }
            }
        }
        //4.查询当前业主下所有环信群组
        List<Map> groupsByMemberCode = getGroupsByMemberCode(ownerCode);
        //5.判断当前业主与用户是否存在议价群组，如果有直接返回群组信息。没有则创建群组
        Optional<Map> first = groupsByMemberCode.stream().parallel().filter(map -> groupName.equals(map.get("groupname"))).findFirst();
        if (first.isPresent()) {
            //存在议价群
            Map firstMap = first.get();
            String groupId = StringUtil.trim(firstMap.get("groupid"));
            String groupname = StringUtil.trim(firstMap.get("groupname"));
            //5.1已有议价群组
            resultMap.put("groupId", groupId);
            resultMap.put("groupName", groupname);
            resultMap.put("isExist",true);
            return resultMap;
        }
        //5.2没有议价群组，创建环信群组
        //5.2.1 创建环信群组
        //封装群成员
        UserName userName = new UserName();
        userName.add(ownerCode);
        userName.add(clientCode);
        //群组名称，此属性为必须的
        group.groupname(groupName)
                //群组描述，此属性为必须的。暂时为房源id
                .desc(houseId)
                //是否是公开群，此属性为必须的
                ._public(true)
                //群组成员最大数（包括群主），值为数值类型，默认值200，最大值2000，此属性为可
                .maxusers(10)
                //是否允许群成员邀请别人加入此群。 true：允许群成员邀请人加入此群，false：只有群主或者管理员才可以往群里加人
                .approval(false)
                //群组的管理员，此属性为必须的
                .owner(ownerCode)
                //群组成员，此属性为可选的，但是如果加了此项，数组元素至少一个（注：群主不需要写入到members里面）
                .members(userName);
        Object chatGroup = easemobChatGroup.createChatGroup(group);
        JSONObject jsonObject = JSON.parseObject(chatGroup.toString());
        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) {
            //创建环信议价群失败
            throw new RuntimeException("创建环信群失败");
        }
        String groupId = data.getString("groupid");
        resultMap.put("groupId", groupId);
        resultMap.put("groupName", groupName);
        if(groupName.endsWith("_yy")){
            /*预约看房，插入预约看房数据*/
            Date date = new Date();
            int leaseType = StringUtil.getAsInt(StringUtil.trim(houseInfo.get("leaseType")));
            HsMemberHousingApplication housingApplication = new HsMemberHousingApplication();
            housingApplication.setGroupId(groupId);
            housingApplication.setGroupName(groupName);
            housingApplication.setOwnerId(StringUtil.getAsInt(ownerId));
            housingApplication.setApplicationType(0);
            housingApplication.setHouseId(StringUtil.getAsInt(houseId));
            housingApplication.setMemberId(StringUtil.getAsInt(clientId));
            //房源类型(0：出租，1：出售)
            housingApplication.setHouseType(leaseType);
            housingApplication.setCreateTime(date);
            housingApplication.setUpdateTime(date);
            housingApplication.setCreateBy(StringUtil.getAsInt(clientId));
            ResultVo insert = memberService.insert(housingApplication);
            if(insert.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return null;
            }
        }
        return resultMap;
    }

    /**
     * 获取房源信息
     * @param houseId 房源id
     * @return
     * @throws Exception
     */
    public Map<Object,Object> getHouseInfo(Integer houseId) throws Exception {
        //获取房源信息，进行房源数据判断 房源信息是否存在、前端传递的业主id是否与数据库id相同、房源是否锁定
        Map<Object,Object> house = new HashMap<>(16);
        Map<Object,Object> condition = Maps.newHashMap();
        List<String> queryColumn = new ArrayList<>();
        //主键ID
        queryColumn.add("ID id");
        //业主id
        queryColumn.add("MEMBER_ID memberId");
        //预约类型（0：出租，1：出售）
        queryColumn.add("LEASE_TYPE leaseType");
        //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
        queryColumn.add("IS_LOCK isLock");
        condition.put("queryColumn",queryColumn);
        condition.put("id",houseId);
        condition.put("isDel",0);
        ResultVo result = null;
        result = housesService.selectCustomColumnNamesList(HsMainHouse.class,condition);
        if (result == null || result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
            //房源信息查询失败
            throw new RuntimeException("房源信息查询失败");
        }
        List<Map<Object,Object>> houseData = (List<Map<Object,Object>>)result.getDataSet();
        if(houseData.size() < 1){
            //房源信息不存在
            throw new RuntimeException("房源信息不存在");
        }
        house = houseData.get(0);
        //房源状态：0>已提交 1审核通过 2商家申请下架 3下架 4已出售或出租
        int houseStatus = StringUtil.getAsInt(StringUtil.trim(house.get("houseStatus")));
        if(houseStatus == 3 || houseStatus == 4){
            //房源信息不存在
            throw new RuntimeException("房源信息不存在");
        }


        String isLock = StringUtil.trim(house.get("isLock"));
        if("1".equals(isLock)){
            //房源已被锁定
            throw new RuntimeException("房源已被锁定");
        }
        return house;
    }

    /**
     * 根据用户code获取该用户下所有聊天群组
     * @param memberCode 用户code
     * @return
     */
    @Override
    public List<Map> getGroupsByMemberCode(String memberCode) {
        List<Map> mapList = new ArrayList<>();
        EasemobIMUsers easemobIMUsers = new EasemobIMUsers();
        try {
            Object imUserAllChatGroups = easemobIMUsers.getIMUserAllChatGroups(memberCode);
            JSONObject jsonObject = JSON.parseObject(imUserAllChatGroups.toString());
            com.alibaba.fastjson.JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray != null) {
                mapList = jsonArray.toJavaList(Map.class);
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getGroupsByMemberCode Exception message:"+e.getMessage());
        }
        return mapList;
    }

    @Override
    public ResultVo getHouseSettingTime(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<Object, Object>> timeList = new ArrayList<>();
        try {
            //（接收项目的这位兄弟,慢慢修bug吧。）
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
            logger.error("HousesManagerImpl getHouseSettingTime Exception message:"+e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 搜索房源
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo searchHouse(HouseSearchCondition condition) {
        ResultVo vo = null;
        try {
            ResultVo searchVo = searchService.query(condition);
            if (searchVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                List<Long> houseIds = (List<Long>) searchVo.getDataSet();
                if (houseIds != null && houseIds.size() > 0) {//
                    //自定义查询列名
                    List<String> queryColumn = new ArrayList<>();
                    Map<Object, Object> queryFilter = new HashMap<>();
                    queryColumn.add("ID houseId");//房源ID
                    queryColumn.add("HOUSE_NAME houseName");//房源名称
                    queryColumn.add("HOUSE_CODE houseCode");//房源编号
                    queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）uploadHousing
                    queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
                    queryColumn.add("CITY city");//城市
                    queryColumn.add("COMMUNITY community");//社区
                    queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                    queryColumn.add("PROPERTY property");//项目
                    queryColumn.add("ADDRESS address");//房源所在区域名称
                    queryColumn.add("HOUSE_RENT houseRent");//期望租金/或出售价
                    queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                    queryFilter.put("queryColumn", queryColumn);
                    queryFilter.put("houseIds", houseIds);
                    vo = housesService.selectCustomColumnNamesList(HsMainHouse.class, queryFilter);
                    if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> houses = (List<Map<Object, Object>>) vo.getDataSet();
                        List<Map<Object, Object>> _orderByHouses = Lists.newArrayListWithCapacity(houses.size());
                        Map<Long, Map<Object, Object>> idToHouseMap =Maps.newHashMap();
                        for (Map<Object, Object> house : houses) {
                            house.put("todayToSee",1); //今日可看
                            String mainImg = StringUtil.trim(house.get("houseMainImg"));
                            if(StringUtil.hasText(mainImg)){
                                mainImg = ImageUtil.IMG_URL_PREFIX + mainImg;
                            }
                            house.put("houseMainImg", mainImg);
                            idToHouseMap.put(StringUtil.getAsLong(StringUtil.trim(house.get("houseId"))),house);
                        }

                        queryFilter.put("houseIds", houseIds);
                        ResultVo imgResult = housesService.selectList(new HsHouseImg(), queryFilter, 0);
                        if (imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                            List<Map<Object, Object>> imgData = (List<Map<Object, Object>>) imgResult.getDataSet();
                            if (imgData != null && imgData.size() > 0) {
                                for (Map house : houses) {
                                    int id =StringUtil.getAsInt(StringUtil.trim(house.get("houseId")));
                                    int length = imgData.size()-1;
                                    for (int i = length;i>=0;i--) {
                                        Map img = imgData.get(i);
                                        int houseId = StringUtil.getAsInt(StringUtil.trim(img.get("houseId")));
                                        if (houseId == id) {
                                            img.remove("houseId");
                                            img.remove("houseCode");
                                            img.remove("id");
                                            house.put("houseSubImg", img.values());
                                            imgData.remove(img);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        queryFilter.clear();
                        queryColumn.clear();
                        queryFilter.put("isCancel",0); //未取消预约
                        queryFilter.put("isFinish",1); //审核通过
                        Date systemTime = vo.getSystemTime();
                        queryFilter.put("startBetweenDate",DateUtil.getCurrHourTime(systemTime));
                        queryFilter.put("endBetweenDate", DateUtil.getLastHourTime(systemTime,-1));
                        queryFilter.put("houseIds",houseIds);
                        //查询是否有今日可看的房源
                        ResultVo applicationResult = memberService.getToday2SeeHouses(queryFilter);
                        //ResultVo applicationResult = memberService.selectCustomColumnNamesList(HsMemberHousingApplication.class,queryFilter);
                        if(applicationResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                            List<Map<Object,Object>> applicationList = (List<Map<Object, Object>>) applicationResult.getDataSet();
                            if(applicationList.size() > 0){
                                for(Map application : applicationList){
                                    int ahouseId = StringUtil.getAsInt(StringUtil.trim(application.get("houseId"))); //房源ID
                                    for(Map house : houses){
                                        int houseId = StringUtil.getAsInt(StringUtil.trim(house.get("houseId"))); //房源ID
                                        if(ahouseId == houseId){
                                            house.put("todayToSee",0); //今日可看
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 矫正顺序
                        for (Long houseId : houseIds) {
                            _orderByHouses.add(idToHouseMap.get(houseId));
                        }
                        searchVo.setDataSet(_orderByHouses);
                        return searchVo;
                    }
                }else {
                    return searchVo;
                }
                System.out.println("查询到的房源数据：" + houseIds.size());
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl searchHouse Exception message:"+e.getMessage());
            ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取房源自动设置信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getAutoReplySetting(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        Map<Object, Object> resultMap = new HashMap<>();
        try {
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId"))); //房源ID
            result = housesService.select(houseId, new HsMainHouse()); //根据房源ID查询房源信息
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
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getAutoReplySetting Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return result;
    }

    /**
     * 获取今日可看房源列表信息
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getTodayLookHouseList(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");
            queryColumn.add("HOUSE_ID houseId");
            queryColumn.add("START_APARTMENT_TIME startApartmentTime");
            condition.put("queryColumn", queryColumn);
            condition.put("isCancel", 0); //未取消看房预约
            condition.put("now", "today"); //今日可看
            condition.put("startBetweenDate",DateUtil.getCurrHourTime(result.getSystemTime()));
            condition.put("endBetweenDate", DateUtil.getLastHourTime(result.getSystemTime(),-1));
            condition.put("houseType", condition.get("leaseType")); //房屋类型（0：出租，1：出售）
            condition.put("groupBy", "HOUSE_ID");

            //查询预约申请表，今日已预约的房源
            result = memberService.selectCustomColumnNamesList(HsMemberHousingApplication.class, condition);
            if (result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                List<Map<Object, Object>> applicationResult = (List<Map<Object, Object>>) result.getDataSet();
                if (applicationResult != null && applicationResult.size() > 0) {
                    List<String> houseIds = new ArrayList<>();
                    for (Map<Object, Object> application : applicationResult) {
                        houseIds.add(StringUtil.trim(application.get("houseId"))); //房源ID
                    }

                    condition.put("houseIds", houseIds);
                    ResultVo houseResult = housesService.selectList(new HsMainHouse(), condition, 0);
                    if (houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                        List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseResult.getDataSet();
                        if (houseList.size() > 0) {
                            condition.put("houseIds", houseIds);
                            ResultVo imgResult = housesService.selectList(new HsHouseImg(), condition, 0);
                            if (imgResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                                List<Map<Object, Object>> imgList = (List<Map<Object, Object>>) imgResult.getDataSet();
                                if (imgList != null && imgList.size() > 0) {
                                    for (Map img : imgList) {
                                        int houseId = StringUtil.getAsInt((StringUtil.trim(img.get("houseId"))));
                                        for (Map house : houseList) {
                                            int id = StringUtil.getAsInt((StringUtil.trim(img.get("id"))));
                                            if (houseId == id) {
                                                img.remove("id");
                                                img.remove("houseId");
                                                img.remove("houseCode");
                                                house.put("houseMainImg",ImageUtil.imgResultUrl(StringUtil.trim(house.get("houseMainImg"))));
                                                house.put("houseSubImg", img.values());
                                                house.put("advertUrl", null);
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    return imgResult;
                                }
                            } else {
                                return imgResult;
                            }
                            result.setDataSet(houseList);
                        } else {
                            return houseResult;
                        }
                    }
                } else {
                    //无数据，返回
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getTodayLookHouseList Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    @Override
    public List<Map<Object,Object>> getMainHouseList(List<String> houseIds) {
        List<Map<Object,Object>> list = null;
        try {
            Map<Object, Object> condition = new HashMap<>(1);
            condition.put("houseIds", houseIds);
            condition.put("isDel", 0);
            ResultVo houseResult = housesService.selectList(new HsMainHouse(), condition, 0);
            if(houseResult.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> dataSet = (List<Map<Object,Object>>) houseResult.getDataSet();
                list = dataSet;
            }
        } catch (Exception e) {
            logger.error("HousesManagerImpl getMainHouseList Exception message:"+e.getMessage());
        }
        return list;
    }

    @Override
    public ResultVo getBargainList(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try{
            result = memberService.selectCustomColumnNamesList(HsMemberHousingBargain.class,condition);
        }catch (Exception e){
            logger.error("HousesManagerImpl getBargainList Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取预约聊天记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo getReservationMessageList(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try{
            result = memberService.selectList(new HsMemberHousingApplicationMessage(),condition,0);
        }catch (Exception e){
            logger.error("HousesManagerImpl getReservationMessageList Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo getReservationList(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try{
            result = memberService.selectList(new HsMemberHousingApplication(),condition,0);
        }catch (Exception e){
            logger.error("HousesManagerImpl getReservationList Exception message:"+e.getMessage());
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo suggest(String prefix) {
        ResultVo result = null ;
        try{
            result = searchService.suggest(prefix);
        }catch (Exception e){
            logger.error("HousesManagerImpl suggest Exception message:"+e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 地图选房入口
     * @param condition
     * @return
     */
    @Override
    public ResultVo rentHouseMap(Map<Object, Object> condition) {
        ResultVo vo = null;
        Map<Object, Object> queryFilter = Maps.newHashMap();
        try {
            //查询对应城市下的社区信息
            ResultVo communityVo = commonService.findCommunitiesByCityName(condition);
            if (communityVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//请求成功
                vo = new ResultVo();
                List<Map<Object,Object>> communityLists = (List<Map<Object, Object>>) communityVo.getDataSet();
                String cityNameCn = StringUtil.trim(condition.get("cityNameCn"));
                ResultVo searchVo = searchService.aggregateCity(cityNameCn);
                if (searchVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//请求成功
                    return searchVo;
                }
                //统计数据
                List<HouseBucketDTO> bucketList = (List<HouseBucketDTO>) searchVo.getDataSet();
                for (Map<Object, Object> communities : communityLists) {
                    String _cityNameCn = StringUtil.trim(communities.get("cityNameCn"));
                    long count  = 0;
                    for (HouseBucketDTO bucket : bucketList) {
                        if(bucket.getKey().equals(_cityNameCn)){
                            count = bucket.getCount();
                            break;
                        }
                    }
                    communities.put("count",count);
                }
                vo.setDataSet(communityLists);
            }
        } catch (Exception ex) {
            logger.error("CommonManagerImpl rentHouseMap Exception message:"+ ex.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            condition = null;
            queryFilter = null;
            return vo;
        }
    }

    /**
     * 地图选房
     * @param mapSearch
     * @return
     */
    @Override
    public ResultVo rentMapHouses(MapSearch mapSearch) {
        ResultVo vo = null;
        Map<Object, Object> queryFilter = Maps.newHashMap();
        try {
            if (mapSearch.getLevel() < 13) {
                //全地图查询
                vo = searchService.mapQuery(mapSearch.getCityEnName(),mapSearch.getOrderBy(),mapSearch.getOrderDirection(),mapSearch.getPageIndex(),mapSearch.getPageSize());
            } else {
                // 小地图查询必须要传递地图边界参数
                vo = searchService.mapQuery(mapSearch);
            }
            if(vo.getDataSet()!=null){//查询到的房源信息
                List<Long> houseIds  = (List<Long>) vo.getDataSet();
                List<Map<Object, Object>> list = wrapperHouseResult(houseIds);
                vo.setDataSet(list);
            }
        } catch (Exception ex) {
            logger.error("CommonManagerImpl rentMapHouses Exception message:"+ ex.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            queryFilter = null;
            return vo;
        }
    }

    /**
     * 加载房源信息
     * @param houseIds
     * @return
     */
    private List<Map<Object,Object>> wrapperHouseResult(List<Long> houseIds) {
        List<Map<Object,Object>> result = new ArrayList<>();//返回数据
        Map<Long, Map<Object,Object>> idToHouseMap = new HashMap<>();
        if (houseIds != null && houseIds.size() > 0) {//
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object, Object> queryFilter = new HashMap<>();
            queryColumn.add("ID houseId");//房源ID
            queryColumn.add("HOUSE_NAME houseName");//房源名称
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
            queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("PROPERTY property");//项目
            queryColumn.add("ADDRESS address");//房源所在区域名称
            queryColumn.add("HOUSE_RENT houseRent");//期望租金/或出售价
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
            queryFilter.put("queryColumn", queryColumn);
            queryFilter.put("houseIds", houseIds);
            ResultVo vo = null;
            try {
                vo = housesService.selectCustomColumnNamesList(HsMainHouse.class, queryFilter);
                if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    List<Map<Object, Object>> houses = (List<Map<Object, Object>>) vo.getDataSet();
                    houses.forEach(house -> {
                        String houseMainImg = StringUtil.trim(house.get("houseMainImg"));//房源图片主图
                        if(StringUtil.hasText(houseMainImg)){
                            house.put("houseMainImg",ImageUtil.IMG_URL_PREFIX+houseMainImg);
                        }
                        idToHouseMap.put(StringUtil.getAsLong(StringUtil.trim(house.get("houseId"))), house);
                    });

                    //wrapperHouseList(houseIds, idToHouseMap);

                    // 矫正顺序
                    for (Long houseId : houseIds) {
                        result.add(idToHouseMap.get(houseId));
                    }
                }
            } catch (Exception e) {
                logger.error("HousesManagerImpl wrapperHouseResult Exception message:"+e.getMessage());
            }
        }
        return result;
    }

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
            logger.error("HousesManagerImpl addPurchase Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo addDirectSalesProperty(HsHouseNewBuilding hsHouseNewBuilding) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = housesService.insert(hsHouseNewBuilding);
        }catch (Exception e){
            logger.error("HousesManagerImpl addDirectSalesProperty Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getPropertyArea() {
        ResultVo resultVo = new ResultVo();
        try {
            List<Map<Object,Object>> propertyArea = housesService.getPropertyArea();
            resultVo.setDataSet(propertyArea);
        }catch (Exception e){
            logger.error("HousesManagerImpl getPropertyArea Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取开发商信息
     * @return
     */
    @Override
    public ResultVo getDevelopers(){
        ResultVo resultVo = new ResultVo();
        try {
            List<Map<Object,Object>> developers = housesService.getDevelopers();
            resultVo.setDataSet(developers);
        }catch (Exception e){
            logger.error("HousesManagerImpl getDevelopers Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getDirectSalesDetails(Integer id,Integer memberId){
        ResultVo resultVo = new ResultVo();
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object,Object> condition = Maps.newHashMap();
            //查询会员是否已经申请过
            boolean isExistence = false;
            //预约id
            Integer applyId = -1;
            queryColumn.clear();
            queryColumn.add("ID id");
            queryColumn.add("SIGNATURE signature");
            condition.put("queryColumn",queryColumn);
            //项目ID
            condition.put("projectId",id);
            //会员ID
            condition.put("memberId",memberId);
            ResultVo applyResultVo = housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class, condition);
            if(applyResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> memberApplys = (List<Map<Object, Object>>) applyResultVo.getDataSet();
                if(memberApplys!=null && memberApplys.size()>0){
                    Map<Object, Object> map = memberApplys.get(0);
                    String signature = StringUtil.trim(map.get("signature"));
                    if(StringUtil.hasText(signature)){
                        isExistence = true;
                        applyId = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                    }
                }
            }



            resultVo = housesService.select(id,new HsHouseNewBuilding());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                HsHouseNewBuilding houseNewBuilding = (HsHouseNewBuilding) resultVo.getDataSet();
                String projectMainImg = houseNewBuilding.getProjectMainImg();
                projectMainImg = ImageUtil.imgResultUrl(projectMainImg);
                houseNewBuilding.setProjectMainImg(projectMainImg);
                String projectMainImg2 = houseNewBuilding.getProjectMainImg2();
                projectMainImg2 = ImageUtil.imgResultUrl(projectMainImg2);
                houseNewBuilding.setProjectMainImg2(projectMainImg2);

                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(houseNewBuilding, SerializerFeature.WriteMapNullValue));
                Map map = JSON.toJavaObject(jsonObject, Map.class);
                map.put("isExistence",isExistence);
                map.put("applyId",applyId);
                resultVo.setDataSet(map);
            }
        }catch (Exception e){
            logger.error("HousesManagerImpl getDirectSalesDetails Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

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
            logger.error("HousesManagerImpl getDirectSalesPropertyList Exception message:"+e.getMessage());
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

}