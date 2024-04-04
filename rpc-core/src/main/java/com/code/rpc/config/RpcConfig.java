package com.code.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC 框架全局配置
 *
 * @author Liang
 * @create 2024/3/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcConfig {

    /* 通用配置 */
    /**
     * RPC 框架名称
     */
    private String name;

    /**
     * RPC 框架版本号
     */
    private String version;

    /**
     * 序列化器（默认 JDK）
     */
    private String serializer;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /* 服务提供者全局配置 */
    /**
     * 服务器主机名称
     */
    private String serverHost;

    /**
     * 服务器端口号
     */
    private Integer serverPort;

    /* 服务消费者全局配置 */
    /**
     * 开启 Mock 服务代理,模拟调用
     */
    private Boolean mock;

    /**
     * 负载均衡器（默认轮询）
     */
    private String loadBalancer;

    /**
     * 重试策略（默认不重试）
     */
    private String retryStrategy;

    /**
     * 容错策略（默认快速失败）
     */
    private String tolerantStrategy;

    /**
     * 降级模拟服务
     */
    private String mockService;

    /**
     * 代理对象创建器（默认 JDK 创建代理）
     */
    private String proxyCreator;

    /**
     * 拦截器（默认日志拦截器）
     */
    private String interceptor;

    /**
     * 服务缓存过期时间，单位：秒 （默认 100s）
     */
    private String cacheExpireTime;
}
