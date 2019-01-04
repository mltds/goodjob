package org.mltds.goodjob.common.network.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.mltds.goodjob.common.network.serialize.Serialize;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;

public class NettyDecode extends MessageToMessageDecoder<ByteBuf> {

    private SerializeFactory serializeFactory;
    private Serialize serialize;

    public NettyDecode(SerializeFactory serializeFactory) {
        this.serializeFactory = serializeFactory;
        this.serialize = serializeFactory.getSerialize();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {

        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);

        Object object = serialize.read(bytes);
        out.add(object);
    }

}