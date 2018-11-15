package www.ucforward.com.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_feedback
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsFeedback implements Serializable{
    /**
     * Database Column Remarks:
     *   主键ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * 意见反馈code
     */
    private String feedbackCode;

    /**
     * Database Column Remarks:
     *   反馈类型（0：房源状况，1：费用类，2：服务类，3：金融贷款，4：过户类，5：房源信息错误，6：停售改价，7：应用功能反馈，8：其它）
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.FEEDBACK_TYPE
     *
     * @mbg.generated
     */
    private Integer feedbackType;

    /**
     * Database Column Remarks:
     *   反馈内容
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.FEEDBACK_CONTENT
     *
     * @mbg.generated
     */
    private String feedbackContent;

    /**
     * Database Column Remarks:
     *   联系方式
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.MOBILE
     *
     * @mbg.generated
     */
    private String mobile;

    /**
     * 投诉来源 (1外部 2外获 3外看 4区域长 5PC)
     */
    private Integer platform;

    /**
     * 投诉状态 0未处理 1已处理
     */
    private Integer status;

    private Integer closeBy;

    /**
     * 客服备注
     */
    private String remark;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.FEEDBACK_PROOF_PIC1
     *
     * @mbg.generated
     */
    private String feedbackProofPic1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.FEEDBACK_PROOF_PIC2
     *
     * @mbg.generated
     */
    private String feedbackProofPic2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.FEEDBACK_PROOF_PIC3
     *
     * @mbg.generated
     */
    private String feedbackProofPic3;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.CREATE_TIME
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
     * This field corresponds to the database column hs_feedback.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_feedback.UPDATE_TIME
     *
     * @mbg.generated
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.ID
     *
     * @return the value of hs_feedback.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.ID
     *
     * @param id the value for hs_feedback.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.FEEDBACK_TYPE
     *
     * @return the value of hs_feedback.FEEDBACK_TYPE
     *
     * @mbg.generated
     */
    public Integer getFeedbackType() {
        return feedbackType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.FEEDBACK_TYPE
     *
     * @param feedbackType the value for hs_feedback.FEEDBACK_TYPE
     *
     * @mbg.generated
     */
    public void setFeedbackType(Integer feedbackType) {
        this.feedbackType = feedbackType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.FEEDBACK_CONTENT
     *
     * @return the value of hs_feedback.FEEDBACK_CONTENT
     *
     * @mbg.generated
     */
    public String getFeedbackContent() {
        return feedbackContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.FEEDBACK_CONTENT
     *
     * @param feedbackContent the value for hs_feedback.FEEDBACK_CONTENT
     *
     * @mbg.generated
     */
    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent == null ? null : feedbackContent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.MOBILE
     *
     * @return the value of hs_feedback.MOBILE
     *
     * @mbg.generated
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.MOBILE
     *
     * @param mobile the value for hs_feedback.MOBILE
     *
     * @mbg.generated
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.FEEDBACK_PROOF_PIC1
     *
     * @return the value of hs_feedback.FEEDBACK_PROOF_PIC1
     *
     * @mbg.generated
     */
    public String getFeedbackProofPic1() {
        return feedbackProofPic1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.FEEDBACK_PROOF_PIC1
     *
     * @param feedbackProofPic1 the value for hs_feedback.FEEDBACK_PROOF_PIC1
     *
     * @mbg.generated
     */
    public void setFeedbackProofPic1(String feedbackProofPic1) {
        this.feedbackProofPic1 = feedbackProofPic1 == null ? null : feedbackProofPic1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.FEEDBACK_PROOF_PIC2
     *
     * @return the value of hs_feedback.FEEDBACK_PROOF_PIC2
     *
     * @mbg.generated
     */
    public String getFeedbackProofPic2() {
        return feedbackProofPic2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.FEEDBACK_PROOF_PIC2
     *
     * @param feedbackProofPic2 the value for hs_feedback.FEEDBACK_PROOF_PIC2
     *
     * @mbg.generated
     */
    public void setFeedbackProofPic2(String feedbackProofPic2) {
        this.feedbackProofPic2 = feedbackProofPic2 == null ? null : feedbackProofPic2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.FEEDBACK_PROOF_PIC3
     *
     * @return the value of hs_feedback.FEEDBACK_PROOF_PIC3
     *
     * @mbg.generated
     */
    public String getFeedbackProofPic3() {
        return feedbackProofPic3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.FEEDBACK_PROOF_PIC3
     *
     * @param feedbackProofPic3 the value for hs_feedback.FEEDBACK_PROOF_PIC3
     *
     * @mbg.generated
     */
    public void setFeedbackProofPic3(String feedbackProofPic3) {
        this.feedbackProofPic3 = feedbackProofPic3 == null ? null : feedbackProofPic3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.CREATE_BY
     *
     * @return the value of hs_feedback.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.CREATE_BY
     *
     * @param createBy the value for hs_feedback.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.CREATE_TIME
     *
     * @return the value of hs_feedback.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.CREATE_TIME
     *
     * @param createTime the value for hs_feedback.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.UPDATE_BY
     *
     * @return the value of hs_feedback.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.UPDATE_BY
     *
     * @param updateBy the value for hs_feedback.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_feedback.UPDATE_TIME
     *
     * @return the value of hs_feedback.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_feedback.UPDATE_TIME
     *
     * @param updateTime the value for hs_feedback.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFeedbackCode() {
        return feedbackCode;
    }

    public void setFeedbackCode(String feedbackCode) {
        this.feedbackCode = feedbackCode;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCloseBy() {
        return closeBy;
    }

    public void setCloseBy(Integer closeBy) {
        this.closeBy = closeBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}