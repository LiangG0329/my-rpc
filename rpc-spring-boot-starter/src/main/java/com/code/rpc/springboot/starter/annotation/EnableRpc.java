package com.code.rpc.springboot.starter.annotation;

import com.code.rpc.fault.retry.RetryStrategyKeys;
import com.code.rpc.fault.tolerant.TolerantStrategyKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
import com.code.rpc.mock.MockServiceKeys;
import com.code.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.code.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.code.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动 RPC 注解
 *
 * @author Liang
 * @create 2024/3/31
 */
@Target({ElementType.TYPE})  // 作用于java元素
@Retention(RetentionPolicy.RUNTIME)  // 注解信息保留到运行时
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class}) // @Import 注解用于导入其他配置类或组件到当前的 Spring 配置
public @interface EnableRpc {

    /**
     * 需要启动 web sever，默认true，（服务提供者需开启，服务消费者不开启）
     */
    boolean needServer() default true;


    /* 消费者全局配置 */
    /**
     * 负载均衡器，默认轮询
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略，默认不重试
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错策略，默认快速失败
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 是否使用 Mock 服务代理
     */
    boolean mock() default false;

    /**
     * 降级服务
     */
    String mockService() default MockServiceKeys.DEFAULT;
}
