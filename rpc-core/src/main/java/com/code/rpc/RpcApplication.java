package com.code.rpc;

import com.code.rpc.config.RegistryConfig;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.constant.RpcConstant;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * RPC框架应用
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
    private static volatile RpcConfig rpcConfig;

    private RpcApplication() {}

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig prc配置
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", rpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
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
        if(rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }

        return rpcConfig;
    }
}
