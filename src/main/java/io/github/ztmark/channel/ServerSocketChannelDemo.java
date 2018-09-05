package io.github.ztmark.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Author: Mark
 * Date  : 2018/9/5
 */
public class ServerSocketChannelDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
//        blockingVersion();

        nonBlockingVersion();


    }

    private static void nonBlockingVersion() throws IOException, InterruptedException {
        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(8680));

        while (true) {
            final SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                doRead(client);
            } else {
                TimeUnit.MILLISECONDS.sleep(10);
            }
        }

    }

    private static void blockingVersion() throws IOException {
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8680));

        while (true) {
            final SocketChannel client = serverChannel.accept();
            doRead(client);
        }
    }

    private static void doRead(SocketChannel client) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(128);
        int count = client.read(buffer);
        while (count != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char)buffer.get());
            }
            buffer.clear();
            count = client.read(buffer);
        }
        client.close();
    }

}
