package www.ucforward.com.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.binding.ObjectBinding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utils.StringUtil;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dao.*;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.*;

@Service("housesService")
public class HousesServiceImpl<T> implements HousesService<T> {
    protected final static Logger logger = Logger.getLogger(HousesServiceImpl.class);

    @Resource
    private HsOwnerHousingApplicationDao ownerHousingApplyDao;
    @Resource
    private HsOwnerHousingApplicationLogDao ownerHousingApplyLogDao;
    @Resource
    private HsHouseImgDao houseImgDao;
    @Resource
    private HsHouseLogDao houseLogDao;
    @Resource
    private HsMainHouseDao mainHouseDao;
    @Resource
    private HsHouseComplainDao houseComplainDao;
    @Resource
    private HsHouseComplainLogDao houseComplainLogDao;
    @Resource
    private HsHouseEvaluationDao houseEvaluationDao;
    @Resource
    private HsHouseCredentialsDataDao hsHouseCredentialsDataDao;
    @Resource
    private HsHouseAutoReplySettingDao hsHouseAutoReplySettingDao;
    @Resource
    private HsHouseKeyCasesDao hsHouseKeyCasesDao;
    @Resource
    private HsHouseProgressDao hsHouseProgressDao;
    @Resource
    private DictHouseProgressDao dictHouseProgressDao;
    @Resource
    private HsHouseNewBuildingMemberApplyDao hsHouseNewBuildingMemberApplyDao;
    @Resource
    private HsHouseNewBuildingDao hsHouseNewBuildingDao;
    @Resource
    private HsHouseObtainedDao hsHouseObtainedDao;

