package www.ucforward.com.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.client.model.RegisterUsers;
import io.swagger.client.model.User;
import javafx.beans.binding.ObjectBinding;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dao.*;
import www.ucforward.com.emchat.api.impl.EasemobIMUsers;
import www.ucforward.com.entity.*;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.HeaderVo;
import www.ucforward.com.vo.PayLoadVo;
import www.ucforward.com.vo.ResultVo;

import java.util.*;

@Service("memberService")
public class MemberServiceImpl<T> implements MemberService<T> {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class); // 日志记录

    @Autowired
    private HsSysUserDao hsSysUserDao;
    @Autowired
    private HsSysPermissionDao hsSysPermissionDao;
    @Autowired
    private HsSysRoleDao hsSysRoleDao;
    @Autowired
    private HsSysRolePermissionDao hsSysRolePermissionDao;
    @Autowired
    private HsSysUserRoleDao hsSysUserRoleDao;
    @Autowired
    private HsMemberDao hsMemberDao;
    @Autowired
    private HsSystemUserOrderTasksDao hsSystemUserOrderTasksDao;
    @Autowired
    private HsSystemUserOrderTasksLogDao hsSystemUserOrderTasksLogDao;
    @Autowired
    private HsSysUserLocationDao hsSysUserLocationDao;

    @Autowired
    private HsHousesBrowseHistoryDao hsHousesBrowseHistoryDao;
    @Autowired
    private HsMemberFavoriteDao hsMemberFavoriteDao;
    @Autowired
    private HsFeedbackDao hsFeedbackDao;
    @Autowired
    private HsMemberHousesSubscribeDao hsMemberHousesSubscribeDao;
    @Autowired
    private HsMemberHousingApplicationDao hsMemberHousingApplicationDao;
    @Autowired
    private HsMemberHousingBargainMessageDao hsMemberHousingBargainMessageDao;

    private EasemobIMUsers easemobIMUsers = new EasemobIMUsers();

    @Autowired
    private HsUserAttendanceSheetDao hsUserAttendanceSheetDao;
    @Autowired
    private HsUserVacateSheetDao hsUserVacateSheetDao;
    @Autowired
    private HsMemberHousingBargainDao hsMemberHousingBargainDao;
    @Autowired
    private HsMemberPurchaseDao hsMemberPurchaseDao;
    @Autowired
    private HsMemberFinancialLoansApplyDao hsMemberFinancialLoansApplyDao;
    @Autowired
    private HsUserGoldLogDao hsUserGoldLogDao;
    @Autowired
    private HsMemberGoldLogDao hsMemberGoldLogDao;
    @Autowired
    private HsMemberDirectPurchaseApplyDao hsMemberDirectPurchaseApplyDao;
    @Autowired
    private HsMemberHousingApplicationMessageDao hsMemberHousingApplicationMessageDao;

    @Override
    public ResultVo delete(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsSysUser) {//
                result = hsSysUserDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSysPermission) {
                result = hsSysPermissionDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsMember) {
                result = hsMemberDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsMemberHousesSubscribe) {
                result = hsMemberHousesSubscribeDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsMemberHousingApplication) {
                result = hsMemberHousingApplicationDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsSysUserLocation) {
                result = hsSysUserLocationDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsMemberHousingBargainMessage) {
                result = hsMemberHousingBargainMessageDao.deleteByPrimaryKey(id);
            }else if (o instanceof HsUserGoldLog) {
                result = hsUserGoldLogDao.deleteByPrimaryKey(id);
            }else if (o instanceof HsMemberGoldLog) {
                result = hsMemberGoldLogDao.deleteByPrimaryKey(id);
            }
            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            logger.error("MemberServiceImpl delete Exception message:" + e.getMessage());
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
            if (o instanceof HsSysUser) {//
                result = hsSysUserDao.insert((HsSysUser) o);
            } else if (o instanceof HsSysPermission) {
                result = hsSysPermissionDao.insert((HsSysPermission) o);
            } else if (o instanceof HsMember) {
                result = hsMemberDao.insert((HsMember) o);
            } else if (o instanceof HsHousesBrowseHistory) {
                result = hsHousesBrowseHistoryDao.insert((HsHousesBrowseHistory) o);
            } else if (o instanceof HsMemberFavorite) {
                result = hsMemberFavoriteDao.insert((HsMemberFavorite) o);
            } else if (o instanceof HsFeedback) {
                result = hsFeedbackDao.insert((HsFeedback) o);
            } else if (o instanceof HsMemberHousesSubscribe) {
                result = hsMemberHousesSubscribeDao.insert((HsMemberHousesSubscribe) o);
            } else if (o instanceof HsMemberHousingApplication) {
                result = hsMemberHousingApplicationDao.insert((HsMemberHousingApplication) o);
            } else if (o instanceof HsSysUserLocation) {
                result = hsSysUserLocationDao.insert((HsSysUserLocation) o);
            } else if (o instanceof HsMemberHousingBargainMessage) {
                result = hsMemberHousingBargainMessageDao.insert((HsMemberHousingBargainMessage) o);
            } else if (o instanceof HsSystemUserOrderTasks) {
                result = hsSystemUserOrderTasksDao.insert((HsSystemUserOrderTasks) o);
            } else if (o instanceof HsSystemUserOrderTasksLog) {
                result = hsSystemUserOrderTasksLogDao.insert((HsSystemUserOrderTasksLog) o);
            } else if (o instanceof HsUserAttendanceSheet) {
                result = hsUserAttendanceSheetDao.insert((HsUserAttendanceSheet) o);
            }else if (o instanceof HsMemberHousingBargain) {
                result = hsMemberHousingBargainDao.insert((HsMemberHousingBargain) o);
            }else if (o instanceof HsMemberPurchase) {
                result = hsMemberPurchaseDao.insert((HsMemberPurchase) o);
            }else if (o instanceof HsSysRole) {
                result = hsSysRoleDao.insert((HsSysRole) o);
            }else if (o instanceof HsSysPermission) {
                result = hsSysPermissionDao.insert((HsSysPermission) o);
            }else if (o instanceof HsMemberFinancialLoansApply) {
                result = hsMemberFinancialLoansApplyDao.insert((HsMemberFinancialLoansApply) o);
            }else if (o instanceof HsUserGoldLog) {
                result = hsUserGoldLogDao.insert((HsUserGoldLog) o);
            }else if (o instanceof HsMemberGoldLog) {
                result = hsMemberGoldLogDao.insert((HsMemberGoldLog) o);
            }else if (o instanceof HsMemberDirectPurchaseApply) {
                result = hsMemberDirectPurchaseApplyDao.insert((HsMemberDirectPurchaseApply) o);
            }else if (o instanceof HsMemberHousingApplicationMessage) {
                result = hsMemberHousingApplicationMessageDao.insert((HsMemberHousingApplicationMessage) o);
            }

            if (result > 0) {//操作成功
                vo.setDataSet(o);
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberServiceImpl insert Exception message:" + e.getMessage());
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
            if (o instanceof HsSysUser) {//
                obj = hsSysUserDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSysPermission) {
                obj = hsSysPermissionDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMember) {
                obj = hsMemberDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHousesBrowseHistory) {
                obj = hsHousesBrowseHistoryDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMemberFavorite) {
                obj = hsMemberFavoriteDao.selectByPrimaryKey(id);
            } else if (o instanceof HsFeedback) {
                obj = hsFeedbackDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMemberHousesSubscribe) {
                obj = hsMemberHousesSubscribeDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMemberHousingApplication) {
                obj = hsMemberHousingApplicationDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSysUserLocation) {
                obj = hsSysUserLocationDao.selectByPrimaryKey(id);
            } else if (o instanceof HsMemberHousingBargainMessage) {
                obj = hsMemberHousingBargainMessageDao.selectByPrimaryKey(id);
            } else if (o instanceof HsSystemUserOrderTasks) {
                obj = hsSystemUserOrderTasksDao.selectByPrimaryKey(id);
            } else if (o instanceof HsUserAttendanceSheet) {
                obj = hsUserAttendanceSheetDao.selectByPrimaryKey(id);
            }else if (o instanceof HsMemberHousingBargain) {
                obj = hsMemberHousingBargainDao.selectByPrimaryKey(id);
            }else if (o instanceof HsMemberPurchase) {
                obj = hsMemberPurchaseDao.selectByPrimaryKey(id);
            }else if (o instanceof HsUserGoldLog) {
                obj = hsUserGoldLogDao.selectByPrimaryKey(id);
            }else if (o instanceof HsMemberGoldLog) {
                obj = hsMemberGoldLogDao.selectByPrimaryKey(id);
            }else if (o instanceof HsMemberFinancialLoansApply) {
                obj = hsMemberFinancialLoansApplyDao.selectByPrimaryKey(id);
            }else if (o instanceof HsMemberDirectPurchaseApply) {
                obj = hsMemberDirectPurchaseApplyDao.selectByPrimaryKey(id);
            }
            vo.setDataSet(obj);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        } catch (Exception e) {
            logger.error("MemberServiceImpl insert Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo select(String id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        Object obj = null;
        try {
            if (o instanceof HsSysRole) {//查询角色
                obj = hsSysRoleDao.selectByPrimaryKey(id);
            }
            vo.setDataSet(obj);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        } catch (Exception e) {
            logger.error("MemberServiceImpl insert Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo selectList(Object o, Map<Object, Object> condition, int returnType) throws Exception {
        ResultVo vo = new ResultVo();
        List<Object> data = null;
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            if (o instanceof HsHousesBrowseHistory) {
                result = hsHousesBrowseHistoryDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsMemberFavorite) {
                result = hsMemberFavoriteDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsMemberHousesSubscribe) {
                result = hsMemberHousesSubscribeDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsMemberHousingApplication) {
                result = hsMemberHousingApplicationDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsSysUserLocation) {
                //result = hsSysUserLocationDao.selectListByCondition(condition,returnType);
            } else if (o instanceof HsMemberHousingBargainMessage) {
                result = hsMemberHousingBargainMessageDao.selectListByCondition(condition,returnType);
            } else if (o instanceof HsSystemUserOrderTasks) {
                result = hsSystemUserOrderTasksDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsMember) {
                result = hsMemberDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsSysUser) {
                result = hsSysUserDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsMemberHousingBargain) {
                result = hsMemberHousingBargainDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsMemberPurchase) {
                result = hsMemberPurchaseDao.selectListByCondition(condition, returnType);
            } else if (o instanceof HsSysRole) {
                result = hsSysRoleDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsSysPermission) {
                result = hsSysPermissionDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsMemberFinancialLoansApply) {
                result = hsMemberFinancialLoansApplyDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsMemberDirectPurchaseApply) {
                result = hsMemberDirectPurchaseApplyDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsMemberHousingApplicationMessage) {
                result = hsMemberHousingApplicationMessageDao.selectListByCondition(condition, returnType);
            }else if (o instanceof HsFeedback) {
                result = hsFeedbackDao.selectListByCondition(condition, returnType);
            }


            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        } catch (Exception e) {
            logger.info("MemberServiceImpl selectList Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 自定义查询列数据
     *
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo selectCustomColumnNamesList(T t, Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
            if (t.hashCode() == HsSystemUserOrderTasks.class.hashCode()) {//
                result = hsSystemUserOrderTasksDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsMemberHousesSubscribe.class.hashCode()) {
                result = hsMemberHousesSubscribeDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsUserAttendanceSheet.class.hashCode()) {//业务员考勤表
                result = hsUserAttendanceSheetDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsUserVacateSheet.class.hashCode()) {//业务员请假记录表
                result = hsUserVacateSheetDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsSysUser.class.hashCode()) {//业务员表
                result = hsSysUserDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsMemberHousingBargainMessage.class.hashCode()) {//会员议价记录表
                result = hsMemberHousingBargainMessageDao.selectCustomColumnNamesList(condition);
            } else if (t.hashCode() == HsMemberHousingApplication.class.hashCode()) {
                result = hsMemberHousingApplicationDao.selectCustomColumnNames(condition);
            } else if (t.hashCode() == HsMember.class.hashCode()) {
                result = hsMemberDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsMemberHousingBargain.class.hashCode()) {
                result = hsMemberHousingBargainDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsSysRole.class.hashCode()) {
                result = hsSysRoleDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsSysPermission.class.hashCode()) {
                result = hsSysPermissionDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsSysRolePermission.class.hashCode()) {
                result = hsSysRolePermissionDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsSysUserRole.class.hashCode()) {
                result = hsSysUserRoleDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() == HsUserGoldLog.class.hashCode()) {
                result = hsUserGoldLogDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsMemberGoldLog.class.hashCode()) {
                result = hsMemberGoldLogDao.selectCustomColumnNamesList(condition);
            }else if (t.hashCode() ==  HsMemberHousingApplicationMessage.class.hashCode()) {
                result = hsMemberHousingApplicationMessageDao.selectCustomColumnNamesList(condition);
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
            if (o instanceof HsSysUser) {//
                result = hsSysUserDao.updateByPrimaryKeySelective((HsSysUser) o);
            } else if (o instanceof HsSysPermission) {
                result = hsSysPermissionDao.updateByPrimaryKeySelective((HsSysPermission) o);
            } else if (o instanceof HsMember) {
                result = hsMemberDao.updateByPrimaryKeySelective((HsMember) o);
            } else if (o instanceof HsHousesBrowseHistory) {
                result = hsHousesBrowseHistoryDao.updateByPrimaryKeySelective((HsHousesBrowseHistory) o);
            } else if (o instanceof HsMemberFavorite) {
                result = hsMemberFavoriteDao.updateByPrimaryKeySelective((HsMemberFavorite) o);
            } else if (o instanceof HsFeedback) {
                result = hsFeedbackDao.updateByPrimaryKeySelective((HsFeedback) o);
            } else if (o instanceof HsMemberHousesSubscribe) {
                result = hsMemberHousesSubscribeDao.updateByPrimaryKeySelective((HsMemberHousesSubscribe) o);
            } else if (o instanceof HsMemberHousingApplication) {
                result = hsMemberHousingApplicationDao.updateByPrimaryKeySelective((HsMemberHousingApplication) o);
            } else if (o instanceof HsSysUserLocation) {
                result = hsSysUserLocationDao.updateByPrimaryKeySelective((HsSysUserLocation) o);
            } else if (o instanceof HsMemberHousingBargainMessage) {
                result = hsMemberHousingBargainMessageDao.updateByPrimaryKeySelective((HsMemberHousingBargainMessage) o);
            } else if (o instanceof HsSystemUserOrderTasks) {
                result = hsSystemUserOrderTasksDao.updateByPrimaryKeySelective((HsSystemUserOrderTasks) o);
            } else if (o instanceof HsUserAttendanceSheet) {
                result = hsUserAttendanceSheetDao.updateByPrimaryKeySelective((HsUserAttendanceSheet) o);
            }else if (o instanceof HsMemberHousingBargain) {
                result = hsMemberHousingBargainDao.updateByPrimaryKeySelective((HsMemberHousingBargain) o);
            }else if (o instanceof HsMemberPurchase) {
                result = hsMemberPurchaseDao.updateByPrimaryKeySelective((HsMemberPurchase) o);
            }else if (o instanceof HsSysRole) {
                result = hsSysRoleDao.updateByPrimaryKeySelective((HsSysRole) o);
            }else if (o instanceof HsMemberFinancialLoansApply) {
                result = hsMemberFinancialLoansApplyDao.updateByPrimaryKeySelective((HsMemberFinancialLoansApply) o);
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
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * @param o
     * @param condition data---》需要插入的数据实体类，
     * @return
     */
    @Override
    public ResultVo batchInsert(Object o, Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsSysUser) {//
                List<HsSysUser> data = (List<HsSysUser>) condition.get("data");
                //result = hsSystemOrderPoolDao.batchInsert(data);
            } else if (o instanceof HsSystemOrderPoolLog) {
                //List<HsSystemOrderPoolLog> data = (List<HsSystemOrderPoolLog>) condition.get("data");
                //result = hsSysPermissionDao.batchInsert(data);
            } else if (o instanceof HsSystemUserOrderTasks) {
                List<HsSystemUserOrderTasks> data = (List<HsSystemUserOrderTasks>) condition.get("data");
                List<HsSystemUserOrderTasksLog> LogData = (List<HsSystemUserOrderTasksLog>) condition.get("logData");
                result = hsSystemUserOrderTasksDao.batchInsert(data);
                if (result > 0) {
                    for (HsSystemUserOrderTasks task : data) {
                        Integer id = task.getId();//任务id
                        Integer poolId = task.getPoolId();//任务id
                        for (HsSystemUserOrderTasksLog log : LogData) {
                            if (log.getPoolId() == poolId) {
                                log.setTaskId(id);
                            }
                        }
                    }
                    int state = hsSystemUserOrderTasksLogDao.batchInsert(LogData);
                }
            } else if (o instanceof HsSystemUserOrderTasksLog) {
                List<HsSystemUserOrderTasksLog> logData = (List<HsSystemUserOrderTasksLog>) condition.get("logData");
                result = hsSystemUserOrderTasksLogDao.batchInsert(logData);
            }else if (o instanceof HsSysRolePermission) {
                List<HsSysRolePermission> data = (List<HsSysRolePermission>) condition.get("data");
                result = hsSysRolePermissionDao.batchInsert(data);
            }else if (o instanceof HsSysUserRole ) {
                List<HsSysUserRole> data = (List<HsSysUserRole>) condition.get("data");
                result = hsSysUserRoleDao.batchInsert(data);
            }else if (o instanceof HsUserGoldLog ) {
                List<HsUserGoldLog> data = (List<HsUserGoldLog>) condition.get("data");
                result = hsUserGoldLogDao.batchInsert(data);
            }else if (o instanceof HsUserVacateSheet ) {
                List<HsUserVacateSheet> data = (List<HsUserVacateSheet>) condition.get("data");
                result = hsUserVacateSheetDao.batchInsert(data);
            }else if (o instanceof HsUserAttendanceSheet ) {
                List<HsUserAttendanceSheet> data = (List<HsUserAttendanceSheet>) condition.get("data");
                result = hsUserAttendanceSheetDao.batchInsert(data);
            }

            if (result > 0) {//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            logger.info("MemberServiceImpl selectList Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    @Override
    public ResultVo batchUpdate(Object o, Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try {
            if (o instanceof HsSystemUserOrderTasks) {
                List<HsSystemUserOrderTasks> list = (List<HsSystemUserOrderTasks>) condition.get("tasksList");
                result = hsSystemUserOrderTasksDao.batchUpdate(list);
            }else if(o instanceof HsSysUser){
                List<HsSysUser> list = (List<HsSysUser>) condition.get("data");
                result = hsSysUserDao.batchUpdate(list);
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
            logger.info("MemberServiceImpl batchUpdate Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 根据条件查询用户信息
     *
     * @param condition
     * @return
     */
    @Override
    public HsSysUser selectHsSysUserByCondition(Map<Object, Object> condition) {
        return hsSysUserDao.selectHsSysUserByCondition(condition);
    }

    /**
     * 根据条件查询用户权限
     *
     * @param condition
     * @return
     */
    @Override
    public List<HsSysPermission> selectPermissionListByUser(Map<Object, Object> condition) {
        //查询用户对应的角色
        List<Map<Object, Object>> userRoles = hsSysRoleDao.selectRolesByUser(condition);
        if (userRoles == null) {
            return null;
        }
        List<String> roleIds = new ArrayList<String>();
        for (Map<Object, Object> userRole : userRoles) {
            roleIds.add(StringUtil.trim(userRole.get("roleId")));
        }
        condition.put("roleIds", roleIds);
        List<HsSysPermission> permissions = hsSysPermissionDao.selectPermissionsByRoleIds(condition);
        return permissions;
    }

    /**
     * 登录
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo login(Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        int language = StringUtil.getAsInt(StringUtil.trim(condition.get("language")));
        String areaCode = StringUtil.trim(condition.get("areaCode"));//区号
        String memberMoble = StringUtil.trim(condition.get("memberMoble"));//手机号
        String invitationCode = StringUtil.trim(condition.get("invitationCode"));//邀请码

        String familyName = StringUtil.trim(condition.get("familyName"));//邀请码
        String name = StringUtil.trim(condition.get("name"));//邀请码

        Map<Object, Object> queryFilter = new HashMap<Object, Object>();
        queryFilter.put("memberMoble", memberMoble);
        HsMember member = hsMemberDao.selectMemberByCondition(queryFilter);
        if (member == null) {
            String member_code = SystemNumberUtil.createCode(ResultConstant.BUS_MEMBER_CODE_VALUE);
            member = new HsMember();
            member.setNickname("sandy_"+RandomUtils.getRandomNumbers(8));
            member.setMemberCode(member_code);
            member.setMemberMoble(memberMoble);
            member.setLanguageVersion(language);
            member.setAreaCode(areaCode);
            member.setCreateTime(vo.getSystemTime());
            member.setFamilyName(familyName);
            member.setName(name);
            int state = hsMemberDao.addMember(member);
            if (state < 1) {
                vo.setResult(-1);
                vo.setMessage("新增会员失败");
                return vo;
            }

            //新增会员成功，判断是否有邀请码，如果有添加积分
            if(StringUtil.hasText(invitationCode)) {//如果有邀请码，则添加业务员积分
                //自定义查询的列名
                condition.clear();
                List<String> queryColumn = new ArrayList<>();
                queryColumn.add("ID userId");//业务员id
                queryColumn.add("GOLD gold");//业务员积分
                condition.put("queryColumn", queryColumn);
                condition.put("usercode",invitationCode);//积分编码
                Map<Object, Object> map = hsSysUserDao.selectCustomColumnNamesList(condition);
                List<Map<Object,Object>> userList = (List<Map<Object, Object>>) map.get("data");
                if(userList!=null && userList.size()==1 ){
                    vo.setDataSet(userList.get(0));
                }
            }

            //注册环信用户

            boolean b = addUser(member_code);
            if (!b) {
                /*vo.setResult(-1);
                vo.setMessage("注册环信用户失败");
                //是否需要记录到日志表中，方便后续人工处理，增加用户
                return vo;*/
            }
        } else {

            //判断用户是否被禁用
            Integer state = member.getState();
            if(state==0 || state ==3){
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("用户已禁用或已删除");
                return vo;
            }

            //数据库中有当前用户，但是环信中并没有该用户。在环信中注册该用户
            String memberCode = member.getMemberCode();
            //查询用户是否在环信中注册
            Object result = easemobIMUsers.getIMUserByUserName(memberCode);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            //环信接口返回错误码
            String code = StringUtil.trim(jsonObject.getString("code"));
            //code为404表示用户不存在。code为null表示请求成功。有该用户信息
            if ("404".equals(code)) {
                boolean b = addUser(memberCode);
                if (!b) {
                    /*vo.setResult(-1);
                    vo.setMessage("注册环信用户失败");
                    return vo;*/
                }
            }
        }
        //生成token值
        PayLoadVo payLoadVo = new PayLoadVo();
        HeaderVo headerVo = new HeaderVo();
        Date date = new Date();
        payLoadVo.setUserId(String.valueOf(member.getId()));
        payLoadVo.setIss(member.getMemberCode());
        payLoadVo.setExp(date);
        payLoadVo.setMoble(memberMoble);
        payLoadVo.setAreaCode(areaCode);
        String content = EncryptionUtil.getBase64(JsonUtil.toJson(headerVo)).replaceAll("\r|\n", "") + "." + EncryptionUtil.getBase64(new Gson().toJson(payLoadVo)).replaceAll("\r|\n", "");
        String signature = EncryptionUtil.getSHA256StrJava(ResultConstant.BUS_TAG_TOKEN + content);
        vo.setToken(content + "." + EncryptionUtil.getBase64(signature).replaceAll("\r|\n", ""));
        //请求成功
        vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
        HsMember _member = new HsMember();//修改最后登录时间
        _member.setId(member.getId());
        _member.setLastLoginTime(date);
        _member.setUpdateTime(date);
        hsMemberDao.updateByPrimaryKeySelective(_member);

        //保存至redis
        RedisUtil.safeSet(member.getMemberCode(), signature, 60*60*24*7);
        return vo;
    }

    //计算可用的业务员
    @Override
    public List<Map<Object, Object>> allotUsableUsers(Map<Object, Object> condition) {
        if (condition == null || condition.size() == 0) {
            return null;
        }
        //查询对应角色的userIds
        List<Map<Object, Object>> users = hsSysUserDao.loadUserRoles(condition);
        if (users == null) {
            return null;
        }
        //获取最新一次位置
//        List<Integer> userIds =  new ArrayList<Integer>();
//        for (Map<Object, Object> user : users) {
//            userIds.add(StringUtil.getAsInt(StringUtil.trim(user.get("userId"))));
//        }
        double longitude = 114.057865;
        double latitude = 22.543096;
        for (Map<Object, Object> user : users) {
            int rd = (int) (Math.random() > 0.5 ? 1 : 0);
            int x = (int) ((Math.random() * 9 + 1) * 100000) / 10000000;
            int y = (int) ((Math.random() * 9 + 1) * 100000) / 10000000;
            if (rd > 0) {
                user.put("longitude", longitude + x);
                user.put("latitude", latitude + y);
            } else {
                user.put("longitude", longitude - x);
                user.put("latitude", latitude - y);
            }
        }
        return users;
    }

    /**
     * 获取外获业务员
     *
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> selectOutsideUser(Map<Object, Object> condition) {
        if (condition == null || condition.size() == 0) {
            return null;
        }
        //查询对应角色的userIds
        return hsSysUserDao.loadUserRoles(condition);
    }

    @Override
    public ResultVo updateUserOrderTask(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        int count = -1;
        try {
            count = hsSystemUserOrderTasksDao.updateOrderTaskByPoolId(condition);
            /*if (count > 0) {
                result.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                result.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl updateUserOrderTask Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 查询今日可看
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getToday2SeeHouses(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            List<String> queryColumn = new ArrayList<>();//自定义查询列名
            queryColumn.add("ID id");//主键ID
            queryColumn.add("HOUSE_ID houseId");//房源ID
            queryColumn.add("HOUSE_TYPE houseType");//房源类型
            condition.put("queryColumn", queryColumn);
            //查询今日可看的
            Map<Object, Object> houses = hsMemberHousingApplicationDao.selectCustomColumnNames(condition);
            result = ResultVo.success();
            result.setDataSet(houses.get("data"));

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl getToday2SeeHouses Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 查询用户角色
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo selectUserRoles(Map<Object, Object> condition) {
        ResultVo result = null;
        List<Map<Object, Object>> users = null;
        try {
            users = hsSysRoleDao.selectRolesByUser(condition);
            result = ResultVo.success();
            result.setDataSet(users);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl selectUserRoles Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 添加用户角色关联
     * @param userId 用户ID
     * @param principal 当前操作人
     * @param roles 角色集合
     * @return
     */
    @Override
    public ResultVo addUserRoleRef(Integer userId, ActiveUser principal, String[] roles) {
        ResultVo result = null;
        try {
            Date date = new Date();
            Map<Object,Object> condition = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID userRoleId");//用户角色关联主键ID
            queryColumn.add("USER_ID userId");//用户ID
            queryColumn.add("ROLE_ID roleId");//角色id
            condition.put("queryColumn",queryColumn);
            condition.put("userId",userId);
            Map<Object, Object> queryMap = hsSysUserRoleDao.selectCustomColumnNamesList(condition);
            List<Map<Object, Object>> datas = (List<Map<Object, Object>>) queryMap.get("data");
            if(datas!=null && datas.size()>0){
                List<String> userRoleIds = Lists.newArrayList();
                for (Map<Object, Object> data : datas) {
                    userRoleIds.add(StringUtil.trim(data.get("userRoleId")));
                }
                condition.clear();
                condition.put("ids",userRoleIds);
                int i = hsSysUserRoleDao.batchDelete(condition);
                if(i<=0){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
            }
            List<HsSysUserRole> userRoles = Lists.newArrayList();
            if(roles == null || roles.length==0){
                return ResultVo.success();
            }
            for (String roleId:roles) {
                HsSysUserRole userRole=new HsSysUserRole();
                userRole.setId(RandomUtils.getUUID());
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateTime(date);
                userRole.setCreateBy(principal.getUsercode());
                userRole.setUpdateBy(principal.getUsercode());
                userRole.setUpdateTime(date);
                userRoles.add(userRole);
            }
            if(userRoles == null ||  userRoles.size()==0){
                return ResultVo.success();
            }
            //插入
            int state = hsSysUserRoleDao.batchInsert(userRoles);
            if(state >0){
                result = ResultVo.success();
            }else{
                result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            logger.info("MemberServiceImpl addUserRoleRef Exception message:" + e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 查询用户所能访问的资源名称
     * @param condition
     * @return
     */
    @Override
    public ResultVo showPermissions(Map<Object, Object> condition) {
        ResultVo vo = ResultVo.success();
        List<Map<Object, Object>> userRoles = hsSysRoleDao.selectRolesByUser(condition);
        condition.clear();
        condition.put("isForbidden",0);
        List<String> roleIds = Lists.newArrayList();
        for (Map<Object, Object> userRole : userRoles) {
            roleIds.add(StringUtil.trim(userRole.get("roleId")));
        }
        condition.clear();
        condition.put("roleIds",roleIds);
        condition.put("isDel",0);
        condition.put("needSort",1);
        condition.put("orderDirection","asc");
        //查询角色所能访问的资源
        List<Map<Object, Object>> rolePermissions = hsSysRolePermissionDao.selectRolePermission(condition);
        if(rolePermissions==null || rolePermissions.size()==0){
            return vo;
        }
        List<Map<String, Object>> menuList = Lists.newArrayList();
        for (Map<Object, Object> rolePermission : rolePermissions) {
            Map<String, Object> menu = Maps.newHashMap();
            Set<Map.Entry<Object, Object>> entries = rolePermission.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                menu.put(StringUtil.trim(entry.getKey()),entry.getValue());
            }
            menuList.add(menu);
        }
        if(menuList!=null && menuList.size()>0){
            List<Map<String, Object>> maps = generatePermissionToTree(menuList, -1, Maps.newHashMap());
            vo.setDataSet(maps);
        }
        return vo;
    }

    /**
     * 查询角色对应的权限
     * @param condition
     * @return
     */
    @Override
    public ResultVo showRolePermissions(Map<Object, Object> condition,String roleId) {
        ResultVo result = null;
        try {
            Map<Object,Object> queryFilter = Maps.newHashMap();
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID id");//角色权限表ID
            queryColumn.add("PERMISSION_ID permissionId");//权限id
            queryColumn.add("ROLE_ID roleId");//角色id
            queryFilter.put("queryColumn",queryColumn);
            queryFilter.put("isForbidden",0);//是否禁用,0：启用，1禁用
            queryFilter.put("roleId",roleId);//是否禁用,0：启用，1禁用
            Map<Object, Object> resultMap = hsSysRolePermissionDao.selectCustomColumnNamesList(queryFilter);
            List<Map<String, Object>> selectPermMap= (List<Map<String, Object>>) resultMap.get("data");

            List<Map<String, Object>> permissions = generatePermissionSelectToTree(selectPermMap,Lists.newArrayList(),-1,condition);
            result = ResultVo.success();
            result.setDataSet(permissions);

        } catch (Exception e) {
            logger.info("MemberServiceImpl findAllPermission Exception message:" + e.getMessage());
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);

        }
        return result;
    }

    /**
     * 查询所有权限
     * @return
     */
    @Override
    public List<Map<String, Object>> findAllPermission(Map<Object, Object> condition) {
        List<Map<String, Object>> result = null;
        try {
            result = generatePermissionToTree(Lists.newArrayList(),-1,condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl findAllPermission Exception message:" + e.getMessage());

        }
        return result;
    }


    /**
     * 递归查询所有子节点
     * @param permMap
     * @param pid
     * @return
     */
    private List<Map<String, Object>> generatePermissionSelectToTree(List<Map<String, Object>> selectPermMaps,List<Map<String, Object>> permMap, Integer pid,Map<Object, Object> condition) {
        if (null == permMap || permMap.size() == 0) {
            Map<Object, Object> resultMap = hsSysPermissionDao.selectCustomColumnNamesList(condition);
            permMap= (List<Map<String, Object>>) resultMap.get("data");
            //permMap = hsSysPermissionDao.findAllPermission(condition);
        }
        List<Map<String, Object>> orgList = new ArrayList<>();
        if (permMap != null && permMap.size() > 0) {
            for (Map<String, Object> item : permMap) {
                //比较传入pid与当前对象pid是否相等
                if (pid.equals(item.get("parentId"))) {
                    //将当前对象id做为pid递归调用当前方法，获取下级结果
                    List<Map<String, Object>> children = generatePermissionSelectToTree(selectPermMaps,permMap, Integer.valueOf(item.get("id").toString()),null);
                    //将子结果集存入当前对象的children字段中
                    item.put("children", children);
                    Boolean bool = false;
                    for (Map<String, Object> selectPermMap : selectPermMaps) {
                        if(StringUtil.trim(item.get("id")).equals(StringUtil.trim(selectPermMap.get("permissionId")))){
                            bool = true;
                            break;
                        }
                    }
                    item.put("isShow", bool);
                    //添加当前对象到主结果集中
                    orgList.add(item);
                }
            }
        }
        return orgList;
    }

    /**
     * 递归查询所有子节点
     * @param permMap
     * @param pid
     * @return
     */
    private List<Map<String, Object>> generatePermissionToTree(List<Map<String, Object>> permMap, Integer pid,Map<Object, Object> condition) {
        if (null == permMap || permMap.size() == 0) {
            Map<Object, Object> resultMap = hsSysPermissionDao.selectCustomColumnNamesList(condition);
            permMap= (List<Map<String, Object>>) resultMap.get("data");
            //permMap = hsSysPermissionDao.findAllPermission(condition);
        }
        List<Map<String, Object>> orgList = new ArrayList<>();
        if (permMap != null && permMap.size() > 0) {
            for (Map<String, Object> item : permMap) {
                //比较传入pid与当前对象pid是否相等
                if (pid.equals(item.get("parentId"))) {
                    //将当前对象id做为pid递归调用当前方法，获取下级结果
                    List<Map<String, Object>> children = generatePermissionToTree(permMap, Integer.valueOf(item.get("id").toString()),null);
                    //将子结果集存入当前对象的children字段中
                    item.put("children", children);
                    //添加当前对象到主结果集中
                    orgList.add(item);
                }
            }
        }
        return orgList;
    }


    /**
     * 查询过期的任务最后在哪个业务员手里
     * @param condition
     * @return
     */
    @Override
    public List<HsSystemUserOrderTasks> selectExpiredTasks(Map<Object, Object> condition) {
        return hsSystemUserOrderTasksDao.selectExpiredTasks(condition);
    }

    @Override
    public List<Map<String,Object>> getLastMsg(Object o,Map<Object, Object> condition){
        if(o instanceof HsMemberHousingBargainMessage){
            return hsMemberHousingBargainMessageDao.getLastMsg(condition);
        }else if(o instanceof HsMemberHousingApplicationMessage){
            return hsMemberHousingApplicationMessageDao.getLastMsg(condition);
        }
        return new ArrayList<>();
    }

    @Override
    public ResultVo selectPersonalPerformance(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int count = hsSystemUserOrderTasksDao.selectPersonalPerformance(condition);
            result.setDataSet(count);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl selectPersonalPerformance Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    @Override
    public ResultVo updateHousingApplicationByPoolId(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int count = -1;
            count = hsMemberHousingApplicationDao.updateHousingApplicationByPoolId(condition);
            if (count > 0) {//操作成功
                result.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                result.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            } else {
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl updateHousingApplicationByPoolId Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 查询业务员对应的单数
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo selectGroupUserTasksByCondition(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            List<Map<Object, Object>> taskGroup = hsSystemUserOrderTasksDao.selectGroupUserTasksByCondition(condition);
            result.setDataSet(taskGroup);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl selectGroupUserTasksByCondition Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }


    /**
     * 业务员打卡记录
     *
     * @return
     */
    @Override
    public ResultVo clockIn(HsUserAttendanceSheet sheet) {
        ResultVo result = new ResultVo();
        int state = -1;
        try {
            Map<Object, Object> condition = Maps.newHashMap();
            //获取业务员ID,并查询今天是否有打过卡，打过卡直接返回成功
            Integer userId = sheet.getUserId();
            condition.put("userId", userId);
            state = hsUserAttendanceSheetDao.selectToDayCount(condition);
            if (state > 0) {
                return ResultVo.success();
            }
            state = hsUserAttendanceSheetDao.insert(sheet);
            if (state <= 0) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MemberServiceImpl selectGroupUserTasksByCondition Exception message:" + e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 注册环信用户
     *
     * @param memberCode 用户code
     * @return
     */
    public boolean addUser(String memberCode) {
        boolean b = true;
        //新增环信用户
        RegisterUsers users = new RegisterUsers();
        User user = new User().username(memberCode).password(memberCode);
        users.add(user);
        Object result = easemobIMUsers.createNewIMUserSingle(users);
        JSONObject jsonObject = JSON.parseObject(result.toString());
        String status = StringUtil.trim(jsonObject.getString("status"));
        if ("fail".equals(status)) {
            b = false;
        }
        return b;
    }

    /**
     * 修改看房数据
     * @param resultMap
     * @return
     */
    @Override
    public ResultVo updateSeeHouseApply(Map<Object, Object> resultMap) {
        //修改预约看房状态
        int houseId = StringUtil.getAsInt(StringUtil.trim(resultMap.get("houseId")));
        Date startApartmentTime = (Date) resultMap.get("estimatedTime");
        int poolId  = StringUtil.getAsInt(StringUtil.trim(resultMap.get("poolId")));
        Map<Object, Object> applicationCondition = new HashMap<>(4);
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//主键ID
        applicationCondition.put("queryColumn",queryColumn);
        applicationCondition.put("houseId",houseId);
        applicationCondition.put("isCancel",0);
        applicationCondition.put("isDel",0);
        applicationCondition.put("_startApartmentTime",startApartmentTime);
        Map<Object, Object> applicationMap = hsMemberHousingApplicationDao.selectCustomColumnNames(applicationCondition);
        List<Map<Object,Object>> applicationList = (List<Map<Object, Object>>) applicationMap.get("data");
        if(applicationList!=null && applicationList.size()>0){
            List<Integer> applyIds  = Lists.newArrayList();
            for (Map<Object, Object> apply : applicationList) {
                applyIds.add(StringUtil.getAsInt(StringUtil.trim(apply.get("id"))));
            }
            applicationCondition.clear();
            applicationCondition.put("ids",applyIds);
            applicationCondition.put("poolId",poolId);
            int state = hsMemberHousingApplicationDao.updatePoolIdByApplyIds(applicationCondition);
            if(state>0){
                return  new ResultVo();
            }
        }
//        HsMemberHousingApplication houseApply = new HsMemberHousingApplication();
//        houseApply.setHouseId(houseId);
//        houseApply.setStandby1(StringUtil.trim(poolId));
//        int state = hsMemberHousingApplicationDao.updateByPrimaryKey(houseApply);
//        if(state>0){
//            return  new ResultVo();
//        }
        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
    }
    @Override
    public ResultVo getMyBargainList(Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        List<Map<Object, Object>> myBargainList = hsMemberHousingBargainDao.getMyBargainList(condition);
        result.setDataSet(myBargainList);
        return result;
    }

    /**
     * 检查系统中是否存在账号或手机号码
     * @param condition
     * @return
     */
    @Override
    public int checkExistUserCodeOrMobile(Map<Object, Object> condition) {
        return hsSysUserDao.checkExistUserCodeOrMobile(condition);
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
            if (t.hashCode() == HsSysRolePermission.class.hashCode()) {
                result = hsSysRolePermissionDao.batchDelete(condition);
            }else if(t.hashCode() == HsSysUserRole.class.hashCode()) {
                result = hsSysUserRoleDao.batchDelete(condition);
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

    @Override
    public Integer updateCustomColumnByCondition(Object o,Map<Object,Object> condition){
        int result = -1;
        if(o instanceof HsMemberHousingBargainMessage){
            result = hsMemberHousingBargainMessageDao.updateCustomColumnByCondition(condition);
        }if(o instanceof HsMemberHousingApplicationMessage){
            result = hsMemberHousingApplicationMessageDao.updateCustomColumnByCondition(condition);
        }if(o instanceof HsMemberHousingApplication){
            result = hsMemberHousingApplicationDao.updateCustomColumnByCondition(condition);
        }
        if(o instanceof HsMemberHousingBargain){
            result = hsMemberHousingBargainDao.updateCustomColumnByCondition(condition);
        }
        return result;
    }



    /**
     * 加载下级员工
     * @param condition
     * @return
     */
    @Override
    public ResultVo getSubordinatePosition(Map<Object, Object> condition) {
        ResultVo vo = null;
        Map<Object,Object> resultMap = Maps.newHashMap();
        try {
            int queryMonthsAgo = StringUtil.getAsInt(StringUtil.trim(condition.get("queryMonthsAgo")), 0);
            condition.remove("queryMonthsAgo");
            //加载用户角色
            List<Map<Object, Object>> users = hsSysRoleDao.selectRolesByUser(condition);
            if(users!=null && users.size()>0){
                Map<Object,Object> user  = users.get(0);
                String roleId = StringUtil.trim(user.get("roleId"));
                condition.clear();
                condition.put("pid",roleId);
//                condition.put("pid","0f9a0878fcaf0dde29b4e487aa8bbb44");
                //查询角色下的子员工
                List<Map<Object,Object>> userList = hsSysUserDao.getSubordinatePositionByRoleId(condition);
                List<Integer> userIds = Lists.newArrayList();
                for (Map<Object, Object> _user : userList) {
                    userIds.add(StringUtil.getAsInt(StringUtil.trim(_user.get("id"))));
                }
                List<Map<Object,Object>> vacateList = new ArrayList<>();
                if(userIds!=null && userIds.size()>0){
                    List<String> queryColumn = new ArrayList<>();
                    //查询业务员当月请假记录
                    queryColumn.clear();
                    queryColumn.add("ID vacateId");//请假记录Id
                    queryColumn.add("USER_ID userId");//业务员Id
                    queryColumn.add("VACATE_TYPE vacateType");//请假类型 0:年假 1：事假 2：病假 3：调休 4：产假
                    queryColumn.add("BEGIN_TIME beginTime");//开始时间
                    queryColumn.add("END_TIME endTime");//结束时间
                    queryColumn.add("DAYS days");//请假天数
                    condition.put("userIds",userIds);
                    condition.put("queryColumn",queryColumn);
                    condition.put("queryMonthsAgo",queryMonthsAgo);
                    //业务员请假记录表
                    Map<Object,Object> vacatesMap = hsUserVacateSheetDao.selectCustomColumnNamesList(condition);
                    List<Map<Object,Object>> vacates = (List<Map<Object, Object>>)vacatesMap.get("data");
                    List<String> dayListOfMonths = DateUtil.getMonthFullDays(queryMonthsAgo);

                    Map<Object,Object> vacateTmp = null;
                    for (String dayListOfMonth : dayListOfMonths) {
                        int day = StringUtil.getAsInt(dayListOfMonth.substring(8, dayListOfMonth.length()));
                        vacateTmp = Maps.newHashMap();
                        List<Integer> _userIds = null;
                        for (int i = vacates.size()-1 ;i>=0 ; i--) {
                            Map<Object, Object> vacate = vacates.get(i);
                            int userId = StringUtil.getAsInt(StringUtil.trim(vacate.get("userId")));
                            String beginTime = StringUtil.trim(vacate.get("beginTime"));
                            String endTime = StringUtil.trim(vacate.get("endTime"));
                            String _beginTime = beginTime.substring(0,10);
                            String _endTime = endTime.substring(0,10);
                            String substring = _beginTime.substring(8, _beginTime.length());
                            int beginDay =  StringUtil.getAsInt(substring);
                            int endDay =  StringUtil.getAsInt(_endTime.substring(8, _endTime.length()));
                            if(beginDay>endDay){
                                endDay = endDay+beginDay;
                            }
                            if(beginDay<=day && day< endDay){
                                if(_userIds == null || _userIds.size()==0){
                                    _userIds = Lists.newArrayList();
                                }
                                _userIds.add(userId);
                                vacates.remove(i);
                            }
                        }
//                    if(_userIds==null || _userIds.size() == 0){
//                        _userIds = userIds;
//                    }
                        vacateTmp.put(dayListOfMonth,_userIds);
                        vacateList.add(vacateTmp);
                    }
                }
//                resultMap.put("attendance",attendances);//考勤记录
                resultMap.put("vacates",vacateList);//请假记录


                resultMap.put("userInfo",userList);//员工信息
                vo = ResultVo.success();
                vo.setDataSet(resultMap);
            }
        } catch (Exception e) {
            logger.error("MemberServiceImpl getSubordinatePosition Exception message:" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 加载下级员工
     * @param condition
     * @return
     */
    @Override
    public List<Integer> loadSubordinates(Map<Object, Object> condition) {
        List<Integer> userIds = null;
        try {
            //加载用户角色
            List<Map<Object, Object>> users = hsSysRoleDao.selectRolesByUser(condition);
            if(users!=null && users.size()>0){
                Map<Object,Object> user  = users.get(0);
                String roleId = StringUtil.trim(user.get("roleId"));
                condition.clear();
                condition.put("pid",roleId);
//                condition.put("pid","0f9a0878fcaf0dde29b4e487aa8bbb44");
                //查询角色下的子员工
                List<Map<Object,Object>> userList = hsSysUserDao.getSubordinatePositionByRoleId(condition);
                userIds = Lists.newArrayList();
                for (Map<Object, Object> _user : userList) {
                    userIds.add(StringUtil.getAsInt(StringUtil.trim(_user.get("id"))));
                }
            }
        } catch (Exception e) {
            logger.error("MemberServiceImpl loadSubordinates Exception message:" + e);
        }
        return userIds;
    }

    /**
     * 清除当月设置的排班数据
     * @param condition
     * @return
     */
    @Override
    public ResultVo clearAttendance(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //加载用户角色
            List<Integer> userIds = loadSubordinates(condition);
            if(userIds!=null && userIds.size()>0 ){
                condition.clear();
                condition.put("userIds",userIds);
                int state = hsUserVacateSheetDao.clearAttendanceCurrentMonth(condition);
                if(state >0 ){
                    vo = ResultVo.success();
                    return vo;
                }
            }
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } catch (Exception e) {
            logger.error("MemberServiceImpl getSubordinatePosition Exception message:" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 清除用户的请假记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo clearUserAttendance(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            int state = hsUserVacateSheetDao.clearUserAttendance(condition);
            if(state >0 ){
                vo = ResultVo.success();
                return vo;
            }
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } catch (Exception e) {
            logger.error("MemberServiceImpl clearUserAttendance Exception message:" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }
}
