package com.code.rpc.interceptor;

import com.code.rpc.spi.SpiLoader;

/**
 * 拦截器工厂
 *
 * @author Liang
 * @create 2024/4/2
 */
public class InterceptorFactory {

    /**
     * 是否已完成 SPI 加载
     */
    private static volatile boolean isInterceptorSpiLoaded = false;

    /**
     * 默认拦截器
     */
    private static final Interceptor DEFAULT_INTERCEPTOR = new LogInterceptor();

    public static Interceptor getInstance(String key) {
        if (!isInterceptorSpiLoaded) {
            synchronized(InterceptorFactory.class) {
                if (!isInterceptorSpiLoaded) {
                    SpiLoader.load(Interceptor.class);
                    isInterceptorSpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(Interceptor.class, key);
    }
}
