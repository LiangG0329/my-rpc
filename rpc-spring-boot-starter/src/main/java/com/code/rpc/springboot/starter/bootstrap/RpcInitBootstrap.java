package com.code.rpc.springboot.starter.bootstrap;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.server.tcp.VertxTcpServer;
import com.code.rpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.Objects;

/**
 * RPC 框架启动
 *
 * @author Liang
 * @create 2024/3/31
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 @EnableRpc 注解属性值
        Map<String, Object> annotationAttributes = Objects.requireNonNull(importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()));
        boolean needServer = (boolean) annotationAttributes.get("needServer");

        // RPC全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 设置消费者配置，先根据注解属性设置，再根据配置文件设置（配置优先级：配置文件 > 注解属性 > 默认值）
        if (!needServer) {
            String loadBalancer = (String) annotationAttributes.get("loadBalancer");
            String retryStrategy = (String) annotationAttributes.get("retryStrategy");
            String tolerantStrategy = (String) annotationAttributes.get("tolerantStrategy");
            boolean mock = (boolean) annotationAttributes.get("mock");
            String mockService = (String) annotationAttributes.get("mockService");
            String proxyCreator = (String) annotationAttributes.get("proxyCreator");
            String interceptor = (String) annotationAttributes.get("interceptor");
            String cacheExpireTime = (String) annotationAttributes.get("cacheExpireTime");
            rpcConfig.setLoadBalancer(loadBalancer);
            rpcConfig.setRetryStrategy(retryStrategy);
            rpcConfig.setTolerantStrategy(tolerantStrategy);
            rpcConfig.setMock(mock);
            rpcConfig.setMockService(mockService);
            rpcConfig.setProxyCreator(proxyCreator);
            rpcConfig.setInterceptor(interceptor);
            rpcConfig.setCacheExpireTime(cacheExpireTime);
        }

        // RPC框架初始化（配置和注册中心）
        RpcApplication.init();

        log.info("Global RPC Config: " + rpcConfig);

        // 启动服务
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
            log.info("服务端启动 TCP SERVER");
        } else {
            log.info("未启动 TCP SERVER");
        }
    }
}
