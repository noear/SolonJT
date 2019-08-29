package org.noear.solonjt.utils;

import java.security.MessageDigest;

public class EncryptUtils {
    private static final char[] _hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /** 生成md5码 */
    public static String sha1(String cleanData) {
        return hashEncode("SHA-1", cleanData);
    }

    public static String md5(String cleanData) {
        return hashEncode("MD5", cleanData);
    }

    public static String md5Bytes(byte[] bytes) {
        try {
            return do_hashEncode("MD5", bytes);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private static String hashEncode(String algorithm, String cleanData) {

        try {
            byte[] btInput = cleanData.getBytes("UTF-16LE");
            return do_hashEncode(algorithm,btInput);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String do_hashEncode(String algorithm, byte[] btInput) throws Exception{
        MessageDigest mdInst = MessageDigest.getInstance(algorithm);
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;

        for (int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            str[k++] = _hexDigits[byte0 >>> 4 & 15];
            str[k++] = _hexDigits[byte0 & 15];
        }

        return new String(str);
    }

}
