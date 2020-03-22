package com.marsdl.websecurity.util;


import com.marsdl.websecurity.entity.CacheModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {

    private static Map<String, CacheModel> cacheMap = new ConcurrentHashMap<>();
    private static final long MAX_CACHE_SIZE = 65534;


    public static CacheModel getValueByKey(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            return null;
        }
        return cacheMap.get(deviceId);
    }

    public static boolean setPrivateByKey(String deviceId, String value) {

        if (cacheMap.size() >= MAX_CACHE_SIZE) {
            return false;
        }
        CacheModel cacheModel = cacheMap.get(deviceId);

        if (cacheModel == null) {
            cacheModel = new CacheModel();
        }
        cacheModel.setPriavteKey(value);

        cacheMap.put(deviceId, cacheModel);

        return true;
    }


    public static boolean setAesKeyAndIvByKey(String deviceId, String aesKey, String iv) {

        if (cacheMap.size() >= MAX_CACHE_SIZE) {
            return false;
        }
        CacheModel cacheModel = cacheMap.get(deviceId);

        if (cacheModel == null) {
            cacheModel = new CacheModel();
        }

        cacheModel.setAesKey(aesKey);
        cacheModel.setIv(iv);

        cacheMap.put(deviceId, cacheModel);

        return true;
    }

    public static boolean removeKey(String key) {

        if (cacheMap.size() <= 0) {
            return false;
        }
        cacheMap.remove(key);

        return true;
    }

}
