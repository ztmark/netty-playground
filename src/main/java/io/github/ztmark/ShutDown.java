package io.github.ztmark;

import java.util.concurrent.TimeUnit;

import sun.misc.Signal;

/**
 * Author: Mark
 * Date : 2020/6/16
 */
public class ShutDown {


    // https://www.jianshu.com/p/3cb9aacc26a2
    public static void main(String[] args) throws InterruptedException {
//        hook();
        String sigName = System.getProperties().getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "TERM";
//        String sigName = "KILL"; // already use by os or vm
        System.out.println(sigName); // kill 命令会发送 TERM 信号
        final Signal signal = new Signal(sigName);
        Signal.handle(signal, sig -> {
            System.out.println("start signal handler");
            System.out.println(sig);
            System.out.println("end signal handler");
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shut down hook start");

        }));
        TimeUnit.SECONDS.sleep(100);
        System.out.println("end");
    }

    private static void hook() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shut down hook called");
        }));
        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }

}
