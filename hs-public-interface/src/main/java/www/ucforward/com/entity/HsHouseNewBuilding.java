package www.ucforward.com.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_house_new_building
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsHouseNewBuilding implements Serializable {
    /**
     * Database Column Remarks:
     *   主键ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   项目编号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.PROJECT_CODE
     *
     * @mbg.generated
     */
    private String projectCode;

    /**
     * Database Column Remarks:
     *   项目名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.PROJECT_NAME
     *
     * @mbg.generated
     */
    private String projectName;

    /**
     * Database Column Remarks:
     *   开发商
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.DEVELOPERS
     *
     * @mbg.generated
     */
    private String developers;

    /**
     * 房屋面积
     */
    private BigDecimal houseAcreage;

    /**
     * Database Column Remarks:
     *   城市
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.CITY
     *
     * @mbg.generated
     */
    private String city;

    /**
     * Database Column Remarks:
     *   社区
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.COMMUNITY
     *
     * @mbg.generated
     */
    private String community;

    /**
     * Database Column Remarks:
     *   子社区
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.SUB_COMMUNITY
     *
     * @mbg.generated
     */
    private String subCommunity;

    /**
     * Database Column Remarks:
     *   项目主图
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.PROJECT_MAIN_IMG
     *
     * @mbg.generated
     */
    private String projectMainImg;

    /**
     * 项目主图2
     */
    private String projectMainImg2;

    /**
     * 交房时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    /**
     * Database Column Remarks:
     *   房屋类型(取数据字典)
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.HOUSING_TYPE_DICTCODE
     *
     * @mbg.generated
     */
    private String housingTypeDictcode;

    /**
     * Database Column Remarks:
     *   卧室数量（卧室数量 ，逗号分割）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.BEDROOM_NUM
     *
     * @mbg.generated
     */
    private String bedroomNum;

    /**
     * Database Column Remarks:
     *   最低租金/或出售价
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.MIN_HOUSE_RENT
     *
     * @mbg.generated
     */
    private BigDecimal minHouseRent;

    /**
     * Database Column Remarks:
     *   最高租金/或出售价
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.MAX_HOUSE_RENT
     *
     * @mbg.generated
     */
    private BigDecimal maxHouseRent;

    /**
     * 开盘状态 0未开盘 1已开盘
     */
    private Integer openingStatus;

    /**
     * 开盘时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date openingTime;

    /**
     * RERA_PERMIT_NO
     */
    private String reraPermitNo;

    /**
     * Database Column Remarks:
     *   是否删除,0：未删除，1删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * Database Column Remarks:
     *   备注描述
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.CREATE_TIME
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
     * This field corresponds to the database column hs_house_new_building.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.UPDATE_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_house_new_building.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.ID
     *
     * @return the value of hs_house_new_building.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.ID
     *
     * @param id the value for hs_house_new_building.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.PROJECT_CODE
     *
     * @return the value of hs_house_new_building.PROJECT_CODE
     *
     * @mbg.generated
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.PROJECT_CODE
     *
     * @param projectCode the value for hs_house_new_building.PROJECT_CODE
     *
     * @mbg.generated
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode == null ? null : projectCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.PROJECT_NAME
     *
     * @return the value of hs_house_new_building.PROJECT_NAME
     *
     * @mbg.generated
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.PROJECT_NAME
     *
     * @param projectName the value for hs_house_new_building.PROJECT_NAME
     *
     * @mbg.generated
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.DEVELOPERS
     *
     * @return the value of hs_house_new_building.DEVELOPERS
     *
     * @mbg.generated
     */
    public String getDevelopers() {
        return developers;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.DEVELOPERS
     *
     * @param developers the value for hs_house_new_building.DEVELOPERS
     *
     * @mbg.generated
     */
    public void setDevelopers(String developers) {
        this.developers = developers == null ? null : developers.trim();
    }

    public BigDecimal getHouseAcreage() {
        return houseAcreage;
    }

    public void setHouseAcreage(BigDecimal houseAcreage) {
        this.houseAcreage = houseAcreage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.CITY
     *
     * @return the value of hs_house_new_building.CITY
     *
     * @mbg.generated
     */
    public String getCity() {
        return city;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.CITY
     *
     * @param city the value for hs_house_new_building.CITY
     *
     * @mbg.generated
     */
    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.COMMUNITY
     *
     * @return the value of hs_house_new_building.COMMUNITY
     *
     * @mbg.generated
     */
    public String getCommunity() {
        return community;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.COMMUNITY
     *
     * @param community the value for hs_house_new_building.COMMUNITY
     *
     * @mbg.generated
     */
    public void setCommunity(String community) {
        this.community = community == null ? null : community.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.SUB_COMMUNITY
     *
     * @return the value of hs_house_new_building.SUB_COMMUNITY
     *
     * @mbg.generated
     */
    public String getSubCommunity() {
        return subCommunity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.SUB_COMMUNITY
     *
     * @param subCommunity the value for hs_house_new_building.SUB_COMMUNITY
     *
     * @mbg.generated
     */
    public void setSubCommunity(String subCommunity) {
        this.subCommunity = subCommunity == null ? null : subCommunity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.PROJECT_MAIN_IMG
     *
     * @return the value of hs_house_new_building.PROJECT_MAIN_IMG
     *
     * @mbg.generated
     */
    public String getProjectMainImg() {
        return projectMainImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.PROJECT_MAIN_IMG
     *
     * @param projectMainImg the value for hs_house_new_building.PROJECT_MAIN_IMG
     *
     * @mbg.generated
     */
    public void setProjectMainImg(String projectMainImg) {
        this.projectMainImg = projectMainImg == null ? null : projectMainImg.trim();
    }

    public String getProjectMainImg2() {
        return projectMainImg2;
    }

    public void setProjectMainImg2(String projectMainImg2) {
        this.projectMainImg2 = projectMainImg2;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.HOUSING_TYPE_DICTCODE
     *
     * @return the value of hs_house_new_building.HOUSING_TYPE_DICTCODE
     *
     * @mbg.generated
     */
    public String getHousingTypeDictcode() {
        return housingTypeDictcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.HOUSING_TYPE_DICTCODE
     *
     * @param housingTypeDictcode the value for hs_house_new_building.HOUSING_TYPE_DICTCODE
     *
     * @mbg.generated
     */
    public void setHousingTypeDictcode(String housingTypeDictcode) {
        this.housingTypeDictcode = housingTypeDictcode == null ? null : housingTypeDictcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.BEDROOM_NUM
     *
     * @return the value of hs_house_new_building.BEDROOM_NUM
     *
     * @mbg.generated
     */
    public String getBedroomNum() {
        return bedroomNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.BEDROOM_NUM
     *
     * @param bedroomNum the value for hs_house_new_building.BEDROOM_NUM
     *
     * @mbg.generated
     */
    public void setBedroomNum(String bedroomNum) {
        this.bedroomNum = bedroomNum == null ? null : bedroomNum.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.MIN_HOUSE_RENT
     *
     * @return the value of hs_house_new_building.MIN_HOUSE_RENT
     *
     * @mbg.generated
     */
    public BigDecimal getMinHouseRent() {
        return minHouseRent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.MIN_HOUSE_RENT
     *
     * @param minHouseRent the value for hs_house_new_building.MIN_HOUSE_RENT
     *
     * @mbg.generated
     */
    public void setMinHouseRent(BigDecimal minHouseRent) {
        this.minHouseRent = minHouseRent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.MAX_HOUSE_RENT
     *
     * @return the value of hs_house_new_building.MAX_HOUSE_RENT
     *
     * @mbg.generated
     */
    public BigDecimal getMaxHouseRent() {
        return maxHouseRent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.MAX_HOUSE_RENT
     *
     * @param maxHouseRent the value for hs_house_new_building.MAX_HOUSE_RENT
     *
     * @mbg.generated
     */
    public void setMaxHouseRent(BigDecimal maxHouseRent) {
        this.maxHouseRent = maxHouseRent;
    }

    public Integer getOpeningStatus() {
        return openingStatus;
    }

    public void setOpeningStatus(Integer openingStatus) {
        this.openingStatus = openingStatus;
    }

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public String getReraPermitNo() {
        return reraPermitNo;
    }

    public void setReraPermitNo(String reraPermitNo) {
        this.reraPermitNo = reraPermitNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.IS_DEL
     *
     * @return the value of hs_house_new_building.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.IS_DEL
     *
     * @param isDel the value for hs_house_new_building.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.REMARK
     *
     * @return the value of hs_house_new_building.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.REMARK
     *
     * @param remark the value for hs_house_new_building.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.CREATE_BY
     *
     * @return the value of hs_house_new_building.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.CREATE_BY
     *
     * @param createBy the value for hs_house_new_building.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.CREATE_TIME
     *
     * @return the value of hs_house_new_building.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.CREATE_TIME
     *
     * @param createTime the value for hs_house_new_building.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.UPDATE_BY
     *
     * @return the value of hs_house_new_building.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.UPDATE_BY
     *
     * @param updateBy the value for hs_house_new_building.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.UPDATE_TIME
     *
     * @return the value of hs_house_new_building.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.UPDATE_TIME
     *
     * @param updateTime the value for hs_house_new_building.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.STANDBY1
     *
     * @return the value of hs_house_new_building.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.STANDBY1
     *
     * @param standby1 the value for hs_house_new_building.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.STANDBY2
     *
     * @return the value of hs_house_new_building.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.STANDBY2
     *
     * @param standby2 the value for hs_house_new_building.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.STANDBY3
     *
     * @return the value of hs_house_new_building.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.STANDBY3
     *
     * @param standby3 the value for hs_house_new_building.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.STANDBY4
     *
     * @return the value of hs_house_new_building.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.STANDBY4
     *
     * @param standby4 the value for hs_house_new_building.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_house_new_building.STANDBY5
     *
     * @return the value of hs_house_new_building.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_house_new_building.STANDBY5
     *
     * @param standby5 the value for hs_house_new_building.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}