package org.mltds.goodjob.worker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.mltds.goodjob.common.CommonApplicationContext;
import org.mltds.goodjob.common.model.JobResult;
import org.mltds.goodjob.worker.core.Job;

/**
 * @author sunyi 2018/11/6.
 */
public class WorkerApplicationContext extends CommonApplicationContext {

    private static WorkerApplicationContext instance = new WorkerApplicationContext();

    /**
     * Job Cache
     */
    private Map<Class<Job>, Job> jobCache = new ConcurrentHashMap<>();

    /**
     * 存放正在执行中的 job snapshot id 的队列
     */
    private Map<Long, Future> executingJobs = new ConcurrentHashMap<>();

    /**
     * 需要终止的任务
     */
    private Map<Long, Boolean> needTerminateJos = new ConcurrentHashMap<>();

    /**
     * 存放已完成的 job snapshot id 以及执行结果的队列
     */
    private Map<Long, JobResult> finishedJobs = new ConcurrentHashMap<>();

    private WorkerApplicationContext() {
    }

    public static WorkerApplicationContext getInstance() {
        return instance;
    }

    public void setJobCache(ConcurrentHashMap<Class<Job>, Job> jobCache) {
        this.jobCache = jobCache;
    }

}