package io.github.ztmark.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Author: Mark
 * Date  : 2018/9/5
 */
public class DatagramChannelDemo2 {

    public static void main(String[] args) throws IOException {
        final DatagramChannel client = DatagramChannel.open();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put("\nnice to meet you".getBytes());
        byteBuffer.flip();
        client.send(byteBuffer, new InetSocketAddress("localhost", 8680));
        client.close();
    }

}
