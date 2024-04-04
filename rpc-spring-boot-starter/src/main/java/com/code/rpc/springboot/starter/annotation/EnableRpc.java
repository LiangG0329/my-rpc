package com.code.rpc.springboot.starter.annotation;

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


    /* 消费端全局配置 */
    /**
     * 负载均衡器，默认轮询（消费端）
     */
    String loadBalancer() default "";

    /**
     * 重试策略，默认不重试（消费端）
     */
    String retryStrategy() default "";

    /**
     * 容错策略，默认快速失败（消费端）
     */
    String tolerantStrategy() default "";

    /**
     * 是否使用 Mock 服务代理（消费端）
     */
    boolean mock() default false;

    /**
     * 降级服务（消费端）
     */
    String mockService() default "";

    /**
     * 代理对象创建器，默认jdk代理（消费端）
     */
    String proxyCreator() default "";

    /**
     * 拦截器，默认日志拦截器（消费端）
     */
    String interceptor() default "";

    /**
     * 本地服务缓存过期时间，单位：秒，默认 100s （消费端）
     */
    String cacheExpireTime() default "";
}
