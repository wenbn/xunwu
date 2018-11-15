package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_booth
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsBooth implements Serializable {
    /**
     * Database Column Remarks:
     *   展位表ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   语言版本（0：中文，1：英文）默认为0
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    private Integer languageVersion;

    /**
     * Database Column Remarks:
     *   展位的名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_NAME
     *
     * @mbg.generated
     */
    private String boothName;

    /**
     * Database Column Remarks:
     *   展位别名
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_ALIAS_NAME
     *
     * @mbg.generated
     */
    private String boothAliasName;

    /**
     * Database Column Remarks:
     *   展位描述
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_DESC
     *
     * @mbg.generated
     */
    private String boothDesc;

    /**
     * Database Column Remarks:
     *   展位类型（1：广告，2：文章，3：友情链接，4：出租，5：出售，6：新楼盘）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_TYPE
     *
     * @mbg.generated
     */
    private Integer boothType;

    /**
     * Database Column Remarks:
     *   展位状态（0：启用，1：禁用）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_STATE
     *
     * @mbg.generated
     */
    private Integer boothState;

    /**
     * Database Column Remarks:
     *   排序
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_SORT
     *
     * @mbg.generated
     */
    private Integer boothSort;

    /**
     * Database Column Remarks:
     *   是否删除0:未删除，1：删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.IS_DEL
     *
     * @mbg.generated
     */
    private Integer isDel;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * Database Column Remarks:
     *   该栏位限制的数据条数0标识不限制
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.DATA_SIZE
     *
     * @mbg.generated
     */
    private Integer dataSize;

    /**
     * Database Column Remarks:
     *   该展位的专属(1:PC, 2:TC, 3:APP)
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BOOTH_EXCLUSIVE
     *
     * @mbg.generated
     */
    private Integer boothExclusive;

    /**
     * Database Column Remarks:
     *   所对应的hs_business主键
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.BID
     *
     * @mbg.generated
     */
    private Integer bid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_booth.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.ID
     *
     * @return the value of hs_booth.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.ID
     *
     * @param id the value for hs_booth.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.LANGUAGE_VERSION
     *
     * @return the value of hs_booth.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    public Integer getLanguageVersion() {
        return languageVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.LANGUAGE_VERSION
     *
     * @param languageVersion the value for hs_booth.LANGUAGE_VERSION
     *
     * @mbg.generated
     */
    public void setLanguageVersion(Integer languageVersion) {
        this.languageVersion = languageVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_NAME
     *
     * @return the value of hs_booth.BOOTH_NAME
     *
     * @mbg.generated
     */
    public String getBoothName() {
        return boothName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_NAME
     *
     * @param boothName the value for hs_booth.BOOTH_NAME
     *
     * @mbg.generated
     */
    public void setBoothName(String boothName) {
        this.boothName = boothName == null ? null : boothName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_ALIAS_NAME
     *
     * @return the value of hs_booth.BOOTH_ALIAS_NAME
     *
     * @mbg.generated
     */
    public String getBoothAliasName() {
        return boothAliasName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_ALIAS_NAME
     *
     * @param boothAliasName the value for hs_booth.BOOTH_ALIAS_NAME
     *
     * @mbg.generated
     */
    public void setBoothAliasName(String boothAliasName) {
        this.boothAliasName = boothAliasName == null ? null : boothAliasName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_DESC
     *
     * @return the value of hs_booth.BOOTH_DESC
     *
     * @mbg.generated
     */
    public String getBoothDesc() {
        return boothDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_DESC
     *
     * @param boothDesc the value for hs_booth.BOOTH_DESC
     *
     * @mbg.generated
     */
    public void setBoothDesc(String boothDesc) {
        this.boothDesc = boothDesc == null ? null : boothDesc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_TYPE
     *
     * @return the value of hs_booth.BOOTH_TYPE
     *
     * @mbg.generated
     */
    public Integer getBoothType() {
        return boothType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_TYPE
     *
     * @param boothType the value for hs_booth.BOOTH_TYPE
     *
     * @mbg.generated
     */
    public void setBoothType(Integer boothType) {
        this.boothType = boothType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_STATE
     *
     * @return the value of hs_booth.BOOTH_STATE
     *
     * @mbg.generated
     */
    public Integer getBoothState() {
        return boothState;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_STATE
     *
     * @param boothState the value for hs_booth.BOOTH_STATE
     *
     * @mbg.generated
     */
    public void setBoothState(Integer boothState) {
        this.boothState = boothState;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_SORT
     *
     * @return the value of hs_booth.BOOTH_SORT
     *
     * @mbg.generated
     */
    public Integer getBoothSort() {
        return boothSort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_SORT
     *
     * @param boothSort the value for hs_booth.BOOTH_SORT
     *
     * @mbg.generated
     */
    public void setBoothSort(Integer boothSort) {
        this.boothSort = boothSort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.IS_DEL
     *
     * @return the value of hs_booth.IS_DEL
     *
     * @mbg.generated
     */
    public Integer getIsDel() {
        return isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.IS_DEL
     *
     * @param isDel the value for hs_booth.IS_DEL
     *
     * @mbg.generated
     */
    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.CREATE_BY
     *
     * @return the value of hs_booth.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.CREATE_BY
     *
     * @param createBy the value for hs_booth.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.CREATE_TIME
     *
     * @return the value of hs_booth.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.CREATE_TIME
     *
     * @param createTime the value for hs_booth.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.UPDATE_BY
     *
     * @return the value of hs_booth.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.UPDATE_BY
     *
     * @param updateBy the value for hs_booth.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.UPDATE_TIME
     *
     * @return the value of hs_booth.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.UPDATE_TIME
     *
     * @param updateTime the value for hs_booth.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.DATA_SIZE
     *
     * @return the value of hs_booth.DATA_SIZE
     *
     * @mbg.generated
     */
    public Integer getDataSize() {
        return dataSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.DATA_SIZE
     *
     * @param dataSize the value for hs_booth.DATA_SIZE
     *
     * @mbg.generated
     */
    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BOOTH_EXCLUSIVE
     *
     * @return the value of hs_booth.BOOTH_EXCLUSIVE
     *
     * @mbg.generated
     */
    public Integer getBoothExclusive() {
        return boothExclusive;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BOOTH_EXCLUSIVE
     *
     * @param boothExclusive the value for hs_booth.BOOTH_EXCLUSIVE
     *
     * @mbg.generated
     */
    public void setBoothExclusive(Integer boothExclusive) {
        this.boothExclusive = boothExclusive;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.BID
     *
     * @return the value of hs_booth.BID
     *
     * @mbg.generated
     */
    public Integer getBid() {
        return bid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.BID
     *
     * @param bid the value for hs_booth.BID
     *
     * @mbg.generated
     */
    public void setBid(Integer bid) {
        this.bid = bid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.STANDBY1
     *
     * @return the value of hs_booth.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.STANDBY1
     *
     * @param standby1 the value for hs_booth.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.STANDBY2
     *
     * @return the value of hs_booth.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.STANDBY2
     *
     * @param standby2 the value for hs_booth.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.STANDBY3
     *
     * @return the value of hs_booth.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.STANDBY3
     *
     * @param standby3 the value for hs_booth.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.STANDBY4
     *
     * @return the value of hs_booth.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.STANDBY4
     *
     * @param standby4 the value for hs_booth.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_booth.STANDBY5
     *
     * @return the value of hs_booth.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_booth.STANDBY5
     *
     * @param standby5 the value for hs_booth.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}