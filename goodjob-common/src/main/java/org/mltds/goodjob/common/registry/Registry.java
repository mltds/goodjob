package org.mltds.goodjob.common.registry;

import java.util.List;

/**
 * 注册中心
 *
 * @author sunyi
 */
public interface Registry {

    /**
     * 注册一个 Worker
     */
    void exportWorker(WorkerConfig workerConfig);

    /**
     * 注册一个 Trigger
     */
    void exportTrigger(TriggerConfig triggerConfig);

    /**
     * 获取 Worker 列表
     */
    List<WorkerConfig> getWorkerList(Class jobClass);

    List<TriggerConfig> getTriggerList(String group);

    /**
     * 监听 Worker 列表变动
     */
    void listenWorkers(Class jobClass, WorkerRegistryListener listener);

    /**
     * 监听 Triiger 列表变动
     */
    void listenTriggers(String group, TriggerRegistryListener listener);

    /**
     * 断开注册中心链接
     */
    void close();

}