package com.code.example.provider;

import com.code.example.common.service.UserService;
import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.registry.LocalRegistry;
import com.code.rpc.server.HttpServer;
import com.code.rpc.server.VertxHttpServer;

/**
 * 简易提供者示例
 *
 * @author Liang
 * @create 2024/3/14
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig = " + rpcConfig);

        // 向服务中心注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        System.out.println(RpcApplication.getRpcConfig().getVersion());
    }
}
