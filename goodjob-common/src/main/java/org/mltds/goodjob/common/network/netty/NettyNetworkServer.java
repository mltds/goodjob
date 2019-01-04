package org.mltds.goodjob.common.network.netty;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.mltds.goodjob.common.CommonApplicationContext;
import org.mltds.goodjob.common.GoodjobException;
import org.mltds.goodjob.common.network.NetworkServerProcess;
import org.mltds.goodjob.common.network.model.NetworkRequest;
import org.mltds.goodjob.common.network.model.NetworkResponse;
import org.mltds.goodjob.common.network.NetworkServer;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;

/**
 * 
 * @author sunyi
 */
public class NettyNetworkServer implements NetworkServer {

    private Logger logger = LoggerFactory.getLogger(NettyNetworkServer.class);

    private volatile Integer port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture f;
    private Channel channel;

    private SerializeFactory serializeFactory;
    private NetworkServerProcess networkServerProcess;

    NettyNetworkServer(Integer port, NetworkServerProcess networkServerProcess) {
        this.port = port;
        this.serializeFactory = CommonApplicationContext.getSerializeFactory();
        this.networkServerProcess = networkServerProcess;
    }

    @Override
    public Integer getPort() {
        return this.port;
    }

    void start() {

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast("LengthFieldPrepender", new LengthFieldPrepender(4));
                    ch.pipeline().addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                    ch.pipeline().addLast("encode", new NettyEncode(serializeFactory));
                    ch.pipeline().addLast("decode", new NettyDecode(serializeFactory));

                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                            if (!(msg instanceof NettyNetworkRequest)) {
                                logger.warn("NetworkServer read the message, but not instance of RpcRequest.");
                                return;
                            }

                            NettyNetworkRequest request = (NettyNetworkRequest) msg;
                            NetworkRequest networkRequest = request.networkRequest;

                            NetworkResponse networkResponse = NettyNetworkServer.this.networkServerProcess.process(networkRequest);
                            NettyNetworkResponse response = new NettyNetworkResponse(request.reqId, networkResponse);

                            ctx.writeAndFlush(response);

                        }
                    });
                }
            });

            f = b.bind(getPort() == null ? 0 : getPort()).sync();
            channel = f.channel();

            port = ((InetSocketAddress) channel.localAddress()).getPort();

            logger.info(NettyNetworkServer.class.getSimpleName() + " started, Port: " + port + ".");

        } catch (InterruptedException e) {
            throw new GoodjobException("NettyNetworkServer start fail.", e);
        }

    }

    @Override
    public void shutdown() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            channel.close().sync();
            logger.info(NettyNetworkServer.class.getSimpleName() + " shutdown.");
        } catch (Exception e) {
            throw new GoodjobException("NettyNetworkServer shutdown fail.", e);
        }
    }

}
