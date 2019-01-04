package org.mltds.goodjob.common.network.netty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.mltds.goodjob.common.CommonApplicationContext;
import org.mltds.goodjob.common.GoodjobException;
import org.mltds.goodjob.common.network.NetworkClient;
import org.mltds.goodjob.common.network.model.NetworkRequest;
import org.mltds.goodjob.common.network.model.NetworkResponse;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;
import org.mltds.goodjob.common.network.model.NetworkStatus;

/**
 * @author sunyi
 */
public class NettyNetworkClient implements NetworkClient {

    private static final AtomicLong REQ_ID = new AtomicLong(Long.MIN_VALUE);
    private static final Map<Long, SyncHolder> holderMap = new ConcurrentHashMap<>();

    private final String remoteHostAddress;
    private final int remoteHostPort;

    private volatile Channel channel = null;
    private Integer connectionTimeoutMsec = 1000;
    private Integer sendTimeout = 1000;

    public NettyNetworkClient(InetSocketAddress inetSocketAddress) {
        this.remoteHostAddress = inetSocketAddress.getAddress().getHostAddress();
        this.remoteHostPort = inetSocketAddress.getPort();
    }

    void connection() {

        EventLoopGroup group = new NioEventLoopGroup();

        if (this.isActive()) {
            return;
        } else {
            this.shutdown();
        }

        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMsec).handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast("LengthFieldPrepender", new LengthFieldPrepender(4));
                            ch.pipeline().addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                            SerializeFactory serializeFactory = CommonApplicationContext.getSerializeFactory();
                            ch.pipeline().addLast("encode", new NettyEncode(serializeFactory));
                            ch.pipeline().addLast("decode", new NettyDecode(serializeFactory));
                            ch.pipeline().addLast(new NettyHandler());
                        }
                    });

            ChannelFuture channelFuture = b.connect(remoteHostAddress, remoteHostPort);
            channelFuture.awaitUninterruptibly(connectionTimeoutMsec, TimeUnit.MILLISECONDS);
            channel = channelFuture.channel();

        } catch (Exception e) {
            throw new GoodjobException("Netty 连接服务器, 建立失败 remoteHostAddress:[\" + remoteHostAddress + \"],remoteHostPort:[\" + remoteHostPort + \"]", e);
        }
    }

    @Override
    public NetworkResponse send(NetworkRequest request, Long timeout) {

        SyncHolder holder = new SyncHolder();

        Long reqId = REQ_ID.addAndGet(1L);
        holder.request = new NettyNetworkRequest(reqId, request);

        ReentrantLock rel = new ReentrantLock();
        holder.rel = rel;

        Condition condition = rel.newCondition();
        holder.condition = condition;

        holderMap.put(reqId, holder);

        rel.lock();

        try {

            boolean isSend = channel.writeAndFlush(request).await(sendTimeout, TimeUnit.MILLISECONDS);
            if (!isSend) {
                // 发送数据超时， 这种服务器应该是接收不到信息
                NetworkResponse response = new NetworkResponse();
                response.setNetworkStatus(NetworkStatus.ERROR);
                response.setNetworkException(new GoodjobException("Send message timeout, " + getDigest(reqId, timeout)));
                return response;
            }

            boolean isResponse = condition.await(timeout, TimeUnit.MILLISECONDS);
            if (!isResponse) {
                // 等待服务器响应超时
                NetworkResponse response = new NetworkResponse();
                response.setNetworkStatus(NetworkStatus.ERROR);
                response.setNetworkException(new GoodjobException("Waiting response timeout, " + getDigest(reqId, timeout)));
                return response;
            }

            if (holder.response == null) {
                // 因为未知原因，造成的服务器响应没有收到, 这种情况应该不会出现
                NetworkResponse response = new NetworkResponse();
                response.setNetworkStatus(NetworkStatus.ERROR);
                response.setNetworkException(new GoodjobException("Not found the response, " + getDigest(reqId, timeout)));
                return response;
            }

            NetworkResponse response = holder.response.networkResponse;

            return response;

        } catch (Throwable e) {
            NetworkResponse response = new NetworkResponse();
            response.setNetworkStatus(NetworkStatus.ERROR);
            response.setNetworkException(new GoodjobException("Send message faile, " + getDigest(reqId, timeout), e));
            return response;
        } finally {
            rel.unlock();
            holderMap.remove(reqId);
        }

    }

    @Override
    public void shutdown() {
        if (channel == null)
            return;
        channel.close();
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    private String getDigest(Long reqId, Long timeout) {

        StringBuilder digest = new StringBuilder();
        digestAppend(digest, "reqId", reqId);
        digestAppend(digest, "remoteHostAddress", remoteHostAddress);
        digestAppend(digest, "remoteHostPort", remoteHostPort);
        digestAppend(digest, "timeout", timeout);
        digestAppend(digest, "beginTime", System.currentTimeMillis());
        return digest.substring(0, digest.length() - 1);

    }

    private void digestAppend(StringBuilder digest, String k, Object v) {
        digest.append(k == null ? "" : k);
        digest.append(":");
        digest.append(v == null ? "" : v);
        digest.append(",");
    }

    /**
     * 因为请求-响应是个异步过程， 接收响应是在另外一个线程中， 需要某个东西把请求-响应的东西融合在一起。
     */
    private static class SyncHolder {
        volatile ReentrantLock rel;
        volatile Condition condition;
        volatile NettyNetworkRequest request;
        volatile NettyNetworkResponse response;

    }


    private static class NettyHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof NettyNetworkResponse)) {
                throw new GoodjobException("收到一个 Response 非, 但不是 NettyNetworkResponse");
            }

            NettyNetworkResponse response = (NettyNetworkResponse) msg;

            // 根据 ID 找到发送请求的线程 Condition， 将其唤醒
            Long reqId = response.reqId;
            SyncHolder holder = holderMap.get(reqId);
            if (holder == null) {
                throw new GoodjobException("收到 Response, 但没有找到对应的上下文, Response Req Id:[" + reqId + "]");
            }

            holder.rel.lock();

            try {
                holder.response = response;
                holder.condition.signal();
            } finally {
                holder.rel.unlock();
            }
        }
    }

}
