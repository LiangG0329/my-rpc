package com.code.rpc.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 拦截器接口
 *
 * @author Liang
 * @create 2024/4/1
 */
public interface Interceptor {

    /**
     * 拦截
     * @param obj 目标对象
     * @param method 目标方法
     * @param args 参数
     * @return 执行结果
     * @throws Throwable
     */
    Object intercept(Object obj, Method method, Object[] args) throws Throwable;

    /**
     * 拦截 （bytebuddy 代理创建器使用）
     * @param obj 对象
     * @param method 方法
     * @param args 参数
     * @param zuper 原始调用对象
     * @return 执行结果
     * @throws Throwable
     */
    public Object intercept(Object obj, Method method, Object[] args, Callable<?> zuper) throws Throwable;
}
