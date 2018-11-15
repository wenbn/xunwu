package www.ucforward.com.utils;

import com.google.common.collect.Maps;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.HsGoldRule;
import www.ucforward.com.entity.HsSysPlatformSetting;
import www.ucforward.com.manager.SystemCacheManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.vo.ResultVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:wenbn
 * Date:2018/1/8
 * Description: 处理缓存的工具类
 */
public class SystemCacheUtil {

    private static final String _systemCacheManager = "systemCacheManager";

    /**
     * 刷新数据字典缓存
     */
    public static void resetSysDictcodeCache(Map<Object,Object> condition) {
        SystemCacheManager systemCacheManager = SpringContextUtils.getBean(_systemCacheManager);
        systemCacheManager.resetSysDictcodeCache(condition);
    }

    public static Map<Object,Object> getCacheDictCodes(){
        //获取缓存
        List<String> dictcodeCaches =RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY);
//        List<String> dictcodeCaches = JsonUtil.json2List(RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY), String.class);
        Map<Object,Object> cacheMap = new HashMap<Object,Object>();
        for (String dictcodeCache : dictcodeCaches) {
            cacheMap.put(dictcodeCache,RedisUtil.safeGet(dictcodeCache));
//            cacheMap.put(dictcodeCache,JsonUtil.parseJSON2List(RedisUtil.safeGet(dictcodeCache)));
        }
        return cacheMap;
    }

    /**
     * 重置积分规则缓存
     */
    public static void resetSysGoldRuleCache() {
        ResultVo vo = null;
        Map<Object, Object> condition = Maps.newHashMap();
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//主键ID
        queryColumn.add("GOLD_CODE goldCode");//积分编码
        queryColumn.add("GOLD_TYPE goldType");//0：推荐并注册会员 1：外获上传任务完成，2：区域长上传任务完成，3：外看任务完成，4：区域长送钥匙完成，5：合同单任务完成 6 第一个业务员获取钥匙 7:业务员评价房源 8：外获超时，订单关闭 9 外看任务超时，订单关闭
        queryColumn.add("GOLD_RULE_NAME_CN goldRuleNameCn");//积分名称中文名
        queryColumn.add("GOLD_RULE_NAME_EN goldRuleNameEn");//积分名称英文名
        queryColumn.add("IS_ADD_SUBTRACT isAddSubtract");//控制加分还是减分 0 ：加分 1减分
        queryColumn.add("IS_SALESMAN isSalesman");//是否是业务员 0 ：业务员 1 会员
        queryColumn.add("SCORE score");//积分值
        queryColumn.add("REMARK remark");//备注描述
        condition.put("queryColumn",queryColumn);
        condition.put("isDel",0);//未删除的
        CommonService commonService = SpringContextUtils.getBean("commonService");
        try {
            vo = commonService.selectCustomColumnNamesList(HsGoldRule.class,condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//请求成功
                List<Map<Object,Object>> golds = (List<Map<Object, Object>>) vo.getDataSet();
                if(golds !=null && golds.size()>0){
                    RedisUtil.safeSet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY,golds,RedisConstant.SYS_GOLD_RULE_CACHE_KEY_TIME);
//                    RedisUtil.safeSet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY,JsonUtil.list2Json(golds),RedisConstant.SYS_GOLD_RULE_CACHE_KEY_TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置平台设置缓存
     */
    public static void resetSysPlatformSettingCache() {
        ResultVo vo = null;
        Map<Object, Object> condition = Maps.newHashMap();
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("CALL_CENTER_ASSISTANT_RATIO callCenterAssistantRatio");//客服任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("SELLER_ASSISTANT_RATIO sellerAssistantRatio");//外获任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("REGION_LEADER_RATIO regionLeaderRatio");//区域长实勘任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("REGION_LEADER_TAKE_KEY_RATIO regionLeaderTakeKeyRatio");//区域长送钥匙任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("BUYER_ASSISTANT_RATIO buyerAssistantRatio");//外看结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("INTERNAL_ASSISTANT_RATIO internalAssistantRatio");//内勤结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("COMPANY_ASSISTANT_RATIO companyAssistantRatio");//公司结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("ELSE_ASSISTANT_RATIO elseAssistantRatio");//其它结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("RENT_MIN rentMin");//出租最低服务费（单位AED）
        queryColumn.add("SELL_MIN sellMin");//出售最低服务费（单位AED）
        queryColumn.add("RENT_PROPORTION rentProportion");//出租服务费比例（百分比）
        queryColumn.add("SELL_PROPORTION sellProportion");//出售服务费比例（百分比）
        condition.put("queryColumn",queryColumn);
        condition.put("isDel",0);//未删除的
        CommonService commonService = SpringContextUtils.getBean("commonService");
        try {
            vo = commonService.selectCustomColumnNamesList(HsSysPlatformSetting.class,condition);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){//请求成功
                List<Map<Object,Object>> settings = (List<Map<Object, Object>>) vo.getDataSet();
                if(settings !=null && settings.size()>0){
                    RedisUtil.safeSet(RedisConstant.SYS_PLATFORM_SETTING_CACHE_KEY,settings.get(0),RedisConstant.SYS_PLATFORM_SETTING_CACHE_KEY_TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
