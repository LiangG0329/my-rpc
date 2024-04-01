package com.code.rpc.bootstrap;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务消费者启动类
 *
 * @author Liang
 * @create 2024/3/31
 */
@Slf4j
public class ConsumerBootstrap {

    public static void init() {
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        log.info("Consumer RPC Config = " + rpcConfig);
    }
}
