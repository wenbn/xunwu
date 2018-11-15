package www.ucforward.com.serviceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utils.StringUtil;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dao.*;
import www.ucforward.com.entity.*;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.utils.ImageUtil;
import www.ucforward.com.vo.ResultVo;
import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("commonService")
public class CommonServiceImpl<T> implements CommonService<T>{

    protected final static Logger logger = Logger.getLogger(CommonServiceImpl.class);

    @Autowired
    private HsAdvertDataDao hsAdvertDataDao;
    @Autowired
    private HsArticleDataDao hsArticleDataDao;
    @Autowired
    private HsBoothAdvertRelDao hsBoothAdvertRelDao;
    @Autowired
    private HsBoothArticleRelDao hsBoothArticleRelDao;
    @Autowired
    private HsBoothChannelRelDao hsBoothChannelRelDao;
    @Autowired
    private HsBoothDao hsBoothDao;
    @Autowired
    private HsBoothHouseRelDao hsBoothHouseRelDao;
    @Autowired
    private HsChannelDao hsChannelDao;
    @Autowired
    private HsSysDictcodeGroupDao hsSysDictcodeGroupDao;
    @Autowired
    private HsSysDictcodeItemDao hsSysDictcodeItemDao;
    @Autowired
    private HsSupportCityDao hsSupportCityDao;
    @Autowired
    private HsRegionCodeDao hsRegionCodeDao;
    @Autowired
    private HsMsgRecordDao hsMsgRecordDao;
    @Autowired
    private HsMsgSettingDao hsMsgSettingDao;
    @Autowired
    private HsGoldRuleDao hsGoldRuleDao;
    @Autowired
    private HsSysPlatformSettingDao hsSysPlatformSettingDao;
    @Autowired
    private HsMsgSysDao hsMsgSysDao;


