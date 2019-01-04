package org.mltds.goodjob.common.model;

import org.mltds.goodjob.common.model.enums.RequestMethod;

public class Request {

    private Long jobInfoId;

    /**
     * 对应job_snapshot表中的id
     */
    private Long jobSnapshotId;

    /**
     * 需要执行的job的类的全路径名
     */
    private String jobClassName;

    /**
     * server 端执行方法标记，值为 INVOKE、OBSERVE 等
     */
    private RequestMethod requestMethod;

    /**
     * 参数
     */
    private String param;

    public Long getJobInfoId() {
        return jobInfoId;
    }

    public void setJobInfoId(Long jobInfoId) {
        this.jobInfoId = jobInfoId;
    }

    public Long getJobSnapshotId() {
        return jobSnapshotId;
    }

    public void setJobSnapshotId(Long jobSnapshotId) {
        this.jobSnapshotId = jobSnapshotId;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}