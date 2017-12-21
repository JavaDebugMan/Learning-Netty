package com.javaman.netty.prototol;

import com.javaman.netty.prototol.codec.NettyMessageDecoder;
import com.javaman.netty.prototol.codec.NettyMessageEncoder;
import com.javaman.netty.prototol.handshake.LoginAuthReqHandler;
import com.javaman.netty.prototol.heartbeat.HeartBeatReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author:彭哲
 * @Date:2017/12/21
 */
public class NettyClient {

    private static final Log LOG = LogFactory.getLog(NettyClient.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {

        //配置客户端NIO线程组

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyMessageDecoder(1024 * 1024, 4, 4));
                            socketChannel.pipeline().addLast("MessageEncode", new NettyMessageEncoder());
                            socketChannel.pipeline().addLast("readTimeOutHandler", new ReadTimeoutHandler(50));
                            socketChannel.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
                            socketChannel.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
                        }
                    });
            //发起异步连接操作
            ChannelFuture furture = bootstrap.connect(
                    new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP,
                            NettyConstant.LOCAL_PORT)).sync();
            //对应的channel关闭的时候,就会返回对应的channel
            furture.channel().closeFuture().sync();
        } finally {
            //所有资源释放后,清空资源,再次发起重连操作
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        try {
                            //发起重连操作
                            connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }


}
