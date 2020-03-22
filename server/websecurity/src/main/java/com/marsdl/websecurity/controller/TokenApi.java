package com.marsdl.websecurity.controller;


import com.marsdl.websecurity.entity.CacheModel;
import com.marsdl.websecurity.util.AESUtil;
import com.marsdl.websecurity.util.CacheUtil;
import com.marsdl.websecurity.util.RSAUtil;
import com.marsdl.websecurity.util.WebResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.util.Base64;

@RequestMapping(value = "token/")
@RestController
@CrossOrigin
public class TokenApi {

    //----------------------RSA非对称加密----------------------//
    @RequestMapping(value = "raspk", method = RequestMethod.POST)
    public WebResult rasPublicKey(String deviceId) {
        try {
            if (StringUtils.isBlank(deviceId)) {
                return WebResult.getFailWebResult("deviceId为空了");
            }

            KeyPair keyPair = RSAUtil.getKeyPair();
            String sk = RSAUtil.getPrivateKey(keyPair);
            String pk = RSAUtil.getPublicKey(keyPair);

            CacheUtil.setPrivateByKey(deviceId, sk);

            return WebResult.getSuccWebResult(pk);
        } catch (Exception e) {
//            e.printStackTrace();
            return WebResult.getFailWebResult("获取公钥失败");
        }
    }

    @RequestMapping(value = "checkRandomNumber", method = RequestMethod.POST)
    public WebResult checkRandomNumber(String deviceId, String publicKey, String content) {

        CacheModel cacheModel = CacheUtil.getValueByKey(deviceId);
        if (cacheModel == null || StringUtils.isBlank(publicKey)) {
            return WebResult.getFailWebResult("check fail!");
        }

        String privateKey = cacheModel.getPriavteKey();
        //删除缓存中的pk与sk非对称密钥对
        try {
            byte[] secretContentByteArr = Base64.getDecoder().decode(content);
            byte[] dencrypteds = RSAUtil.privateDecrypt(secretContentByteArr, RSAUtil.string2Privatekey(privateKey));
            String dencrypted = new String(dencrypteds, "utf8");

            if (StringUtils.isBlank(dencrypted) || !dencrypted.contains("!==!")) {
                return WebResult.getFailWebResult("check fail!");
            }

            String[] decryptedAndIv = dencrypted.split("!==!");
            if (decryptedAndIv.length != 2) {
                return WebResult.getFailWebResult("check fail!");
            }

            CacheUtil.setAesKeyAndIvByKey(deviceId, decryptedAndIv[0], decryptedAndIv[1]);
            return WebResult.getSuccWebResult("ok");
        } catch (Exception e) {
//            e.printStackTrace();
            return WebResult.getFailWebResult("check fail!");
        }
    }
    //----------------------RSA非对称加密----------------------//


    //----------------------AES对称加密-----------------------//
    @RequestMapping(value = "checkAes", method = RequestMethod.POST)
    public WebResult checkAes(String deviceId, String content) {
        CacheModel cacheModel = CacheUtil.getValueByKey(deviceId);
        String aesKey = cacheModel.getAesKey();
        String iv = cacheModel.getIv();
        try {
            String resultContent = AESUtil.decryptAES(content.getBytes(), aesKey, iv);
            String contentSend = "Hello World";
            String contentSecret = AESUtil.encryptAES(contentSend.getBytes(), aesKey, iv);

            return WebResult.getSuccWebResult(" 服务端返回AES加密的内容=> " + contentSecret + " 解密的内容=> " + resultContent);
        } catch (Exception e) {
//            e.printStackTrace();
            return WebResult.getFailWebResult("失败");
        }
    }
    //----------------------AES对称加密-----------------------//

}
