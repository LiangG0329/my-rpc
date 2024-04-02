package com.code.rpc.interceptor.nouse;

import java.lang.reflect.InvocationHandler;

/**
 * 创建 InvocationHandler 实例的工厂接口
 *
 * @author Liang
 * @create 2024/4/2
 */
@Deprecated
public interface InterceptorByJDKFactory {

    /**
     * 创建 InvocationHandler 实例 （用于获得JDK反射对象）
     * @param obj 目标对象
     * @return InvocationHandler
     */
    InvocationHandler createFor(Object obj);
}
