package com.code.rpc.interceptor.proxycreator;

import com.code.rpc.spi.SpiLoader;

/**
 * 代理创建器工厂
 *
 * @author Liang
 * @create 2024/4/2
 */
public class ProxyCreatorFactory {

    /**
     * 是否已完成 SPI 加载
     */
    private static volatile boolean isProxyCreatorSpiLoaded = false;

    /**
     * 默认注册中心
     */
    private static final ProxyCreator DEFAULT_PROXY_CREATOR = new JdkProxyCreator();

    /**
     * 获取指定注册中心实例
     *
     * @param key 键
     * @return 注册中心实例
     */
    public static ProxyCreator getInstance(String key) {
        if (!isProxyCreatorSpiLoaded) {
            synchronized (ProxyCreatorFactory.class) {
                if (!isProxyCreatorSpiLoaded) {
                    SpiLoader.load(ProxyCreator.class);
                    isProxyCreatorSpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(ProxyCreator.class, key);
    }
}