    //处理消息
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResultVo delete(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsOwnerHousingApplication) {//
                result = ownerHousingApplyDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsOwnerHousingApplicationLog) {
                result = ownerHousingApplyLogDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsHouseImg) {
                result = houseImgDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsHouseLog) {
                result = houseLogDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsMainHouse) {
                result = mainHouseDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsHouseComplain) {
                result = houseComplainDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsHouseAutoReplySetting) {
                result = hsHouseAutoReplySettingDao.deleteByPrimaryKey(id);
            }
            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }
        } catch (Exception e) {
            logger.error("HousesServiceImpl delete Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo insert(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsOwnerHousingApplication) {//
                result = ownerHousingApplyDao.insert((HsOwnerHousingApplication) o);
            } else if (o instanceof HsOwnerHousingApplicationLog) {
                result = ownerHousingApplyLogDao.insert((HsOwnerHousingApplicationLog) o);
            } else if (o instanceof HsHouseImg) {
                result = houseImgDao.insert((HsHouseImg) o);
            } else if (o instanceof HsHouseLog) {
                result = houseLogDao.insert((HsHouseLog) o);
            } else if (o instanceof HsMainHouse) {
                result = mainHouseDao.insert((HsMainHouse) o);
            } else if (o instanceof HsHouseComplain) {
                result = houseComplainDao.insert((HsHouseComplain) o);
            } else if (o instanceof HsHouseComplainLog) {
                result = houseComplainLogDao.insert((HsHouseComplainLog) o);
            } else if (o instanceof HsHouseEvaluation) {
                result = houseEvaluationDao.insert((HsHouseEvaluation) o);
            } else if (o instanceof HsHouseCredentialsData) {
                result = hsHouseCredentialsDataDao.insert((HsHouseCredentialsData) o);
            } else if (o instanceof HsHouseAutoReplySetting) {
                result = hsHouseAutoReplySettingDao.insert((HsHouseAutoReplySetting) o);
            }else if (o instanceof HsHouseProgress) {
                result = hsHouseProgressDao.insert((HsHouseProgress) o);
            }else if (o instanceof HsHouseNewBuildingMemberApply) {
                result = hsHouseNewBuildingMemberApplyDao.insert((HsHouseNewBuildingMemberApply) o);
            }else if (o instanceof HsHouseNewBuilding) {
                result = hsHouseNewBuildingDao.insert((HsHouseNewBuilding) o);
            }else if (o instanceof HsHouseObtained) {
                result = hsHouseObtainedDao.insert((HsHouseObtained) o);
            }


            if (result > 0) {//操作成功
                vo.setDataSet(o);
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl insert Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo select(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        Object obj = null;
        try {
            if (o instanceof HsOwnerHousingApplication) {//
                obj = ownerHousingApplyDao.selectByPrimaryKey(id);
            } else if (o instanceof HsOwnerHousingApplicationLog) {
                obj = ownerHousingApplyLogDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHouseImg) {
                obj = houseImgDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHouseLog) {
                obj = houseLogDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMainHouse) {
                obj = mainHouseDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHouseComplain) {
                obj = houseComplainDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHouseAutoReplySetting) {
                obj = hsHouseAutoReplySettingDao.selectByPrimaryKey(id);
            }else if (o instanceof HsHouseNewBuildingMemberApply) {
                obj = hsHouseNewBuildingMemberApplyDao.selectByPrimaryKey(id);
            }else if (o instanceof HsHouseNewBuilding) {
                obj = hsHouseNewBuildingDao.selectByPrimaryKey(id);
            }else if (o instanceof HsHouseEvaluation) {
                obj = houseEvaluationDao.selectByPrimaryKey(id);
            }else if (o instanceof HsHouseObtained) {
                obj = hsHouseObtainedDao.selectByPrimaryKey(id);
            }
            vo.setDataSet(obj);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        } catch (Exception e) {
            logger.error("HousesServiceImpl select Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 查询列表数据
     *
     * @param o          查询的实体，用于控制查询的dao
     * @param condition  查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @throws Exception
     */
    @Override
    public ResultVo selectList(Object o, Map<Object, Object> condition, int returnType) throws Exception {
        ResultVo vo = new ResultVo();
        List<Object> data = null;
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            if (o instanceof HsOwnerHousingApplication) {//
                result = ownerHousingApplyDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsOwnerHousingApplicationLog) {
                //data = ownerHousingApplyLogDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHouseImg) {
                result = houseImgDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsHouseLog) {
                result = houseLogDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsMainHouse) {
                result = mainHouseDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsHouseEvaluation) {//房源评价
                result = houseEvaluationDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsHouseAutoReplySetting) {//房源自动应答设置
                result = hsHouseAutoReplySettingDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsHouseNewBuilding) {
                //新楼盘，开发商直售
                result = hsHouseNewBuildingDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsHouseComplain) { //房源投诉
                result = houseComplainDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsHouseEvaluation) { //房源评价
                result = houseEvaluationDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsHouseNewBuildingMemberApply) { //房源评价
                result = hsHouseNewBuildingMemberApplyDao.selectListByCondition(condition, returnType);
            }

            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        } catch (Exception e) {
            logger.error("HousesServiceImpl selectList Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 自定义查询列数据
     *
     * @param t         查询的实体类
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo selectCustomColumnNamesList(T t, Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            if (t.hashCode() == HsMainHouse.class.hashCode()) {//
                result = mainHouseDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsHouseEvaluation.class.hashCode()) {//
                result = houseEvaluationDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsOwnerHousingApplication.class.hashCode()) {
                result = ownerHousingApplyDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsHouseKeyCases.class.hashCode()) {
                result = hsHouseKeyCasesDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsOwnerHousingApplicationLog.class.hashCode()) {
                result = ownerHousingApplyLogDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsHouseProgress.class.hashCode()) {
                result = hsHouseProgressDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsHouseCredentialsDataDao.class.hashCode()) {
                result = hsHouseCredentialsDataDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsHouseNewBuilding.class.hashCode()) {
                result = hsHouseNewBuildingDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsHouseNewBuildingMemberApply.class.hashCode()) {
                result = hsHouseNewBuildingMemberApplyDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsHouseObtained.class.hashCode()) {
                result = hsHouseObtainedDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsHouseAutoReplySetting.class.hashCode()) {
                //hsHouseAutoReplySettingDao.selectCustomColumnNamesList(condition);
            }

            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl selectCustomColumnNamesList Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo update(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsOwnerHousingApplication) {//
                result = ownerHousingApplyDao.updateByPrimaryKeySelective((HsOwnerHousingApplication) o);
            } else if (o instanceof HsOwnerHousingApplicationLog) {
                result = ownerHousingApplyLogDao.updateByPrimaryKeySelective((HsOwnerHousingApplicationLog) o);
            } else if (o instanceof HsHouseImg) {
                result = houseImgDao.updateByPrimaryKeySelective((HsHouseImg) o);
            } else if (o instanceof HsHouseLog) {
                result = houseLogDao.updateByPrimaryKeySelective((HsHouseLog) o);
            } else if (o instanceof HsMainHouse) {
                result = mainHouseDao.updateByPrimaryKeySelective((HsMainHouse) o);
            } else if (o instanceof HsHouseCredentialsData) {
                result = hsHouseCredentialsDataDao.updateByPrimaryKeySelective((HsHouseCredentialsData) o);
            } else if (o instanceof HsHouseComplain) {
                result = houseComplainDao.updateByPrimaryKey((HsHouseComplain) o);
            } else if (o instanceof HsHouseAutoReplySetting) {
                result = hsHouseAutoReplySettingDao.updateByPrimaryKey((HsHouseAutoReplySetting) o);
            }else if (o instanceof HsHouseObtained) {
                result = hsHouseObtainedDao.updateByPrimaryKey((HsHouseObtained) o);
            }else if (o instanceof HsHouseNewBuildingMemberApply) {
                result = hsHouseNewBuildingMemberApplyDao.updateByPrimaryKey((HsHouseNewBuildingMemberApply) o);
            }else if (o instanceof HsHouseNewBuilding) {
                result = hsHouseNewBuildingDao.updateByPrimaryKey((HsHouseNewBuilding) o);
            }
            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl update Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 获取房源位置
     *
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> loadHousesPosition(Map<Object, Object> condition) {
        if (condition == null && condition.size() == 0) return null;
        return mainHouseDao.loadHousesPosition(condition);
    }

    /**
     * 审核房源申请
     */
    @Transactional
    @Override
    public ResultVo checkHousingApplyTx(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        int state = -1;//
        int houseId = 0;
        try {
            int isCheck = StringUtil.getAsInt(StringUtil.trim(condition.get("isCheck")));
            String remarks = StringUtil.trim(condition.get("remarks"));
            //业主申请ID
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            //获取房源列表;
            HsOwnerHousingApplication housingApply = ownerHousingApplyDao.selectByPrimaryKey(id);
            if (housingApply == null) {
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return vo;
            }
            if(isCheck != 3){
                if (!StringUtil.hasText(StringUtil.trim(housingApply.getAppointmentDoorTime()))) {
                    vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    vo.setMessage("请先完善获取房源时间");
                    return vo;
                }
                if (!StringUtil.hasText(StringUtil.trim(housingApply.getAppointmentMeetPlace()))) {
                    vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    vo.setMessage("请先完善见面地点");
                    return vo;
                }
            }
            //获取申请类型（0：自主完善，1：联系客服上传，2：业务员上传）
            Integer applyType = housingApply.getApplyType();
            HsOwnerHousingApplication _new = new HsOwnerHousingApplication();
            _new.setId(id);
            _new.setIsCheck(isCheck);
            _new.setRemarks(remarks);
            state = ownerHousingApplyDao.updateByPrimaryKeySelective(_new);
            if (state <= 0) {//修改失败
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return vo;
            }
            Date date = new Date();
            if(isCheck != 3){
                //插入房源信息表
                HsMainHouse mainHouse = new HsMainHouse();
                RequestUtil.copy(housingApply, mainHouse);
                mainHouse.setId(null);
                mainHouse.setApplyId(id);//房源申请ID
                String houseCode = "HC_" + RandomUtils.getRandomNumbersAndLetters(32);
                mainHouse.setHouseCode(houseCode);//房源编号
                //mainHouse.setHouseName(housingApply.getHou);//房源名称
                //  mainHouse.setIsTemporary(0);//是否临时数据
                mainHouse.setIsCheck(0);
                mainHouse.setCreateTime(date);
                mainHouse.setUpdateTime(date);
                state = mainHouseDao.insertSelective(mainHouse);
                if (state <= 0) {
                    new RuntimeException();
                }
                houseId = mainHouse.getId();
                /**
                 * 获取自动应答信息，如果存在自动应答信息，将房源id更新到自动应答表
                 */
                condition.clear();
                condition.put("applyId",id);
                condition.put("isDel",0);
                Map<Object, Object> replySettingMap = hsHouseAutoReplySettingDao.selectListByCondition(condition, 1);
                List<HsHouseAutoReplySetting> replySettings = (List<HsHouseAutoReplySetting>) replySettingMap.get("data");
                if(replySettings != null && replySettings.size()>0){
                    List<HsHouseAutoReplySetting> _replySettings = Lists.newArrayListWithCapacity(replySettings.size());
                    HsHouseAutoReplySetting _replySetting = null;
                    for (HsHouseAutoReplySetting replySetting : replySettings) {
                        _replySetting = new HsHouseAutoReplySetting();
                        _replySetting.setId(replySetting.getId());
                        _replySetting.setHouseId(houseId);
                        _replySettings.add(_replySetting);
                    }
                    //自动应答不为空，更新房源id houseId
                    hsHouseAutoReplySettingDao.batchUpdate(_replySettings);
                }
                /**
                 * 修改房源图片id
                 */
                condition.clear();
                condition.put("applyId",id);
                Map<Object, Object> credentialsMap = hsHouseCredentialsDataDao.selectListByCondition(condition, 1);
                if(credentialsMap !=null){
                    List<HsHouseCredentialsData> credentialsDataList = (List<HsHouseCredentialsData>) credentialsMap.get("data");
                    if(credentialsDataList != null && credentialsDataList.size() > 0){
                        HsHouseCredentialsData credentialsData = credentialsDataList.get(0);
                        credentialsData.setHouseId(houseId);
                        hsHouseCredentialsDataDao.updateByPrimaryKey(credentialsData);
                    }
                }
                //为业主添加钥匙
                HsHouseKeyCases houseKeyCases = new HsHouseKeyCases();
                houseKeyCases.setHouseId(houseId);//房源ID
                houseKeyCases.setHouseCode(houseCode);//房源编号
                houseKeyCases.setMemberId(housingApply.getMemberId());//业主ID
                houseKeyCases.setIsExpire(0);//未过期
                houseKeyCases.setCreateTime(date);
                houseKeyCases.setRemarks("业主创建钥匙");
                state = hsHouseKeyCasesDao.addHouseKeys(houseKeyCases);
            }
            //添加日志信息
            HsOwnerHousingApplicationLog log = new HsOwnerHousingApplicationLog();
            log.setApplyId(id);
            if (houseId != 0) {
                log.setHouseId(houseId);
            }
            log.setNodeType(1);
            log.setCreateTime(date);
            if(isCheck == 3){
                log.setRemarks(remarks);
            }else{
                log.setRemarks("客服初审房源");
            }
            log.setOperatorType(3);
            state = ownerHousingApplyLogDao.insert(log);
            if (state < 0) {
                new RuntimeException();
            }
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);

            condition.clear();
            //返回房源Id
            condition.put("houseId", houseId);
            condition.put("applyId", id);
            condition.put("appointmentDoorTime", housingApply.getAppointmentDoorTime());//预约上门获取房源时间
            condition.put("appointmentMeetPlace", housingApply.getAppointmentMeetPlace());//见面地点
            condition.put("contactName", housingApply.getContacts());//联系人
            vo.setDataSet(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl checkHousingApplyTx Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 客服审核房源信息
     *
     * @param condition
     * @return
     */
    @Override
    @Transactional
    public ResultVo checkHouse(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //人员id
            String memberId = condition.get("userId").toString();
            condition.remove("userId");
            //房源
            //房源ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            //获取房源列表;
            HsMainHouse house = mainHouseDao.selectByPrimaryKey(houseId);
            if (house == null) {//房源数据异常
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            //获取申请类型（0：自主完善，1：联系客服上传，2：业务员上传）
            if (!StringUtil.hasText(StringUtil.trim(house.getHouseName()))) {
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, "请完善房源名称");
            }
            if(house.getIsCheck() == 1){
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION,"此房源已审核");
            }
            HsMainHouse mainHouse = new HsMainHouse();
            StringBuffer sb = new StringBuffer();
            sb.append("L");
            String city = house.getCity();
            String community = house.getCommunity();
            List<Map<Object, Object>> citys = RedisUtil.safeGet(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY);
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
            Map<Object, Object> housingTypelist = RedisUtil.safeGet("SYS_HOUSING_TYPE_DICTCODE");
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
            mainHouse.setHouseCode(sb.toString());
            mainHouse.setId(houseId);
            mainHouse.setVersionNo(house.getVersionNo());
            mainHouse.setIsCheck(1);
            mainHouse.setHouseStatus(1);//房源上架
            mainHouse.setRemark("客户审核房源并上传。。。");
            //mainHouse.setStandby1(house.getHouseRent().toString());
            int state = mainHouseDao.updateByPrimaryKeySelective(mainHouse);
            if (state > 0) {

                Date date = new Date();
                //添加日志记录
                HsHouseLog houseLog = new HsHouseLog();
                houseLog.setHouseId(houseId);
                houseLog.setNodeType(1);//审核通过
                houseLog.setCreateTime(date);
                houseLog.setRemarks("客户审核房源并上传。。。");
                int insert = houseLogDao.insert(houseLog);

                //记录房源进度 发布房源
                //1.封装进度信息
                HsHouseProgress hsHouseProgress = new HsHouseProgress();
                //房源ID
                hsHouseProgress.setHouseId(houseId);
                //创建人
                hsHouseProgress.setCreateBy(Integer.parseInt(memberId));
                //进度
                hsHouseProgress.setProgressCode("102");
                //创建日期
                hsHouseProgress.setCreateTime(date);
                //3.插入数据
                hsHouseProgressDao.insert(hsHouseProgress);

                //创建索引
                HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, 0);
                kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));

                //添加积分
                //查询第一个获取钥匙的业务员
                condition.clear();
                condition.put("houseId",houseId);
                Map<Object,Object> houseKeyMap = hsHouseKeyCasesDao.selectFirstGetHouseKey(condition);
                vo  = ResultVo.success();
                vo.setDataSet(houseKeyMap);
                return vo;

            }
            house = null;
            mainHouse = null;
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } catch (Exception e) {
            logger.error("HousesServiceImpl checkHousingApplyTx Exception message:" + e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Override
    @Transactional
    public ResultVo auditFailure(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        try {
            condition.remove("userId");
            //房源
            //房源ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            //获取房源列表;
            HsMainHouse house = mainHouseDao.selectByPrimaryKey(houseId);
            if (house == null) {
                //房源数据异常
                return ResultVo.error(ResultConstant.HOUSES_DATA_EXCEPTION, ResultConstant.HOUSES_DATA_EXCEPTION_VALUE);
            }
            HsMainHouse mainHouse = new HsMainHouse();
            mainHouse.setId(houseId);
            mainHouse.setVersionNo(house.getVersionNo());
            mainHouse.setIsCheck(2);
            mainHouse.setRemark(StringUtil.trim(condition.get("remarks")));
            int state = mainHouseDao.updateByPrimaryKeySelective(mainHouse);
            if (state > 0) {
                Date date = new Date();
                //添加日志记录
                HsHouseLog houseLog = new HsHouseLog();
                houseLog.setHouseId(houseId);
                houseLog.setNodeType(1);//审核通过
                houseLog.setCreateTime(date);
                houseLog.setRemarks(StringUtil.trim(condition.get("remarks")));
                return vo;

            }
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Override
    public ResultVo updateRecord(Object obj) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;
        try {
            if (obj instanceof HsMainHouse) {
                result = mainHouseDao.updateHousesRecord((HsMainHouse) obj);
            }
            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl updateRecord Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Override
    public ResultVo getHousingApplicationCount() throws Exception {
        ResultVo result = new ResultVo();
        try {
            int count = mainHouseDao.selectHousingApplicationCount();
            result.setDataSet(count);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl getHousingApplicationCount Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }

    @Override
    public ResultVo batchInsert(Object o, Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsHouseAutoReplySetting) {
                List<HsHouseAutoReplySetting> list = (List<HsHouseAutoReplySetting>) condition.get("list");
                result = hsHouseAutoReplySettingDao.batchInsert(list);
            }

            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
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

    @Override
    public ResultVo batchUpdate(Object o, Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        int result = -1;
        if (o instanceof HsHouseAutoReplySetting) {
            List<HsHouseAutoReplySetting> list = (List<HsHouseAutoReplySetting>) condition.get("list");
            result = hsHouseAutoReplySettingDao.batchUpdate(list);
        }
        //更新失败
        if (result < 1) {
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return vo;
    }

    @Override
    public ResultVo selectDataByCondition(Object obj, Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            if (obj instanceof HsHouseCredentialsData) {
                HsHouseCredentialsData hsHouseCredentialsData = hsHouseCredentialsDataDao.selectCredentialsDataByCondition(condition);
                result.setDataSet(hsHouseCredentialsData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HousesServiceImpl selectDataByCondition Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 根据条件查询主房源信息条数
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo selectHouseCountByCondition(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int count = mainHouseDao.selectHouseCountByCondition(condition);
            result.setDataSet(count);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HousesServiceImpl selectHsMainHouseCountByCondition Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return result;
    }

    /**
     * 业务员获取房源钥匙
     *
     * @param houseKeyCases
     * @return
     */
    @Override
    public ResultVo getHouseKeys(HsHouseKeyCases houseKeyCases) {
        ResultVo vo = new ResultVo();
        int i = hsHouseKeyCasesDao.addHouseKeys(houseKeyCases);
        if (i != 0) {
            vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }else{//记录钥匙在平台
            Integer userId = houseKeyCases.getUserId();
            if(userId!=null){
                //业务员扫码,修改房源信息,钥匙在平台
                HsMainHouse house = new HsMainHouse();
                house.setId(houseKeyCases.getHouseId());
                house.setHaveKey(1);//是否有钥匙：0>无,1有
                mainHouseDao.updateByPrimaryKeySelective(house);
            }
        }
        return vo;
    }

    /**
     * 检查房源钥匙是否过期
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo checkKeyIsExpire(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> result = hsHouseKeyCasesDao.checkKeyIsExpire(condition);
        int count = StringUtil.getAsInt(StringUtil.trim(result.get("count")), -1);
        if (count > 0) {//钥匙未过期证明没扫
            vo.setResult(ResultConstant.KEY_IS_EXPIRED_ERROR);
            vo.setMessage(ResultConstant.KEY_IS_NOT_EXPIRED_ERROR_VALUE);
        } else {
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.KEY_IS_EXPIRED_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取房源钥匙数据
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo loadMyHousekeys(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //房源ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(condition.get("houseId")));
            Map<Object, Object> queryFilter = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID houseKeyId");
            queryColumn.add("HOUSE_ID houseId");
            queryFilter.put("queryColumn", queryColumn);
            queryFilter.putAll(condition);
            //查询业务员所拥有的房源钥匙
            Map<Object, Object> map = hsHouseKeyCasesDao.selectCustomColumnNamesList(queryFilter);
            List<Map<Object, Object>> houseKeylist = (List<Map<Object, Object>>) map.get("data");
            if (houseKeylist == null || houseKeylist.size() == 0) {
                return ResultVo.success();
            }
            List<Integer> houseIds = Lists.newArrayList();
            for (Map<Object, Object> houseKey : houseKeylist) {
                houseIds.add(StringUtil.getAsInt(StringUtil.trim(houseKey.get("houseId"))));
            }
            //查询对应的房源信息
            queryFilter.clear();
            queryColumn.clear();
            queryColumn.add("ID houseId");//主键ID
            queryColumn.add("HOUSE_CODE houseCode");//房源编号
            queryColumn.add("HOUSE_NAME houseName");//房源ID
            queryColumn.add("LEASE_TYPE leaseType");//预约类型（0：出租，1：出售）
            queryColumn.add("HOUSE_MAIN_IMG houseMainImg");//房源主图
            queryColumn.add("HOUSE_RENT houseRent");//租金/或出售价
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("HOUSE_ACREAGE houseAcreage");//房屋面积
            queryColumn.add("ADDRESS address");//所在位置
            queryFilter.put("queryColumn", queryColumn);
            queryFilter.put("houseIds", houseIds);
            Map<Object, Object> houseMap = mainHouseDao.selectCustomColumnNamesList(queryFilter);
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseMap.get("data");
            vo = ResultVo.success();
            if (houseList != null && houseList.size() > 0) {
                for (Map<Object, Object> houseKey : houseKeylist) {
                    int houseId1 = StringUtil.getAsInt(StringUtil.trim(houseKey.get("houseId")));
                    for (Map<Object, Object> house : houseList) {
                        if (houseId1 == StringUtil.getAsInt(StringUtil.trim(house.get("houseId")))) {
                            houseKey.putAll(house);
                            houseKey.put("houseMainImg", ImageUtil.IMG_URL_PREFIX + houseKey.get("houseMainImg"));
                            break;
                        }
                    }
                }
                vo.setDataSet(houseKeylist);
            }
        } catch (Exception e) {
            logger.error("HousesServiceImpl loadMyHousekeys Exception message:" + e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Override
    public ResultVo selectProgressInfoByCondition(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            result = ownerHousingApplyLogDao.selectProgressInfoByCondition(condition);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
        } catch (Exception e) {
            logger.error("HousesServiceImpl selectProgressInfoByCondition Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 获取进度流程
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> findProgressList(String type){
        return dictHouseProgressDao.findProgressList(type);
    }

    /**
     * 获取进度列表
     * @param condition
     * @return
     */
    @Override
    public List<Map<String,Object>> findProgress(Map<Object, Object> condition){
        return dictHouseProgressDao.findProgress(condition);
    }

    @Override
    public List<Map<Object,Object>> getProgress(Map<Object,Object> condition){
        return hsHouseProgressDao.getProgress(condition);
    }

    @Override
    public List<Map<Object,Object>> getPropertyArea() {
        return hsHouseNewBuildingDao.getPropertyArea();
    }

    @Override
    public List<Map<Object,Object>> getDevelopers(){
        return hsHouseNewBuildingDao.getDevelopers();
    }

    @Override
    public int updateByCondition(Map<Object,Object> condition){
        return hsHouseProgressDao.updateByCondition(condition);
    }
}
