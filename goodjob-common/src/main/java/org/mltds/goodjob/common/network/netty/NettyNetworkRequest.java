package org.mltds.goodjob.common.network.netty;

import org.mltds.goodjob.common.network.model.NetworkRequest;

class NettyNetworkRequest {

    Long reqId;
    NetworkRequest networkRequest;

    NettyNetworkRequest(Long reqId, NetworkRequest networkRequest) {
        this.reqId = reqId;
        this.networkRequest = networkRequest;
    }
}