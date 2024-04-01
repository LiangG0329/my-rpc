package com.code.rpc.bootstrap;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.model.ServiceRegisterInfo;
import com.code.rpc.registry.LocalRegistry;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务提供者启动类
 *
 * @author Liang
 * @create 2024/3/31
 */
@Slf4j
public class ProviderBootstrap {

    /**
     * 初始化
     * @param serviceRegisterInfoList 服务注册信息列表
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            log.info("Provider RPC Config = " + rpcConfig);
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        // 启动 web 服务
        // - 启动 HTTP 服务器
        // VertxHttpServer httpServer = new VertxHttpServer();
        // httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        // - 启动 TCP 服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
