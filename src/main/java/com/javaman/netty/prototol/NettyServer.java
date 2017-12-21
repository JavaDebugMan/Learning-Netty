package com.javaman.netty.prototol;


import com.javaman.netty.prototol.codec.NettyMessageDecoder;
import com.javaman.netty.prototol.codec.NettyMessageEncoder;
import com.javaman.netty.prototol.handshake.LoginAuthRespHandler;
import com.javaman.netty.prototol.heartbeat.HeartBeatRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author:彭哲
 * @Date:2017/12/21
 */
public class NettyServer {

    private static final Log LOG = LogFactory.getLog(NettyServer.class);

    public void bind() throws Exception {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().addLast
                                (new NettyMessageDecoder(1024 * 1024, 4, 4));
                        socketChannel.pipeline().addLast(
                                new NettyMessageEncoder());
                        socketChannel.pipeline().addLast("readTimeOutHandler",
                                new ReadTimeoutHandler(50));
                        socketChannel.pipeline().addLast(new LoginAuthRespHandler());
                        socketChannel.pipeline().addLast("heartBeatHandler", new HeartBeatRespHandler());
                    }
                });
        bootstrap.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
        LOG.info("Netty server start ok:" + (NettyConstant.REMOTEIP + ":" + NettyConstant.PORT));
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }
}
