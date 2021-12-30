package me.ahmetmelihomerabdullah.textfileencoder.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Formatter;

public class SHA256 {
    private static final String HASH_ALGORITHM = "HmacSHA256";

    public static String hashMac(String text, String secretKey) throws SignatureException {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            throw new SignatureException(HASH_ALGORITHM + " gibi bir hash algoritması yok.");
        } catch (InvalidKeyException e) {
            throw new SignatureException(HASH_ALGORITHM + " hash algoritması için geçersiz anahtar girdiniz.");
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }
}
