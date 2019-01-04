package org.mltds.goodjob.common.network;

import org.mltds.goodjob.common.network.model.NetworkRequest;
import org.mltds.goodjob.common.network.model.NetworkResponse;

/**
 * 网络客户端
 */
public interface NetworkClient {

    NetworkResponse send(NetworkRequest request, Long timeout);

    void shutdown();

}
