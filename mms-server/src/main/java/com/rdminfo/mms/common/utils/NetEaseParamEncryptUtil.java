package com.rdminfo.mms.common.utils;

/**
 * NetEase 请求数据加密处理
 *
 * @author rdminfo 2023/11/13 10:25
 */

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class NetEaseParamEncryptUtil {

    private final static String MODULES = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7" +
            "b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280" +
            "104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932" +
            "575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b" +
            "3ece0462db0a22b8e7";

    private final static String NONCE = "0CoJUm6Qyw8W8jud";
    private final static String PUBKEY = "010001";

    public static Data encrypt(String text) {
        String secKey = RandomUtil.randomString("0123456789abcde", 16);
        String encText = aesEncrypt(aesEncrypt(text, NONCE), secKey);
        String encSecKey = rsaEncrypt(secKey, PUBKEY, MODULES);

        Data data = new Data(); data.setParams(encText); data.setEncSecKey(encSecKey);
        return data;
    }

    private static String aesEncrypt(String text, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(text.getBytes());

            return Base64.encodeStr(encrypted, false, true);
        } catch (Exception e) {
            return "";
        }
    }

    private static String rsaEncrypt(String text, String pubKey, String modulus) {
        text = new StringBuilder(text).reverse().toString();
        BigInteger rs = new BigInteger(String.format("%x", new BigInteger(1, text.getBytes())), 16)
                .modPow(new BigInteger(pubKey, 16), new BigInteger(modulus, 16));
        StringBuilder r = new StringBuilder(rs.toString(16));
        if (r.length() >= 256) {
            return r.substring(r.length() - 256, r.length());
        } else {
            while (r.length() < 256) {
                r.insert(0, 0);
            }
            return r.toString();
        }
    }


    public static class Data {
        private String params;
        private String encSecKey;

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getEncSecKey() {
            return encSecKey;
        }

        public void setEncSecKey(String encSecKey) {
            this.encSecKey = encSecKey;
        }
    }

}