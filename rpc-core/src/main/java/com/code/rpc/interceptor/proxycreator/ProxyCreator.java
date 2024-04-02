package com.code.rpc.interceptor.proxycreator;

import com.code.rpc.interceptor.Interceptor;

/**
 * 代理创建器接口
 *
 * @author Liang
 * @create 2024/4/2
 */
public interface ProxyCreator {

    /**
     * 创建代理
     * @param target 目标对象
     * @param interceptor 拦截器
     * @return 代理对象
     * @param <T>
     */
    <T> T createProxy(T target, Interceptor interceptor);
}
