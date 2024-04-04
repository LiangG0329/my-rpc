package com.code.rpc.registry;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务-本地缓存
 *
 * @author Liang
 * @create 2024/3/22
 */
@Slf4j
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

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
        return serviceCache.get(serviceKey);
    }

    /**
     * 清空缓存
     */
    void clearCache(String serviceKey) {
        log.info("消费端清空服务 {} 缓存", serviceKey);
        this.serviceCache.remove(serviceKey);
    }

    void updateCache(String serviceKey) {
        clearCache(serviceKey);
        Registry registry = RegistryFactory.getInstance(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        writeCache(serviceKey, serviceMetaInfoList);
    }
}