    @Override
    public ResultVo delete(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsAdvertData) {//
                result = hsAdvertDataDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsArticleData) {
                result = hsArticleDataDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsBoothAdvertRel) {
                result = hsBoothAdvertRelDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsBoothArticleRel) {
                result = hsBoothArticleRelDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsBoothChannelRel) {
                result = hsBoothChannelRelDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsBooth) {
                result = hsBoothDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsBoothHouseRel) {
                result = hsBoothHouseRelDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsChannel) {
                result = hsChannelDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSysDictcodeGroup) {
                result = hsSysDictcodeGroupDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSysDictcodeItem) {
                result = hsSysDictcodeItemDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSupportCity) {
                result = hsSupportCityDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsRegionCodeDao) {
                result = hsRegionCodeDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsGoldRule) {
                result = hsGoldRuleDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSysPlatformSetting) {
                result = hsSysPlatformSettingDao.deleteByPrimaryKey(id);
            }
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo insert(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsAdvertData) {//
                result = hsAdvertDataDao.insert((HsAdvertData)o);
            } else if (o instanceof HsArticleData) {
                result = hsArticleDataDao.insert((HsArticleData)o);
            } else if (o instanceof HsBoothAdvertRel) {
                result = hsBoothAdvertRelDao.insert((HsBoothAdvertRel)o);
            } else if (o instanceof HsBoothArticleRel) {
                result = hsBoothArticleRelDao.insert((HsBoothArticleRel)o);
            } else if (o instanceof HsBoothChannelRel) {
                result = hsBoothChannelRelDao.insert((HsBoothChannelRel)o);
            } else if (o instanceof HsBooth) {
                result = hsBoothDao.insert((HsBooth)o);
            } else if (o instanceof HsBoothHouseRel) {
                result = hsBoothHouseRelDao.insert((HsBoothHouseRel)o);
            } else if (o instanceof HsChannel) {
                result = hsChannelDao.insert((HsChannel)o);
            } else if (o instanceof HsSysDictcodeGroup) {
                result = hsSysDictcodeGroupDao.insert((HsSysDictcodeGroup)o);
            } else if (o instanceof HsSysDictcodeItem) {
                result = hsSysDictcodeItemDao.insert((HsSysDictcodeItem)o);
            } else if (o instanceof HsSupportCity) {
                result = hsSupportCityDao.insert((HsSupportCity)o);
            } else if (o instanceof HsRegionCodeDao) {
                result = hsRegionCodeDao.insert((HsRegionCode) o);
            }else if (o instanceof HsMsgSetting) {
                result = hsMsgSettingDao.insert((HsMsgSetting) o);
            } else if (o instanceof HsGoldRule) {
                result = hsGoldRuleDao.insert((HsGoldRule) o);
            } else if (o instanceof HsSysPlatformSetting) {
                result = hsSysPlatformSettingDao.insert((HsSysPlatformSetting) o);
            }else if (o instanceof HsMsgRecord) {
                result = hsMsgRecordDao.insert((HsMsgRecord) o);
            }else if (o instanceof HsMsgSys) {
                result = hsMsgSysDao.insert((HsMsgSys) o);
            }
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo select(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        Object obj = null;
        try{
            if (o instanceof HsAdvertData) {//
                obj = hsAdvertDataDao.selectByPrimaryKey(id);
            } else if (o instanceof HsArticleData) {
                obj = hsArticleDataDao.selectByPrimaryKey(id);
            } else if (o instanceof HsBoothAdvertRel) {
                obj = hsBoothAdvertRelDao.selectByPrimaryKey(id);
            } else if (o instanceof HsBoothArticleRel) {
                obj = hsBoothArticleRelDao.selectByPrimaryKey(id);
            } else if (o instanceof HsBoothChannelRel) {
                obj = hsBoothChannelRelDao.selectByPrimaryKey(id);
            } else if (o instanceof HsBooth) {
                obj = hsBoothDao.selectByPrimaryKey(id);
            } else if (o instanceof HsBoothHouseRel) {
                obj = hsBoothHouseRelDao.selectByPrimaryKey(id);
            } else if (o instanceof HsChannel) {
                obj = hsChannelDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSysDictcodeGroup) {
                obj = hsSysDictcodeGroupDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSysDictcodeItem) {
                obj = hsSysDictcodeItemDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSupportCity) {
                obj = hsSupportCityDao.selectByPrimaryKey(id);
            } else if (o instanceof HsRegionCodeDao) {
                obj = hsRegionCodeDao.selectByPrimaryKey(id);
            } else if (o instanceof HsGoldRule) {
                obj = hsGoldRuleDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSysPlatformSetting) {
                obj = hsSysPlatformSettingDao.selectByPrimaryKey(id);
            }
            vo.setDataSet(obj);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        }catch (Exception e){
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    /**
     * 查询列表数据
     * @param o 查询的实体，用于控制查询的dao
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo selectList(Object o, Map<Object, Object> condition,int returnType) throws Exception {
        ResultVo vo = new ResultVo();
        List<Object> data = null;
        Map<Object,Object> result = new HashMap<Object,Object>();
        try{
            if (o instanceof HsAdvertData) {//
                //data =  hsAdvertDataDao.selectListByCondition(condition);
//            } else if (t instanceof HsArticleData) {
//                data =  hsArticleDataDao.selectListByCondition(condition);
//            } else if (t instanceof HsBoothAdvertRel) {
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
//            } else if (t instanceof HsBoothArticleRel) {
//                vo.setDataSet(hsBoothArticleRelDao.selectByPrimaryKey(id));
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
//            } else if (t instanceof HsBoothChannelRel) {
//                vo.setDataSet(hsBoothChannelRelDao.selectByPrimaryKey(id));
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
//            } else if (t instanceof HsBooth) {
//                vo.setDataSet(hsBoothDao.selectByPrimaryKey(id));
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
//            } else if (t instanceof HsBoothHouseRel) {
//                vo.setDataSet(hsBoothHouseRelDao.selectByPrimaryKey(id));
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
//            } else if (t instanceof HsChannel) {
//                vo.setDataSet(hsChannelDao.selectByPrimaryKey(id));
//                data =  hsBoothAdvertRelDao.selectListByCondition(condition);
            } else if (o instanceof HsSysDictcodeGroup) {
                result = hsSysDictcodeGroupDao.selectListByCondition(condition,returnType);
            } else if (o instanceof HsSysDictcodeItem) {
                result =  hsSysDictcodeItemDao.selectListByCondition(condition,returnType);
            } else if (o instanceof HsSupportCity) {
                result = hsSupportCityDao.selectListByCondition(condition,returnType);
            } else if (o instanceof HsRegionCodeDao) {
                result = hsRegionCodeDao.selectListByCondition(condition,returnType);
            }else if (o instanceof HsMsgRecord) {
                result = hsMsgRecordDao.selectListByCondition(condition,returnType);
            }else if (o instanceof HsMsgSetting) {
                result = hsMsgSettingDao.selectListByCondition(condition,returnType);
            }
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        }catch (Exception e){
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo update(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsAdvertData) {//
                result = hsAdvertDataDao.updateByPrimaryKeySelective((HsAdvertData)o);
            } else if (o instanceof HsArticleData) {
                result = hsArticleDataDao.updateByPrimaryKeySelective((HsArticleData)o);
            } else if (o instanceof HsBoothAdvertRel) {
                result = hsBoothAdvertRelDao.updateByPrimaryKeySelective((HsBoothAdvertRel)o);
            } else if (o instanceof HsBoothArticleRel) {
                result = hsBoothArticleRelDao.updateByPrimaryKeySelective((HsBoothArticleRel)o);
            } else if (o instanceof HsBoothChannelRel) {
                result = hsBoothChannelRelDao.updateByPrimaryKeySelective((HsBoothChannelRel)o);
            } else if (o instanceof HsBooth) {
                result = hsBoothDao.updateByPrimaryKeySelective((HsBooth)o);
            } else if (o instanceof HsBoothHouseRel) {
                result = hsBoothHouseRelDao.updateByPrimaryKeySelective((HsBoothHouseRel)o);
            } else if (o instanceof HsChannel) {
                result = hsChannelDao.updateByPrimaryKeySelective((HsChannel)o);
            } else if (o instanceof HsSysDictcodeGroup) {
                result = hsSysDictcodeGroupDao.updateByPrimaryKeySelective((HsSysDictcodeGroup)o);
            } else if (o instanceof HsSysDictcodeItem) {
                result = hsSysDictcodeItemDao.updateByPrimaryKeySelective((HsSysDictcodeItem)o);
            } else if (o instanceof HsSupportCity) {
                result = hsSupportCityDao.updateByPrimaryKeySelective((HsSupportCity) o);
            }else if (o instanceof HsMsgSetting) {
                result = hsMsgSettingDao.updateByPrimaryKeySelective((HsMsgSetting) o);
            } else if (o instanceof HsGoldRule) {
                result = hsGoldRuleDao.updateByPrimaryKeySelective((HsGoldRule) o);
            } else if (o instanceof HsSysPlatformSetting) {
                result = hsSysPlatformSettingDao.updateByPrimaryKeySelective((HsSysPlatformSetting) o);
            }
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public Integer updateCustomColumnByCondition(Object o,Map<Object,Object> condition){
        int result = -1;
        if(o instanceof HsMsgRecord){
            result = hsMsgRecordDao.updateCustomColumnByCondition(condition);
        }
        return result;
    }


    /**
     * 自定义查询列数据
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo selectCustomColumnNamesList(T t, Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object,Object> result = new HashMap<Object,Object>();
        try{
            if (t.hashCode() ==  HsRegionCode.class.hashCode()) {//
                result = hsRegionCodeDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsSupportCity.class.hashCode()) {//
                result = hsSupportCityDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsChannel.class.hashCode()) {//
                result = hsChannelDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsBooth.class.hashCode()) {//
                result = hsBoothDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsBoothChannelRel.class.hashCode()) {//
                result = hsBoothChannelRelDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsAdvertData.class.hashCode()) {//
                result = hsAdvertDataDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsArticleData.class.hashCode()) {//
                result = hsArticleDataDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsBoothAdvertRel.class.hashCode()) {//
                result = hsBoothAdvertRelDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsBoothArticleRel.class.hashCode()) {//
                result = hsBoothArticleRelDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsSysDictcodeGroup.class.hashCode()) {//
                result = hsSysDictcodeGroupDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsSysDictcodeItem.class.hashCode()) {//
                result = hsSysDictcodeItemDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() ==  HsGoldRule.class.hashCode()) {
                result = hsGoldRuleDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() ==  HsSysPlatformSetting.class.hashCode()) {
                result = hsSysPlatformSettingDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsMsgRecord.class.hashCode()) {
                result = hsMsgRecordDao.selectCustomColumnNamesList(condition);
            }
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("HousesServiceImpl selectCustomColumnNamesList Exception message:"+e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    /**
     * 批量插入数据
     * @param t
     * @param data
     * @return
     */
    @Override
    public ResultVo batchInsert(T t, List<T> data) {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (t.hashCode() == HsBoothChannelRel.class.hashCode()) {
                result = hsBoothChannelRelDao.batchInsert(data);
            }else if (t.hashCode() == HsBoothAdvertRel.class.hashCode()) {
                result = hsBoothAdvertRelDao.batchInsert(data);
            }else if (t.hashCode() == HsBoothArticleRel.class.hashCode()) {
                result = hsBoothArticleRelDao.batchInsert(data);
            }

            if (result <= 0) {//操作失败
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl batchInsert Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 批量删除数据,只有关联表才做删除
     * @param t
     * @param condition
     * @return
     */
    @Override
    public ResultVo batchDelete(T t, Map<Object,Object> condition) {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if(condition==null){
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return vo;
            }
            if (t.hashCode() == HsBoothChannelRel.class.hashCode()) {
                result = hsBoothChannelRelDao.batchDelete(condition);
            }else if(t.hashCode() == HsBoothAdvertRel.class.hashCode()){
                result = hsBoothAdvertRelDao.batchDelete(condition);
            }else if(t.hashCode() == HsBoothArticleRel.class.hashCode()){
                result = hsBoothArticleRelDao.batchDelete(condition);
            }
            if (result <= 0) {//操作失败
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl batchInsert Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Transactional
    @Override
    public int testTx() throws Exception {
        HsChannel hsChannel = new HsChannel();
        hsChannel.setChannelAliasName("kdkdkdk");
        int state = hsChannelDao.insertSelective(hsChannel);
        state = 1/0;
        HsBooth hsBooth = new HsBooth();
        state = hsBoothDao.insertSelective(hsBooth);
        return state;
    }

    @Override
    public ResultVo getIndexDatas(Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object,Object> data = new HashMap<Object,Object>();
        Map<Object,Object> queryFilter = new HashMap<Object,Object>();//查询条件
        if(condition==null){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return vo;
        }
        List<String> boothAliasNames = (List<String>) condition.get("boothAliasName");
        List<Map<Object,Object>> hsChannels = hsChannelDao.selectChannelsByCondition(condition);
        if(hsChannels == null){
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return vo;
        }
        List<String> channel_ids = new ArrayList<String>();
        for (Map<Object, Object> map : hsChannels) {
            channel_ids.add(StringUtil.trim(map.get("id")));
        }
        queryFilter.clear();
        queryFilter.put("channelIds", channel_ids);//频道ids
        queryFilter.put("boothState", 0);//是否启用
        queryFilter.put("isDel", 0);//是否删除
        //查询展位信息
        List<Map<Object, Object>> booths = hsBoothDao.getBoothsByChannelIds(queryFilter);
        List<String> advert_booth_ids = new ArrayList<String>();//广告展位ids
        List<String> article_booth_ids = new ArrayList<String>();//文章展位ids
        List<String> friendly_link_booth_ids = new ArrayList<String>();//友情链接
        List<String> house_rent_booth_ids = new ArrayList<String>();//房屋出租
        List<String> house_sell_booth_ids = new ArrayList<String>();//房屋出售
        List<String> new_house_booth_ids = new ArrayList<String>();//预售商品ids
        for (Map<Object, Object> booth : booths) {//判断展位类型（ 1：广告，2：文章，3：友情链接，4：出租，5：出售，6：新楼盘）
            String boothType = StringUtil.trim(booth.get("boothType"));
            String boothId = StringUtil.trim(booth.get("boothId"));
            if (boothType.equals("1")) {
                advert_booth_ids.add(boothId);
            } else if (boothType.equals("2")) {
                article_booth_ids.add(boothId);
            } else if (boothType.equals("3")) {
                friendly_link_booth_ids.add(boothId);
            } else if (boothType.equals("4")) {
                house_rent_booth_ids.add(boothId);
            } else if (boothType.equals("5")) {
                house_sell_booth_ids.add(boothId);
            } else if (boothType.equals("6")) {
                new_house_booth_ids.add(boothId);
            }
        }

        //查询对应展位类型绑定的数据
        if (advert_booth_ids != null && advert_booth_ids.size() > 0) {
            condition.clear();
            condition.put("ids", advert_booth_ids);//ids
            condition.put("isDel", 0);//是否删除
            condition.put("adState", 1);//是否禁用
            List<Map<Object, Object>> adverts = hsAdvertDataDao.selectAdvertDataByCondition(condition);
            if(adverts!=null && adverts.size()>0){
                for (Map<Object, Object> booth : booths) {//将广告内容填充到展位中
                    List<Map<Object, Object>> advertList = new ArrayList<Map<Object, Object>>();
                    for (Map<Object, Object> advert : adverts) {
                        Map<Object, Object> _advert = new HashMap<Object, Object>();
                        if (StringUtil.trim(booth.get("boothId")).equals(StringUtil.trim(advert.get("boothId")))) {
                            _advert.put("adImgUrl", ImageUtil.IMG_URL_PREFIX+advert.get("adImgUrl"));
                            advertList.add(_advert);
                        }
                    }
                    booth.put("advertList", advertList);
                }
                advert_booth_ids.clear();
            }
        }

        for (Map<Object, Object> map : hsChannels) {
            List<Map<Object, Object>> boothList = new ArrayList<Map<Object, Object>>();
            for (Map<Object, Object> booth : booths) {
                if (StringUtil.trim(map.get("id")).equals(StringUtil.trim(booth.get("channelId")))) {
                    boothList.add(booth);
                }
            }
            if (boothList != null && boothList.size() > 0) {
                map.put("boothList", boothList);
            }
        }
        vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
        vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        vo.setDataSet(hsChannels);
        return vo;
    }

    //获取频道下的所有展位别名
    @Override
    public List<Map<Object,Object>> getChannelBoothAliasName(Map<Object, Object> condition) throws Exception {
        return hsBoothDao.getBoothByChannelAliasName(condition);
    }

    @Override
    public Map<Object, Object> getBoothDataByCondition(Map<Object, Object> condition) {
        Map<Object, Object> data = new HashMap<Object, Object>();
        //查询展位信息
        List<Map<Object, Object>> booths = hsBoothDao.getBoothsByCondition(condition);
        List<String> advert_booth_ids = new ArrayList<String>();//广告展位ids
        List<String> article_ids = new ArrayList<String>();
/*        List<String> buyer_rent_question_article_booth_ids = new ArrayList<String>();//买家出租问题文章展位ids
        List<String> buyer_rentout_question_article_booth_ids = new ArrayList<String>();//买家出售问题文章展位ids
        List<String> seller_rent_question_article_booth_ids = new ArrayList<String>();//卖家出租问题问题文章展位ids
        List<String> seller_rentout_question_article_booth_ids = new ArrayList<String>();//卖家出售问题文章展位ids*/
        List<String> friendly_link_booth_ids = new ArrayList<String>();//友情链接
        List<String> house_rent_booth_ids = new ArrayList<String>();//房屋出租
        List<String> house_sell_booth_ids = new ArrayList<String>();//房屋出售
        List<String> new_house_booth_ids = new ArrayList<String>();//预售商品ids
        for (Map<Object, Object> booth : booths) {//判断展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：首页FAQ，8：新楼盘）
            String boothType = StringUtil.trim(booth.get("boothType"));
            String boothId = StringUtil.trim(booth.get("boothId"));
            if (boothType.equals("1")) {
                advert_booth_ids.add(boothId);
            } else if (boothType.equals("2") || boothType.equals("3") || boothType.equals("4") || boothType.equals("5") || boothType.equals("7")) {
                article_ids.add(boothId);
            } else if (boothType.equals("6")) {
                friendly_link_booth_ids.add(boothId);
            } else if (boothType.equals("7")) {
                //house_rent_booth_ids.add(boothId);
            } else if (boothType.equals("8")) {
                house_sell_booth_ids.add(boothId);
            } else if (boothType.equals("9")) {
                new_house_booth_ids.add(boothId);
            }


        }

        //查询广告展位类型绑定的数据
        if (advert_booth_ids != null && advert_booth_ids.size() > 0) {
            condition.clear();
            condition.put("ids", advert_booth_ids);//ids
            condition.put("isDel", 0);//是否删除
            condition.put("adState", 1);//是否禁用
            List<Map<Object, Object>> adverts = hsAdvertDataDao.selectAdvertDataByCondition(condition);
            if(adverts!=null && adverts.size()>0){
                for (Map<Object, Object> booth : booths) {//将广告内容填充到展位中
                    String boothId = StringUtil.trim(booth.get("boothId"));
                    if(!advert_booth_ids.contains(boothId)){//
                        continue;
                    }
                    List<Map<Object, Object>> advertList = new ArrayList<Map<Object, Object>>();
                    for (Map<Object, Object> advert : adverts) {
                        Map<Object, Object> _advert = new HashMap<Object, Object>();
                        if (StringUtil.trim(booth.get("boothId")).equals(StringUtil.trim(advert.get("boothId")))) {
                            _advert.put("adImgUrl", ImageUtil.IMG_URL_PREFIX+advert.get("adImgUrl"));
                            advertList.add(_advert);
                        }
                    }
                    if(advertList!=null){
                        booth.put("advertList", advertList);
                    }
                    data.put(booth.get("boothAliasName"),booth);
                }
                advert_booth_ids.clear();
            }
        }
        //查询广告展位类型绑定的数据
        if (article_ids != null && article_ids.size() > 0) {
            condition.clear();
            condition.put("ids", article_ids);//ids
            condition.put("isDel", 0);//是否删除
            condition.put("status", 1);//是否禁用
            List<Map<Object, Object>> articles = hsArticleDataDao.selectArticleDataByCondition(condition);
            if(articles!=null && articles.size()>0){
                for (Map<Object, Object> booth : booths) {//将广告内容填充到展位中
                    String boothId = StringUtil.trim(booth.get("boothId"));
                    if(!article_ids.contains(boothId)){//
                        continue;
                    }
                    List<Map<Object, Object>> articlesList = new ArrayList<Map<Object, Object>>();
                    for (Map<Object, Object> article : articles) {
                        Map<Object, Object> _article = new HashMap<Object, Object>();
                        if (boothId.equals(StringUtil.trim(article.get("boothId")))) {
                            _article.put("articleName",article.get("articleName"));
                            _article.put("articleDesc",article.get("articleDesc"));
                            articlesList.add(_article);
                        }
                    }
                    if(articlesList!=null){
                        booth.put("articlesList", articlesList);
                    }
                    data.put(booth.get("boothAliasName"),booth);
                }
                article_ids.clear();
            }
        }
        return data;
    }

    @Override
    public ResultVo selectAdvertDataByCondition(Map<Object,Object> condition){
        ResultVo result = new ResultVo();
        try {
            List<Map<Object,Object>> list = hsArticleDataDao.selectArticleDataByCondition(condition);
            result.setDataSet(list);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return  result;
    }

    @Override
    public Map<Object, Object> getOwnerArticleDataByCondition(Map<Object, Object> condition) {
        Map<Object, Object> data = new HashMap<Object, Object>();
        //查询展位信息
        List<Map<Object, Object>> booths = hsBoothDao.getBoothsByCondition(condition);
        List<String> article_ids = new ArrayList<String>();
        for (Map<Object, Object> booth : booths) {//判断展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            String boothType = StringUtil.trim(booth.get("boothType"));
            String boothId = StringUtil.trim(booth.get("boothId"));
            if (boothType.equals("4") || boothType.equals("5")) {
                article_ids.add(boothId);
            }
        }
        //查询广告展位类型绑定的数据
        if (article_ids != null && article_ids.size() > 0) {
            condition.clear();
            condition.put("ids", article_ids);//ids
            condition.put("isDel", 0);//是否删除
            condition.put("status", 1);//是否禁用
            List<Map<Object, Object>> articles = hsArticleDataDao.selectArticleDataByCondition(condition);
            if(articles!=null && articles.size()>0){
                for (Map<Object, Object> booth : booths) {//将广告内容填充到展位中
                    String boothId = StringUtil.trim(booth.get("boothId"));
                    if(!article_ids.contains(boothId)){//
                        continue;
                    }
                    List<Map<Object, Object>> articlesList = new ArrayList<Map<Object, Object>>();
                    for (Map<Object, Object> article : articles) {
                        Map<Object, Object> _article = new HashMap<Object, Object>();
                        if (boothId.equals(StringUtil.trim(article.get("boothId")))) {
                            _article.put("articleName",article.get("articleName"));
                            _article.put("articleDesc",article.get("articleDesc"));
                            articlesList.add(_article);
                        }
                    }
                    if(articlesList!=null){
                        booth.put("articlesList", articlesList);
                    }
                    data.put(booth.get("boothAliasName"),booth);
                }
                article_ids.clear();
            }
        }
        return data;
    }

    /**
     * 查询对应城市下的社区信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo findCommunitiesByCityName(Map<Object, Object> condition) throws Exception {
        ResultVo vo = null;
        //自定义查询的列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//城市id
        condition.put("queryColumn", queryColumn);
        Map<Object, Object> cityMaps = hsSupportCityDao.selectCustomColumnNamesList(condition);
        List<Map<Object, Object>> citys = (List<Map<Object, Object>>) cityMaps.get("data");
        if(citys==null || citys.size()<=0){
            return ResultVo.error(ResultConstant.GET_CITY_DATA_ERROR,ResultConstant.GET_CITY_DATA_ERROR_VALUE);
        }
        Map<Object, Object> cityMap =citys.get(0);
        //获取城市ID
        int cityId = StringUtil.getAsInt(StringUtil.trim(cityMap.get("id")));
        Map<Object,Object> queryFilter = Maps.newHashMap();
        queryColumn.clear();
        queryColumn.add("ID id");//城市id
        queryColumn.add("CITY_NAME_EN cityNameEn");//行政单位英文名
        queryColumn.add("CITY_NAME_CN cityNameCn");//行政单位中文名
        queryColumn.add("CITY_LONGITUDE lon");//地图经度
        queryColumn.add("CITY_LATITUDE lat");//地图纬度
        queryFilter.put("queryColumn", queryColumn);
        queryFilter.put("pid",cityId);
        //获取子社区数据
        Map<Object, Object> communityMaps = hsSupportCityDao.selectCustomColumnNamesList(queryFilter);
        List<Map<Object, Object>> communityLists = (List<Map<Object, Object>>) communityMaps.get("data");
        vo = new ResultVo();
        vo.setDataSet(communityLists);
        cityMap = null;
        queryColumn = null;
        queryFilter = null;
        communityMaps = null;
        return vo;
    }

    @Override
    public ResultVo getMsgList(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        List<Map<String, Object>> msgList = hsMsgRecordDao.getMsgList(condition);
        result.setDataSet(msgList);
        return result;
    }

    @Override
    public ResultVo getMsgType(String type) {
        ResultVo result = new ResultVo();
        List<Map<String, Object>> msgList = hsMsgRecordDao.getMsgType(type);
        result.setDataSet(msgList);
        return result;
    }

    @Override
    public Map<Object, Object> getSysMsg(Map<Object, Object> condition) {
        return hsMsgSysDao.getSysMsg(condition);
    }

    @Override
    public List<Map<String, Object>> findAllCity(Map<Object, Object> queryFilter) {
        List<Map<String, Object>> result = null;
        try {
            result = generateOrgMapToTree(Lists.newArrayList(),-1,queryFilter);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("CommonServiceImpl findAllCity Exception message:" + e.getMessage());

        }
        return result;
    }

    /**
     * 递归查询所有子节点
     * @param permMap
     * @param pid
     * @return
     */
    private List<Map<String, Object>> generateOrgMapToTree(List<Map<String, Object>> permMap, Integer pid,Map<Object, Object> condition) {
        if (null == permMap || permMap.size() == 0) {
            Map<Object, Object> resultMap = hsSupportCityDao.selectCustomColumnNamesList(condition);
            permMap= (List<Map<String, Object>>) resultMap.get("data");
        }
        List<Map<String, Object>> orgList = new ArrayList<>();
        if (permMap != null && permMap.size() > 0) {
            for (Map<String, Object> item : permMap) {
                //比较传入pid与当前对象pid是否相等
                if (pid.equals(item.get("pid"))) {
                    //将当前对象id做为pid递归调用当前方法，获取下级结果
                    List<Map<String, Object>> children = generateOrgMapToTree(permMap, Integer.valueOf(item.get("id").toString()),null);
                    //将子结果集存入当前对象的children字段中
                    if(children!=null && children.size()>0){
                        item.put("sub", children);
                    }
                    //添加当前对象到主结果集中
                    orgList.add(item);
                }
            }
        }
        return orgList;
    }
}
