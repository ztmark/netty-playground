package io.github.ztmark.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Author: Mark
 * Date  : 2018/9/5
 */
public class DatagramChannelDemo {

    public static void main(String[] args) throws IOException {
        final DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(8680));

        final ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        while (true) {

            byteBuffer.clear();
            datagramChannel.receive(byteBuffer);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                System.out.print((char) byteBuffer.get());
            }

        }


    }

}
