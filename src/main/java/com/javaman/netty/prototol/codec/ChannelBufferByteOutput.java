package com.javaman.netty.prototol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * @author:彭哲
 * @Date:2017/12/20
 */
public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int i) throws IOException {
        buffer.writeByte(i);

    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);

    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws IOException {
        buffer.writeBytes(bytes, i, i1);

    }

    @Override
    public void close() throws IOException {
        //Nothing to do
    }

    @Override
    public void flush() throws IOException {
        //Nothing to do

    }

    public ByteBuf getBuffer() {
        return buffer;
    }
}
