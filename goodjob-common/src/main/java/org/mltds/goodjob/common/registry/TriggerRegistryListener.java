package org.mltds.goodjob.common.registry;

import java.util.List;

/**
 * @author sunyi 2018/11/29.
 */
public interface TriggerRegistryListener {

    void onTriggerChange(String group, List<TriggerConfig> triggerConfigs);

}
