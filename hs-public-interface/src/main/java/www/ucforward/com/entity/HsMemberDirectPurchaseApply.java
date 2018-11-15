package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_member_direct_purchase_apply
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsMemberDirectPurchaseApply implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   申购编号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.CODE
     *
     * @mbg.generated
     */
    private String code;

    /**
     * Database Column Remarks:
     *   用户id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    private Integer memberId;

    /**
     * Database Column Remarks:
     *   开发商直售楼盘id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.BUILDING_ID
     *
     * @mbg.generated
     */
    private Integer buildingId;

    /**
     * Database Column Remarks:
     *   0完善个人信息 1
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.STATUS
     *
     * @mbg.generated
     */
    private Integer status;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建日期
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新日期
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * Database Column Remarks:
     *   是否删除：0：未删除，1：已删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_direct_purchase_apply.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.ID
     *
     * @return the value of hs_member_direct_purchase_apply.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.ID
     *
     * @param id the value for hs_member_direct_purchase_apply.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.CODE
     *
     * @return the value of hs_member_direct_purchase_apply.CODE
     *
     * @mbg.generated
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.CODE
     *
     * @param code the value for hs_member_direct_purchase_apply.CODE
     *
     * @mbg.generated
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.MEMBER_ID
     *
     * @return the value of hs_member_direct_purchase_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.MEMBER_ID
     *
     * @param memberId the value for hs_member_direct_purchase_apply.MEMBER_ID
     *
     * @mbg.generated
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.BUILDING_ID
     *
     * @return the value of hs_member_direct_purchase_apply.BUILDING_ID
     *
     * @mbg.generated
     */
    public Integer getBuildingId() {
        return buildingId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.BUILDING_ID
     *
     * @param buildingId the value for hs_member_direct_purchase_apply.BUILDING_ID
     *
     * @mbg.generated
     */
    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.STATUS
     *
     * @return the value of hs_member_direct_purchase_apply.STATUS
     *
     * @mbg.generated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.STATUS
     *
     * @param status the value for hs_member_direct_purchase_apply.STATUS
     *
     * @mbg.generated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.CREATE_BY
     *
     * @return the value of hs_member_direct_purchase_apply.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.CREATE_BY
     *
     * @param createBy the value for hs_member_direct_purchase_apply.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.CREATE_TIME
     *
     * @return the value of hs_member_direct_purchase_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.CREATE_TIME
     *
     * @param createTime the value for hs_member_direct_purchase_apply.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.UPDATE_BY
     *
     * @return the value of hs_member_direct_purchase_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.UPDATE_BY
     *
     * @param updateBy the value for hs_member_direct_purchase_apply.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.UPDATE_TIME
     *
     * @return the value of hs_member_direct_purchase_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.UPDATE_TIME
     *
     * @param updateTime the value for hs_member_direct_purchase_apply.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_direct_purchase_apply.IS_DEL
     *
     * @return the value of hs_member_direct_purchase_apply.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_direct_purchase_apply.IS_DEL
     *
     * @param isDel the value for hs_member_direct_purchase_apply.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}