package io.github.ztmark.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;

/**
 * Author: Mark
 * Date  : 2018/1/29
 */
class FrameChunkDecoderTest {



    @Test
    void testThrowE() {

    }

    @Test
    void testFrameDecoded() {
        final ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        final ByteBuf input = buffer.duplicate();
        final EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        assertTrue(channel.writeInbound(input.readBytes(2)));
        assertThrows(TooLongFrameException.class, () -> {channel.writeInbound(input.readBytes(4));});
        assertTrue(channel.writeInbound(input.readBytes(3)));
        channel.finish();
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(2), read);
        read.release();
        read = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), read);
        read.release();
        buffer.release();
    }
}