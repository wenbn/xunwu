package www.ucforward.com.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 导出退款列表
 * @Auther: lsq
 * @Date: 2018/9/30 16:41
 * @Description:
 */
public class RefundExport implements Serializable {
    /**
     * 主键ID
     */
    public Integer refundId;
    /**
     * 订单编码
     */
    public String orderCode;
    /**
     * 订单类型 0-租房->1-买房
     */
    public Integer orderType;
    /**
     * 订单总金额
     */
    public BigDecimal orderAmount;
    /**
     * 平台服务费
     */
    public BigDecimal platformServiceAmount;
    /**
     * 应退金额
     */
    public BigDecimal refundableAmount;
    /**
     * 是否退款 0:未退款，1：已退款
     */
    public Integer isRefund;
    /**
     * 退款时间
     */
    public Date refundTime;
    /**
     * 备注
     */
    public String remark;
    /**
     * 创建时间
     */
    public Date createTime;

    public Integer getRefundId() {
        return refundId;
    }

    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getPlatformServiceAmount() {
        return platformServiceAmount;
    }

    public void setPlatformServiceAmount(BigDecimal platformServiceAmount) {
        this.platformServiceAmount = platformServiceAmount;
    }

    public BigDecimal getRefundableAmount() {
        return refundableAmount;
    }

    public void setRefundableAmount(BigDecimal refundableAmount) {
        this.refundableAmount = refundableAmount;
    }

    public Integer getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Integer isRefund) {
        this.isRefund = isRefund;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
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
