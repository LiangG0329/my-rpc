package com.code.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 *
 * @author Liang
 * @create 2024/3/15
 */
public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     * @param serviceClass  服务类
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());

        return serviceClass.cast(proxyInstance);
    }
}
