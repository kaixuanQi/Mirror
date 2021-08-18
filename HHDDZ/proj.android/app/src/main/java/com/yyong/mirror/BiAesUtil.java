package com.yyong.mirror;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BiAesUtil {
    public static final String TAG = "BiAesUtil";
    private static final String CHAR_ENCODING = "UTF-8";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS7Padding";

    private static String key = "jeg_.S5]KjZS=.6%";
    private static String iv = "wC2%*wDGm.qJ:+fs";

    /**
     * 加密
     *
     * @param bytes 需要加密的内容
     * @param i     加密类容长度
     * @return 加密结果
     */
    public static byte[] encrypt(byte[] bytes, int i) {

        byte[] encrypted = new byte[i];
        System.arraycopy(bytes, 0, encrypted, 0, i);
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            byte[] raw = key.getBytes(CHAR_ENCODING);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES_ALGORITHM);
            //使⽤CBC模式，需要⼀个向量iv，可增加加密算法的强度
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(CHAR_ENCODING));
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return new byte[0];
    }


    @SuppressWarnings("rawtypes")
    public static void notEmpty(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof String && obj.toString().trim().length() == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
    }
}
