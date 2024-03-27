package com.code.rpc.registry;

import com.code.rpc.spi.SpiLoader;

/**
 * 注册中心工厂 （获取注册中心对象）
 *
 * @author Liang
 * @create 2024/3/22
 */
public class RegistryFactory {

    /**
     * 是否已完成 SPI 加载
     */
    private static volatile boolean isRegistrySpiLoaded = false;

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取指定注册中心实例
     *
     * @param key 键
     * @return 注册中心实例
     */
    public static Registry getInstance(String key) {
        if (!isRegistrySpiLoaded) {
            synchronized (RegistryFactory.class) {
                if (!isRegistrySpiLoaded) {
                    SpiLoader.load(Registry.class);
                    isRegistrySpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(Registry.class, key);
    }
}
