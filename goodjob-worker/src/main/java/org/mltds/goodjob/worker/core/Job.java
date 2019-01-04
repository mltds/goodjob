package org.mltds.goodjob.worker.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public abstract class Job {

    private ThreadLocal<Long> jobInfoId = new ThreadLocal<>();
    private ThreadLocal<Long> jobSnapShotId = new ThreadLocal<>();

    private ThreadLocal<AtomicBoolean> needTerminate = new ThreadLocal<>();

    public Long getJobInfoId() {
        return jobInfoId.get();
    }

    public void setJobInfoId(Long jobInfoId) {
        this.jobInfoId.set(jobInfoId);
    }

    public Long getJobSnapShotId() {
        return jobSnapShotId.get();
    }

    public void setJobSnapShotId(Long jobSnapShotId) {
        this.jobSnapShotId.set(jobSnapShotId);
    }

    public boolean isNeedTerminate() {
        // TODO
    }



    /**
     * 执行业务逻辑代码，返回的结果由业务方自定义
     * <p>
     * 注：如果业务上需要区分执行成功或失败的情况，则往外抛出异常即可。 例如：
     * </p>
     * 
     * <pre>
     * public String execute(String param) {
     *     boolean flag = true;
     *     if (flag) {
     *         throw new RuntimeException(&quot;false&quot;);
     *     } else {
     *         return &quot;success&quot;;
     *     }
     * }
     * </pre>
     * 
     * @return String
     */
    abstract String execute(String param);

}
