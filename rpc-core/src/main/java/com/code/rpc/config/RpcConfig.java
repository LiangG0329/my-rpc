package com.code.rpc.config;

import com.code.rpc.fault.retry.RetryStrategyKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
import com.code.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架全局配置
 *
 * @author Liang
 * @create 2024/3/21
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name ="my-prc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名称
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 开启 mock ,模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器（默认 JDK）
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器（默认轮询）
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;
}
