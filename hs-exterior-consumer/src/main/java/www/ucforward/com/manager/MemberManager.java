package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.entity.*;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/23
 */
@Service
public interface MemberManager {


    /**
     * 登录
     * @param condition
     * @return
     */
    ResultVo login(Map<Object, Object> condition);

    /**
     * 是否新用户
     * @param condition
     * @return
     */
    ResultVo isNewUser(Map<Object, Object> condition);

    /**
     * 新增会员浏览历史记录
     * @param browseHistory
     * @return
     */
    ResultVo addBrowseHistory(HsHousesBrowseHistory browseHistory);

    /**
     * 新增会员收藏记录
     * @param memberFavorite
     * @return
     */
    ResultVo addMemberFavorite(HsMemberFavorite memberFavorite);

    /**
     * 获取会员相关信息（浏览房源记录、收藏房源记录、预约时间表，已看房列表信息）
     * @param condition
     * @return
     */
    ResultVo getMemberRelationInfo(Object obj,Map<Object,Object> condition);

    /**
     * 新增意见反馈信息
     * @param feedback
     * @return
     */
    ResultVo addFeedback(HsFeedback feedback);

    /**
     * 获取会员房源列表信息
     * @param condition
     * @return
     */
    ResultVo getMemberHousing(Map<Object, Object> condition);


    /**
     * 订阅列表 查询更多
     * @param condition
     * @return
     */
    ResultVo getMemberHousingSubscribeMore(Map<Object, Object> condition);

    /**
     * 获取房源进度
     * @param condition
     * @return
     */
    ResultVo getMemberHousingProgress(Map<Object, Object> condition);

    /**
     * 会员申请下架
     * @param houseObtained
     * @return
     */
    ResultVo memberApplyWithdraw(HsHouseObtained houseObtained);

    /**
     * 修改房源租金/看房时间
     * @param condition
     * @return
     */
    ResultVo updateHousingInfo(Map<Object,Object> condition);

    /**
     * 新增会员订阅房源信息
     * @param memberHousesSubscribe
     * @return
     */
    ResultVo addHousingSubscribe(HsMemberHousesSubscribe memberHousesSubscribe,Map<Object,Object> condition);

    /**
     * 获取会员订阅房源信息
     * @param condition
     * @return
     */
    ResultVo getMemberHousingSubscribe(Map<Object,Object> condition);

    /**
     * 修改会员相关信息（订阅房源等）
     * @param object
     * @return
     */
    ResultVo updateMemberRelationInfo(Object object,Map<Object,Object> condition);

    /**
     * 删除会员相关信息（房源订阅等）
     * @param object
     * @return
     */
    ResultVo deleteMemberRelationInfo(Object object,Integer id);

    /**
     * 取消预约看房信息
     * @param condition
     * @return
     */
    ResultVo cancelAppointment(Map<Object, Object> condition);

    /**
     * 新增会员相关记录信息（投诉业务员）
     * @param obj
     * @return
     */
    ResultVo addMemberRelationInfo(Object obj);

    /**
     * 查询房源预约时间
     * @param condition
     * @return
     */
    ResultVo getHouseAppointTime(Map<Object,Object> condition);

    /**
     * 新增预约看房信息
     * @param condition
     * @return
     */
    ResultVo addAppointmentLookHouse(Map<Object,Object> condition);

    ResultVo createReservationGroup(String groupName,String houseId);

    /**
     * 发送看房预约消息
     * @param message
     * @return
     */
    ResultVo sendReservationMessage(HsMemberHousingApplicationMessage message);

    /**
     * 同意预约看房
     * @param message
     * @param condition
     * @return
     */
    ResultVo agreeReservation(HsMemberHousingApplicationMessage message,Map<Object, Object> condition);
    /**
     * 发送议价消息
     * @param message
     * @return
     */
    ResultVo sendBargainMessage(HsMemberHousingBargainMessage message);

    /**
     * 获取预约聊天记录
     * @param condition
     * @return
     */
    ResultVo getReservationHistory(Map<Object,Object> condition);

    /**
     *同意议价接口
     * @param message
     * @return
     * @throws Exception
     */
    ResultVo agreeBargain(HsMemberHousingBargainMessage message) throws Exception;

    /**
     * 拒绝议价
     * @param message
     * @return
     * @throws Exception
     */
    ResultVo refuseBargain(HsMemberHousingBargainMessage message) throws Exception;

    /**
     * 获取议价聊天历史
     * @param condition
     * @return
     */
    ResultVo getBargainRecord(Map<Object,Object> condition);

    /**
     * 获取用户信息接口
     * @param token
     * @return
     * @throws Exception
     */
    ResultVo getMemberInfo(int token);

    /**
     * 获取业主首页数据
     * @param condition
     * @return
     */
    ResultVo getOwnerIndexData (Map<Object,Object> condition);

    /**
     * 修改用户信息
     * @param
     * @return
     *
     * @throws Exception
     */
    ResultVo updateMemberInfo(HsMember member);

    /**
     * 修改用户信息
     * @param condition
     * @return
     */
    ResultVo updateMemberInfo(Map<Object, Object> condition);

    /**
     * 获取变价房源信息
     * @param condition
     * @return
     */
    ResultVo getChangePriceHouse(Map<Object,Object> condition);

    /**
     * 获取地址编码
     * @return
     * @throws Exception
     */
    ResultVo loadCityCode(Map<Object,Object> condition);

    /**
     * 发送短信验证码
     * @param condition
     * @return
     */
    ResultVo sendSmsValidateCode(Map<Object,Object> condition);

    /**
     * 我的  获取议价列表
     * @param condition
     * @return
     */
    ResultVo getBargainsList(Map<Object, Object> condition);

    /**
     * 新增客户购房信息
     * @param purchase
     * @return
     */
    ResultVo addPurchaseInfo(HsMemberPurchase purchase);

    /**
     * 获取用户购房信息
     * @param condition
     * @return
     */
    ResultVo getPurchaseInfo(Map<Object,Object> condition);


    /**
     * 新增人员贷款信息
     * @param financialLoansApply
     * @return
     */
    ResultVo addLoansApply(HsMemberFinancialLoansApply financialLoansApply);

    /**
     * 获取人员贷款信息
     * @param condition
     * @return
     */
    ResultVo getLoansApply(Map<Object,Object> condition);

    /**
     * 获取楼盘列表
     * @param condition
     * @return
     */
    ResultVo getPurchaseApplyList(Map<Object,Object> condition);

    /**
     * 获取楼盘详情
     * @param id
     * @return
     */
    ResultVo getPurchaseApplyDetails(Integer id);

    /**
     * 获取交易订单列表
     * @param condition
     * @return
     */
    ResultVo getMemberOrderList(Map<Object, Object> condition);

    /**
     * 我的消费记录列表
     * @param condition
     * @return
     */
    ResultVo getExpenseCalendarsList(Map<Object, Object> condition);

    /**
     * 保存开发商直售楼盘个人信息填写
     * @param memberApply
     * @return
     */
    ResultVo saveNewBuildingMemberApply(HsHouseNewBuildingMemberApply memberApply);

    /**
     * 修改个人楼盘申购信息
     * @param memberApply
     * @return
     */
    ResultVo updateNewBuildingMemberApply(HsHouseNewBuildingMemberApply memberApply);

    /**
     * 下载申购合同
     * @param condition
     * @return
     */
    byte[] downloadPurchase(Map<Object, Object> condition);


    /**
     * 获取开发商直售楼盘个人信息
     * @param condition
     * @return
     */
    ResultVo getNewBuildingMemberApply(Map<Object, Object> condition);


}
