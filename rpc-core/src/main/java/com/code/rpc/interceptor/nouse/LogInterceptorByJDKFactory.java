package com.code.rpc.interceptor.nouse;

import com.code.rpc.interceptor.nouse.InterceptorByJDKFactory;
import com.code.rpc.interceptor.nouse.LogInterceptorByJDK;

import java.lang.reflect.InvocationHandler;

/**
 * 创建 日志拦截器 实例的工厂
 *
 * @author Liang
 * @create 2024/4/2
 */
@Deprecated
public class LogInterceptorByJDKFactory implements InterceptorByJDKFactory {

    @Override
    public InvocationHandler createFor(Object obj) {
        return new LogInterceptorByJDK(obj);
    }
}
