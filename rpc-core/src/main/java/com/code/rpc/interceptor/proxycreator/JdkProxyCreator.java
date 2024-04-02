package com.code.rpc.interceptor.proxycreator;

import com.code.rpc.interceptor.Interceptor;

import java.lang.reflect.Proxy;

/**
 * jdk 代理创建器
 *
 * @author Liang
 * @create 2024/4/2
 */
public class JdkProxyCreator implements ProxyCreator {

    @Override
    public <T> T createProxy(T target, Interceptor interceptor) {
        try {
            Object proxyInstance = Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    (proxy, method, args) -> interceptor.intercept(target, method, args)
            );

            return (T) proxyInstance;
        } catch (Exception e) {
            throw new RuntimeException("jdk 创建代理对象失败", e);
        }
    }
}
