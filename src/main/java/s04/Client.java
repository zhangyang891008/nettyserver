package s04;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * 简单的server-client收发消息功能，一个连接建立后server保存这个连接，然后接收到写入的数据以后发送给所有客户端
 *2019-06-10
 */
public class Client {
    private Channel serverChannel = null;
    private ClientFrame frame ;
    public Client(){
    }

    public Client(ClientFrame clientFrame) {
        frame = clientFrame;
    }

    public void  connect(){
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        try {
            ChannelFuture f = bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    })
                    .connect("localhost",8888);
            f.sync();
            f.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            group.shutdownGracefully();
        }
    }

    public void send(String text) {
        ByteBuf buf = Unpooled.copiedBuffer(text.getBytes());
        serverChannel.writeAndFlush(buf);
        System.out.println("send msg to server:"+text);
    }

    class ClientHandler extends ChannelHandlerAdapter{
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("server connection is closed:"+serverChannel);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            serverChannel = ctx.channel();
            ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
            ctx.writeAndFlush(buf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            ByteBuf buf = null;
            try {
                buf = (ByteBuf) msg;
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(),bytes);
                String receivedMsg = new String(bytes);
                System.out.println("received from server:"+receivedMsg);
                //添加更新area
                frame.updateArea(receivedMsg);
            } finally{
                if(msg!=null) ReferenceCountUtil.release(buf);
            }
        }
    }

}

