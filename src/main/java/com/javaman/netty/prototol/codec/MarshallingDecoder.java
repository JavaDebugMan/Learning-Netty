package com.javaman.netty.prototol.codec;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * @author:彭哲
 * @Date:2017/12/20
 */
public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        this.unmarshaller = MarshallingCodecFactory.buildUnmarshaller();
    }

    protected Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ChannelBufferByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return obj;
        } finally {
            unmarshaller.close();
        }


    }
}
