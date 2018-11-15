package www.ucforward.com.manager.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.controller.SearchController;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.entity.HouseBucketDTO;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.form.MapSearch;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.SearchService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/28
 */
@Service("commonManager")
public class CommonManagerImpl implements CommonManager {

    private static Logger logger = LoggerFactory.getLogger(CommonManagerImpl.class); // 日志记录

    @Resource
    private CommonService commonService;
    @Resource
    private MemberService memberService;
    @Resource
    private HousesService housesService;
    @Resource
    private SearchService searchService;

    @Override
    public ResultVo getIndexDatas(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> queryFilter = new HashMap<Object, Object>();
        Map<Object, Object> data = new HashMap<Object, Object>();
        try {
            String token = StringUtil.trim(condition.get("token"));
            if (condition == null) {
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return vo;
            }
            String channelAliasName = StringUtil.trim(condition.get("channelAliasName"));
            if (!StringUtil.hasText(channelAliasName)) {
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return vo;
            }
            if (RedisUtil.isExistCache(channelAliasName)) {//获取频道下的缓存，只缓存下面的key值
                data =RedisUtil.safeGet(channelAliasName);
            } else {
                //查询数据库，并加缓存
                queryFilter.clear();
                queryFilter.put("channelAliasName", channelAliasName);
                //查询频道下的所有展位
                List<Map<Object, Object>> channelBooths = commonService.getChannelBoothAliasName(queryFilter);
                if (channelBooths != null) {
                    List<String> unCacheBooths = new ArrayList<String>();
                    for (Map<Object, Object> channelBooth : channelBooths) {
                        String boothAliasName = StringUtil.trim(channelBooth.get("boothAliasName"));
                        if (!RedisUtil.existKey(boothAliasName)) {
                            unCacheBooths.add(boothAliasName);
                        } else {
                            data.put(boothAliasName, RedisUtil.safeGet(boothAliasName));
//                            data.put(boothAliasName, JsonUtil.parseJSON2List(RedisUtil.safeGet(boothAliasName)));
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
                            }
                        }
                    }
                }
                RedisUtil.safeSet(channelAliasName, data, 60 * 60 * 24 * 1);
            }

            //查询今日可看内容
            condition.clear();
            condition.put("isCancel", 0);//是否取消
            condition.put("isFinish", 0);//是否完成
            condition.put("isCheck", 0);//是否审核
            Date systemTime = vo.getSystemTime();
            condition.put("startBetweenDate",DateUtil.getCurrHourTime(systemTime));
            condition.put("endBetweenDate", DateUtil.getLastHourTime(systemTime,-1));
            //查询是否有今日可看的房源
            ResultVo housesVo = memberService.getToday2SeeHouses(condition);
            List<Map<Object, Object>> houses = (List<Map<Object, Object>>) housesVo.getDataSet();
            List<String> queryColumn = new ArrayList<>();
            if (houses != null && houses.size() > 0) {
                //查询房源信息
                List<Integer> houseIds = new ArrayList<>();
                for (Map<Object, Object> house : houses) {
                    houseIds.add(StringUtil.getAsInt(StringUtil.trim(house.get("houseId"))));
                }
                condition.clear();
                //自定义查询列名
                queryColumn.add("ID id");//主键ID
                queryColumn.add("HOUSE_NAME houseName");//房源ID
                queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
                queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
                queryColumn.add("CITY city");//城市
                queryColumn.add("COMMUNITY community");//社区
                //预约类型（0：出租，1：出售）
                queryColumn.add("LEASE_TYPE leaseType");
                queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
                queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
                queryColumn.add("ADDRESS address");//所在位置

                condition.put("queryColumn", queryColumn);

                condition.put("isCheck", 1);//审核通过
                condition.put("isLock", 0);//未锁定
                condition.put("isDel", 0);//未删除
                condition.put("houseStatus", 1);//房源状态：0>已提交 1审核通过
                condition.put("houseIds", houseIds);//房源IDs
                //自定义查询

                housesVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                if (housesVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//请求成功
                    //获取房源主表信息
                    List<Map<Object, Object>> houseMapList = (List<Map<Object, Object>>) housesVo.getDataSet();
                    if (houseMapList != null && houseMapList.size() > 0) {
                        for (Map<Object, Object> house : houses) {
                            String houseId = StringUtil.trim(house.get("houseId"));
                            for (Map<Object, Object> houseMap : houseMapList) {
                                String _houseId = StringUtil.trim(houseMap.get("id"));
                                if (houseId.equals(_houseId)) {
                                    house.putAll(houseMap);
                                    houseMapList.remove(houseMap);
                                    house.put("houseMainImg", ImageUtil.imgResultUrl(StringUtil.trim(house.get("houseMainImg"))));
                                    break;
                                }
                            }
                        }
                    } else {
                        houses = null;
                    }
                }
            }
            data.put("todayToSee", houses);
            //推荐
            data.put("recommendHouses", recommendHouses(token));
            //查询开发商直售
            data.put("developer", queryDeveloper());

            //放频道缓存
            vo.setDataSet(data);
            //未缓存的展位名称
            //vo = commonService.getIndexDatas(condition);
        } catch (Exception ex) {
            ex.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 查询开发商直售
     * @return
     */
    private List<Map<Object,Object>> queryDeveloper() {
        List<Map<Object,Object>> datas = null;
        //自定义查询列名
        Map<Object, Object> condition = Maps.newHashMap();
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//主键ID
        queryColumn.add("PROJECT_CODE projectCode");//项目编号
        queryColumn.add("PROJECT_NAME projectName"); //房源主图
        queryColumn.add("DEVELOPERS developers");//开发商
        queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
        queryColumn.add("CITY city");//城市
        queryColumn.add("COMMUNITY community");//社区
        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
        queryColumn.add("PROJECT_MAIN_IMG projectMainImg");//项目主图
        queryColumn.add("DELIVERY_TIME deliveryTime");//交房时间
        queryColumn.add("MIN_HOUSE_RENT minHouseRent");//最低租金/或出售价
        queryColumn.add("MAX_HOUSE_RENT maxHouseRent");//最高租金/或出售价
        condition.put("queryColumn",queryColumn);
        condition.put("isDel", 0);
        condition.put("pageIndex", 1);
        condition.put("pageSize", 10);
        try {
            ResultVo newBuildingVo = housesService.selectCustomColumnNamesList(HsHouseNewBuilding.class, condition);
            if(newBuildingVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return datas;
            }
            datas = (List<Map<Object, Object>>) newBuildingVo.getDataSet();
            if(datas== null || datas.size() == 0){
                return Lists.newArrayList();
            }
            for (Map<Object, Object> data : datas) {
                String projectMainImg = StringUtil.trim(data.get("projectMainImg"));
                if(StringUtil.hasText(projectMainImg)){
                    data.put("projectMainImg",ImageUtil.IMG_URL_PREFIX+projectMainImg);
                }
            }
        } catch (Exception e) {
            logger.warn("exterior Remote call to queryDeveloper fails"+e.getMessage());
        }
        return datas;
    }

    /**
     * 推荐房源
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> recommendHouses(String token) throws Exception {
        Map<Object, Object> queryFilter = new HashMap<Object, Object>(16);
        Map<Object, Object> condition = new HashMap<>(16);
        Map<String, Object> result = new HashMap<>(2);
        //推荐出租信息
        List<Map<Object, Object>> rent = new ArrayList<>();
        //推荐出售信息
        List<Map<Object, Object>> sell = new ArrayList<>();
        //出租房源列表
        ResultVo rentResultVo = new ResultVo();
        //出售房源列表
        ResultVo sellResultVo = new ResultVo();
        //首页默认显示  0出租 1出售
        int type = 0;

        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//主键ID
        queryColumn.add("HOUSE_NAME houseName");//房源ID
        queryColumn.add("HOUSE_MAIN_IMG houseMainImg"); //房源主图
        queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
        queryColumn.add("CITY city");//城市
        queryColumn.add("COMMUNITY community");//社区
        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
        queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
        queryColumn.add("ADDRESS address");//所在位置
        queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
        //审核通过
        condition.put("isCheck", 1);
        //未锁定
        condition.put("isLock", 0);
        //未删除
        condition.put("isDel", 0);
        //房源状态：0>已提交 1审核通过
        condition.put("houseStatus", 1);
        condition.put("pageIndex", 1);
        condition.put("pageSize", 5);
        condition.put("queryColumn", queryColumn);
        //查询的房源结果
        ResultVo recommendHousesVo = null;
        //如果用户已登录
        if (StringUtil.hasText(token)){
            //区号
            String areaCode = RequestUtil.analysisToken(token).getAreaCode();
            queryFilter.clear();
            //自定义查询的列名
            List<String> queryColumnHousesSubscribe = new ArrayList<>();
            queryColumnHousesSubscribe.add("MIN_HOUSE_AREA minHouseArea");
            queryColumnHousesSubscribe.add("MAX_HOUSE_AREA maxHouseArea");
            queryColumnHousesSubscribe.add("MIN_PRICE minPrice");
            queryColumnHousesSubscribe.add("MAX_PRICE maxPrice");
            queryColumnHousesSubscribe.add("MIN_BEDROOM minBedroom");
            queryColumnHousesSubscribe.add("MAX_BEDROOM maxBedroom");
            queryColumnHousesSubscribe.add("MIN_BATHROOM minBathroom");
            queryColumnHousesSubscribe.add("MAX_BATHROOM maxBathroom");
            //房屋类型（0 房屋出租 1房屋出售）
            queryColumnHousesSubscribe.add("HOUSE_TYPE houseType");
            queryFilter.put("queryColumn", queryColumnHousesSubscribe);
            queryFilter.put("memberId", RequestUtil.analysisToken(token).getUserId());
            //查询订阅记录
            ResultVo subscribeVo = memberService.selectCustomColumnNamesList(HsMemberHousesSubscribe.class, queryFilter);
            //查询订阅信息成功且有订阅信息
            boolean isSubscribe = true;
            //请求成功
            if (subscribeVo != null && subscribeVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && subscribeVo.getDataSet() != null) {
                List<Map<Object, Object>> subscribeList = (List<Map<Object, Object>>) subscribeVo.getDataSet();
                if (subscribeList.size() > 0) {
                    //已注册 有订阅信息

                    //出租订阅信息列表
                    List<Map<Object, Object>> rentList = subscribeList.stream().parallel().filter(map ->
                            StringUtil.getAsInt(map.get("houseType") == null ? "-1" : StringUtil.trim(map.get("houseType"))) == 0
                    ).collect(Collectors.toList());

                    //出售订阅信息列表
                    List<Map<Object, Object>> sellList = subscribeList.stream().parallel().filter(map ->
                            StringUtil.getAsInt(map.get("houseType") == null ? "-1" : StringUtil.trim(map.get("houseType"))) == 1
                    ).collect(Collectors.toList());
                    //如果出售的订阅信息比较多前台默认展示出售列表
                    if(sellList.size() > rentList.size()){
                        type = 1;
                    }
                    //根据订阅信息获取出租/出售信息
                    condition.put("leaseType", 0);
                    rentResultVo = searchHouses(rentList,condition);
                    condition.put("leaseType", 1);
                    sellResultVo = searchHouses(sellList,condition);
                }else{
                    //已注册 没有订阅信息
                    isSubscribe = false;
                }
            }else{
                //已注册 查询订阅信息失败
                isSubscribe = false;
            }
            //已注册  获取订阅信息失败，或者没有订阅信息
            if(!isSubscribe){
                //获取用户浏览记录
                //是否有浏览记录
                boolean isHistory = true;
                //会员ID
                String memberId = RequestUtil.analysisToken(token).getUserId();
                Map<Object,Object> map = new HashMap<>(16);
                map.put("memberId",memberId);
                map.put("pageIndex",1);
                map.put("pageSize",10);
                ResultVo historyResultVo = memberService.selectList(new HsHousesBrowseHistory(), map, 0);
                if(historyResultVo != null && historyResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && historyResultVo.getDataSet() != null){
                    List<Map<Object, Object>> historyList = (List<Map<Object, Object>>) historyResultVo.getDataSet();
                    if(historyList.size() > 0){
                        // 有浏览记录 老用户
                        // 根据最近的十条浏览记录来判断用户主要查看的是出租房产还是出售房产，

                        //出租浏览记录列表
                        List<Map<Object, Object>> rentList = historyList.stream().parallel().filter(history ->
                                StringUtil.getAsInt(history.get("houseType") == null ? "-1" : StringUtil.trim(history.get("houseType"))) == 0
                        ).collect(Collectors.toList());
                        //出售浏览记录列表
                        List<Map<Object, Object>> sellList = historyList.stream().parallel().filter(history ->
                                StringUtil.getAsInt(history.get("houseType") == null ? "-1" : StringUtil.trim(history.get("houseType"))) == 1
                        ).collect(Collectors.toList());
                        if(sellList.size() > rentList.size()){
                            type = 1;
                        }
                        //根据浏览记录获取出租/出售信息
                        condition.put("leaseType", 0);
                        rentResultVo = getHouseByHistory(rentList,condition);
                        condition.put("leaseType", 1);
                        sellResultVo = getHouseByHistory(sellList,condition);

                    }else{
                        isHistory = false;
                    }
                }else{
                    isHistory = false;
                }
                if(!isHistory){
                    //没有浏览记录或者获取浏览记录失败 新用户
                    //阿拉伯联合酋长国  首页默认随机推荐5套不同户型和区域的出租类房产，页面默认停留在出租上
                    if (!areaCode.equals("971")) {
                        //默认显示出租
                        type = 1;
                    }
                    //推荐浏览记录较多的
                    condition.put("orderBy", "BROWSE_COUNT");
                    condition.put("leaseType", 0);
                    //按浏览量进行推荐
                    rentResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                    condition.put("leaseType", 1);
                    sellResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
                }

            }
        }else {
            //用户未登录
            //推荐浏览记录较多的
            condition.put("orderBy", "BROWSE_COUNT");
            condition.put("leaseType", 0);
            //按浏览量进行推荐
            rentResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
            condition.put("leaseType", 1);
            sellResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, condition);
        }

        //封装推荐出租信息
        if(rentResultVo != null && rentResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object, Object>> rentList = (List<Map<Object, Object>>) rentResultVo.getDataSet();
            condition.put("leaseType", 0);
            rent = completion(rentList,condition);
        }
        //封装推荐出售信息
        if(sellResultVo != null && sellResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
            List<Map<Object, Object>> sellList = (List<Map<Object, Object>>) sellResultVo.getDataSet();
            condition.put("leaseType", 1);
            sell = completion(sellList,condition);
        }
        result.put("type",type);
        result.put("rent",rent);
        result.put("sell",sell);
        return result;
    }

    /**
     * 获取的推荐数量小于5条（符合条件的出租信息不足），按照浏览量补齐5条
     * @param list
     * @param condition
     * @return
     * @throws Exception
     */
    public List<Map<Object, Object>> completion(List<Map<Object, Object>> list,Map<Object, Object> condition) throws Exception {
        HashMap<Object, Object> conditionMap = new HashMap<>();
        conditionMap.putAll(condition);
        if(list == null){
            list = new ArrayList<>();
        }
        if(list.size() < 5){
            //已有的房源id
            List<Integer> rentIds = list.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("id")))).collect(Collectors.toList());
            conditionMap.put("orderBy", "BROWSE_COUNT");
            ResultVo browseResultVo = housesService.selectCustomColumnNamesList(HsMainHouse.class, conditionMap);
            if(browseResultVo != null && browseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && browseResultVo.getDataSet() != null){
                List<Map<Object, Object>> browseList = (List<Map<Object, Object>>) browseResultVo.getDataSet();
                for (Map<Object, Object> map : browseList) {
                    int id = StringUtil.getAsInt(StringUtil.trim(map.get("id")));
                    if(rentIds.contains(id)){
                        continue;
                    }
                    list.add(map);
                }
            }
        }
        return list;
    }

    /**
     * 根据浏览记录查询符合条件的房源
     * 根据 城市(CITY) 期望价格(HOUSE_RENT) 面积(HOUSE_ACREAGE) 进行推荐
     * @param list
     * @return
     * @throws Exception
     */
    public ResultVo getHouseByHistory(List<Map<Object, Object>> list,Map<Object, Object> condition) throws Exception {
        ResultVo result = new ResultVo();
        HashMap<Object, Object> conditionMap = new HashMap<>();
        conditionMap.putAll(condition);
        //获取房源信息
        List<Integer> ids = new ArrayList<>();
        for (Map<Object, Object> map : list) {
            int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")));
            ids.add(houseId);
        }
        ResultVo housesResultVo = housesService.selectList(new HsMainHouse(), conditionMap, 0);
        if(housesResultVo == null || housesResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || housesResultVo.getDataSet() == null){
            //查询房源信息失败
            return result;
        }
        List<Map<Object, Object>> housesList = (List<Map<Object, Object>>) housesResultVo.getDataSet();
        if(housesList.size() < 1){
            //根据浏览记录未查询到房源信息
            return result;
        }
        //根据 城市(CITY) 期望价格(HOUSE_RENT) 面积(HOUSE_ACREAGE) 进行推荐
        List<BigDecimal> houseRentList = new ArrayList<>();
        List<BigDecimal> houseAcreageList = new ArrayList<>();
        Map<String,Integer> cityMap = new HashMap<>(10);
        for (Map<Object, Object> houseMap : housesList) {
            //城市出现的次数
            String city = StringUtil.trim(houseMap.get("city"));
            if(cityMap.containsKey(city)){
                Integer count = cityMap.get(city);
                cityMap.put(city,count++);
            }else{
                cityMap.put(city,1);
            }
            BigDecimal houseRent = new BigDecimal(StringUtil.trim(houseMap.get("houseRent")));
            houseRentList.add(houseRent);
            BigDecimal houseAcreage = new BigDecimal(StringUtil.trim(houseMap.get("houseAcreage")));
            houseAcreageList.add(houseAcreage);
        }
        //按照次数降序排序
        Map<String, Integer> descMap = new LinkedHashMap<>();
        cityMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEachOrdered(e -> descMap.put(e.getKey(), e.getValue()));
        Map.Entry<String, Integer> stringIntegerEntry = descMap.entrySet().stream().findFirst().get();
        //最近10条浏览记录中出现城市最多的城市
        String city = stringIntegerEntry.getKey();

        //最小价格
        BigDecimal rentMin = houseRentList.stream().parallel().min((a, b) -> a.compareTo(b)).get();
        //最大价格
        BigDecimal rentMax = houseRentList.stream().parallel().max((a, b) -> a.compareTo(b)).get();
        //最小房屋面积
        BigDecimal acreageMin = houseAcreageList.stream().parallel().min((a, b) -> a.compareTo(b)).get();
        //最大房屋面积
        BigDecimal acreageMax = houseAcreageList.stream().parallel().max((a, b) -> a.compareTo(b)).get();
        //搜索对应房源信息
        HouseSearchCondition searchCondition = new HouseSearchCondition();
        searchCondition.setMinPrice(rentMin.intValue());
        searchCondition.setMaxPrice(rentMax.intValue());
        searchCondition.setMinArea(acreageMin.intValue());
        searchCondition.setMaxArea(acreageMax.intValue());
        searchCondition.setPageSize(5);
        searchCondition.setCity(city);

        ResultVo searchVo = searchService.query(searchCondition);
        if (searchVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
            List<Integer> houseIds = (List<Integer>) searchVo.getDataSet();
            if (houseIds != null && houseIds.size() > 0) {
                conditionMap.put("houseIds", houseIds);
                return housesService.selectCustomColumnNamesList(HsMainHouse.class, conditionMap);
            }
        }

        return result;
    }


    /**
     * 根据订阅信息查询房源信息
     * @param subscribeList
     * @return
     */
    public ResultVo searchHouses(List<Map<Object, Object>> subscribeList,Map<Object, Object> condition) throws Exception {
        ResultVo result = new ResultVo();
        HashMap<Object, Object> conditionMap = new HashMap<>();
        conditionMap.putAll(condition);
        //最小价格
        int minPrice = 10000;
        //最大价格
        int maxPrice = 0;
        //最小面积
        int minArea = 10000;
        //最大面积
        int maxArea = 0;
        //最小卧室数量
        int minBedroom = 10000;
        //最大卧室数量
        int maxBedroom = 0;
        //最小浴室数量
        int minBathroom = 10000;
        //最大浴室数量
        int maxBathroom = 0;
        for (Map<Object, Object> subscribe : subscribeList) {
            int _minPrice = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minPrice")));
            int _maxPrice = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxPrice")));
            int _minArea = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minHouseArea")));
            int _maxArea = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxHouseArea")));
            int _minBedroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minBedroom")));
            int _maxBedroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxBedroom")));
            int _minBathroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("minBathroom")));
            int _maxBathroom = StringUtil.getAsInt(StringUtil.trim(subscribe.get("maxBathroom")));
            if (minPrice >= _minPrice) {
                minPrice = _minPrice;
            }
            if (_maxPrice >= maxPrice) {
                minPrice = _maxPrice;
            }
            if (minArea >= _minArea) {
                minArea = _minArea;
            }
            if (_maxArea >= maxArea) {
                maxArea = _maxArea;
            }
            if (minBedroom >= _minBedroom) {
                minBedroom = _minBedroom;
            }
            if (_maxBedroom >= maxBedroom) {
                maxBedroom = _maxBedroom;
            }
            if (minBathroom >= _minBathroom) {
                minBathroom = _minBathroom;
            }
            if (_maxBathroom >= maxBathroom) {
                maxBathroom = _maxBathroom;
            }
        }

        //搜索对应房源信息
        HouseSearchCondition searchCondition = new HouseSearchCondition();
        searchCondition.setMinPrice(minPrice);
        searchCondition.setMaxPrice(maxPrice);
        searchCondition.setMinArea(minArea);
        searchCondition.setMaxArea(maxArea);
        searchCondition.setMinBedroom(minBedroom);
        searchCondition.setMaxBedroom(maxBedroom);
        searchCondition.setMinBathroom(minBathroom);
        searchCondition.setMaxBathroom(maxBathroom);
        searchCondition.setPageSize(5);

        ResultVo searchVo = searchService.query(searchCondition);
        if (searchVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
            List<Integer> houseIds = (List<Integer>) searchVo.getDataSet();
            if (houseIds != null && houseIds.size() > 0) {
                conditionMap.put("houseIds", houseIds);
                return housesService.selectCustomColumnNamesList(HsMainHouse.class, conditionMap);
            }
        }
        return result;
    }

    /**
     * 获取支持的城市信息
     *
     * @return
     */
    @Override
    public ResultVo getSupportCities(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//城市id
            queryColumn.add("PID pid");//父级城市id
            queryColumn.add("CITY_CODE cityCode");//行政单位中文名
            queryColumn.add("CITY_NAME_EN cityNameEn");//行政单位英文名
            queryColumn.add("CITY_NAME_CN cityNameCn");//行政单位中文名
            queryColumn.add("CITY_LONGITUDE longitude");//google地图经度
            queryColumn.add("CITY_LATITUDE latitude");//google地图纬度
            queryColumn.add("LEVEL level");//行政级别 1:市 2:社区 3:子社区
            condition.put("queryColumn", queryColumn);
            condition.putAll(condition);
            //查询支持的所有城市信息
            List<Map<String, Object>> perms =commonService.findAllCity(condition);
            vo = ResultVo.success();
            vo.setDataSet(perms);
            queryColumn = null;
        } catch (Exception e) {
            logger.warn("exterior Remote call to getSupportCities fails"+e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            condition = null;
            return vo;
        }
    }



}
