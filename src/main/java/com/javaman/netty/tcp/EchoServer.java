package com.javaman.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author:彭哲
 * @Date:2017/12/19 分隔符解决TCP拆包粘包
 * TCP拆包/黏包产生的原因
 * 1:应用程序write写入的字节大小大于套接字发送缓冲区的大小
 * 2:进行MSS大小的TCP分段
 * 3:以太网帧的payload大于MTU进行IP分段
 * <p>
 * 由于底层的TCP无法理解上层的业务数据,所以在底层是无法保证数据包不被拆分和重组的,
 * 这个问题只能通过上层的应用协议栈来解决,根据业界的主流协议的解决方案,可以归纳如下
 * 1:消息定长,例如每个报文的大小固定为200字节,如果不够,空位补空格
 * 2:在包尾增加回车换行符进行分割,例如FTP协议
 * 3:将消息分为消息头和消息体,消息头中包含表示消息总长度(或者消息体长度)的字段
 * 通常设计思路为消息头的第一个字段使用int32来表示消息的总长度
 * 4:使用更复杂的应用层协议
 */
public class EchoServer {

    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_"
                                    .getBytes());
                            ch.pipeline().addLast(
                                    new DelimiterBasedFrameDecoder(1024,
                                            delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new EchoServer().bind(port);
    }
}
