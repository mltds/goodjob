package org.mltds.goodjob.worker;

/**
 * @author sunyi 2018/11/7.
 */
public class Config {

    /***************** 配置文件 *****************/

    /**
     * 外部提供的配置文件
     */
    public static final String CONFIG_FILE = "goodjob.porperties";

    /**
     * 默认配置文件
     */
    public static final String CONFIG_FILE_DEFAULT = CONFIG_FILE + ".default";

    /***************** 配置项 *****************/

    /**
     * 配置项前缀
     */
    public static final String CONFIG_ITEM_PREFIX = "goodjob.";

    /**
     * 网络通讯的端口
     */
    public static final String CONFIG_ITEM_WORKER_NETWORK_PORT = CONFIG_ITEM_PREFIX + "worker.network.port";


    public static final String CONFIG_ITEM_COMPONENT_PREFIX= CONFIG_ITEM_PREFIX  + "component.NetworkServerFactory";


}