package com.code.rpc.interceptor.nouse;

import com.code.rpc.interceptor.nouse.InterceptorByJDKFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Proxy;

/**
 * 拦截器代理对象工厂
 *
* @author Liang
* @create 2024/4/1
*/
@Deprecated
public class InterceptorProxyFactory {

    /**
     * 使用 CGLib 获取目标类代理对象
     *
     * @param obj 目标对象
     * @param methodInterceptor MethodInterceptor接口实现实例
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getCGLibProxy(T obj, MethodInterceptor methodInterceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(methodInterceptor);

        return (T) enhancer.create();
    }

    /**
     * 使用 JDK 动态代理获取代理对象（只能创建接口代理对象）
     *
     * @param obj 目标对象
     * @return 代理对象
     * @param <T>
     */
    public static <T> T getJDKProxy(T obj, InterceptorByJDKFactory interceptorByJDKFactory) {
        Object proxyInstance = Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                interceptorByJDKFactory.createFor(obj));

        return (T) proxyInstance;
    }
}
