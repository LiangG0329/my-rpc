package com.code.example.consumer;

import com.code.rpc.config.RpcConfig;
import com.code.rpc.utils.ConfigUtils;

/**
 * @author
 * @create 2024/3/21
 */
public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println("rpc = " + rpc);
    }
}
