package com.code.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  RPC 单服务级别配置（消费者端）
 *
 * @author Liang
 * @create 2024/4/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRpcConfig {

    /**
     * 是否使用 Mock 服务代理，默认 false，全局配置优先
     */
    private Boolean mock;

    /**
     * 负载均衡器（默认轮询）
     */
    private String loadBalancer;

    /**
     * 重试策略
     */
    private String retryStrategy;

    /**
     * 容错策略
     */
    private String tolerantStrategy;

    /**
     * 降级模拟服务
     */
    private String mockService;

    /**
     * 代理对象创建器
     */
    private String proxyCreator;

    /**
     * 拦截器
     */
    private String interceptor;
}
