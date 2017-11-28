package org.mware.sponge.browser.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Dan on 10/2/2017.
 */
public class Util {
    private static final Random secureRand = new Random();

    /**
     * @return
     */
    public static String randomFileName() {
        return randomAlphanumeric();
    }

    private static String randomAlphanumeric() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(Long.toString(Math.abs(secureRand.nextInt()), Math.min(36, Character.MAX_RADIX)));
        }
        return builder.toString();
    }

    /**
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {}
        }
    }

    /**
     * @param host
     * @return
     * @throws IOException
     */
    public static int findPort(String host) throws IOException {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(host, 0));

            return socket.getLocalPort();
        } finally {
            Util.close(socket);
        }
    }
}
