package io.github.ztmark.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Author: Mark
 * Date  : 2018/1/29
 */
class FixedLengthFrameDecoderTest {

    @Test
    void testFramesDecoded() {
        final ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        final ByteBuf input = buffer.duplicate();
        final EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        assertNull(channel.readInbound());
        buffer.release();
    }

    @Test
    void testFramesDecode2() {
        final ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        final EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        final ByteBuf input = buffer.duplicate();
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));

        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();
        assertNull(channel.readInbound());
        buffer.release();

    }

}