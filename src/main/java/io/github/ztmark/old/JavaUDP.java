package io.github.ztmark.old;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class JavaUDP {

    public static void main(String[] args) {
        server();
    }

    private static void clientV2() {
        try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)) {

            if (!channel.isOpen()) {
                System.out.println("channel open failed");
                return;
            }

            channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);

            channel.connect(new InetSocketAddress("127.0.0.1", 8001));

            if (channel.isConnected()) {
                int count = channel.write(ByteBuffer.wrap("hello from client v2".getBytes(StandardCharsets.UTF_8)));
                System.out.println("send " + count + " bytes data");

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.receive(buffer);

                buffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(buffer).toString());
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void client() {
        try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)) {

            if (!channel.isOpen()) {
                System.out.println("channel open failed");
                return;
            }

            channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);

            int send = channel.send(ByteBuffer.wrap("hello from client".getBytes(StandardCharsets.UTF_8)), new InetSocketAddress("127.0.0.1", 8001));
            System.out.println("send " + send + " bytes data");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketAddress receive = channel.receive(buffer);

            buffer.flip();
            System.out.println(StandardCharsets.UTF_8.decode(buffer).toString());

            buffer.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void server() {
        try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)) {
//            channel.supportedOptions().iterator().forEachRemaining(System.out::println);

            if (!channel.isOpen()) {
                System.out.println("channel open failed");
                return;
            }

            channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, 4 * 1024);

            channel.bind(new InetSocketAddress("127.0.0.1", 8001));

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                SocketAddress address = channel.receive(buffer);
                if (address != null) {
                    System.out.println("receive data from " + address.toString());
                    buffer.flip();

                    int count = buffer.remaining();
                    System.out.println("data = " + StandardCharsets.UTF_8.decode(buffer).toString());

                    if (buffer.hasRemaining()) {
                        buffer.compact();
                    } else {
                        buffer.clear();
                    }

                    channel.send(ByteBuffer.wrap(("got " + count + " bytes data").getBytes(StandardCharsets.UTF_8)), address);

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
