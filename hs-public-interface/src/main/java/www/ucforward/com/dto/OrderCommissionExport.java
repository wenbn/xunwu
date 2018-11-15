package www.ucforward.com.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 成单结佣导出
 * @Auther: lsq
 * @Date: 2018/9/30 16:01
 * @Description:
 */
public class OrderCommissionExport implements Serializable {
    /**
     * 主键ID
     */
    public Integer commissionId;
    /**
     * 订单编码
     */
    public String orderCode;
    /**
     * 订单类型 0-租房->1-买房
     */
    public String orderType;
    /**
     * 客服结佣金额
     */
    public BigDecimal customerServiceFee;
    /**
     * 外获结佣金额
     */
    public BigDecimal sellerAssistantFee;
    /**
     * 区域长实勘结佣金额
     */
    public BigDecimal regionLeaderFee;
    /**
     * 区域长送钥匙结佣金额
     */
    public BigDecimal regionLeaderTakeKeyFee;
    /**
     * 外看结佣金额
     */
    public BigDecimal buyerAssistantFee;
    /**
     * 内勤结佣金额
     */
    public BigDecimal internalAssistantFee;
    /**
     * 中介金额（过户费）
     */
    public BigDecimal transferFee;
    /**
     * 其它结佣金额
     */
    public BigDecimal elseAssistantAmount;
    /**
     * 是否完成结算 0:未结算，1：已结算
     */
    public Integer isSettleAccounts;
    /**
     * 是否审核 0:未审核，1：已审核
     */
    public Integer isCheck;
    /**
     * 备注
     */
    public String remark;
    /**
     * 创建时间
     */
    public Date createTime;

    public Integer getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(Integer commissionId) {
        this.commissionId = commissionId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getCustomerServiceFee() {
        return customerServiceFee;
    }

    public void setCustomerServiceFee(BigDecimal customerServiceFee) {
        this.customerServiceFee = customerServiceFee;
    }

    public BigDecimal getSellerAssistantFee() {
        return sellerAssistantFee;
    }

    public void setSellerAssistantFee(BigDecimal sellerAssistantFee) {
        this.sellerAssistantFee = sellerAssistantFee;
    }

    public BigDecimal getRegionLeaderFee() {
        return regionLeaderFee;
    }

    public void setRegionLeaderFee(BigDecimal regionLeaderFee) {
        this.regionLeaderFee = regionLeaderFee;
    }

    public BigDecimal getRegionLeaderTakeKeyFee() {
        return regionLeaderTakeKeyFee;
    }

    public void setRegionLeaderTakeKeyFee(BigDecimal regionLeaderTakeKeyFee) {
        this.regionLeaderTakeKeyFee = regionLeaderTakeKeyFee;
    }

    public BigDecimal getBuyerAssistantFee() {
        return buyerAssistantFee;
    }

    public void setBuyerAssistantFee(BigDecimal buyerAssistantFee) {
        this.buyerAssistantFee = buyerAssistantFee;
    }

    public BigDecimal getInternalAssistantFee() {
        return internalAssistantFee;
    }

    public void setInternalAssistantFee(BigDecimal internalAssistantFee) {
        this.internalAssistantFee = internalAssistantFee;
    }

    public BigDecimal getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }

    public BigDecimal getElseAssistantAmount() {
        return elseAssistantAmount;
    }

    public void setElseAssistantAmount(BigDecimal elseAssistantAmount) {
        this.elseAssistantAmount = elseAssistantAmount;
    }

    public Integer getIsSettleAccounts() {
        return isSettleAccounts;
    }

    public void setIsSettleAccounts(Integer isSettleAccounts) {
        this.isSettleAccounts = isSettleAccounts;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
