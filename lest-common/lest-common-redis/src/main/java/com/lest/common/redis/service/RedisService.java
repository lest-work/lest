package com.lest.common.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final long DEFAULT_EXPIRE_TIME = 30L;

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // ==================== Cache Object Operations ====================

    public <T> T getCacheObject(String key) {
        return (T) stringRedisTemplate.opsForValue().get(key);
    }

    public <T> void setCacheObject(String key, T value, long expireTime) {
        stringRedisTemplate.opsForValue().set(key, toJson(value), Duration.ofMinutes(expireTime));
    }

    public <T> void setCacheObject(String key, T value) {
        setCacheObject(key, value, DEFAULT_EXPIRE_TIME);
    }

    public boolean deleteObject(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    public void deleteObject(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    // ==================== Cache List Operations ====================

    public <T> void setCacheList(String key, List<T> list) {
        stringRedisTemplate.opsForValue().set(key, toJson(list));
    }

    public <T> List<T> getCacheList(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);
        return json == null ? Collections.emptyList() : parseJsonList(json);
    }

    public <T> Long rightPush(String key, T value) {
        return stringRedisTemplate.opsForList().rightPush(key, toJson(value));
    }

    public <T> List<T> getCacheList(String key, long start, long end) {
        List<String> jsonList = stringRedisTemplate.opsForList().range(key, start, end);
        if (jsonList == null || jsonList.isEmpty()) {
            return Collections.emptyList();
        }
        return jsonList.stream()
                .map(json -> (T) parseJson(json))
                .collect(java.util.stream.Collectors.toList());
    }

    // ==================== Cache Set Operations ====================

    public <T> void setCacheSet(String key, Set<T> set) {
        stringRedisTemplate.opsForValue().set(key, toJson(set));
    }

    public <T> Set<T> getCacheSet(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);
        return json == null ? Collections.emptySet() : parseJsonSet(json);
    }

    public void setCacheSet(String key, long expireTime) {
        stringRedisTemplate.expire(key, Duration.ofMinutes(expireTime));
    }

    // ==================== Cache Map Operations ====================

    public <T> void setCacheMap(String key, Map<String, T> dict) {
        Map<String, String> stringMap = dict.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> toJson(e.getValue())
                ));
        stringRedisTemplate.opsForHash().putAll(key, stringMap);
    }

    public <T> Map<String, T> getCacheMap(String key) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        Map<String, T> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(String.valueOf(k), parseJson(String.valueOf(v))));
        return result;
    }

    public <T> void setCacheMapValue(String key, String hkey, T value) {
        stringRedisTemplate.opsForHash().put(key, hkey, toJson(value));
    }

    public <T> T getCacheMapValue(String key, String hkey) {
        Object value = stringRedisTemplate.opsForHash().get(key, hkey);
        return value == null ? null : parseJson(String.valueOf(value));
    }

    public void delCacheMap(String key, String... hkeys) {
        stringRedisTemplate.opsForHash().delete(key, (Object[]) hkeys);
    }

    public boolean hasCacheMapValue(String key, String hkey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hkey);
    }

    public long getCacheMapSize(String key) {
        Long size = stringRedisTemplate.opsForHash().size(key);
        return size == null ? 0 : size;
    }

    // ==================== Common Operations ====================

    public boolean expire(String key, long expireTime) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, Duration.ofMinutes(expireTime)));
    }

    public long getExpire(String key) {
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire == null ? -1 : expire;
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void hasKey(Collection<String> keys) {
        keys.forEach(this::hasKey);
    }

    public long count(String type) {
        Set<String> keys = stringRedisTemplate.keys(type + ":*");
        return keys == null ? 0 : keys.size();
    }

    public Set<String> keys(String pattern) {
        Set<String> keySet = stringRedisTemplate.keys(pattern);
        return keySet == null ? Collections.emptySet() : keySet;
    }

    public <T> T getCacheObjectIfAbsent(String key, Supplier<T> valueSupplier) {
        T value = getCacheObject(key);
        if (value == null) {
            synchronized (RedisService.class) {
                value = getCacheObject(key);
                if (value == null) {
                    value = valueSupplier.get();
                    setCacheObject(key, value);
                }
            }
        }
        return value;
    }

    public <T> Map<String, T> getCacheMapEntries(String key) {
        return getCacheMap(key);
    }

    public <T> void addCacheMapEntry(String key, String hkey, T value) {
        setCacheMapValue(key, hkey, value);
    }

    // ==================== JSON Serialization Helpers ====================

    private String toJson(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return com.alibaba.fastjson2.JSON.toJSONString(obj);
    }

    @SuppressWarnings("unchecked")
    private <T> T parseJson(String json) {
        return (T) com.alibaba.fastjson2.JSON.parse(json);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> parseJsonList(String json) {
        return (List<T>) com.alibaba.fastjson2.JSON.parseArray(json);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> parseJsonSet(String json) {
        return (Set<T>) com.alibaba.fastjson2.JSON.parseObject(json, Set.class);
    }
}
