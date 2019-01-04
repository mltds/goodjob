package org.mltds.goodjob.common.network.netty;

import org.mltds.goodjob.common.network.model.NetworkResponse;

class NettyNetworkResponse {
    Long reqId;
    NetworkResponse networkResponse;

    NettyNetworkResponse(Long reqId, NetworkResponse networkResponse) {
        this.reqId = reqId;
        this.networkResponse = networkResponse;
    }
}
