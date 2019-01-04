package org.mltds.goodjob.common;

import org.mltds.goodjob.common.network.NetworkClientFactory;
import org.mltds.goodjob.common.network.NetworkServerFactory;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;
import org.mltds.goodjob.common.registry.RegistryFactory;

/**
 *
 *
 * @author sunyi 2018/11/21.
 */
public abstract class CommonApplicationContext {

    /**
     * 服务端的端口
     */
    private static volatile Integer serverPort;

    /**
     * 注册中心地址，可选
     */
    private static volatile String registryUrl;

    /**
     * 注册中心
     */
    private static volatile RegistryFactory registryFactory;

    /**
     * 网络通讯客户端工厂
     */
    private static volatile NetworkClientFactory networkClientFactory;

    /**
     * 网络通讯服务端工厂
     */
    private static volatile NetworkServerFactory networkServerFactory;

    /**
     * 网络通讯报文的序列化工厂
     */
    private static volatile SerializeFactory serializeFactory;

    public static Integer getServerPort() {
        return serverPort;
    }

    public static void setServerPort(Integer serverPort) {
        CommonApplicationContext.serverPort = serverPort;
    }

    public static String getRegistryUrl() {
        return registryUrl;
    }

    public static void setRegistryUrl(String registryUrl) {
        CommonApplicationContext.registryUrl = registryUrl;
    }

    public static RegistryFactory getRegistryFactory() {
        return registryFactory;
    }

    public static void setRegistryFactory(RegistryFactory registryFactory) {
        CommonApplicationContext.registryFactory = registryFactory;
    }

    public static NetworkClientFactory getNetworkClientFactory() {
        return networkClientFactory;
    }

    public static void setNetworkClientFactory(NetworkClientFactory networkClientFactory) {
        CommonApplicationContext.networkClientFactory = networkClientFactory;
    }

    public static NetworkServerFactory getNetworkServerFactory() {
        return networkServerFactory;
    }

    public static void setNetworkServerFactory(NetworkServerFactory networkServerFactory) {
        CommonApplicationContext.networkServerFactory = networkServerFactory;
    }

    public static SerializeFactory getSerializeFactory() {
        return serializeFactory;
    }

    public static void setSerializeFactory(SerializeFactory serializeFactory) {
        CommonApplicationContext.serializeFactory = serializeFactory;
    }
}
