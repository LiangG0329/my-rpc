package com.code.rpc.interceptor.proxycreator;

import com.code.rpc.interceptor.Interceptor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * cglib 代理创建器
 *
 * @author Liang
 * @create 2024/4/2
 */
public class CglibProxyCreator implements ProxyCreator {

    @Override
    public <T> T createProxy(T target, Interceptor interceptor) {
        try {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(target.getClass());
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> interceptor.intercept(target, method, args));

            return (T) enhancer.create();
        } catch (Exception e) {
            throw new RuntimeException("cglib 创建代理对象失败", e);
        }
    }
}
