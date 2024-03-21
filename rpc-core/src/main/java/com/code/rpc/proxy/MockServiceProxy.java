package com.code.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理 (JDK动态代理)
 *
 * @author Liang
 * @create 2024/3/21
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return 根据方法返回类型生成的默认值对象
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
         log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 返回指定类型的默认值对象
     *
     * @param methodReturnType 方法返回类型
     * @return 默认值对象
     */
    private Object getDefaultObject(Class<?> methodReturnType) {
        // 基本类型
        if (methodReturnType.isPrimitive()) {
            if (methodReturnType == boolean.class) {
                return false;
            } else if (methodReturnType == short.class) {
                return (short) 0;
            } else if (methodReturnType == int.class) {
                return 0;
            } else if (methodReturnType == long.class) {
                return 0L;
            } else if (methodReturnType == char.class) {
                return '\u0000';
            } else if (methodReturnType == float.class) {
                return 0.0f;
            } else if (methodReturnType == double.class) {
                return 0.0d;
            }
        }
        // 引用类型
        return null;
    }
}
