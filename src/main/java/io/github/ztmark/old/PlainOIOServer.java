package io.github.ztmark.old;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Author: Mark
 * Date  : 2018/1/23
 */
public class PlainOIOServer {

    private static AtomicLong count = new AtomicLong(0);

    public static void main(String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(8680);
        System.out.println("Listening in 8680");
        while (true) {
            final Socket conn = serverSocket.accept();
            System.out.println("Accepted connection " + conn);
            new Thread(() -> {
                final OutputStream output;
                try {
                    output = conn.getOutputStream();
                    final Pattern compile = Pattern.compile("http[s]?://([^.]+).([^.]+).com");
                    final long num = count.incrementAndGet();
                    output.write(("Hello NO. " + num).getBytes(StandardCharsets.UTF_8));
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        conn.close();
                    } catch (IOException ignore) {
                    }
                }
            }).start();
        }
    }


}
