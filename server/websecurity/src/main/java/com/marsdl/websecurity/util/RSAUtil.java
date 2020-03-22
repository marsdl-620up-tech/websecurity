package com.marsdl.websecurity.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 是非对称的密码算法，密钥分公钥和私钥, 可以使用一方加密另一方解密，不过由于私钥长度往往很长，
 * 考虑到对网络资源的消耗，一般就公开公钥，使用公钥加密，私钥进行解密，所以这里只提供这种模式需要
 * 的方法。
 * 对外提供密钥对生成、密钥转换、公钥加密、私钥解密方法
 */
public class RSAUtil {
    /**
     * 生成密钥对：密钥对中包含公钥和私钥
     *
     * @return 包含 RSA 公钥与私钥的 keyPair
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        // 获得RSA密钥对的生成器实例
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 说的一个安全的随机数
        SecureRandom secureRandom = new SecureRandom(String.valueOf(System.currentTimeMillis())
                .getBytes("utf-8"));

        // 这里可以是1024、2048 初始化一个密钥对
        keyPairGenerator.initialize(2048, secureRandom);
        // 返回获得密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取公钥 (并进行 Base64 编码，返回一个 Base64 编码后的字符串)
     *
     * @param keyPair：RSA 密钥对
     * @return 返回一个 Base64 编码后的公钥字符串
     */
    public static String getPublicKey(KeyPair keyPair) {

        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();

        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取私钥(并进行Base64编码，返回一个 Base64 编码后的字符串)
     *
     * @param keyPair：RSA 密钥对
     * @return 返回一个 Base64 编码后的私钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair) {

        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 Base64 编码后的公钥转换成 PublicKey 对象
     *
     * @param pubStr：Base64 编码后的公钥字符串
     * @return PublicKey
     */
    public static PublicKey string2PublicKey(String pubStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] bytes = Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将 Base64 码后的私钥转换成 PrivateKey 对象
     *
     * @param priStr：Base64 编码后的私钥字符串
     * @return PrivateKey
     */
    public static PrivateKey string2Privatekey(String priStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] bytes = Base64.getDecoder().decode(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     *
     * @param content   待加密的内容 byte[]
     * @param publicKey 加密所需的公钥对象 PublicKey
     * @return 加密后的字节数组 byte[]
     */
    public static byte[] publicEncrytype(byte[] content, PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(content);
    }

    /**
     * 私钥解密
     *
     * @param content    待解密的内容 byte[]，这里要注意，由于我们中间过程用的都是 BASE64 ，所以在传入参数前应先进行 BASE64 解析
     * @param privateKey 解密需要的私钥对象 PrivateKey
     * @return 解密后的字节数组 byte[]，这里是元数据，需要根据情况自行转码
     */
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(content);
    }

    public static void main(String[] args) {
        // 待加密内容
        String content = "Hello World";

        // 经 BASE64 处理之后的公钥和私钥
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs02q0YdzYndJXqvr+DiFwfeQ+VSXSz3yj6vlCmvcorkKs0qIchDNd20/xY12a8hh2p8HkAt8lTL0qpC+dDahMJk8OIoeSltAHPwneUMu6EdeG5F5HNBQfuzFcFDEjZFI2mdUMsSFZqyw5HlNGF12YPNbOrR5FTiRcTRUgzlvcXM1gDDwbxpZY3rNZpoXvIwpsKMrlB+DTkn9802Qwrs07u+UCaCCqxnAQGmCiGwKbha/jQTa/1Y5aTtC9Zn+RPjvjZ+M03GZfin3u0rhLRGNJfcRsDd5zcdsZwsf8fId+TpcuHOOUvvkcLT/WEL5I5TQV0o2AR41BNW2cGOxFL+0xwIDAQAB";
        String privateKeyStr = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCzTarRh3Nid0leq+v4OIXB95D5VJdLPfKPq+UKa9yiuQqzSohyEM13bT/FjXZryGHanweQC3yVMvSqkL50NqEwmTw4ih5KW0Ac/Cd5Qy7oR14bkXkc0FB+7MVwUMSNkUjaZ1QyxIVmrLDkeU0YXXZg81s6tHkVOJFxNFSDOW9xczWAMPBvGlljes1mmhe8jCmwoyuUH4NOSf3zTZDCuzTu75QJoIKrGcBAaYKIbApuFr+NBNr/VjlpO0L1mf5E+O+Nn4zTcZl+Kfe7SuEtEY0l9xGwN3nNx2xnCx/x8h35Oly4c45S++RwtP9YQvkjlNBXSjYBHjUE1bZwY7EUv7THAgMBAAECggEAQHjD3DV1Ism7owP0hDtmtRkcktp80DxFFK39XGLuYcBhfZhmOYWbK78nuBQmqZjSvraCFKRctpUs7ou/P7BJA12GDtpzC8+F3SY511t16WWIDCehwd+RoiHm2HziP/kmlgmjd+G8CfA8ZtrLAuDQaQn4GsK76wp9GZR0cv7a+JKW0LyaTDidTx8fYGsKHV4odps/erR08SBBs5rUETRXcJpGktOUe7k7YlNHCJf+Y1W5yKKehWvTh71veayhjBzxv9RqR0fVgnJDUMVEPATaTj1pVKyZgA/9oia9m8yXAnCVR6GutH0P1FisFijnAN1CpVbHeKhUy1KLZeemGG0zgQKBgQDqQRg9HBwfAHbzWE2NLRcCPIl5UOimYFeRRSBY+7DfDZFCu/jfmgh4mhrlJzDkny7dAJGtfSSM+ozppahuDB6uZjWHrbKZu2WG0qQwWojDAKsfY/TbsWlFm+TLhfALsFnSlVbTpUWqcG6/B/Jdd07ieccGFwTfLNCFdH6b64vDBwKBgQDD8rMHKQcvTEXdQTZGqJ1crTwqXuU3XMOau4h27VR7snDe4xpwl1rsP5W5SiraP5/9EtPwGKwjnEPUSWRHvj2wtBs/SB4A1VBdzKY+yM98eFzSf9HJZRra42SlU/Fw/jHKkRAUkp7gluc5DwZi7JfuxXS1RVgfMWbsTLKT8udQQQKBgQDlPUqBEu8aD5RYU0OhMkzf7WoDBICHwKQxD1q2eaf+wAI1Mko8VzqO+w/yzEV2laiAsbvd8SdBpzcatvh6qPWlaXRdEEhFVTPnml7+yronSpIrp9/I1nbUndhqquncJnngMDDF8WiZgGmAHEC74rOZwd5YQVKNLAfrcbMs1nbxJQKBgQC3SfOq8/bTiF4lq5VQnPKtuSH5ZFC265/QwjDRRgjruCuaYgbeYMXdDVFJRBY3lqJaAN2czgdfPBG6pngWH97mxmJiXFwsXVzSkNbFDeP/wzrYcFXVNCzdqS0A9Td4gV4j5HONOuVAogdhuSs5J6Sq5arY0Sev7e8fhFLaz7ENwQKBgQDjRRfpDUqxeZNjjx0Huu5EFe776ATLxhXHJT7xT7+NXKQK+SwzGMnYPK6fHlkoCOACIX0yqm2CqFNv/XT2rYJ2mFsXs4uDYV7QSDfqJ4fMyUQxsYzqQH/qBi2gP630+il7UmIA7Uddsfl6Wx8ugICX2frUTLNu6/B0a0hFiO++bw==";

        try {
            PrivateKey privateKey = RSAUtil.string2Privatekey(privateKeyStr);
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);


            // 加密
            byte[] encrypteds = RSAUtil.publicEncrytype(content.getBytes(), publicKey);
            String encryptedBase64Str = Base64.getEncoder().encodeToString(encrypteds);

//            String encryptedBase64Str = "MPgbiM3u3ZnY0X9ilqO6+hzlxXEEKYGDMdxbZMQOXAMN149WBK/i1AB8/TnSmbclcqC6oy/Xe3UnqxWJmiybTz8hXdAdZJ6HmWWN8LXajv+1ElJJr+ZKDd3kg/cj39UKmwSmCEccX8Ntaj8BYXPxvzZfAuRqp9xjQ5SQtvcxJu4E4dZ1ecnvWvWiYI9qnJWLjMUhPaqWwJlX1fS4dKTp8GCJ+ZMdh6+oU9090famP0l4RN8nxnIiJPEKX3Z/si5Ad2MjoeFyZKjyuHihyGGZ5Ilpvb4DZzqcv55uRKvr0D4XZDcZN9eumx+rcqUGtV2RsIpBsqleqNJ4y1Z0jk5YMA==";
//            byte[] encrypteds = Base64.getDecoder().decode(encryptedBase64Str);


            // 解密
            byte[] dencrypteds = RSAUtil.privateDecrypt(encrypteds, privateKey);
            // byte[] dencrypteds = RSAUtil.privateDecrypt(Base64.getDecoder().decode(encryptedBase64Str), privateKey);
            String dencrypted = new String(dencrypteds, "utf8");

            // 信息打印
            System.out.println(
                    "公钥为（BASE64 处理之后）：" + publicKeyStr + "\n"
                            + "私钥为(BASE64 处理之后)：" + privateKeyStr + "\n"
                            + "公钥加密密文(BASE64 处理之后)：" + encryptedBase64Str + "\n"
                            + "私钥解密之后(UTF-8 处理之后)：" + dencrypted + "\n"
                            + "Java平台测试是否通过：" + (dencrypted.equals(content))
            );

            RSAUtil.getPublicKey(RSAUtil.getKeyPair());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}