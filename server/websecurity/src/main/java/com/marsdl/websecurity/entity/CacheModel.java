package com.marsdl.websecurity.entity;

public class CacheModel {

    private String priavteKey;
    private String iv;
    private String aesKey;

    public String getPriavteKey() {
        return priavteKey;
    }

    public void setPriavteKey(String priavteKey) {
        this.priavteKey = priavteKey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
