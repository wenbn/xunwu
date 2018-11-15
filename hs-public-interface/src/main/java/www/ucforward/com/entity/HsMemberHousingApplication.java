package www.ucforward.com.entity;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_member_housing_application
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsMemberHousingApplication implements Serializable {
    /**
     * Database Column Remarks:
     *   预约申请id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   语言版本(0:中文,1:英文)默认为0
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    private Integer languageVersion;

    /**
     * Database Column Remarks:
     *   环信群ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.GROUP_ID
     *
     * @mbg.generated
     */
    private String groupId;

    /**
     * Database Column Remarks:
     *   环信群名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.GROUP_NAME
     *
     * @mbg.generated
     */
    private String groupName;

    /**
     * Database Column Remarks:
     *   房源id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.HOUSE_ID
     *
     * @mbg.generated
     */
    private Integer houseId;

    /**
     * Database Column Remarks:
     *   房屋类型（0 房屋出租 1房屋出售）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.HOUSE_TYPE
     *
     * @mbg.generated
     */
    private Integer houseType;

    /**
     * Database Column Remarks:
     *   会员ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.MEMBER_ID
     *
     * @mbg.generated
     */
    private Integer memberId;

    /**
     * Database Column Remarks:
     *   业主ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.OWNER_ID
     *
     * @mbg.generated
     */
    private Integer ownerId;

    /**
     * Database Column Remarks:
     *   开始看房时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.START_APARTMENT_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startApartmentTime;

    /**
     * Database Column Remarks:
     *   结束看房时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.END_APARTMENT_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endApartmentTime;

    /**
     * Database Column Remarks:
     *   是否取消0:不取消，1：客户取消 2：业主取消
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.IS_CANCEL
     *
     * @mbg.generated
     */
    private Integer isCancel;

    /**
     * Database Column Remarks:
     *   系统派单完成 0:未完成 ，1：已完成
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.IS_FINISH
     *
     * @mbg.generated
     */
    private Integer isFinish;

    /**
     * Database Column Remarks:
     *   预约类型（0：申请预约，1：无需预约，参与看房，2：让客服联系）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.APPLICATION_TYPE
     *
     * @mbg.generated
     */
    private Integer applicationType;

    /**
     * Database Column Remarks:
     *   是否处理：0>客服无需处理，1>客服待处理，2>客服已处理
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.IS_DISPOSE
     *
     * @mbg.generated
     */
    private Integer isDispose;

    /**
     * Database Column Remarks:
     *   备注
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.CREATE_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.UPDATE_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * Database Column Remarks:
     *   poolID 订单池ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_housing_application.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.ID
     *
     * @return the value of hs_member_housing_application.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.ID
     *
     * @param id the value for hs_member_housing_application.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.LANGUAGE_VERSION
     *
     * @return the value of hs_member_housing_application.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    public Integer getLanguageVersion() {
        return languageVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.LANGUAGE_VERSION
     *
     * @param languageVersion the value for hs_member_housing_application.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    public void setLanguageVersion(Integer languageVersion) {
        this.languageVersion = languageVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.GROUP_ID
     *
     * @return the value of hs_member_housing_application.GROUP_ID
     *
     * @mbg.generated
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.GROUP_ID
     *
     * @param groupId the value for hs_member_housing_application.GROUP_ID
     *
     * @mbg.generated
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.GROUP_NAME
     *
     * @return the value of hs_member_housing_application.GROUP_NAME
     *
     * @mbg.generated
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.GROUP_NAME
     *
     * @param groupName the value for hs_member_housing_application.GROUP_NAME
     *
     * @mbg.generated
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.HOUSE_ID
     *
     * @return the value of hs_member_housing_application.HOUSE_ID
     *
     * @mbg.generated
     */
    public Integer getHouseId() {
        return houseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.HOUSE_ID
     *
     * @param houseId the value for hs_member_housing_application.HOUSE_ID
     *
     * @mbg.generated
     */
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.HOUSE_TYPE
     *
     * @return the value of hs_member_housing_application.HOUSE_TYPE
     *
     * @mbg.generated
     */
    public Integer getHouseType() {
        return houseType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.HOUSE_TYPE
     *
     * @param houseType the value for hs_member_housing_application.HOUSE_TYPE
     *
     * @mbg.generated
     */
    public void setHouseType(Integer houseType) {
        this.houseType = houseType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.MEMBER_ID
     *
     * @return the value of hs_member_housing_application.MEMBER_ID
     *
     * @mbg.generated
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.MEMBER_ID
     *
     * @param memberId the value for hs_member_housing_application.MEMBER_ID
     *
     * @mbg.generated
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.OWNER_ID
     *
     * @return the value of hs_member_housing_application.OWNER_ID
     *
     * @mbg.generated
     */
    public Integer getOwnerId() {
        return ownerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.OWNER_ID
     *
     * @param ownerId the value for hs_member_housing_application.OWNER_ID
     *
     * @mbg.generated
     */
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.START_APARTMENT_TIME
     *
     * @return the value of hs_member_housing_application.START_APARTMENT_TIME
     *
     * @mbg.generated
     */
    public Date getStartApartmentTime() {
        return startApartmentTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.START_APARTMENT_TIME
     *
     * @param startApartmentTime the value for hs_member_housing_application.START_APARTMENT_TIME
     *
     * @mbg.generated
     */
    public void setStartApartmentTime(Date startApartmentTime) {
        this.startApartmentTime = startApartmentTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.END_APARTMENT_TIME
     *
     * @return the value of hs_member_housing_application.END_APARTMENT_TIME
     *
     * @mbg.generated
     */
    public Date getEndApartmentTime() {
        return endApartmentTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.END_APARTMENT_TIME
     *
     * @param endApartmentTime the value for hs_member_housing_application.END_APARTMENT_TIME
     *
     * @mbg.generated
     */
    public void setEndApartmentTime(Date endApartmentTime) {
        this.endApartmentTime = endApartmentTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.IS_CANCEL
     *
     * @return the value of hs_member_housing_application.IS_CANCEL
     *
     * @mbg.generated
     */
    public Integer getIsCancel() {
        return isCancel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.IS_CANCEL
     *
     * @param isCancel the value for hs_member_housing_application.IS_CANCEL
     *
     * @mbg.generated
     */
    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.IS_FINISH
     *
     * @return the value of hs_member_housing_application.IS_FINISH
     *
     * @mbg.generated
     */
    public Integer getIsFinish() {
        return isFinish;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.IS_FINISH
     *
     * @param isFinish the value for hs_member_housing_application.IS_FINISH
     *
     * @mbg.generated
     */
    public void setIsFinish(Integer isFinish) {
        this.isFinish = isFinish;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.APPLICATION_TYPE
     *
     * @return the value of hs_member_housing_application.APPLICATION_TYPE
     *
     * @mbg.generated
     */
    public Integer getApplicationType() {
        return applicationType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.APPLICATION_TYPE
     *
     * @param applicationType the value for hs_member_housing_application.APPLICATION_TYPE
     *
     * @mbg.generated
     */
    public void setApplicationType(Integer applicationType) {
        this.applicationType = applicationType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.IS_DISPOSE
     *
     * @return the value of hs_member_housing_application.IS_DISPOSE
     *
     * @mbg.generated
     */
    public Integer getIsDispose() {
        return isDispose;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.IS_DISPOSE
     *
     * @param isDispose the value for hs_member_housing_application.IS_DISPOSE
     *
     * @mbg.generated
     */
    public void setIsDispose(Integer isDispose) {
        this.isDispose = isDispose;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.REMARK
     *
     * @return the value of hs_member_housing_application.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.REMARK
     *
     * @param remark the value for hs_member_housing_application.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.CREATE_BY
     *
     * @return the value of hs_member_housing_application.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.CREATE_BY
     *
     * @param createBy the value for hs_member_housing_application.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.CREATE_TIME
     *
     * @return the value of hs_member_housing_application.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.CREATE_TIME
     *
     * @param createTime the value for hs_member_housing_application.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.UPDATE_BY
     *
     * @return the value of hs_member_housing_application.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.UPDATE_BY
     *
     * @param updateBy the value for hs_member_housing_application.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.UPDATE_TIME
     *
     * @return the value of hs_member_housing_application.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.UPDATE_TIME
     *
     * @param updateTime the value for hs_member_housing_application.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.STANDBY1
     *
     * @return the value of hs_member_housing_application.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.STANDBY1
     *
     * @param standby1 the value for hs_member_housing_application.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.STANDBY2
     *
     * @return the value of hs_member_housing_application.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.STANDBY2
     *
     * @param standby2 the value for hs_member_housing_application.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.STANDBY3
     *
     * @return the value of hs_member_housing_application.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.STANDBY3
     *
     * @param standby3 the value for hs_member_housing_application.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.STANDBY4
     *
     * @return the value of hs_member_housing_application.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.STANDBY4
     *
     * @param standby4 the value for hs_member_housing_application.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_housing_application.STANDBY5
     *
     * @return the value of hs_member_housing_application.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_housing_application.STANDBY5
     *
     * @param standby5 the value for hs_member_housing_application.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}