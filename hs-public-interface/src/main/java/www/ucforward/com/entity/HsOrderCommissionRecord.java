package www.ucforward.com.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_order_commission_record
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsOrderCommissionRecord implements Serializable{
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   订单id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.ORDER_ID
     *
     * @mbg.generated
     */
    private Integer orderId;

    /**
     * Database Column Remarks:
     *   订单编码
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.ORDER_CODE
     *
     * @mbg.generated
     */
    private String orderCode;

    /**
     * Database Column Remarks:
     *   订单类型 0-租房->1-买房
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.ORDER_TYPE
     *
     * @mbg.generated
     */
    private Integer orderType;

    /**
     * Database Column Remarks:
     *   房源ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.HOUSE_ID
     *
     * @mbg.generated
     */
    private Integer houseId;

    /**
     * Database Column Remarks:
     *   订单金额（平台收取的服务费）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.PLATFORM_SERVICE_AMOUNT
     *
     * @mbg.generated
     */
    private BigDecimal platformServiceAmount;

    /**
     * Database Column Remarks:
     *   客服结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.CUSTOMER_SERVICE_FEE
     *
     * @mbg.generated
     */
    private BigDecimal customerServiceFee;

    /**
     * Database Column Remarks:
     *   外获结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.SELLER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    private BigDecimal sellerAssistantFee;

    /**
     * Database Column Remarks:
     *   区域长实勘结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.REGION_LEADER_FEE
     *
     * @mbg.generated
     */
    private BigDecimal regionLeaderFee;

    /**
     * Database Column Remarks:
     *   区域长送钥匙结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.REGION_LEADER_TAKE_KEY_FEE
     *
     * @mbg.generated
     */
    private BigDecimal regionLeaderTakeKeyFee;

    /**
     * Database Column Remarks:
     *   外看结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.BUYER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    private BigDecimal buyerAssistantFee;

    /**
     * Database Column Remarks:
     *   内勤结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.INTERNAL_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    private BigDecimal internalAssistantFee;

    /**
     * Database Column Remarks:
     *   过户费
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.TRANSFER_FEE
     *
     * @mbg.generated
     */
    private BigDecimal transferFee;

    /**
     * Database Column Remarks:
     *   其它结佣金额
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.ELSE_ASSISTANT_AMOUNT
     *
     * @mbg.generated
     */
    private BigDecimal elseAssistantAmount;

    /**
     * Database Column Remarks:
     *   客服ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.USER_ID1
     *
     * @mbg.generated
     */
    private Integer userId1;

    /**
     * Database Column Remarks:
     *   外获业务员id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.USER_ID2
     *
     * @mbg.generated
     */
    private Integer userId2;

    /**
     * Database Column Remarks:
     *   区域长id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.USER_ID3
     *
     * @mbg.generated
     */
    private Integer userId3;

    /**
     * Database Column Remarks:
     *   外看业务员id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.USER_ID4
     *
     * @mbg.generated
     */
    private Integer userId4;

    /**
     * Database Column Remarks:
     *   内勤业务员id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.USER_ID5
     *
     * @mbg.generated
     */
    private Integer userId5;

    /**
     * Database Column Remarks:
     *   是否完成结算0:未结算，1：已结算
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.IS_SETTLE_ACCOUNTS
     *
     * @mbg.generated
     */
    private Integer isSettleAccounts;

    /**
     * Database Column Remarks:
     *   是否审核0:未审核，1：已审核
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.IS_CHECK
     *
     * @mbg.generated
     */
    private Integer isCheck;

    /**
     * Database Column Remarks:
     *   备注描述
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_order_commission_record.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.ID
     *
     * @return the value of hs_order_commission_record.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.ID
     *
     * @param id the value for hs_order_commission_record.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.ORDER_ID
     *
     * @return the value of hs_order_commission_record.ORDER_ID
     *
     * @mbg.generated
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.ORDER_ID
     *
     * @param orderId the value for hs_order_commission_record.ORDER_ID
     *
     * @mbg.generated
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.ORDER_CODE
     *
     * @return the value of hs_order_commission_record.ORDER_CODE
     *
     * @mbg.generated
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.ORDER_CODE
     *
     * @param orderCode the value for hs_order_commission_record.ORDER_CODE
     *
     * @mbg.generated
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.ORDER_TYPE
     *
     * @return the value of hs_order_commission_record.ORDER_TYPE
     *
     * @mbg.generated
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.ORDER_TYPE
     *
     * @param orderType the value for hs_order_commission_record.ORDER_TYPE
     *
     * @mbg.generated
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.HOUSE_ID
     *
     * @return the value of hs_order_commission_record.HOUSE_ID
     *
     * @mbg.generated
     */
    public Integer getHouseId() {
        return houseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.HOUSE_ID
     *
     * @param houseId the value for hs_order_commission_record.HOUSE_ID
     *
     * @mbg.generated
     */
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.PLATFORM_SERVICE_AMOUNT
     *
     * @return the value of hs_order_commission_record.PLATFORM_SERVICE_AMOUNT
     *
     * @mbg.generated
     */
    public BigDecimal getPlatformServiceAmount() {
        return platformServiceAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.PLATFORM_SERVICE_AMOUNT
     *
     * @param platformServiceAmount the value for hs_order_commission_record.PLATFORM_SERVICE_AMOUNT
     *
     * @mbg.generated
     */
    public void setPlatformServiceAmount(BigDecimal platformServiceAmount) {
        this.platformServiceAmount = platformServiceAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.CUSTOMER_SERVICE_FEE
     *
     * @return the value of hs_order_commission_record.CUSTOMER_SERVICE_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getCustomerServiceFee() {
        return customerServiceFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.CUSTOMER_SERVICE_FEE
     *
     * @param customerServiceFee the value for hs_order_commission_record.CUSTOMER_SERVICE_FEE
     *
     * @mbg.generated
     */
    public void setCustomerServiceFee(BigDecimal customerServiceFee) {
        this.customerServiceFee = customerServiceFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.SELLER_ASSISTANT_FEE
     *
     * @return the value of hs_order_commission_record.SELLER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getSellerAssistantFee() {
        return sellerAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.SELLER_ASSISTANT_FEE
     *
     * @param sellerAssistantFee the value for hs_order_commission_record.SELLER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public void setSellerAssistantFee(BigDecimal sellerAssistantFee) {
        this.sellerAssistantFee = sellerAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.REGION_LEADER_FEE
     *
     * @return the value of hs_order_commission_record.REGION_LEADER_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getRegionLeaderFee() {
        return regionLeaderFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.REGION_LEADER_FEE
     *
     * @param regionLeaderFee the value for hs_order_commission_record.REGION_LEADER_FEE
     *
     * @mbg.generated
     */
    public void setRegionLeaderFee(BigDecimal regionLeaderFee) {
        this.regionLeaderFee = regionLeaderFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.REGION_LEADER_TAKE_KEY_FEE
     *
     * @return the value of hs_order_commission_record.REGION_LEADER_TAKE_KEY_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getRegionLeaderTakeKeyFee() {
        return regionLeaderTakeKeyFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.REGION_LEADER_TAKE_KEY_FEE
     *
     * @param regionLeaderTakeKeyFee the value for hs_order_commission_record.REGION_LEADER_TAKE_KEY_FEE
     *
     * @mbg.generated
     */
    public void setRegionLeaderTakeKeyFee(BigDecimal regionLeaderTakeKeyFee) {
        this.regionLeaderTakeKeyFee = regionLeaderTakeKeyFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.BUYER_ASSISTANT_FEE
     *
     * @return the value of hs_order_commission_record.BUYER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getBuyerAssistantFee() {
        return buyerAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.BUYER_ASSISTANT_FEE
     *
     * @param buyerAssistantFee the value for hs_order_commission_record.BUYER_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public void setBuyerAssistantFee(BigDecimal buyerAssistantFee) {
        this.buyerAssistantFee = buyerAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.INTERNAL_ASSISTANT_FEE
     *
     * @return the value of hs_order_commission_record.INTERNAL_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getInternalAssistantFee() {
        return internalAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.INTERNAL_ASSISTANT_FEE
     *
     * @param internalAssistantFee the value for hs_order_commission_record.INTERNAL_ASSISTANT_FEE
     *
     * @mbg.generated
     */
    public void setInternalAssistantFee(BigDecimal internalAssistantFee) {
        this.internalAssistantFee = internalAssistantFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.TRANSFER_FEE
     *
     * @return the value of hs_order_commission_record.TRANSFER_FEE
     *
     * @mbg.generated
     */
    public BigDecimal getTransferFee() {
        return transferFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.TRANSFER_FEE
     *
     * @param transferFee the value for hs_order_commission_record.TRANSFER_FEE
     *
     * @mbg.generated
     */
    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.ELSE_ASSISTANT_AMOUNT
     *
     * @return the value of hs_order_commission_record.ELSE_ASSISTANT_AMOUNT
     *
     * @mbg.generated
     */
    public BigDecimal getElseAssistantAmount() {
        return elseAssistantAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.ELSE_ASSISTANT_AMOUNT
     *
     * @param elseAssistantAmount the value for hs_order_commission_record.ELSE_ASSISTANT_AMOUNT
     *
     * @mbg.generated
     */
    public void setElseAssistantAmount(BigDecimal elseAssistantAmount) {
        this.elseAssistantAmount = elseAssistantAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.USER_ID1
     *
     * @return the value of hs_order_commission_record.USER_ID1
     *
     * @mbg.generated
     */
    public Integer getUserId1() {
        return userId1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.USER_ID1
     *
     * @param userId1 the value for hs_order_commission_record.USER_ID1
     *
     * @mbg.generated
     */
    public void setUserId1(Integer userId1) {
        this.userId1 = userId1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.USER_ID2
     *
     * @return the value of hs_order_commission_record.USER_ID2
     *
     * @mbg.generated
     */
    public Integer getUserId2() {
        return userId2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.USER_ID2
     *
     * @param userId2 the value for hs_order_commission_record.USER_ID2
     *
     * @mbg.generated
     */
    public void setUserId2(Integer userId2) {
        this.userId2 = userId2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.USER_ID3
     *
     * @return the value of hs_order_commission_record.USER_ID3
     *
     * @mbg.generated
     */
    public Integer getUserId3() {
        return userId3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.USER_ID3
     *
     * @param userId3 the value for hs_order_commission_record.USER_ID3
     *
     * @mbg.generated
     */
    public void setUserId3(Integer userId3) {
        this.userId3 = userId3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.USER_ID4
     *
     * @return the value of hs_order_commission_record.USER_ID4
     *
     * @mbg.generated
     */
    public Integer getUserId4() {
        return userId4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.USER_ID4
     *
     * @param userId4 the value for hs_order_commission_record.USER_ID4
     *
     * @mbg.generated
     */
    public void setUserId4(Integer userId4) {
        this.userId4 = userId4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.USER_ID5
     *
     * @return the value of hs_order_commission_record.USER_ID5
     *
     * @mbg.generated
     */
    public Integer getUserId5() {
        return userId5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.USER_ID5
     *
     * @param userId5 the value for hs_order_commission_record.USER_ID5
     *
     * @mbg.generated
     */
    public void setUserId5(Integer userId5) {
        this.userId5 = userId5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.IS_SETTLE_ACCOUNTS
     *
     * @return the value of hs_order_commission_record.IS_SETTLE_ACCOUNTS
     *
     * @mbg.generated
     */
    public Integer getIsSettleAccounts() {
        return isSettleAccounts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.IS_SETTLE_ACCOUNTS
     *
     * @param isSettleAccounts the value for hs_order_commission_record.IS_SETTLE_ACCOUNTS
     *
     * @mbg.generated
     */
    public void setIsSettleAccounts(Integer isSettleAccounts) {
        this.isSettleAccounts = isSettleAccounts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.IS_CHECK
     *
     * @return the value of hs_order_commission_record.IS_CHECK
     *
     * @mbg.generated
     */
    public Integer getIsCheck() {
        return isCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.IS_CHECK
     *
     * @param isCheck the value for hs_order_commission_record.IS_CHECK
     *
     * @mbg.generated
     */
    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.REMARK
     *
     * @return the value of hs_order_commission_record.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.REMARK
     *
     * @param remark the value for hs_order_commission_record.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.CREATE_BY
     *
     * @return the value of hs_order_commission_record.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.CREATE_BY
     *
     * @param createBy the value for hs_order_commission_record.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.CREATE_TIME
     *
     * @return the value of hs_order_commission_record.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.CREATE_TIME
     *
     * @param createTime the value for hs_order_commission_record.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.UPDATE_BY
     *
     * @return the value of hs_order_commission_record.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.UPDATE_BY
     *
     * @param updateBy the value for hs_order_commission_record.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.UPDATE_TIME
     *
     * @return the value of hs_order_commission_record.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.UPDATE_TIME
     *
     * @param updateTime the value for hs_order_commission_record.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.STANDBY1
     *
     * @return the value of hs_order_commission_record.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.STANDBY1
     *
     * @param standby1 the value for hs_order_commission_record.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.STANDBY2
     *
     * @return the value of hs_order_commission_record.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.STANDBY2
     *
     * @param standby2 the value for hs_order_commission_record.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.STANDBY3
     *
     * @return the value of hs_order_commission_record.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.STANDBY3
     *
     * @param standby3 the value for hs_order_commission_record.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.STANDBY4
     *
     * @return the value of hs_order_commission_record.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.STANDBY4
     *
     * @param standby4 the value for hs_order_commission_record.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_order_commission_record.STANDBY5
     *
     * @return the value of hs_order_commission_record.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_order_commission_record.STANDBY5
     *
     * @param standby5 the value for hs_order_commission_record.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}