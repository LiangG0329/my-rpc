package com.code.rpc.interceptor.proxycreator;

import com.code.rpc.interceptor.Interceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * byte buddy 代理创建器
 *
 * @author Liang
 * @create 2024/4/2
 */
public class ByteBuddyProxyCreator implements ProxyCreator {

    @Override
    public <T> T createProxy(T target, Interceptor interceptor) {
        try {
            Class<?> subclass = new ByteBuddy()
                    .subclass(target.getClass())
                    .method(ElementMatchers.named("doRetry"))
                    .intercept(MethodDelegation.to(new GenericInterceptor(target, interceptor), "intercept"))
                    .make()
                    .load(this.getClass().getClassLoader())
                    .getLoaded();

            return (T)subclass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("byte buddy 创建代理对象失败", e);
        }
    }

    public static class GenericInterceptor {
        private final Object target;
        private final Interceptor interceptor;

        public GenericInterceptor(Object target, Interceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        // 使用 @SuperCall Callable<?> zuper 来执行原始方法
        @RuntimeType
        public Object intercept(@AllArguments Object[] args, @Origin Method method, @SuperCall Callable<?> zuper) throws Throwable {
            return interceptor.intercept(target, method, args, zuper);
        }
    }
}
