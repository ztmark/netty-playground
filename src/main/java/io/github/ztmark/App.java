package io.github.ztmark;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws URISyntaxException {
        final URL location = App.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.toURI());
        System.out.println(App.class.getClassLoader().getResource("index.html").toURI());
    }
}
