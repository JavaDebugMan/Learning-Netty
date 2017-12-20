package com.javaman.netty.prototol;

/**
 * @author:彭哲
 * @Date:2017/12/20 Netty私有协议数据结构
 * (心跳信息,握手请求,握手应答消息统一由NettyMessage承载)
 */
public class NettyMessage {

    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
