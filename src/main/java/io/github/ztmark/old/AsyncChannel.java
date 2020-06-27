package io.github.ztmark.old;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.*;

public class AsyncChannel {

    public static void main(String[] args) {
//        write();
//        read();
//        lock();
        customPool();
    }

    private static void customPool() {
        Path path = Paths.get("test.txt");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try (AsynchronousFileChannel open = AsynchronousFileChannel.open(path, Set.of(StandardOpenOption.READ), executor)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            open.read(buffer, 0).get();
            buffer.flip();
            System.out.println("read data " + StandardCharsets.UTF_8.decode(buffer).toString());
            executor.shutdown();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void lock() {
        Path path = Paths.get("test.txt");
        try (AsynchronousFileChannel open = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
//            open.lock("something attach", new CompletionHandler<FileLock, String>() {
//                @Override
//                public void completed(FileLock result, String attachment) {
//                    System.out.println("lock success");
//                    Future<Integer> write = open.write(ByteBuffer.wrap("hhhhh".getBytes(StandardCharsets.UTF_8)), 0);
//                    try {
//                        write.get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void failed(Throwable exc, String attachment) {
//                    System.out.println("lock failed");
//                }
//            });
            FileLock fileLock = open.lock().get();
            if (fileLock.isValid()) {
                Future<Integer> write = open.write(ByteBuffer.wrap("hhhhh".getBytes(StandardCharsets.UTF_8)), 0);
                write.get();
                System.out.println("write data to file");
                fileLock.release();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void read() {
        Path path = Paths.get("test.txt");
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {

            CountDownLatch latch = new CountDownLatch(1);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer, 0, "something to attach", new CompletionHandler<>() {
                @Override
                public void completed(Integer result, String attachment) {
                    System.out.println("read completed");
                    System.out.println("attachment " + attachment);
                    System.out.println("read size " + result);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable exc, String attachment) {
                    System.out.println("read failed");
                    System.out.println("attachment " + attachment);
                    System.out.println("error = " + exc);
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException ignored) {
            }
            buffer.flip();
            System.out.println("data: " + StandardCharsets.UTF_8.decode(buffer).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write() {
        Path path = Paths.get("test.txt");
        Future<Integer> future = null;
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            future = channel.write(ByteBuffer.wrap("hello from the other side....\n".getBytes(StandardCharsets.UTF_8)), path.toFile().length());
/*
            while (!future.isDone()) {
                System.out.println("do something else");
                TimeUnit.MILLISECONDS.sleep(1);
            }
*/

            System.out.println("done write file size=" +future.get(1, TimeUnit.MILLISECONDS));
//            System.out.println("done write file size=" + future.get()); // block

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("future get timeout");
                future.cancel(false);
                System.out.println("done? " + future.isDone());
                System.out.println("cancelled " + future.isCancelled());
        }
    }

}
