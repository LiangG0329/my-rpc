package com.code.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册中心
 *
 * @author Liang
 * @create 2024/3/14
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private final static Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName  服务名称
     * @param serviceImplClass  服务实现类
     */
    public static void register(String serviceName, Class<?> serviceImplClass) {
        map.put(serviceName, serviceImplClass);
    }

    /**
     * 获取服务
     * @param serviceName  服务名称
     * @return  服务实现类
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName  服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
