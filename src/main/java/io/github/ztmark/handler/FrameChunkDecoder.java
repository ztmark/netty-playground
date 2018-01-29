package io.github.ztmark.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

/**
 * Author: Mark
 * Date  : 2018/1/29
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {

    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int readableBytes = in.readableBytes();
        if (readableBytes > maxFrameSize) {
            in.clear(); // discard the bytes important!
            throw new TooLongFrameException();
        }
        final ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }
}
