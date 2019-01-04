package org.mltds.goodjob.common.registry.zookeeper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import org.mltds.goodjob.common.registry.TriggerConfig;
import org.mltds.goodjob.common.registry.WorkerConfig;
import org.mltds.goodjob.common.registry.Registry;
import org.mltds.goodjob.common.registry.TriggerRegistryListener;
import org.mltds.goodjob.common.registry.WorkerRegistryListener;

/**
 * 基于 zookeeper 实现的注册中心
 * 
 * @author sunyi
 */
public class ZookeeperRegistry implements Registry {

    private static final String BASE_DIR_PATH = "goodjob";

    private static final String WORKER_DIR_PATH = BASE_DIR_PATH + File.separator + "worker";
    private static final String TRIGGER_DIR_PATH = BASE_DIR_PATH + File.separator + "trigger";

    private Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private ZookeeperClient client;

    void setClient(ZookeeperClient client) {
        this.client = client;
    }

    @Override
    public void exportWorker(WorkerConfig workerConfig) {
        client.createPersistent(WORKER_DIR_PATH);

        String jobClassDir = getJobClassDir(workerConfig.getJobClass());
        client.createPersistent(jobClassDir);

        String workerConfigJson = JSONObject.toJSONString(workerConfig);
        String workerDir = jobClassDir + File.separator + workerConfigJson;
        client.createEphemeral(workerDir);
    }

    @Override
    public void exportTrigger(TriggerConfig triggerConfig) {
        client.createPersistent(TRIGGER_DIR_PATH);

        String triggerGroup = triggerConfig.getGroup();
        String triggerGroupDir = getTriggerDir(triggerGroup);
        client.createPersistent(triggerGroupDir);

        String triggerConfigJson = JSONObject.toJSONString(triggerConfig);
        String triggerDir = triggerGroupDir + File.separator + triggerConfigJson;
        client.createEphemeral(triggerDir);

    }

    @Override
    public List<WorkerConfig> getWorkerList(Class jobClass) {

        String jobClassDir = getJobClassDir(jobClass);
        List<String> children = client.getChildren(jobClassDir);
        if (children != null) {
            List<WorkerConfig> list = new ArrayList<>(children.size());
            for (String json : children) {
                list.add(JSONObject.parseObject(json, WorkerConfig.class));
            }
            return list;
        } else {
            return new ArrayList<>(0);
        }

    }

    @Override
    public List<TriggerConfig> getTriggerList(String group) {
        String triggerGroupDir = getTriggerDir(group);
        List<String> children = client.getChildren(triggerGroupDir);
        if (children != null) {
            List<TriggerConfig> list = new ArrayList<>(children.size());
            for (String json : children) {
                list.add(JSONObject.parseObject(json, TriggerConfig.class));
            }
            return list;
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public void listenWorkers(Class jobClass, WorkerRegistryListener listener) {
        String jobClassdir = getJobClassDir(jobClass);

        client.registerChangeListener(jobClassdir, new ZookeeperListener() {
            @Override
            public void handleDataDeleted(String dataPath) {
                //
            }

            @Override
            public void handleDataChange(String dataPath, Object data) {
                //
            }

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) {
                List<WorkerConfig> list;
                if (currentChilds != null) {
                    list = new ArrayList<>(currentChilds.size());
                    for (String json : currentChilds) {
                        list.add(JSONObject.parseObject(json, WorkerConfig.class));
                    }
                } else {
                    list = new ArrayList<>(0);
                }

                listener.onWorkerChange(jobClass, list);
            }
        });
    }

    @Override
    public void listenTriggers(String group, TriggerRegistryListener listener) {
        String triggerDir = getTriggerDir(group);

        client.registerChangeListener(triggerDir, new ZookeeperListener() {
            @Override
            public void handleDataDeleted(String dataPath) {
            }

            @Override
            public void handleDataChange(String dataPath, Object data) {
            }

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) {
                List<TriggerConfig> list;
                if (currentChilds != null) {
                    list = new ArrayList<>(currentChilds.size());
                    for (String json : currentChilds) {
                        list.add(JSONObject.parseObject(json, TriggerConfig.class));
                    }
                } else {
                    list = new ArrayList<>(0);
                }

                listener.onTriggerChange(group, list);
            }
        });
    }

    @Override
    public void close() {
        client.doClose();
        logger.info(ZookeeperRegistry.class.getSimpleName() + " closed.");
    }

    private String getJobClassDir(Class jobClass) {
        String jobClassName = jobClass.getName();
        return WORKER_DIR_PATH + File.separator + jobClassName;
    }

    private String getTriggerDir(String group) {
        return TRIGGER_DIR_PATH + File.pathSeparator + group;
    }

}
