package org.mltds.goodjob.common.network.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.mltds.goodjob.common.network.NetworkClient;
import org.mltds.goodjob.common.network.NetworkClientFactory;
import org.mltds.goodjob.common.network.model.NetworkRequest;
import org.mltds.goodjob.common.network.model.NetworkResponse;

/**
 * @author sunyi
 */
public class NettyNetworkClientFactory implements NetworkClientFactory {

    /**
     * 保存服务器地址与目标的Client
     */
    private static volatile ConcurrentHashMap<InetSocketAddress, NetworkClientReferenceCount> clients = new ConcurrentHashMap<>();

    /**
     * 获取目标服务器地址的并且是共享的网络连接客户端<br/>
     * 使用引用计数器方式，来决定是否真实的关闭连接。
     */
    private static NetworkClient getSharedNetworkClient(InetSocketAddress inetSocketAddress) {

        NetworkClientReferenceCount rcNetworkClient = clients.get(inetSocketAddress);
        if (rcNetworkClient == null) {
            synchronized (NettyNetworkClientFactory.class) {
                // 防止重复创建连接，增加同步锁。
                rcNetworkClient = clients.get(inetSocketAddress);
                if (rcNetworkClient == null) {
                    NettyNetworkClient networkClient = new NettyNetworkClient(inetSocketAddress);
                    networkClient.connection();

                    rcNetworkClient = new NetworkClientReferenceCount(networkClient);
                    clients.put(inetSocketAddress, rcNetworkClient);
                }
            }
        }

        // 引用计数加一
        rcNetworkClient.increment();

        if (!rcNetworkClient.getNetworkClient().isActive()) {
            NettyNetworkClient networkClient = rcNetworkClient.getNetworkClient();
            networkClient.connection();
        }

        return rcNetworkClient;

    }

    @Override
    public NetworkClient getNetworkClient(InetSocketAddress inetSocketAddress) {
        return getSharedNetworkClient(inetSocketAddress);
    }

    private static class NetworkClientReferenceCount implements NetworkClient {

        private final AtomicLong count = new AtomicLong(0);

        private final NettyNetworkClient networkClient;

        NetworkClientReferenceCount(NettyNetworkClient networkClient) {
            this.networkClient = networkClient;
        }

        void increment() {
            count.incrementAndGet();
        }

        NettyNetworkClient getNetworkClient() {
            return networkClient;
        }

        @Override
        public NetworkResponse send(NetworkRequest request, Long timeout) {
            return networkClient.send(request, timeout);
        }

        @Override
        public void shutdown() {
            if (count.decrementAndGet() <= 0) { // 计数器减一
                networkClient.shutdown();
            }
        }

    }

}
