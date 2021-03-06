package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_housing_order_log
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsHousingOrderLog implements Serializable {
    /**
     * Database Column Remarks:
     *   ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   来源订单主键
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.ORDER_ID
     *
     * @mbg.generated
     */
    private Integer orderId;

    /**
     * Database Column Remarks:
     *   该日志所关联的订单节点类型（如果没有，则为-1；0-待付款->1-已支付->2-财务已审核（待确认线上合同）->3-已确认线上合同->4-线下合同派单->5-已签署合同->6-已完成->7-已取消）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.NODE_TYPE
     *
     * @mbg.generated
     */
    private Integer nodeType;

    /**
     * Database Column Remarks:
     *   操作人用户ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.OPERATOR_UID
     *
     * @mbg.generated
     */
    private Integer operatorUid;

    /**
     * Database Column Remarks:
     *   操作人商家名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.OPERATOR_BNAME
     *
     * @mbg.generated
     */
    private String operatorBname;

    /**
     * Database Column Remarks:
     *   操作人用户名
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.OPERATOR_UNAME
     *
     * @mbg.generated
     */
    private String operatorUname;

    /**
     * Database Column Remarks:
     *   操作人别名
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.OPERATOR_ALIASNAME
     *
     * @mbg.generated
     */
    private String operatorAliasname;

    /**
     * Database Column Remarks:
     *   操作人类型1:普通会员 2:商家 3:系统管理员
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.OPERATOR_TYPE
     *
     * @mbg.generated
     */
    private Integer operatorType;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * Database Column Remarks:
     *   操作时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.POST_TIME
     *
     * @mbg.generated
     */
    private Date postTime;

    /**
     * Database Column Remarks:
     *   是否删除0:不删除，1：删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * Database Column Remarks:
     *   备注
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.REMARKS
     *
     * @mbg.generated
     */
    private String remarks;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_housing_order_log.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.ID
     *
     * @return the value of hs_housing_order_log.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.ID
     *
     * @param id the value for hs_housing_order_log.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.ORDER_ID
     *
     * @return the value of hs_housing_order_log.ORDER_ID
     *
     * @mbg.generated
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.ORDER_ID
     *
     * @param orderId the value for hs_housing_order_log.ORDER_ID
     *
     * @mbg.generated
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.NODE_TYPE
     *
     * @return the value of hs_housing_order_log.NODE_TYPE
     *
     * @mbg.generated
     */
    public Integer getNodeType() {
        return nodeType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.NODE_TYPE
     *
     * @param nodeType the value for hs_housing_order_log.NODE_TYPE
     *
     * @mbg.generated
     */
    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.OPERATOR_UID
     *
     * @return the value of hs_housing_order_log.OPERATOR_UID
     *
     * @mbg.generated
     */
    public Integer getOperatorUid() {
        return operatorUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.OPERATOR_UID
     *
     * @param operatorUid the value for hs_housing_order_log.OPERATOR_UID
     *
     * @mbg.generated
     */
    public void setOperatorUid(Integer operatorUid) {
        this.operatorUid = operatorUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.OPERATOR_BNAME
     *
     * @return the value of hs_housing_order_log.OPERATOR_BNAME
     *
     * @mbg.generated
     */
    public String getOperatorBname() {
        return operatorBname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.OPERATOR_BNAME
     *
     * @param operatorBname the value for hs_housing_order_log.OPERATOR_BNAME
     *
     * @mbg.generated
     */
    public void setOperatorBname(String operatorBname) {
        this.operatorBname = operatorBname == null ? null : operatorBname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.OPERATOR_UNAME
     *
     * @return the value of hs_housing_order_log.OPERATOR_UNAME
     *
     * @mbg.generated
     */
    public String getOperatorUname() {
        return operatorUname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.OPERATOR_UNAME
     *
     * @param operatorUname the value for hs_housing_order_log.OPERATOR_UNAME
     *
     * @mbg.generated
     */
    public void setOperatorUname(String operatorUname) {
        this.operatorUname = operatorUname == null ? null : operatorUname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.OPERATOR_ALIASNAME
     *
     * @return the value of hs_housing_order_log.OPERATOR_ALIASNAME
     *
     * @mbg.generated
     */
    public String getOperatorAliasname() {
        return operatorAliasname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.OPERATOR_ALIASNAME
     *
     * @param operatorAliasname the value for hs_housing_order_log.OPERATOR_ALIASNAME
     *
     * @mbg.generated
     */
    public void setOperatorAliasname(String operatorAliasname) {
        this.operatorAliasname = operatorAliasname == null ? null : operatorAliasname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.OPERATOR_TYPE
     *
     * @return the value of hs_housing_order_log.OPERATOR_TYPE
     *
     * @mbg.generated
     */
    public Integer getOperatorType() {
        return operatorType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.OPERATOR_TYPE
     *
     * @param operatorType the value for hs_housing_order_log.OPERATOR_TYPE
     *
     * @mbg.generated
     */
    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.CREATE_BY
     *
     * @return the value of hs_housing_order_log.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.CREATE_BY
     *
     * @param createBy the value for hs_housing_order_log.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.CREATE_TIME
     *
     * @return the value of hs_housing_order_log.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.CREATE_TIME
     *
     * @param createTime the value for hs_housing_order_log.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.UPDATE_BY
     *
     * @return the value of hs_housing_order_log.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.UPDATE_BY
     *
     * @param updateBy the value for hs_housing_order_log.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.UPDATE_TIME
     *
     * @return the value of hs_housing_order_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.UPDATE_TIME
     *
     * @param updateTime the value for hs_housing_order_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.POST_TIME
     *
     * @return the value of hs_housing_order_log.POST_TIME
     *
     * @mbg.generated
     */
    public Date getPostTime() {
        return postTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.POST_TIME
     *
     * @param postTime the value for hs_housing_order_log.POST_TIME
     *
     * @mbg.generated
     */
    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.IS_DEL
     *
     * @return the value of hs_housing_order_log.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.IS_DEL
     *
     * @param isDel the value for hs_housing_order_log.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.REMARKS
     *
     * @return the value of hs_housing_order_log.REMARKS
     *
     * @mbg.generated
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.REMARKS
     *
     * @param remarks the value for hs_housing_order_log.REMARKS
     *
     * @mbg.generated
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.STANDBY1
     *
     * @return the value of hs_housing_order_log.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.STANDBY1
     *
     * @param standby1 the value for hs_housing_order_log.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.STANDBY2
     *
     * @return the value of hs_housing_order_log.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.STANDBY2
     *
     * @param standby2 the value for hs_housing_order_log.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.STANDBY3
     *
     * @return the value of hs_housing_order_log.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.STANDBY3
     *
     * @param standby3 the value for hs_housing_order_log.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.STANDBY4
     *
     * @return the value of hs_housing_order_log.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.STANDBY4
     *
     * @param standby4 the value for hs_housing_order_log.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_housing_order_log.STANDBY5
     *
     * @return the value of hs_housing_order_log.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_housing_order_log.STANDBY5
     *
     * @param standby5 the value for hs_housing_order_log.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}