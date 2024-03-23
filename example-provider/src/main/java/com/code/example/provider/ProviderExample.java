package com.code.example.provider;

import com.code.example.common.service.UserService;
import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.registry.LocalRegistry;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.server.VertxHttpServer;

/**
 * 注册中心测试
 *
 * @author Liang
 * @create 2024/3/22
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
