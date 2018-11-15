package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务实体类
 * http://snailxr.iteye.com/blog/2076903
 */
public class ScheduleJob implements Serializable {

    public static final String STATUS_RUNNING = "1"; //是否已经运行
    public static final String STATUS_NOT_RUNNING = "0";
    public static final String CONCURRENT_IS = "1";
    public static final String CONCURRENT_NOT = "0";

    /** 任务id */
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** 任务状态 0禁用 1启用 2删除*/
    private String jobStatus;

    /** 任务运行时间表达式 */
    private String cronExpression;

    /** 任务描述 */
    private String description;

    /** 任务执行时调用哪个类的方法 包名+类名 */
    private String beanClass;

    /** spring bean */
    private String springId;

    /** 任务调用的方法名*/
    private String methodName;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getSpringId() {
        return springId;
    }

    public void setSpringId(String springId) {
        this.springId = springId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
