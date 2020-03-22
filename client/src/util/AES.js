import crypto from 'crypto';
import * as UUID from "uuid";
import CryptJS from "crypto-js";

export const getIv = () => {
    let uuid = UUID.v1();
    let iv = CryptJS.enc.Utf8.parse(uuid);
    iv = CryptJS.enc.Base64.stringify(iv).substring(2, 18);
    // console.log(iv + "\n" + "长度：" + iv.length);
    return iv;
}

export const getAesKey = () => {
    let uuid = UUID.v1();
    // console.log(uuid)
    let aeskey = CryptJS.enc.Utf8.parse(uuid);
    aeskey = CryptJS.enc.Base64.stringify(aeskey).substring(19, 35);
    return aeskey;
}

export const decrypt = (_key, _iv, _content) => {
    let aesKey = CryptJS.enc.Utf8.parse(_key);
    let iv = CryptJS.enc.Utf8.parse(_iv);

    // 解密
    let decrypted = CryptJS.AES.decrypt(_content, aesKey, {
        iv: iv,
        mode: CryptJS.mode.CBC,
        padding: CryptJS.pad.Pkcs7
    });
    // console.log(decrypted)
    return decrypted.toString(CryptJS.enc.Utf8);
}

/**
 * _content 加密的内容
 * _key 公钥
 * _iv 盐 
 */
export const aesEncrypt = (_key, _iv, _content) => {
    // 先以 UTF-8 编码解码参数 返回 any 类型
    let content = CryptJS.enc.Utf8.parse(_content);
    let aesKey = CryptJS.enc.Utf8.parse(_key);
    let iv = CryptJS.enc.Utf8.parse(_iv);

    // 加密
    let encrypted = CryptJS.AES.encrypt(content, aesKey, {
        iv: iv,
        mode: CryptJS.mode.CBC,
        padding: CryptJS.pad.Pkcs7
    });

    return CryptJS.enc.Base64.stringify(encrypted.ciphertext);
}

export const aesDecrypt = (crypted, key, iv) => {
    let decipher = crypto.createDecipheriv('aes-128-cbc', key, iv);
    return decipher.update(crypted, 'hex', 'utf8') + decipher.final('utf8');
}

// let _key = "VkZGNiY2YtNWExNy"
// let _iv = "UxMzZhM2YtZjIzNi"
// let content = "8NIFg/FYe9b9KAOjnUzgcQ=="
// let message = decrypt(_key, _iv, content);
// console.log(message);
