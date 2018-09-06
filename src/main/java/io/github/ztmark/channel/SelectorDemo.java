package io.github.ztmark.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Author: Mark
 * Date  : 2018/9/6
 */
public class SelectorDemo {

    public static void main(String[] args) throws IOException {
        final Selector selector = Selector.open();

        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8680));

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(10) == 0) {
                continue;
            }

            final Set<SelectionKey> selectionKeys = selector.selectedKeys();

            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    final SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(128));

                }
                if (key.isReadable()) {

                    final SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();

                    if (channel.read(byteBuffer) != -1) {

                        byteBuffer.flip();

                        while (byteBuffer.hasRemaining()) {
                            System.out.print((char)byteBuffer.get());
                        }

                        byteBuffer.clear();

                    } else {
                        channel.close();
                    }

                    key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);

                }
                if (key.isValid() && key.isWritable()) {

                    final SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();

                    byteBuffer.flip();
                    channel.write(byteBuffer);
                    if (!byteBuffer.hasRemaining()) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    byteBuffer.compact();

                }


            }

        }
    }


}
