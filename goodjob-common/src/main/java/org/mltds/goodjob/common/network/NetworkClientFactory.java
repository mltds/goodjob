package org.mltds.goodjob.common.network;

import org.mltds.goodjob.common.Extensible;

import java.net.InetSocketAddress;

/**
 * 具体实现需要支持无参构造函数, 默认使用 newInstance 的方式来创建具体实现。
 *
 * @author sunyi
 */
@Extensible
public interface NetworkClientFactory {

    NetworkClient getNetworkClient(InetSocketAddress inetSocketAddress);

}