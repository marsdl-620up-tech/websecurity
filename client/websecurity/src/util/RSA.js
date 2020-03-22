import node_rsa from "node-rsa";

export const getRSAKeyPair = () => {
    // 生成空对象
    let keyPair = new node_rsa();
    keyPair.setOptions({
        encryptionScheme: "pkcs1"
    });
    // keyPairObj, 保存经 BASE64 编码处理之后 PEM 格式的 RSA 密钥对
    let keyPairObj = {
        publicKey: '',
        privateKey: ''
    };

    // keysize: 2048; 公指数为：65537
    keyPair.generateKeyPair(2048, 65537);

    /**
     * 导出密钥，对输出的密钥做一些格式化处理，以便 Java 端能直接使用，算然经过处理但是并不影响 JS 端的密钥导入，及正确性。
     * 1. 公钥
     * 2. 私钥
     */
    keyPairObj.publicKey = keyPair.exportKey("pkcs8-public-pem").replace(/-----BEGIN PUBLIC KEY-----/, '').replace(/-----END PUBLIC KEY-----/, '').replace(/\n/g, '');
    keyPairObj.privateKey = keyPair.exportKey("pkcs8-private-pem").replace(/-----BEGIN PRIVATE KEY-----/, '').replace(/-----END PRIVATE KEY-----/, '').replace(/\n/g, '');

    return keyPairObj;
}

export const publicKeyEncrypt = (buffer, pubicKey, encoding = "base64", source_encoding = "utf8") => {
    // 导入 publickey
    let key = new node_rsa();
    key.setOptions({
        encryptionScheme: "pkcs1", // 默认是：pkcs1_oaep，Java 端默认是 pkcs1, 这里做个修改
    });
    key.importKey(pubicKey, "pkcs8-public-pem");
    // 加密并返回加密结果
    return key.encrypt(buffer, encoding, source_encoding);
}

export const privateKeyDecrypt = (buffer, privateKey, encoding = "buffer") => {
    // 导入 privatekey
    let key = new node_rsa();
    key.setOptions({
        // 默认是：pkcs1_oaep，Java 端默认是 pkcs1, 这里做个修改
        encryptionScheme: "pkcs1",
    });
    key.importKey(privateKey, "pkcs8-private-pem");
    // 解密
    return key.decrypt(buffer, encoding);
}
