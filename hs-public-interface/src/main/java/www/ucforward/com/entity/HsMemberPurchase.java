package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_member_purchase
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsMemberPurchase implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.ID
     *
     * @mbg.generated
     */
    private Integer id;

    private Integer languageVersion;
    /**
     * Database Column Remarks:
     *   客户id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.MEMBER_ID
     *
     * @mbg.generated
     */
    private Integer memberId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * Database Column Remarks:
     *   客户姓名
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.NAME
     *
     * @mbg.generated
     */
    private String memberName;

    /**
     * Database Column Remarks:
     *   客户电话
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.PHONE
     *
     * @mbg.generated
     */
    private String phone;

    /**
     * Database Column Remarks:
     *   电子邮箱
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.EMAIL
     *
     * @mbg.generated
     */
    private String email;

    /**
     * Database Column Remarks:
     *   身份证号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.ID_CARD
     *
     * @mbg.generated
     */
    private String idCard;

    /**
     * Database Column Remarks:
     *   国籍
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.NATIONALITY
     *
     * @mbg.generated
     */
    private String nationality;

    /**
     * Database Column Remarks:
     *   护照号
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.PASSPORT_NUMBER
     *
     * @mbg.generated
     */
    private String passportNumber;

    /**
     * Database Column Remarks:
     *   护照扫描件
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.PASSPORT_IMG
     *
     * @mbg.generated
     */
    private String passportImg;

    /**
     * Database Column Remarks:
     *   签证扫描件
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.VISA_IMG
     *
     * @mbg.generated
     */
    private String visaImg;

    /**
     * Database Column Remarks:
     *   EID扫描件
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.EID_IMG
     *
     * @mbg.generated
     */
    private String eidImg;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   会员注册时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_member_purchase.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.ID
     *
     * @return the value of hs_member_purchase.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.ID
     *
     * @param id the value for hs_member_purchase.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(Integer languageVersion) {
        this.languageVersion = languageVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.MEMBER_ID
     *
     * @return the value of hs_member_purchase.MEMBER_ID
     *
     * @mbg.generated
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.MEMBER_ID
     *
     * @param memberId the value for hs_member_purchase.MEMBER_ID
     *
     * @mbg.generated
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.PHONE
     *
     * @return the value of hs_member_purchase.PHONE
     *
     * @mbg.generated
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.PHONE
     *
     * @param phone the value for hs_member_purchase.PHONE
     *
     * @mbg.generated
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.EMAIL
     *
     * @return the value of hs_member_purchase.EMAIL
     *
     * @mbg.generated
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.EMAIL
     *
     * @param email the value for hs_member_purchase.EMAIL
     *
     * @mbg.generated
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.ID_CARD
     *
     * @return the value of hs_member_purchase.ID_CARD
     *
     * @mbg.generated
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.ID_CARD
     *
     * @param idCard the value for hs_member_purchase.ID_CARD
     *
     * @mbg.generated
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.NATIONALITY
     *
     * @return the value of hs_member_purchase.NATIONALITY
     *
     * @mbg.generated
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.NATIONALITY
     *
     * @param nationality the value for hs_member_purchase.NATIONALITY
     *
     * @mbg.generated
     */
    public void setNationality(String nationality) {
        this.nationality = nationality == null ? null : nationality.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.PASSPORT_NUMBER
     *
     * @return the value of hs_member_purchase.PASSPORT_NUMBER
     *
     * @mbg.generated
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.PASSPORT_NUMBER
     *
     * @param passportNumber the value for hs_member_purchase.PASSPORT_NUMBER
     *
     * @mbg.generated
     */
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber == null ? null : passportNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.PASSPORT_IMG
     *
     * @return the value of hs_member_purchase.PASSPORT_IMG
     *
     * @mbg.generated
     */
    public String getPassportImg() {
        return passportImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.PASSPORT_IMG
     *
     * @param passportImg the value for hs_member_purchase.PASSPORT_IMG
     *
     * @mbg.generated
     */
    public void setPassportImg(String passportImg) {
        this.passportImg = passportImg == null ? null : passportImg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.VISA_IMG
     *
     * @return the value of hs_member_purchase.VISA_IMG
     *
     * @mbg.generated
     */
    public String getVisaImg() {
        return visaImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.VISA_IMG
     *
     * @param visaImg the value for hs_member_purchase.VISA_IMG
     *
     * @mbg.generated
     */
    public void setVisaImg(String visaImg) {
        this.visaImg = visaImg == null ? null : visaImg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.EID_IMG
     *
     * @return the value of hs_member_purchase.EID_IMG
     *
     * @mbg.generated
     */
    public String getEidImg() {
        return eidImg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.EID_IMG
     *
     * @param eidImg the value for hs_member_purchase.EID_IMG
     *
     * @mbg.generated
     */
    public void setEidImg(String eidImg) {
        this.eidImg = eidImg == null ? null : eidImg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.CREATE_BY
     *
     * @return the value of hs_member_purchase.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.CREATE_BY
     *
     * @param createBy the value for hs_member_purchase.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.CREATE_TIME
     *
     * @return the value of hs_member_purchase.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.CREATE_TIME
     *
     * @param createTime the value for hs_member_purchase.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.UPDATE_BY
     *
     * @return the value of hs_member_purchase.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.UPDATE_BY
     *
     * @param updateBy the value for hs_member_purchase.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_member_purchase.UPDATE_TIME
     *
     * @return the value of hs_member_purchase.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_member_purchase.UPDATE_TIME
     *
     * @param updateTime the value for hs_member_purchase.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}