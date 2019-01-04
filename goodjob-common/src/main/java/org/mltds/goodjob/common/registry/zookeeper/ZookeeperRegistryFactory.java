package org.mltds.goodjob.common.registry.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mltds.goodjob.common.registry.Registry;
import org.mltds.goodjob.common.registry.RegistryFactory;

/**
 * @author sunyi 2018/11/28.
 */
public class ZookeeperRegistryFactory implements RegistryFactory {

    private Logger logger = LoggerFactory.getLogger(ZookeeperRegistryFactory.class);

    private String zkUrl;
    private ZookeeperClient client;
    private ZookeeperRegistry registry;

    @Override
    public Registry getRegistry(String registryServerUrl) {

        StringUtils.isNotEmpty(registryServerUrl);
        zkUrl = registryServerUrl;

        if (client == null) {
            synchronized (ZookeeperRegistryFactory.class) {
                if (client == null) {
                    client = new ZookeeperClient(zkUrl);
                    registry.setClient(client);
                    logger.info(ZookeeperRegistry.class.getSimpleName() + " initialized, zkUrl: " + zkUrl + ".");

                }
            }

        }

        return registry;
    }

}
