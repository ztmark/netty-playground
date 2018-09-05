package io.github.ztmark.channel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Author: Mark
 * Date  : 2018/9/5
 */
public class FileChannelDemo {

    public static void main(String[] args) throws IOException {
        final RandomAccessFile file = new RandomAccessFile("README.md", "rw");
        final FileChannel channel = file.getChannel();

        System.out.println(channel.size());

        final ByteBuffer buffer = ByteBuffer.allocate(48);
        int count = channel.read(buffer);
        while (count != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char)buffer.get());
            }
            buffer.clear();
            count = channel.read(buffer);
        }

        buffer.clear();

        buffer.put("\na new string".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        file.close();
    }

}
