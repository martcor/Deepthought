package org.telegram.bot;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hendrik Hofstadt on 06.04.2014.
 */
public class Utilities {
    public static String SHA512(String original) {
        String sha512 = "";
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(original.getBytes());
            final byte[] digest = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            sha512 = sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }

        return sha512;
    }
}
