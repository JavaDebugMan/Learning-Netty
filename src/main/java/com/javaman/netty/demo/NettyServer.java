package com.javaman.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Author:彭哲
 * Date:2017/11/23
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        /**
         *1.第一个线程组是用于接收Client请求的
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        /**
         *2.第二个线程组是用来实际处理业务逻辑的
         */
        EventLoopGroup workGroup = new NioEventLoopGroup();
        /**
         *3.创建一个辅助类,就是对我们的Server进行一系列的配置
         */
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //将两个线程组加入进来
        serverBootstrap.group(bossGroup, workGroup)
                //指定使用NioServerSocketChannel这种通道
                .channel(NioServerSocketChannel.class)
                //一定要使用childHandler去绑定具体的事件处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                })
                //设置TCP连接缓冲区
                .option(ChannelOption.SO_BACKLOG, 128)
                //保持连接
                .option(ChannelOption.SO_KEEPALIVE, true);
        //绑定指定的端口进行监听
        ChannelFuture sync = serverBootstrap.bind(8765).sync();

        sync.channel().closeFuture().sync();

        workGroup.shutdownGracefully();

        bossGroup.shutdownGracefully();
    }
}
