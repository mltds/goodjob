package org.mltds.goodjob.common.dataobject;

import java.util.Date;

import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;

public class JobSnapshot {
    private Long id;
    private Long jobInfoId;
    private String name;
    private String group;
    private JobSnapshotStatusEnum status;
    private String ip;
    private String url;
    private String result;
    private Long timeConsume;
    private String detail;
    private String serverAddress;
    private Date createTime;
    private Date modifyTime;
    private Date actualStartTime;
    private Date actualFinishTime;

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

    public JobSnapshotStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JobSnapshotStatusEnum status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getTimeConsume() {
        return timeConsume;
    }

    public void setTimeConsume(Long timeConsume) {
        this.timeConsume = timeConsume;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualFinishTime() {
        return actualFinishTime;
    }

    public void setActualFinishTime(Date actualFinishTime) {
        this.actualFinishTime = actualFinishTime;
    }

    @Override
    public String toString() {
        return "JobSnapshot{" + "id=" + id + ", jobInfoId=" + jobInfoId + ", name='" + name + '\'' + ", group='" + group + '\'' + ", status=" + status
                + ", ip='" + ip + '\'' + ", url='" + url + '\'' + ", result='" + result + '\'' + ", timeConsume=" + timeConsume + ", detail='" + detail + '\''
                + ", serverAddress='" + serverAddress + '\'' + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", actualStartTime="
                + actualStartTime + ", actualFinishTime=" + actualFinishTime + '}';
    }
}
