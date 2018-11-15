package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.entity.*;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * 系统及业务员相关管理
 * @author wenbn
 * @version 1.0
 * @date 2018/6/20
 */
@Service
public interface MemberAdminManager {

    /**
     * 业务员发送定位信息
     * @param userLocation
     * @return
     */
    ResultVo sendLocation(HsSysUserLocation userLocation);

    /**
     * 外看业务员已带看房列表
     * @param condition
     * @return
     */
    ResultVo selectLookForHouseFinished(Map<Object, Object> condition);

    /**
     * 外看业务员已带看房详情
     * @param condition
     * @return
     */
    ResultVo selectLookForHouseFinishedDetail(Map<Object, Object> condition);

    /**
     * 查询外获派单列表信息
     * @param condition
     * @return
     */
    ResultVo getOutGainDispatchOrder(Map<Object,Object> condition);

    /**
     * 批量转单操作
     * @param condition
     * @return
     */
    ResultVo batchDispathOrder(Map<Object,Object> condition);


    /**
     * 查询外获任务详情信息
     * @param condition
     * @return
     */
    ResultVo outGainOrderDetail(Map<Object,Object> condition);

    /**
     * 新增房源评论
     * @return
     * @throws Exception
     */
    ResultVo addHouseEvaluation(HsHouseEvaluation evaluation);


    /**
     * 操作用户订单任务（到达客户，完成看房）
     * @param condition
     * @return
     */
    ResultVo operateUserOrderTask(Map<Object,Object> condition);

    /**
     * 取消预约操作（业主取消预约）
     * @param condition
     * @return
     */
    ResultVo cancelAppointment(Map<Object,Object> condition);

    /**
     * 查询合同类订单
     * @param condition
     * @return
     */
    ResultVo getContractOrdersList(Map<Object, Object> condition);

    /**
     * 查询外获业务员合同类派送记录
     * @param condition
     * @return
     */
    ResultVo getContractOrderRecordsList(Map<Object, Object> condition);


    /**
     * 获取已上传房源列表信息
     * @param condition
     * @return
     */
    ResultVo getUploadedHousingList(Map<Object,Object> condition);

    /**
     * 获取外获人员个人绩效
     * @param condition
     * @return
     */
    ResultVo getOutGainPersonalPerformance(Map<Object,Object> condition);

    /**
     * 修改用户信息
     * @param condition
     * @return
     */
    ResultVo updateSysUser(Map<Object,Object> condition);

    /**
     * 修改用户密码/手机号
     * @param condition
     * @return
     */
    ResultVo updateSysUserMobile(Map<Object,Object> condition);

    /**
     * 获取外看人员派单列表
     * @param condition
     * @return
     */
    ResultVo getOutLookDispatchOrder(Map<Object,Object> condition);

    /**
     * 获取外看、外获投诉派单详情
     * @param condition
     * @return
     */
    ResultVo getComplaintDispatchOrderDetail(Map<Object,Object> condition);

    /**
     * 获取外看业务员派单详情
     * @param condition
     * @return
     */
    ResultVo getOutLookDispatchOrderDetail(Map<Object,Object> condition);

    /**
     * 上传房源
     * @param mainHouse
     * @param houseCredentialsData
     * @return
     */
    ResultVo uploadHousing(HsMainHouse mainHouse, HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition);

    /**
     * 获取申请详细信息
     * @param condition
     * @return
     */
    ResultVo getApplicationDetails(Map<Object,Object> condition);

    /**
     * 获取上传房源详细信息
     * @param condition
     * @return
     */
    ResultVo getUploadHouseDetail(Map<Object,Object> condition);

    /**
     * 更新已上传的房源
     * @param mainHouse
     * @param houseCredentialsData
     * @return
     */
    ResultVo updateUploadedHouse(HsMainHouse mainHouse,HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition);

    /**
     * 获取外看人员个人绩效
     * @param condition
     * @return
     */
    ResultVo getOutLookPersonalPerformance(Map<Object,Object> condition);

    /**
     * 业务员查询考勤
     * @param condition
     * @return
     */
    ResultVo queryUserAttendances(Map<Object, Object> condition);
    /**
     * 进度查询
     * @param condition
     * @return
     */
    ResultVo getProgressInfo(Map<Object,Object> condition);

    /**
     * 发送短信验证码
     * @param condition
     * @return
     */
    ResultVo sendSmsValidateCode(Map<Object,Object> condition);

    /**
     * 修改见面时间
     * @param condition
     * @return
     */
    ResultVo updateMeetTime(Map<Object,Object> condition);

    /**
     * 修改见面地点
     * @param condition
     * @return
     */
    ResultVo updateMeetPlace(Map<Object,Object> condition);

    /**
     * 获取业务员信息
     * @param condition
     * @return
     */
    ResultVo getUserInfo(Map<Object, Object> condition);

    /**
     * 获取房源钥匙
     * @param condition
     * @return
     */
    ResultVo getHouseKey(Map<Object, Object> condition);

    /**
     * 检查房源钥匙是否过期
     * @return
     */
    ResultVo checkKeyIsExpire(int houseId,int userId);

    /**
     * 获取房源钥匙数据
     * @param condition
     * @return
     */
    ResultVo loadMyHousekeys(Map<Object, Object> condition);

    /**
     * 忘记密码
     * @param condition
     * @return
     */
    ResultVo forgetPassword(Map<Object, Object> condition);

    /**
     * 检验短信验证码
     * @param condition
     * @return
     */
    ResultVo validateSmsCode(Map<Object, Object> condition);

    /**
     * 业务员修改见面时间和见面地点
     * @param condition
     * @return
     */
    ResultVo updatePoolInfo(Map<Object, Object> condition);

    /**
     * 获取钥匙管理员派单任务列表信息
     * @param condition
     * @return
     */
    ResultVo getKeysManagerTaskList(Map<Object,Object> condition);

    /**
     * 完成合同单任务
     * @param condition
     * @return
     */
    ResultVo finishedContractTask(Map<Object,Object> condition);

    /**
     * 获取区域长个人绩效
     * @param condition
     * @return
     */
    ResultVo getKeysManagerPersonalPerformance(Map<Object,Object> condition);

    /**
     * 检查是否可以显示房源二维码
     * @param houseId
     * @param userid
     * @return
     */
    ResultVo checkShowHouseQrcode(Integer houseId, Integer userid);

    /**
     * 获取送钥匙任务列表信息
     * @param condition
     * @return
     */
    ResultVo getDeliveryKeysTaskList(Map<Object,Object> condition);

    /**
     * 获取钥匙派送记录信息
     * @param condition
     * @return
     */
    ResultVo getDeliveredKeysRecordList(Map<Object,Object> condition);

    /**
     * 获取派送钥匙任务详情信息
     * @param condition
     * @return
     */
    ResultVo getDeliveryKeysTaskDetail(Map<Object,Object> condition);

    /**
     * 完成钥匙派送
     * @param condition
     * @return
     */
    ResultVo finishedDeliveryKeys(Map<Object,Object> condition);

    /**
     * 到达送钥匙地点
     * @param condition
     * @return
     */
    ResultVo arriveDeliveryKeysPlace(Map<Object,Object> condition);

    /**
     * 我的结佣记录表
     * @param condition
     * @return
     */
    ResultVo myBrokerages(Map<Object, Object> condition);
}
