package com.sysn.bitflyerbot.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shiny on 2017/12/11.
 */

public class Encrypt {
    public static String sha256(String secret,String plaintext) {
        String value = null;
        String algo = "HmacSHA256";
        try {
            SecretKeySpec sk = new SecretKeySpec(secret.getBytes(), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(sk);
            byte[] macBytes = mac.doFinal(plaintext.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte bite : macBytes) {
                builder.append(String.format("%02x", bite & 0xff));
            }
            value = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return value;
    }
}
