package com.javaman.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Author:彭哲
 * Date:2017/11/23
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        //发起异步连接操作
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8765).sync();
        channelFuture.channel().write(Unpooled.copiedBuffer("伟大的哲哥".getBytes()));
        channelFuture.channel().flush();

        channelFuture.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
    }

}
