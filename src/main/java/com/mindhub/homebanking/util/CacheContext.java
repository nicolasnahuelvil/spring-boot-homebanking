package com.mindhub.homebanking.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CacheContext {

    private CacheContext() {

    }

    static final Map<String, Object> cachedMap;

    static {
        cachedMap = new HashMap<>();
    }

    public static String addKey(String key, String value) {
        return (String) cachedMap.put(key, value);
    }

    public static Object addKey(String key, Object value) {
        return cachedMap.put(key, value);
    }

    public static Optional<Object> getKey(String key) {
        return Optional.ofNullable(cachedMap.get(key));
    }

    public static Optional<String> getKeyStr(String key) {
        return Optional.ofNullable((String) cachedMap.get(key));
    }

}
