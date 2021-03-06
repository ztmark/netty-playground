package io.github.ztmark.old;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author: Mark
 * Date  : 2020/6/20
 */
public class PlainJavaNIOServer {

    public static void main(String[] args) {

//        select();

        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            for (SocketOption<?> option : serverChannel.supportedOptions()) {
                System.out.println(option);
            }

            System.out.println(serverChannel.getLocalAddress());
            NetworkInterface.networkInterfaces().flatMap(NetworkInterface::inetAddresses).forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clientSelect() {
        try (Selector selector = Selector.open(); SocketChannel channel = SocketChannel.open()) {
            if (selector.isOpen() && channel.isOpen()) {
                channel.configureBlocking(false);
                channel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                channel.register(selector, SelectionKey.OP_CONNECT);
                channel.connect(new InetSocketAddress("127.0.0.1", 8000));
                System.out.println("connected to " + channel.getRemoteAddress());
                while (selector.select(1000) > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (!key.isValid()) {
                            continue;
                        }
                        try (SocketChannel sc = (SocketChannel) key.channel()) {
                            if (sc.isConnected()) {
                                if (sc.isConnectionPending()) {
                                    sc.finishConnect();
                                }

                                int count = 4;
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                while (sc.read(buffer) > -1) {
                                    buffer.flip();
                                    System.out.println(StandardCharsets.UTF_8.decode(buffer).toString());
                                    if (buffer.hasRemaining()) {
                                        buffer.compact();
                                    } else {
                                        buffer.clear();
                                    }
                                    if (count-- > 0) {
                                        sc.write(ByteBuffer.wrap("Hello from client".getBytes(StandardCharsets.UTF_8)));
                                    }
                                }



                            }
                        }
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void select() {
        try (final Selector selector = Selector.open(); final ServerSocketChannel ssc = ServerSocketChannel.open()) {
            if (selector.isOpen() && ssc.isOpen()) {
                ssc.configureBlocking(false);
                ssc.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                ssc.bind(new InetSocketAddress(8000));
                ssc.register(selector, SelectionKey.OP_ACCEPT);

                Map<Channel, String> data = new HashMap<>();
                while (true) {
                    selector.select();
                    final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey key = iterator.next();
                        iterator.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            System.out.println("accept");
                            final ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                            final SocketChannel socket = channel.accept();
                            socket.configureBlocking(false);
                            socket.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            System.out.println("read");
                            final SocketChannel channel = (SocketChannel) key.channel();
                            final ByteBuffer buffer = ByteBuffer.allocate(1024);
                            boolean quit = false;
                            while (channel.read(buffer) > 0) {
                                buffer.flip();
                                final String msg = StandardCharsets.UTF_8.decode(buffer).toString().trim();
                                if ("quit".equalsIgnoreCase(msg)) {
                                    System.out.println("close quit");
                                    quit = true;
                                    data.remove(channel);
                                    channel.close();
                                    key.cancel();
                                    break;
                                }
                                data.put(channel, msg);
                                System.out.println(msg);
                                buffer.compact();
                            }
                            if (!quit) {
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        } else if (key.isWritable()) {
                            System.out.println("write");
                            final SocketChannel channel = (SocketChannel) key.channel();
                            final String msg = data.getOrDefault(channel, "welcome hi\n");
                            channel.write(ByteBuffer.wrap(msg.getBytes()));
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notBlocking() {
        try (final ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            ssc.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            ssc.bind(new InetSocketAddress("127.0.0.1", 8000));
            while (true) {
                final SocketChannel channel = ssc.accept();
                if (channel != null && channel.isConnected()) {
                    new Thread(() -> {
                        handle(channel);
                    }).start();
                } else {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void blocking() {
        try(ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(true);
            ssc.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);

            ssc.bind(new InetSocketAddress("127.0.0.1", 8000));

            while (true) {
                final SocketChannel socket = ssc.accept();
                new Thread(() -> {
                    handle(socket);
                }).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(SocketChannel socket) {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (socket.read(buffer) > 0) {
                buffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(buffer).toString().trim());
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
