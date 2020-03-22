package com.marsdl.websecurity.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * AES 加密方法，是对称的密码算法(加密与解密的密钥一致)，这里使用最大的 256 位的密钥,
 * 对外提供密钥生成、加密、解密方法
 */
public class AESUtil {
    /**
     * 获得一个 密钥长度为 8*32 = 256 位的 AES 密钥，
     *
     * @return 返回经 BASE64 处理之后的密钥字符串（并截取 16 字符长度）
     */
    public static String getAESStrKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        UUID uuid = UUID.randomUUID();
        String aesKey = Base64.getEncoder().encodeToString(uuid.toString().getBytes()).substring(2, 18);
        return aesKey;
    }

    /**
     * 获得一个初始化向量，初始化向量长度为 4*4 = 16 个字节
     *
     * @return 返回经 BASE64 处理之后的密钥字符串（并截取 16 字符长度）
     */
    public static String getIv() {
        UUID uuid = UUID.randomUUID();
        String iv = Base64.getEncoder().encodeToString(uuid.toString().getBytes()).substring(2, 18);
        return iv;
    }

    /**
     * 获得 AES key 及 初始化向量 iv
     * 其实 iv 和 aesKey 两者的生成并没有什么关系，两者只是对各自的长度有限制，
     * 这里只是为了方便使用，进行了一个组合返回。
     *
     * @return 返回 iv 和 aesKey 的组合
     */
    public static HashMap<String, String> getAESKeyAndIv() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap<String, String> aesKeyAndIv = new HashMap<>();
        aesKeyAndIv.put("aesKey", AESUtil.getAESStrKey());
        aesKeyAndIv.put("iv", AESUtil.getIv());

        return aesKeyAndIv;
    }

    /**
     * 加密
     *
     * @param content      待加密内容
     * @param secretKeyStr 加密使用的 AES 密钥，BASE64 编码后的字符串
     * @param iv           初始化向量，长度为 16 个字节，16*8 = 128 位
     * @return 加密后的密文, 进行 BASE64 处理之后返回
     */
    public static String encryptAES(byte[] content, String secretKeyStr, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        // 获得一个 SecretKeySpec
        // SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");
        // 获得加密算法实例对象 Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //"算法/模式/补码方式"
        // 获得一个 IvParameterSpec
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());  // 使用 CBC 模式，需要一个向量 iv, 可增加加密算法的强度
        // 根据参数初始化算法
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        // 执行加密并返回经 BASE64 处助理之后的密文
        return Base64.getEncoder().encodeToString(cipher.doFinal(content));
    }

    /**
     * 解密
     *
     * @param content:      待解密内容，是 BASE64 编码后的字节数组
     * @param secretKeyStr: 解密使用的 AES 密钥，BASE64 编码后的字符串
     * @param iv:           初始化向量，长度 16 字节，16*8 = 128 位
     * @return 解密后的明文，直接返回经 UTF-8 编码转换后的明文
     */
    public static String decryptAES(byte[] content, String secretKeyStr, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        // 密文进行 BASE64 解密处理
        byte[] contentDecByBase64 = Base64.getDecoder().decode(content);
        // 获得一个 SecretKeySpec
        // SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");
        // 获得加密算法实例对象 Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //"算法/模式/补码方式"
        // 获得一个初始化 IvParameterSpec
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        // 根据参数初始化算法
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        // 解密
        return new String(cipher.doFinal(contentDecByBase64), "utf8");
    }


    public static void main(String[] args) throws Exception {
//        HashMap<String, String> map = getAESKeyAndIv();
//        String aesKey = map.get("aesKey");
//        String iv = map.get("iv");
//
//        System.out.println("aesKey: " + aesKey);
//        System.out.println("iv: " + iv);



        String aesKey = "VkZGNiY2YtNWExNy";
        String iv = "UxMzZhM2YtZjIzNi";

//        long star = System.currentTimeMillis();

        String content = "Hello World";
        String contentSecret = encryptAES(content.getBytes(), aesKey, iv);
        System.out.println("contentSecret => " + contentSecret);

//        long end = System.currentTimeMillis() - star;

//        String aesSecret = "743b01dba900f372c6a4e8ea770c6c5f";

//        String aesKey = "VkZGNiY2YtNWExNy";
//        String iv = "UxMzZhM2YtZjIzNi";
        String message = decryptAES("7Ja8Nq1x8l4nSBvrDJwFyw==".getBytes(), aesKey, iv);
        System.out.println("message => " + message);
//        System.out.println(end);

    }
}
