package com.code.rpc;

import com.code.rpc.config.RpcConfig;
import com.code.rpc.constant.RpcConstant;
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

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", rpcConfig.toString());
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
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
