package s04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {

    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws Exception{
        EventLoopGroup boss = null;
        EventLoopGroup worker = null;
        try {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup(3);
            ServerBootstrap bootstrap = new ServerBootstrap().group(boss, worker);
            ChannelFuture f = null;
            f = bootstrap.channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServerChannel());
                        }
                    })
                    .bind(8888);
            f.sync();
            f.channel().closeFuture().sync();
        } finally {
            if(boss!=null) boss.shutdownGracefully();
            if(worker!=null) worker.shutdownGracefully();
        }
    }
}

class ServerChannel extends ChannelHandlerAdapter{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //cause.printStackTrace();
        System.out.println("a connection is closed by client:"+ctx.channel());
        Server.clients.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server: client add:"+ctx.channel());
        Server.clients.add(ctx.channel());
    }

    public void currentConnections(){
        for (Channel client : Server.clients) {
            System.out.println("currentChannel:"+client);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("server read!");
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println("Server read:" + new String(bytes));
            //Server.clients.writeAndFlush(msg);
            for (Channel client : Server.clients) {
                if (client != ctx.channel()) {
                    client.writeAndFlush(msg);
                }
            }
            //if(new String(bytes).equals("channel")){
                currentConnections();
            //}
        }finally{

        }
    }
}
