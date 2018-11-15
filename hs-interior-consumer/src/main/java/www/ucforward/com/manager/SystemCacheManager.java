package www.ucforward.com.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.entity.HsSysDictcodeGroup;
import www.ucforward.com.entity.HsSysDictcodeItem;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RedisUtil;
import www.ucforward.com.vo.ResultVo;

import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/31
 */
@Service("systemCacheManager")
public class SystemCacheManager {

    @Autowired
    private CommonService commonService;

    /**
     * 重置缓存
     * @param condition
     */
    public void resetSysDictcodeCache(Map<Object,Object> condition)  {
        Map<Object,Object> cacheData = new HashMap<Object,Object>();
        condition.put("isDel",0);
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//城市id
            queryColumn.add("GROUP_CODE groupCode");//'字典编码'
            queryColumn.add("GROUP_NAME groupName");//'组名称'
            queryColumn.add("GROUP_NAME_EN GROUP_NAME_EN");//'英文组名称'
            condition.put("queryColumn", queryColumn);
            ResultVo resultVo = commonService.selectCustomColumnNamesList(HsSysDictcodeGroup.class, condition);
            int result = resultVo.getResult();
            if(result == 0){
                List<String> unCacheDictIds = new ArrayList<String>();
                List<String> groupCodes = new ArrayList<String>();
                List<Map<Object,Object>> groups = (List<Map<Object,Object>>) resultVo.getDataSet();
                if(groups == null) return ;
                for (Map<Object, Object> group : groups) {
                    String id = StringUtil.trim(group.get("id"));
                    String groupCode = StringUtil.trim(group.get("groupCode"));
                    //判断是否存在缓存中
//                    if(!RedisUtil.existKey(groupCode)){
//                        unCacheDictcodes.add(id);
//                    }
                    unCacheDictIds.add(id);
                    groupCodes.add(groupCode);
                }
                if(unCacheDictIds!=null && unCacheDictIds.size()>0){
                    queryColumn.clear();
                    queryColumn.add("ID id");//城市id
                    queryColumn.add("GROUP_ID groupId");//绑定hs_sys_dictcode_group的主键
                    queryColumn.add("ITEM_NAME itemName");//'组名称'
                    queryColumn.add("ITEM_VALUE itemValue");//'英文组名称'
                    queryColumn.add("ITEM_VALUE_EN itemValueEn");//'英文组名称'
                    queryColumn.add("SORT sort");//排序
                    queryColumn.add("BACK_IMG backImg");//背景图
                    queryColumn.add("STANDBY1 standby1");//背景图
                    queryColumn.add("STANDBY2 standby2");//背景图
                    condition.put("queryColumn", queryColumn);
                    condition.put("groupIds",unCacheDictIds);
                    ResultVo resultVo1 = commonService.selectCustomColumnNamesList(HsSysDictcodeItem.class, condition);
                    List<Map<Object,Object>> items = (List<Map<Object,Object>> ) resultVo1.getDataSet();
                    List<Map<Object,Object>> group_items = null;
                    for (Map<Object, Object> group : groups) {
                        String id = StringUtil.trim(group.get("id"));
                        String groupCode = StringUtil.trim(group.get("groupCode"));
                        for (Map<Object, Object> item : items) {
                            String groupId = StringUtil.trim(item.get("groupId"));
                            if(id.equals(groupId)){
                                if(group.containsKey("items")){//若存在 ，则继续添加
                                    group_items = (List<Map<Object, Object>>) group.get("items");
                                }else{
                                    group_items = new ArrayList<Map<Object,Object>>();
                                }
                                group_items.add(item);
                                group.put("items",group_items);
                            }
                        }
                        RedisUtil.safeSet(groupCode, group, RedisConstant.SYS_DICTCODE_CACHE_KEY_TIME);
//                        RedisUtil.safeSet(groupCode, JsonUtil.toJson(group), RedisConstant.SYS_DICTCODE_CACHE_KEY_TIME);
                        cacheData.put(groupCode,group);
                    }
                }
                if(groupCodes!=null && groupCodes.size()>0){
                    if(RedisUtil.existKey(RedisConstant.SYS_DICTCODE_CACHE_KEY)) {
                        List<String> dictcodeCaches = RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY);
//                        List<String> dictcodeCaches = JsonUtil.json2List(RedisUtil.safeGet(RedisConstant.SYS_DICTCODE_CACHE_KEY), String.class);
                        if (dictcodeCaches == null) {
                            dictcodeCaches = new ArrayList<String>();
                        }
                        groupCodes.addAll(dictcodeCaches);
                        HashSet h = new HashSet(groupCodes);
                        groupCodes.clear();
                        groupCodes.addAll(h);
                    }
                    RedisUtil.safeSet(RedisConstant.SYS_DICTCODE_CACHE_KEY,groupCodes, RedisConstant.SYS_DICTCODE_CACHE_KEY_TIME);
//                    RedisUtil.safeSet(RedisConstant.SYS_DICTCODE_CACHE_KEY,JsonUtil.toJson(groupCodes), RedisConstant.SYS_DICTCODE_CACHE_KEY_TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
