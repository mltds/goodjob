package org.mltds.goodjob.common.dataobject;

import java.util.Date;

import org.mltds.goodjob.common.dataobject.enums.JobInfoInvokePolicyEnum;
import org.mltds.goodjob.common.dataobject.enums.JobInfoTypeEnum;

public class JobInfoHistory {

    private Long id;
    private Long jobInfoId;
    private String name;
    private String group;
    private JobInfoTypeEnum type;
    private Date time;
    private String cron;
    private String urls;
    private String classPath;
    private JobInfoInvokePolicyEnum invokePolicy;
    private Boolean isActivity;
    private String desc;
    private Date createTime;
    private Date modifyTime;
    private String param;
    private Date latestTriggerTime;
    private String latestServerAddress;
    private String ownerPhone;
    private Boolean isCheckFinish; // 是否每日检查完成情况
    private String checkFinishTime; // 检查完成时间，HH:mm 格式，0可省略

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobInfoId() {
        return jobInfoId;
    }

    public void setJobInfoId(Long jobInfoId) {
        this.jobInfoId = jobInfoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public JobInfoTypeEnum getType() {
        return type;
    }

    public void setType(JobInfoTypeEnum type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public JobInfoInvokePolicyEnum getInvokePolicy() {
        return invokePolicy;
    }

    public void setInvokePolicy(JobInfoInvokePolicyEnum invokePolicy) {
        this.invokePolicy = invokePolicy;
    }

    public Boolean isActivity() {
        return isActivity;
    }

    public void setActivity(Boolean isActivity) {
        this.isActivity = isActivity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getLatestServerAddress() {
        return latestServerAddress;
    }

    public void setLatestServerAddress(String latestServerAddress) {
        this.latestServerAddress = latestServerAddress;
    }

    public Date getLatestTriggerTime() {
        return latestTriggerTime;
    }

    public void setLatestTriggerTime(Date latestTriggerTime) {
        this.latestTriggerTime = latestTriggerTime;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getCheckFinishTime() {
        return checkFinishTime;
    }

    public void setCheckFinishTime(String checkFinishTime) {
        this.checkFinishTime = checkFinishTime;
    }

    public Boolean isCheckFinish() {
        return isCheckFinish;
    }

    public void setCheckFinish(Boolean isCheckFinish) {
        this.isCheckFinish = isCheckFinish;
    }

    @Override
    public String toString() {
        return "JobInfoHistory{" + "id=" + id + ", jobInfoId=" + jobInfoId + ", name='" + name + '\'' + ", group='" + group + '\'' + ", type=" + type
                + ", time=" + time + ", cron='" + cron + '\'' + ", urls='" + urls + '\'' + ", classPath='" + classPath + '\'' + ", invokePolicy=" + invokePolicy
                + ", isActivity=" + isActivity + ", desc='" + desc + '\'' + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", param='" + param
                + '\'' + ", latestTriggerTime=" + latestTriggerTime + ", latestServerAddress='" + latestServerAddress + '\'' + ", ownerPhone='" + ownerPhone
                + '\'' + ", isCheckFinish=" + isCheckFinish + ", checkFinishTime='" + checkFinishTime + '\'' + '}';
    }
}
