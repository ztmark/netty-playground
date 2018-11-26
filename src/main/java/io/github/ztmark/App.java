package io.github.ztmark;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws URISyntaxException {
//        final URL location = App.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.toURI());
//        System.out.println(App.class.getClassLoader().getResource("index.html").toURI());

        final int countBit = Integer.SIZE - 3;
        System.out.println("COUNT_BITS = " + countBit);
        System.out.println(String.format("CAPACITY = %32s", Integer.toBinaryString((1 << countBit) - 1)));
        System.out.println("RUNNING = " + Integer.toBinaryString(-1 << countBit));
        System.out.println("SHUTDOWN = " + Integer.toBinaryString(0 << countBit));
        System.out.println(String.format("STOP = %32s", Integer.toBinaryString(1 << countBit)));
        System.out.println(String.format("TIDYING = %32s", Integer.toBinaryString(2 << countBit)));
        System.out.println(String.format("TERMINATED = %32s", Integer.toBinaryString(3 << countBit)));
    }
}
