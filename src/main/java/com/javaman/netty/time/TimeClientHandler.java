package com.javaman.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author:彭哲
 * @Date:2017/12/19
 */
public class TimeClientHandler extends ChannelHandlerAdapter {


    private ByteBuf firstMessage = null;

    public TimeClientHandler() {
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 当客户端跟服务端TCP连接建立成功后,Netty的NIO线程会调用channelActive方法,发送查询的指令给服务端
     *
     * @param ctx
     * @throws Exception
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //将请求消息发送给服务端
        ctx.writeAndFlush(firstMessage);
    }

    /**
     * 当服务端响应应答消息后,channelRead方法被调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is:" + body);
    }
}
