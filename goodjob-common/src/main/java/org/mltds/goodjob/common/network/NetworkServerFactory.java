package org.mltds.goodjob.common.network;

import org.mltds.goodjob.common.Extensible;

/**
 * 具体实现需要支持无参构造函数。
 *
 * @author sunyi
 */
@Extensible
public interface NetworkServerFactory {

    /**
     * 获取NetworkServer，是否为单例模式由具体实现类决定。
     * 
     * @param port 设置服务端接收请求的网络端口，没有默认值，如不需要可忽略。
     * @return 已经启动的 NetworkServer
     */
    NetworkServer startNetworkServer(Integer port, NetworkServerProcess process);

}
