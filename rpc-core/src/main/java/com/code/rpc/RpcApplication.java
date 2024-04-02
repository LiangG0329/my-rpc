package com.code.rpc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.code.rpc.config.RegistryConfig;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.constant.RpcConstant;
import com.code.rpc.fault.retry.RetryStrategyKeys;
import com.code.rpc.fault.tolerant.TolerantStrategyKeys;
import com.code.rpc.interceptor.InterceptorKeys;
import com.code.rpc.interceptor.proxycreator.ProxyKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
import com.code.rpc.mock.MockServiceKeys;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.serializer.SerializerKeys;
import com.code.rpc.utils.ConfigUtils;
import com.code.rpc.utils.YamlConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * RPC框架应用<br>
 * RPC项目启动入口，并维护全局配置对象
 *
 * @author Liang
 * @create 2024/3/21
 */
@Slf4j
public class RpcApplication {

    /**
     * 全局配置对象
     */
    private static final RpcConfig rpcConfig = new RpcConfig();

    private RpcApplication() {}

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig prc配置
     */
    public static void init(RpcConfig newRpcConfig) {
        // rpcConfig = newRpcConfig;
        // 新配置覆盖旧配置
        BeanUtil.copyProperties(newRpcConfig, rpcConfig, CopyOptions.create().setIgnoreNullValue(true));
        // 验证，为空值填充默认值
        RpcApplication.validAndFillRpcConfig(rpcConfig);
        log.info("RPC init, config = {}", rpcConfig);
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化 读取配置文件
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            // 读取 .yml 文件
            newRpcConfig = YamlConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            if (newRpcConfig == null) {
                // 读取 .properties 文件
                newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            }
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置对象。使用双检锁单例模式
     * @return 配置对象
     */
    public static RpcConfig getRpcConfig() {
//        if(rpcConfig == null) {
//            synchronized (RpcApplication.class) {
//                if (rpcConfig == null) {
//                    init();
//                }
//            }
//        }

        return rpcConfig;
    }

    /**
     * RPC配置验证并填充默认值
     *
     * @param rpcConfig RPC 配置
     */
    private static void validAndFillRpcConfig(RpcConfig rpcConfig) {
        // 名称
        if (StrUtil.isBlank(rpcConfig.getName())) {
            rpcConfig.setName("my-prc");
        }

        // 版本号
        if (StrUtil.isBlank(rpcConfig.getVersion())) {
            rpcConfig.setVersion("1.0");
        }

        // 服务器主机名称
        if (StrUtil.isBlank(rpcConfig.getServerHost())) {
            rpcConfig.setServerHost("localhost");
        }

        // 服务器端口号
        if (rpcConfig.getServerPort() == null) {
            rpcConfig.setServerPort(8080);
        }

        // 开启 Mock 服务代理,模拟调用
        if (rpcConfig.getMock() == null) {
            rpcConfig.setMock(false);
        }

        // 序列化器（默认 JDK）
        if (StrUtil.isBlank(rpcConfig.getSerializer())) {
            rpcConfig.setSerializer(SerializerKeys.JDK);
        }

        // 负载均衡器（默认轮询）
        if (StrUtil.isBlank(rpcConfig.getLoadBalancer())) {
            rpcConfig.setLoadBalancer(LoadBalancerKeys.ROUND_ROBIN);
        }

        // 重试策略
        if (StrUtil.isBlank(rpcConfig.getRetryStrategy())) {
            rpcConfig.setRetryStrategy(RetryStrategyKeys.NO);
        }

        // 容错策略
        if (StrUtil.isBlank(rpcConfig.getTolerantStrategy())) {
            rpcConfig.setTolerantStrategy(TolerantStrategyKeys.FAIL_FAST);
        }

        // 降级模拟服务
        if (StrUtil.isBlank(rpcConfig.getMockService())) {
            rpcConfig.setMockService(MockServiceKeys.DEFAULT);
        }

        // 代理创建器
        if (StrUtil.isBlank(rpcConfig.getProxyCreator())) {
            rpcConfig.setProxyCreator(ProxyKeys.JDK);
        }

        // 拦截器
        if (StrUtil.isBlank(rpcConfig.getInterceptor())) {
            rpcConfig.setInterceptor(InterceptorKeys.LOG);
        }
    }
}
