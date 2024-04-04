package com.code.rpc.registry;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.model.ServiceMetaInfo;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 注册中心服务-本地缓存（消费端）
 *
 * @author Liang
 * @create 2024/3/22
 */
@Slf4j
public class RegistryServiceCache {

    /**
     * 缓存过期时间
     */
    Long cacheExpireTime = Long.valueOf(RpcApplication.getRpcConfig().getCacheExpireTime());

    /**
     * 服务缓存<br>
     * 使用 Caffeine 作为本地缓存，为缓存增加过期时间，定期刷新缓存
     */
    Cache<String, List<ServiceMetaInfo>> serviceCache = Caffeine.newBuilder()
            .expireAfterWrite(cacheExpireTime, TimeUnit.SECONDS)
            .maximumSize(10_000)
            .build();

    /**
     * 写缓存
     * @param newServiceCache 新的服务缓存
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        serviceCache.put(serviceKey, newServiceCache);
    }

    /**
     * 读缓存
     * @return 注册中心服务本地缓存
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return serviceCache.getIfPresent(serviceKey);
    }

    /**
     * 清空缓存
     */
    void clearCache(String serviceKey) {
        //log.info("消费端清空服务 {} 缓存", serviceKey);
        this.serviceCache.invalidate(serviceKey);
    }

    void updateCache(String serviceKey) {
        clearCache(serviceKey);
        Registry registry = RegistryFactory.getInstance(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        writeCache(serviceKey, serviceMetaInfoList);
    }
}
