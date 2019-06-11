package s05;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TankMsgEncoder extends MessageToByteEncoder<TankMessage> {
        protected void encode(ChannelHandlerContext channelHandlerContext, TankMessage tankMessage, ByteBuf byteBuf) throws Exception {
            byteBuf.writeInt(tankMessage.x);
            byteBuf.writeInt(tankMessage.y);
        }
}
