package com.dxxx.jsdemo;

import android.annotation.SuppressLint;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;

public class AesUtils {
    private static String key = "truesharemanyous";
    private static String iv = "showbeingarebyme";
/*
CRLF：这个参数看起来比较眼熟，它就是Win风格的换行符，意思就是使用CR LF这一对作为一行的结尾而不是Unix风格的LF

DEFAULT：这个参数是默认，使用默认的方法来编码

NO_PADDING：这个参数是略去编码字符串最后的“=”

NO_WRAP：这个参数意思是略去所有的换行符（设置后CRLF就没用了）

URL_SAFE：这个参数意思是编码时不使用对URL和文件名有特殊意义的字符来作为编码字符，具体就是以-和_取代+和/
 */

    /**
     * 解密
     *
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        byte[] encrypted1 = Base64.decode(data, Base64.URL_SAFE);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString.trim();
    }

    /**
     * 加密
     *
     * @return
     * @throws Exception
     */
    @SuppressLint("TrulyRandom")
    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes();
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }
        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return new String(Base64.encode(encrypted, Base64.URL_SAFE));
    }

    /**
     * 解密
     *
     * @return
     * @throws Exception
     */
    public static String desEncryptWithJar(String data) throws Exception {
        byte[] encrypted1 = new BASE64Decoder().decodeBuffer(data);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString.trim();
    }

    /**
     * 加密
     *
     * @return
     * @throws Exception
     */
    @SuppressLint("TrulyRandom")
    public static String encryptWithJar(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes();
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }
        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return new sun.misc.BASE64Encoder().encode(encrypted);
    }
}
