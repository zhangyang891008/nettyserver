package com.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class Client {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future;

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(8888))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally{
            group.shutdownGracefully();
        }

    }
}

class ClientHandler extends ChannelHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("send msg to server...");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty hello!", CharsetUtil.UTF_8));
        System.out.println("send msg success");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx ,Object msg) throws Exception{
        System.out.println("received msg from server...");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(buf.toString());
    }
}
