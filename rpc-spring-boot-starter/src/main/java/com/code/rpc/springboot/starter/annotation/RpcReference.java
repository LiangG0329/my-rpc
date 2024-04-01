package com.code.rpc.springboot.starter.annotation;

import com.code.rpc.constant.RpcConstant;
import com.code.rpc.fault.retry.RetryStrategyKeys;
import com.code.rpc.fault.tolerant.TolerantStrategyKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
import com.code.rpc.mock.MockServiceKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者注解（用于注入服务）
 *
 * @author Liang
 * @create 2024/3/31
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器，默认轮询
     */
    String loadBalancer() default "";

    /**
     * 重试策略，默认不重试
     */
    String retryStrategy() default "";

    /**
     * 容错策略，默认快速失败
     */
    String tolerantStrategy() default "";

    /**
     * 是否使用 Mock 服务代理
     */
    boolean mock() default false;

    /**
     * 降级服务
     */
    String mockService() default "";
}
