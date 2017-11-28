package org.mware.sponge.webui.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private static MessageDigest getSha512Digest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException var1) {
            throw new RuntimeException(var1.getMessage());
        }
    }

    /**
     * @param data
     * @return
     */
    public static byte[] sha(byte[] data) {
        return getSha512Digest().digest(data);
    }

    /**
     * @param data
     * @return
     */
    public static byte[] sha(String data) {
        return sha(data.getBytes());
    }

    /**
     * @param data
     * @return
     */
    public static String shaHex(String data) {
        return new String(Hex.encode(sha(data)));
    }
}
