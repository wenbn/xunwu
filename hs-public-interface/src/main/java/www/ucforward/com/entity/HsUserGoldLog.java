package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hs_user_gold_log
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class HsUserGoldLog implements Serializable {
    /**
     * Database Column Remarks:
     *   主键ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.ID
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   任务ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.TASK_ID
     *
     * @mbg.generated
     */
    private Integer taskId;

    /**
     * Database Column Remarks:
     *   积分计算规则ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.GOLD_RULE_ID
     *
     * @mbg.generated
     */
    private Integer goldRuleId;

    /**
     * Database Column Remarks:
     *   所属hs_user主键id,业务员ID
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.USER_ID
     *
     * @mbg.generated
     */
    private Integer userId;

    /**
     * Database Column Remarks:
     *   变更的积分值
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.GOLD
     *
     * @mbg.generated
     */
    private Integer gold;

    /**
     * Database Column Remarks:
     *   备注描述
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   创建人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.CREATE_BY
     *
     * @mbg.generated
     */
    private Integer createBy;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.CREATE_TIME
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新人
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.UPDATE_BY
     *
     * @mbg.generated
     */
    private Integer updateBy;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.STANDBY1
     *
     * @mbg.generated
     */
    private String standby1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.STANDBY2
     *
     * @mbg.generated
     */
    private String standby2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.STANDBY3
     *
     * @mbg.generated
     */
    private String standby3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.STANDBY4
     *
     * @mbg.generated
     */
    private String standby4;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hs_user_gold_log.STANDBY5
     *
     * @mbg.generated
     */
    private String standby5;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.ID
     *
     * @return the value of hs_user_gold_log.ID
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.ID
     *
     * @param id the value for hs_user_gold_log.ID
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.TASK_ID
     *
     * @return the value of hs_user_gold_log.TASK_ID
     *
     * @mbg.generated
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.TASK_ID
     *
     * @param taskId the value for hs_user_gold_log.TASK_ID
     *
     * @mbg.generated
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.GOLD_RULE_ID
     *
     * @return the value of hs_user_gold_log.GOLD_RULE_ID
     *
     * @mbg.generated
     */
    public Integer getGoldRuleId() {
        return goldRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.GOLD_RULE_ID
     *
     * @param goldRuleId the value for hs_user_gold_log.GOLD_RULE_ID
     *
     * @mbg.generated
     */
    public void setGoldRuleId(Integer goldRuleId) {
        this.goldRuleId = goldRuleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.USER_ID
     *
     * @return the value of hs_user_gold_log.USER_ID
     *
     * @mbg.generated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.USER_ID
     *
     * @param userId the value for hs_user_gold_log.USER_ID
     *
     * @mbg.generated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.GOLD
     *
     * @return the value of hs_user_gold_log.GOLD
     *
     * @mbg.generated
     */
    public Integer getGold() {
        return gold;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.GOLD
     *
     * @param gold the value for hs_user_gold_log.GOLD
     *
     * @mbg.generated
     */
    public void setGold(Integer gold) {
        this.gold = gold;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.REMARK
     *
     * @return the value of hs_user_gold_log.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.REMARK
     *
     * @param remark the value for hs_user_gold_log.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.CREATE_BY
     *
     * @return the value of hs_user_gold_log.CREATE_BY
     *
     * @mbg.generated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.CREATE_BY
     *
     * @param createBy the value for hs_user_gold_log.CREATE_BY
     *
     * @mbg.generated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.CREATE_TIME
     *
     * @return the value of hs_user_gold_log.CREATE_TIME
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.CREATE_TIME
     *
     * @param createTime the value for hs_user_gold_log.CREATE_TIME
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.UPDATE_BY
     *
     * @return the value of hs_user_gold_log.UPDATE_BY
     *
     * @mbg.generated
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.UPDATE_BY
     *
     * @param updateBy the value for hs_user_gold_log.UPDATE_BY
     *
     * @mbg.generated
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.UPDATE_TIME
     *
     * @return the value of hs_user_gold_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.UPDATE_TIME
     *
     * @param updateTime the value for hs_user_gold_log.UPDATE_TIME
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.STANDBY1
     *
     * @return the value of hs_user_gold_log.STANDBY1
     *
     * @mbg.generated
     */
    public String getStandby1() {
        return standby1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.STANDBY1
     *
     * @param standby1 the value for hs_user_gold_log.STANDBY1
     *
     * @mbg.generated
     */
    public void setStandby1(String standby1) {
        this.standby1 = standby1 == null ? null : standby1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.STANDBY2
     *
     * @return the value of hs_user_gold_log.STANDBY2
     *
     * @mbg.generated
     */
    public String getStandby2() {
        return standby2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.STANDBY2
     *
     * @param standby2 the value for hs_user_gold_log.STANDBY2
     *
     * @mbg.generated
     */
    public void setStandby2(String standby2) {
        this.standby2 = standby2 == null ? null : standby2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.STANDBY3
     *
     * @return the value of hs_user_gold_log.STANDBY3
     *
     * @mbg.generated
     */
    public String getStandby3() {
        return standby3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.STANDBY3
     *
     * @param standby3 the value for hs_user_gold_log.STANDBY3
     *
     * @mbg.generated
     */
    public void setStandby3(String standby3) {
        this.standby3 = standby3 == null ? null : standby3.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.STANDBY4
     *
     * @return the value of hs_user_gold_log.STANDBY4
     *
     * @mbg.generated
     */
    public String getStandby4() {
        return standby4;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.STANDBY4
     *
     * @param standby4 the value for hs_user_gold_log.STANDBY4
     *
     * @mbg.generated
     */
    public void setStandby4(String standby4) {
        this.standby4 = standby4 == null ? null : standby4.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hs_user_gold_log.STANDBY5
     *
     * @return the value of hs_user_gold_log.STANDBY5
     *
     * @mbg.generated
     */
    public String getStandby5() {
        return standby5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hs_user_gold_log.STANDBY5
     *
     * @param standby5 the value for hs_user_gold_log.STANDBY5
     *
     * @mbg.generated
     */
    public void setStandby5(String standby5) {
        this.standby5 = standby5 == null ? null : standby5.trim();
    }
}