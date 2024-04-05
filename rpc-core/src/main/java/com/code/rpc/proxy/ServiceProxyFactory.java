package com.code.rpc.proxy;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.ServiceRpcConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 *
 * @author Liang
 * @create 2024/3/15
 */
@Slf4j
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass  服务类
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {

        if (RpcApplication.getRpcConfig().getMock()) {
            log.info("当前消费者使用的服务 {} 开启 Mock 服务代理", serviceClass.getName());
            return getMockProxy(serviceClass);
        }
        log.info("当前消费者使用的服务 {} 未开启 Mock 服务代理", serviceClass.getName());

        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());

        return serviceClass.cast(proxyInstance);
    }

    /**
     * 根据服务类和服务配置获取代理对象
     *
     * @param serviceClass  服务类
     * @param serviceRpcConfig 服务配置
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass, ServiceRpcConfig serviceRpcConfig) {

        if (serviceRpcConfig.getMock() || RpcApplication.getRpcConfig().getMock()) {
            log.info("当前消费者使用的服务 {} 开启 Mock 服务代理", serviceClass.getName());
            return getMockProxy(serviceClass);
        }
        log.info("当前消费者使用的服务 {} 未开启 Mock 服务代理", serviceClass.getName());

        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy(serviceRpcConfig));

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
