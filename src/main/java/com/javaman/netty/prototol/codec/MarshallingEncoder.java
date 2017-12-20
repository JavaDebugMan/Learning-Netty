package com.javaman.netty.prototol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.*;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * @author:彭哲
 * @Date:2017/12/20 Netty编码消息工具类
 */
@Sharable
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        this.marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws IOException {
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } finally {
            marshaller.close();
        }

    }

}
