package io.github.ztmark.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Author: Mark
 * Date  : 2018/9/5
 */
public class SocketChannelDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
//        blockingVersion();

        final SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress("localhost", 8680));

        while (!client.finishConnect()) {
            TimeUnit.MILLISECONDS.sleep(10);
        }
        doWrite(client);
        client.close();

    }

    private static void blockingVersion() throws IOException {
        final SocketChannel client = SocketChannel.open();
        if (client.connect(new InetSocketAddress("localhost", 8680))) {
            doWrite(client);
            client.close();
        }
    }

    private static void doWrite(SocketChannel client) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.put("what a amazing day".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            client.write(buffer);
        }
    }

}
