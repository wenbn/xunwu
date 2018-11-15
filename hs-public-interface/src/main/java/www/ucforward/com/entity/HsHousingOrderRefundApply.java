package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_housing_order_refund_apply
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsHousingOrderRefundApply implements Serializable {
    /**
     * Database Column Remarks:
     *   id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   申请编号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.APPLY_CODE
     *
     * @mbg.generated
     */
    private String applyCode;

    /**
     * Database Column Remarks:
     *   订单id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.ORDER_ID
     *
     * @mbg.generated
     */
    private Integer orderId;

    /**
     * Database Column Remarks:
     *   0:业主申请退款，1：买家申请退款
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.REFUND_TYPE
     *
     * @mbg.generated
     */
    private Integer refundType;

    /**
     * Database Column Remarks:
     *   主管是否审核 0:未审核，1：审核通过 2、审核不通过
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.IS_CHECK
     *
     * @mbg.generated
     */
    private Integer isCheck;

    /**
     * Database Column Remarks:
     *   是否处理 0:未处理，1:已处理
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.IS_HANDLE
     *
     * @mbg.generated
     */
    private Integer isHandle;

    /**
     * Database Column Remarks:
     *   是否删除0:不删除，1:已删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * Database Column Remarks:
     *   用户ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.USER_ID
     *
     * @mbg.generated
     */
    private Integer userId;

    /**
     * Database Column Remarks:
     *   会员ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    private Integer memberId;

    /**
     * Database Column Remarks:
     *   备注
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_refund_apply.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.ID
     *
     * @return the value of hs_housing_order_refund_apply.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.ID
     *
     * @param id the value for hs_housing_order_refund_apply.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.APPLY_CODE
     *
     * @return the value of hs_housing_order_refund_apply.APPLY_CODE
     *
     * @mbg.generated
     */
    public String getApplyCode() {
        return applyCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.APPLY_CODE
     *
     * @param applyCode the value for hs_housing_order_refund_apply.APPLY_CODE
     *
     * @mbg.generated
     */
    public void setApplyCode(String applyCode) {
        this.applyCode = applyCode == null ? null : applyCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.ORDER_ID
     *
     * @return the value of hs_housing_order_refund_apply.ORDER_ID
     *
     * @mbg.generated
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.ORDER_ID
     *
     * @param orderId the value for hs_housing_order_refund_apply.ORDER_ID
     *
     * @mbg.generated
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.REFUND_TYPE
     *
     * @return the value of hs_housing_order_refund_apply.REFUND_TYPE
     *
     * @mbg.generated
     */
    public Integer getRefundType() {
        return refundType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.REFUND_TYPE
     *
     * @param refundType the value for hs_housing_order_refund_apply.REFUND_TYPE
     *
     * @mbg.generated
     */
    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.IS_CHECK
     *
     * @return the value of hs_housing_order_refund_apply.IS_CHECK
     *
     * @mbg.generated
     */
    public Integer getIsCheck() {
        return isCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.IS_CHECK
     *
     * @param isCheck the value for hs_housing_order_refund_apply.IS_CHECK
     *
     * @mbg.generated
     */
    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.IS_HANDLE
     *
     * @return the value of hs_housing_order_refund_apply.IS_HANDLE
     *
     * @mbg.generated
     */
    public Integer getIsHandle() {
        return isHandle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.IS_HANDLE
     *
     * @param isHandle the value for hs_housing_order_refund_apply.IS_HANDLE
     *
     * @mbg.generated
     */
    public void setIsHandle(Integer isHandle) {
        this.isHandle = isHandle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.IS_DEL
     *
     * @return the value of hs_housing_order_refund_apply.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.IS_DEL
     *
     * @param isDel the value for hs_housing_order_refund_apply.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.USER_ID
     *
     * @return the value of hs_housing_order_refund_apply.USER_ID
     *
     * @mbg.generated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.USER_ID
     *
     * @param userId the value for hs_housing_order_refund_apply.USER_ID
     *
     * @mbg.generated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.MEMBER_ID
     *
     * @return the value of hs_housing_order_refund_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.MEMBER_ID
     *
     * @param memberId the value for hs_housing_order_refund_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.REMARK
     *
     * @return the value of hs_housing_order_refund_apply.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.REMARK
     *
     * @param remark the value for hs_housing_order_refund_apply.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.CREATE_BY
     *
     * @return the value of hs_housing_order_refund_apply.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.CREATE_BY
     *
     * @param createBy the value for hs_housing_order_refund_apply.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.CREATE_TIME
     *
     * @return the value of hs_housing_order_refund_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.CREATE_TIME
     *
     * @param createTime the value for hs_housing_order_refund_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.UPDATE_BY
     *
     * @return the value of hs_housing_order_refund_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.UPDATE_BY
     *
     * @param updateBy the value for hs_housing_order_refund_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.UPDATE_TIME
     *
     * @return the value of hs_housing_order_refund_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.UPDATE_TIME
     *
     * @param updateTime the value for hs_housing_order_refund_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.STANDBY1
     *
     * @return the value of hs_housing_order_refund_apply.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.STANDBY1
     *
     * @param standby1 the value for hs_housing_order_refund_apply.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.STANDBY2
     *
     * @return the value of hs_housing_order_refund_apply.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.STANDBY2
     *
     * @param standby2 the value for hs_housing_order_refund_apply.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.STANDBY3
     *
     * @return the value of hs_housing_order_refund_apply.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.STANDBY3
     *
     * @param standby3 the value for hs_housing_order_refund_apply.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.STANDBY4
     *
     * @return the value of hs_housing_order_refund_apply.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.STANDBY4
     *
     * @param standby4 the value for hs_housing_order_refund_apply.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_refund_apply.STANDBY5
     *
     * @return the value of hs_housing_order_refund_apply.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_refund_apply.STANDBY5
     *
     * @param standby5 the value for hs_housing_order_refund_apply.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}