<template>
  <div>
    <el-container>
      <el-header>密文传输工具</el-header>
      <el-main>
        <el-button type="primary" @click="sendSecret">AES 加密</el-button>
        <el-button @click="decret">AES 解密</el-button>
        <div>
          <el-button @click="sendReq" style="margin-top: 10px">请求公钥</el-button>
          <el-button @click="randomNumber" style="margin-top: 10px">公钥加密随机数字</el-button>
          <el-button @click="checkAes" style="margin-top: 10px">进行对称加密通信</el-button>
        </div>
      </el-main>
      <el-footer>
        <div>
          <el-input
            id="el-input-textarea"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入内容"
            v-model="message"
          ></el-input>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">AES 密文</span>
          <el-tag type="success" effect="plain">{{secretMessage}}</el-tag>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">AES 解密</span>
          <el-tag type="info" effect="plain">{{decretMessage}}</el-tag>
        </div>
        <el-divider></el-divider>
        <div class="tag-group" style="margin-top: 30px">
          <span class="tag-group__title">服务端 publicKey</span>
          <el-input
            id="el-input-textarea"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入内容"
            v-model="serverPublicKey"
          ></el-input>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">客户端 公钥RSA加密后的内容</span>
          <el-input
            id="el-input-textarea"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入内容"
            v-model="pkContent"
          ></el-input>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">客户端 公钥RSA加密后的内容</span>
          <el-tag type="success" effect="plain">{{serverCheckResult}}</el-tag>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">客户端 生成的AES key</span>
          <el-tag type="warning" effect="plain">{{aesKey}}</el-tag>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">客户端 生成的AES iv</span>
          <el-tag type="warning" effect="plain">{{aesIv}}</el-tag>
        </div>

        <div class="tag-group">
          <span class="tag-group__title">AES 服务端返回报文</span>
          <el-tag type="success" effect="plain">{{checkAesResult}}</el-tag>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { getIv, getAesKey, aesEncrypt, decrypt } from "../util/AES.js";
import { publicKeyEncrypt } from "../util/RSA.js";
import { req } from "../api/axiosFun.js";

export default {
  name: "HelloWorld",
  data() {
    return {
      message: "这个是明文 hello world，陆二零网络科技公司 <a><a> : '' \"",
      secretMessage: "暂无报文",
      decretMessage: "暂无报文",
      serverPublicKey: "暂无报文",
      pkContent: "暂无报文",
      serverCheckResult: "服务端暂无验证结果",
      checkAesResult: "aes的报文",
      aesKey: "未生成",
      aesIv: "未生成"
    };
  },

  methods: {
    sendSecret() {
      let _key = getAesKey();
      let _iv = getIv();
      let message = aesEncrypt(_key, _iv, this.message);

      localStorage.setItem("_key", _key);
      localStorage.setItem("_iv", _iv);

      this.secretMessage = message;
    },

    decret() {
      let _key = localStorage.getItem("_key");
      let _iv = localStorage.getItem("_iv");

      if (this.secretMessage == "暂无报文") {
        return;
      }

      this.decretMessage = decrypt(_key, _iv, this.secretMessage);
    },

    sendReq() {
      let param = {};
      param.deviceId = "123456";
      req("post", "http://localhost:8080/token/raspk", param)
        .then(res => {
          this.serverPublicKey = res.msg;
        })
        .catch(err => {
          this.$message.error("数据更新失败，请稍后再试！");
        });
    },

    randomNumber() {
      let param = {};
      param.deviceId = "123456";
      param.publicKey = this.serverPublicKey;

      let _key = getAesKey();
      let _iv = getIv();

      this.aesKey = _key;
      this.aesIv = _iv;

      localStorage.setItem("_key", _key);
      localStorage.setItem("_iv", _iv);

      let rsapkContentMessage = publicKeyEncrypt(
        _key + "!==!" + _iv,
        this.serverPublicKey
      );

      this.pkContent = rsapkContentMessage;

      param.content = rsapkContentMessage;

      req("post", "http://localhost:8080/token/checkRandomNumber", param)
        .then(res => {
          this.serverCheckResult = res.msg;
        })
        .catch(err => {
          this.$message.error("数据更新失败，请稍后再试！");
        });
    },

    checkAes() {
      let param = {};
      param.deviceId = "123456";
      param.publicKey = this.serverPublicKey;

      let _key = localStorage.getItem("_key");
      let _iv = localStorage.getItem("_iv");

      param.content = aesEncrypt(_key, _iv, this.message);

      req("post", "http://localhost:8080/token/checkAes", param)
        .then(res => {
          // let msg = res.msg;
          this.checkAesResult = res.msg;
        })
        .catch(err => {
          this.$message.error("数据更新失败，请稍后再试！");
        });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
* {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}
h1,
h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

.tag-group {
  margin-top: 10px;
}
</style>
