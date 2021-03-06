package www.ucforward.com.manager;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import www.ucforward.com.entity.*;
import www.ucforward.com.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/20 19:01
 * @Description:
 */
@Service
public interface IHousesManager {
    /**
     * lsq
     * 获取业主预约获取房源申请
     * @param condition
     * @return
     */
    ResultVo getOwnerHousingApplication(Map<Object,Object> condition);

    /**
     * lsq
     * 获取业主预约获取房源申请详情
     * @param id 房源申请id
     * @return
     */
    ResultVo getOwnerHousingApplicationById(Integer id);

    /**
     * 客服审核业主申请房源信息
     * @param condition
     * @return
     */
    ResultVo checkHousingApply(Map<Object, Object> condition);


    /**
     * 客服审核房源信息
     * @param condition
     * @return
     */
    ResultVo checkHouse(Map<Object, Object> condition);

    /**
     * 客服终审失败
     * @param condition
     * @return
     */
    ResultVo auditFailure(Map<Object, Object> condition);

    /**
     * 获取房源列表（终审）
     * @param condition
     * @return
     */
    ResultVo getMainHousingList(Map<Object, Object> condition);

    /**
     * 获取房源详情（终审）
     * @param id
     * @return
     */
    ResultVo getMainHousingInfo(Integer id);

    /**
     * 新增人员申购信息
     * @param buildingMemberApply
     * @return
     */
    ResultVo addPurchase(HsHouseNewBuildingMemberApply buildingMemberApply);

    /**
     * 新增直售楼盘信息
     * @param hsHouseNewBuilding
     * @return
     */
    ResultVo addDirectSalesProperty(HsHouseNewBuilding hsHouseNewBuilding);

    /**
     * 获取楼盘区域
     * @return
     */
    ResultVo getPropertyArea();

    /**
     * 获取楼盘详情
     * @param id
     * @return
     */
    ResultVo getDirectSalesDetails(Integer id);

    /**
     * 获取直售楼盘列表
     * @param condition
     * @return
     */
    ResultVo getDirectSalesPropertyList(Map<Object, Object> condition);

    /**
     * 修改楼盘信息（新增、修改、删除）
     * @param hsHouseNewBuilding 楼盘信息
     * @return
     */
    ResultVo updateDirectSalesDetails(HsHouseNewBuilding hsHouseNewBuilding);

    /**
     * 房源投诉列表
     * @param condition
     * @return
     */
    ResultVo getHouseComplainList(Map<Object, Object> condition);

    /**
     * 更新投诉订单
     * @param condition
     * @return
     */
    ResultVo updateComplaintGrabOrder(Map<Object, Object> condition);

    /**
     * 新增房源投诉（新建单）
     * @param houseComplain 房源投诉表
     * @return
     */
    ResultVo addHouseComplain(HsHouseComplain houseComplain);

    /**
     * 房源投诉转单
     * @param id
     * @param userId
     * @return
     */
    ResultVo houseComplainTransfer(Integer id,Integer userId,Integer type,String remark);

    /**
     * 房源投诉关单
     * @param id 房源投诉id
     * @return
     */
    ResultVo houseComplainClose(Integer id,Integer userId,String remark);

    /**
     * 根据房源code获取房源信息
     * @param houseCode 房源code
     * @return
     */
    ResultVo getHouseByCode(String houseCode);

    /**
     * 房源投诉详情
     * @param id
     * @return
     */
    ResultVo getHouseComplainDetail(Integer id);

    /**
     * 房源评价列表
     * @param condition
     * @return
     */
    ResultVo getHouseEvaluationList(Map<Object, Object> condition);

    /**
     * 房源评价详情
     * @param id
     * @return
     */
    ResultVo getHouseEvaluationDetail(Integer id);

    /**
     * 业主联系客服上传
     * @param apply 业主申请表
     * @param houseCredentialsData
     * @param areaCode
     * @param memberMobile
     * @return
     * @throws IllegalAccessException
     */
    ResultVo addOwnerApply(HsOwnerHousingApplication apply, HsHouseCredentialsData houseCredentialsData,String areaCode, String memberMobile,Integer applyId,Integer userid);

    /**
     * 房源编辑（终审）
     * @param mainHouse                房源信息
     * @param houseCredentialsData 房源图片信息
     * @return
     */
    ResultVo houseEdit(HsMainHouse mainHouse, HsHouseCredentialsData houseCredentialsData,HsHouseImg houseImg,Map<Object,Object> condition);

    /**
     * 获取房屋自动应答
     * @param condition
     * @return
     */
    ResultVo getAutoReplySetting(Map<Object,Object> condition);

    /**
     * 新增、设置自动应答
     * @param houseAutoReplySetting
     * @return
     */
    ResultVo addAutoReplySetting(HsHouseAutoReplySetting houseAutoReplySetting);

    /**
     * 删除自动应答
     * @param autoReplyId
     * @return
     */
    ResultVo deleteAutoReply(String autoReplyId);

    /**
     * 修改房源租金/看房时间
     * @param condition
     * @return
     */
    ResultVo updateHousingInfo(Map<Object,Object> condition);

    /**
     * 查询预约看房时间
     * @param condition
     * @return
     */
    ResultVo getHouseSettingTime(Map<Object,Object> condition);

    /**
     * 意见反馈列表
     * @param condition
     * @return
     */
    ResultVo getFeedbackList(Map<Object, Object> condition);

    /**
     * 意见反馈详情
     * @param id 反馈id
     * @return
     */
    ResultVo getFeedbackDetail(Integer id);

    /**
     * 修改意见反馈（增加备注、处理反馈）
     * @param hsFeedback
     * @return
     */
    ResultVo updateFeedback(HsFeedback hsFeedback);

    /**
     * 获取房源下架详情
     * @param id  下架id
     * @return
     */
    ResultVo getObtained(Integer id);

    /**
     * 修改房源下架信息（增加备注、取消下架、完成下架）
     * @param hsHouseObtained 房源下架申请
     * @return
     */
    ResultVo updateObtained(HsHouseObtained hsHouseObtained);

    /**
     * 房源下架申请列表
     * @param condition
     * @return
     */
    ResultVo getObtainedList(Map<Object, Object> condition);

    /**
     * 获取开发商直售项目申购人员列表
     * @param condition
     * @return
     */
    ResultVo getBuildingMemberList(Map<Object, Object> condition);

    /**
     * 导出开发商直售项目申购人员列表
     * @param condition
     * @return
     */
    XSSFWorkbook buildingMemberExport(Map<Object, Object> condition);

    /**
     * 关单记录
     * @param condition
     * @return
     */
    ResultVo closeOrderList(Map<Object, Object> condition);

    /**
     * 搜索房源信息
     * @param condition
     * @return
     */
    ResultVo searchHouse(Map<Object, Object> condition);

    /**
     * 下架房源
     * @param condition
     * @return
     */
    ResultVo houseObtained(Map<Object, Object> condition);

    /**
     * 房源下架申请
     * @param houseObtained
     * @return
     */
    ResultVo houseApplyWithdraw(HsHouseObtained houseObtained);
}
