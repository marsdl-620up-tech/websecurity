package com.marsdl.websecurity.util;

public class WebResult {

    String code = "";
    String msg = "";
    Object data;

    public static WebResult getSuccWebResult(Object data, String msg) {
        WebResult result = new WebResult();
        result.setCode("200");
        result.setMsg(msg);
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static WebResult getFailWebResult(Object data, String msg) {
        WebResult result = new WebResult();
        result.setCode("-1");
        result.setMsg(msg);
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static WebResult getFailWebResult(String msg) {
        WebResult result = new WebResult();
        result.setCode("-1");
        result.setMsg(msg);
        return result;
    }

    public static WebResult getSuccWebResult(String msg) {
        WebResult result = new WebResult();
        result.setCode("200");
        result.setMsg(msg);
        return result;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
