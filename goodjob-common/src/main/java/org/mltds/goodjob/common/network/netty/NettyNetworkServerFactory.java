package org.mltds.goodjob.common.network.netty;

import org.mltds.goodjob.common.network.NetworkServerProcess;
import org.mltds.goodjob.common.network.NetworkServer;
import org.mltds.goodjob.common.network.NetworkServerFactory;

/**
 * @author sunyi
 */
public class NettyNetworkServerFactory implements NetworkServerFactory {

    private volatile NettyNetworkServer networkServer;

    @Override
    public NetworkServer startNetworkServer(Integer port, NetworkServerProcess process) {
        if (networkServer == null) {
            synchronized (NettyNetworkServerFactory.class) {
                if (networkServer == null) {
                    this.networkServer = new NettyNetworkServer(port, process);
                }
            }
        }
        networkServer.start();
        return networkServer;
    }
}
