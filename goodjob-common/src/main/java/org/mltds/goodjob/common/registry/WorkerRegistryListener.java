package org.mltds.goodjob.common.registry;

import java.util.List;

/**
 * 
 * @author sunyi
 */
public interface WorkerRegistryListener {

    void onWorkerChange(Class jobClass, List<WorkerConfig> workerConfigs);

}