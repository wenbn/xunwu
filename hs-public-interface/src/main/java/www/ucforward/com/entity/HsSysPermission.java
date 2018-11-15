package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_sys_permission
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsSysPermission implements Serializable {
    /**
     * Database Column Remarks:
     *   主键
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   资源名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.PERMISSION_NAME
     *
     * @mbg.generated
     */
    private String permissionName;

    /**
     * Database Column Remarks:
     *   资源类型：1：menu,2：button
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.PERMISSION_TYPE
     *
     * @mbg.generated
     */
    private Integer permissionType;

    /**
     * Database Column Remarks:
     *   访问url地址
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.PERMISSION_URL
     *
     * @mbg.generated
     */
    private String permissionUrl;

    /**
     * Database Column Remarks:
     *   权限代码字符串,权限标示，资源：操作：实例
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.PERMISSION_CODE
     *
     * @mbg.generated
     */
    private String permissionCode;

    /**
     * Database Column Remarks:
     *   父结点id,默认为-1
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.PARENT_ID
     *
     * @mbg.generated
     */
    private Integer parentId;

    /**
     * Database Column Remarks:
     *   当前级
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.CURRENT_LEVEL
     *
     * @mbg.generated
     */
    private Integer currentLevel;

    /**
     * Database Column Remarks:
     *   排序号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.SORT_NO
     *
     * @mbg.generated
     */
    private String sortNo;

    /**
     * Database Column Remarks:
     *   是否禁用,0：启用，1禁用
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.IS_FORBIDDEN
     *
     * @mbg.generated
     */
    private Integer isForbidden;

    /**
     * Database Column Remarks:
     *   是否删除,0：未删除，1删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * Database Column Remarks:
     *   图标
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.ICON
     *
     * @mbg.generated
     */
    private String icon;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.CREATE_BY
     *
     * @mbg.generated
     */
    private String createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.UPDATE_BY
     *
     * @mbg.generated
     */
    private String updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_sys_permission.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.ID
     *
     * @return the value of hs_sys_permission.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.ID
     *
     * @param id the value for hs_sys_permission.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.PERMISSION_NAME
     *
     * @return the value of hs_sys_permission.PERMISSION_NAME
     *
     * @mbg.generated
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.PERMISSION_NAME
     *
     * @param permissionName the value for hs_sys_permission.PERMISSION_NAME
     *
     * @mbg.generated
     */
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName == null ? null : permissionName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.PERMISSION_TYPE
     *
     * @return the value of hs_sys_permission.PERMISSION_TYPE
     *
     * @mbg.generated
     */
    public Integer getPermissionType() {
        return permissionType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.PERMISSION_TYPE
     *
     * @param permissionType the value for hs_sys_permission.PERMISSION_TYPE
     *
     * @mbg.generated
     */
    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.PERMISSION_URL
     *
     * @return the value of hs_sys_permission.PERMISSION_URL
     *
     * @mbg.generated
     */
    public String getPermissionUrl() {
        return permissionUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.PERMISSION_URL
     *
     * @param permissionUrl the value for hs_sys_permission.PERMISSION_URL
     *
     * @mbg.generated
     */
    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl == null ? null : permissionUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.PERMISSION_CODE
     *
     * @return the value of hs_sys_permission.PERMISSION_CODE
     *
     * @mbg.generated
     */
    public String getPermissionCode() {
        return permissionCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.PERMISSION_CODE
     *
     * @param permissionCode the value for hs_sys_permission.PERMISSION_CODE
     *
     * @mbg.generated
     */
    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode == null ? null : permissionCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.PARENT_ID
     *
     * @return the value of hs_sys_permission.PARENT_ID
     *
     * @mbg.generated
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.PARENT_ID
     *
     * @param parentId the value for hs_sys_permission.PARENT_ID
     *
     * @mbg.generated
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.CURRENT_LEVEL
     *
     * @return the value of hs_sys_permission.CURRENT_LEVEL
     *
     * @mbg.generated
     */
    public Integer getCurrentLevel() {
        return currentLevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.CURRENT_LEVEL
     *
     * @param currentLevel the value for hs_sys_permission.CURRENT_LEVEL
     *
     * @mbg.generated
     */
    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.SORT_NO
     *
     * @return the value of hs_sys_permission.SORT_NO
     *
     * @mbg.generated
     */
    public String getSortNo() {
        return sortNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.SORT_NO
     *
     * @param sortNo the value for hs_sys_permission.SORT_NO
     *
     * @mbg.generated
     */
    public void setSortNo(String sortNo) {
        this.sortNo = sortNo == null ? null : sortNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.IS_FORBIDDEN
     *
     * @return the value of hs_sys_permission.IS_FORBIDDEN
     *
     * @mbg.generated
     */
    public Integer getIsForbidden() {
        return isForbidden;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.IS_FORBIDDEN
     *
     * @param isForbidden the value for hs_sys_permission.IS_FORBIDDEN
     *
     * @mbg.generated
     */
    public void setIsForbidden(Integer isForbidden) {
        this.isForbidden = isForbidden;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.IS_DEL
     *
     * @return the value of hs_sys_permission.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.IS_DEL
     *
     * @param isDel the value for hs_sys_permission.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.ICON
     *
     * @return the value of hs_sys_permission.ICON
     *
     * @mbg.generated
     */
    public String getIcon() {
        return icon;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.ICON
     *
     * @param icon the value for hs_sys_permission.ICON
     *
     * @mbg.generated
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.CREATE_BY
     *
     * @return the value of hs_sys_permission.CREATE_BY
     *
     * @mbg.generated
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.CREATE_BY
     *
     * @param createBy the value for hs_sys_permission.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.CREATE_TIME
     *
     * @return the value of hs_sys_permission.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.CREATE_TIME
     *
     * @param createTime the value for hs_sys_permission.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.UPDATE_BY
     *
     * @return the value of hs_sys_permission.UPDATE_BY
     *
     * @mbg.generated
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.UPDATE_BY
     *
     * @param updateBy the value for hs_sys_permission.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.UPDATE_TIME
     *
     * @return the value of hs_sys_permission.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.UPDATE_TIME
     *
     * @param updateTime the value for hs_sys_permission.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.STANDBY1
     *
     * @return the value of hs_sys_permission.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.STANDBY1
     *
     * @param standby1 the value for hs_sys_permission.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.STANDBY2
     *
     * @return the value of hs_sys_permission.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.STANDBY2
     *
     * @param standby2 the value for hs_sys_permission.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_sys_permission.STANDBY3
     *
     * @return the value of hs_sys_permission.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_sys_permission.STANDBY3
     *
     * @param standby3 the value for hs_sys_permission.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }
}