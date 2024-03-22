package com.code.rpc.proxy;

import com.code.rpc.RpcApplication;

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
     *
     * @param serviceClass  服务类
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {

        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }

        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());

        return serviceClass.cast(proxyInstance);
    }

    /**
     * 根据服务类获取 Mock 代理对象
     *
     * @param serviceClass 服务类
     * @return mock代理对象
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        Object mockProxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );

        return serviceClass.cast(mockProxyInstance);
    }
}
