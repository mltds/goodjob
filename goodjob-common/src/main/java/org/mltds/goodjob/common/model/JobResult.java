package org.mltds.goodjob.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 封装了 job 执行结果等相关信息，通过 Response 返回给 Trigger 端，在 Admin 的 UI 上可以查看。
 *
 */
public class JobResult implements Serializable {

    private static final long serialVersionUID = 1530198121024793393L;

    /**
     * job执行成功还是失败，成功返回true，失败返回false
     */
    private boolean isSuccess;

    /**
     * job执行花费的时长，单位：秒
     */
    private long timeConsume;

    /**
     * 成功、失败，返回的结果
     * <p>
     * 注：这个结果由业务方自定义，会返回到控制台界面，方便查看
     * </p>
     */
    private String result;

    private Date actualStartTime; // 任务线程真实开始运行时间，job 线程开始时，取 worker 的机器时间。

    private Date actualFinishTime; // 任务线程真实完成运行时间，job 线程结束时，取 worker 的机器时间。

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public long getTimeConsume() {
        return timeConsume;
    }

    public void setTimeConsume(long timeConsume) {
        this.timeConsume = timeConsume;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
}
