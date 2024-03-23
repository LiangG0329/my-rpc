package com.code.rpc.registry;

import com.code.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务-本地缓存
 *
 * @author Liang
 * @create 2024/3/22
 */

public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param newServiceCache 新的服务缓存
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 读缓存
     * @return 注册中心服务本地缓存
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }
}
