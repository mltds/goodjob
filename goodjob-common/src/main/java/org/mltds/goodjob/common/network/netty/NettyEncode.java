package org.mltds.goodjob.common.network.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.mltds.goodjob.common.network.serialize.Serialize;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;

/**
 * @author sunyi
 */
public class NettyEncode extends MessageToMessageEncoder<Object> {

	private SerializeFactory serializeFactory;
	private Serialize serialize;

	public NettyEncode(SerializeFactory serializeFactory) {
		this.serializeFactory = serializeFactory;
		this.serialize = serializeFactory.getSerialize();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		byte[] bytes = serialize.write(msg);
		ByteBuf bb = Unpooled.buffer(bytes.length);
		bb.writeBytes(bytes);
		out.add(bb);
	}
}