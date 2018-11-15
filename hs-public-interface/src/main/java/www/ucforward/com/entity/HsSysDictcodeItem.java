package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_sys_dictcode_item
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsSysDictcodeItem implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   绑定hs_sys_dictcode_group的主键
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.GROUP_ID
     *
     * @mbg.generated
     */
    private Integer groupId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.ITEM_NAME
     *
     * @mbg.generated
     */
    private String itemName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.ITEM_VALUE
     *
     * @mbg.generated
     */
    private String itemValue;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.ITEM_VALUE_EN
     *
     * @mbg.generated
     */
    private String itemValueEn;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * Database Column Remarks:
     *   备注
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   排序
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.SORT
     *
     * @mbg.generated
     */
    private Integer sort;

    /**
     * Database Column Remarks:
     *   背景图
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.BACK_IMG
     *
     * @mbg.generated
     */
    private String backImg;

    /**
     * Database Column Remarks:
     *   是否删除0:不删除，1：删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_dictcode_item.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.ID
     *
     * @return the value of hs_sys_dictcode_item.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.ID
     *
     * @param id the value for hs_sys_dictcode_item.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.GROUP_ID
     *
     * @return the value of hs_sys_dictcode_item.GROUP_ID
     *
     * @mbg.generated
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.GROUP_ID
     *
     * @param groupId the value for hs_sys_dictcode_item.GROUP_ID
     *
     * @mbg.generated
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.ITEM_NAME
     *
     * @return the value of hs_sys_dictcode_item.ITEM_NAME
     *
     * @mbg.generated
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.ITEM_NAME
     *
     * @param itemName the value for hs_sys_dictcode_item.ITEM_NAME
     *
     * @mbg.generated
     */
    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.ITEM_VALUE
     *
     * @return the value of hs_sys_dictcode_item.ITEM_VALUE
     *
     * @mbg.generated
     */
    public String getItemValue() {
        return itemValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.ITEM_VALUE
     *
     * @param itemValue the value for hs_sys_dictcode_item.ITEM_VALUE
     *
     * @mbg.generated
     */
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue == null ? null : itemValue.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.ITEM_VALUE_EN
     *
     * @return the value of hs_sys_dictcode_item.ITEM_VALUE_EN
     *
     * @mbg.generated
     */
    public String getItemValueEn() {
        return itemValueEn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.ITEM_VALUE_EN
     *
     * @param itemValueEn the value for hs_sys_dictcode_item.ITEM_VALUE_EN
     *
     * @mbg.generated
     */
    public void setItemValueEn(String itemValueEn) {
        this.itemValueEn = itemValueEn == null ? null : itemValueEn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.CREATE_BY
     *
     * @return the value of hs_sys_dictcode_item.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.CREATE_BY
     *
     * @param createBy the value for hs_sys_dictcode_item.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.CREATE_TIME
     *
     * @return the value of hs_sys_dictcode_item.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.CREATE_TIME
     *
     * @param createTime the value for hs_sys_dictcode_item.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.UPDATE_BY
     *
     * @return the value of hs_sys_dictcode_item.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.UPDATE_BY
     *
     * @param updateBy the value for hs_sys_dictcode_item.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.UPDATE_TIME
     *
     * @return the value of hs_sys_dictcode_item.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.UPDATE_TIME
     *
     * @param updateTime the value for hs_sys_dictcode_item.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.REMARK
     *
     * @return the value of hs_sys_dictcode_item.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.REMARK
     *
     * @param remark the value for hs_sys_dictcode_item.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.SORT
     *
     * @return the value of hs_sys_dictcode_item.SORT
     *
     * @mbg.generated
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.SORT
     *
     * @param sort the value for hs_sys_dictcode_item.SORT
     *
     * @mbg.generated
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.BACK_IMG
     *
     * @return the value of hs_sys_dictcode_item.BACK_IMG
     *
     * @mbg.generated
     */
    public String getBackImg() {
        return backImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.BACK_IMG
     *
     * @param backImg the value for hs_sys_dictcode_item.BACK_IMG
     *
     * @mbg.generated
     */
    public void setBackImg(String backImg) {
        this.backImg = backImg == null ? null : backImg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.IS_DEL
     *
     * @return the value of hs_sys_dictcode_item.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.IS_DEL
     *
     * @param isDel the value for hs_sys_dictcode_item.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.STANDBY1
     *
     * @return the value of hs_sys_dictcode_item.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.STANDBY1
     *
     * @param standby1 the value for hs_sys_dictcode_item.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.STANDBY2
     *
     * @return the value of hs_sys_dictcode_item.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.STANDBY2
     *
     * @param standby2 the value for hs_sys_dictcode_item.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.STANDBY3
     *
     * @return the value of hs_sys_dictcode_item.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.STANDBY3
     *
     * @param standby3 the value for hs_sys_dictcode_item.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.STANDBY4
     *
     * @return the value of hs_sys_dictcode_item.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.STANDBY4
     *
     * @param standby4 the value for hs_sys_dictcode_item.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_dictcode_item.STANDBY5
     *
     * @return the value of hs_sys_dictcode_item.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_dictcode_item.STANDBY5
     *
     * @param standby5 the value for hs_sys_dictcode_item.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}