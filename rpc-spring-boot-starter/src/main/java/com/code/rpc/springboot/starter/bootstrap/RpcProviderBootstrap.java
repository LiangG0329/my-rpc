package com.code.rpc.springboot.starter.bootstrap;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RegistryConfig;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.registry.LocalRegistry;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * RPC 服务提供者启动
 *
 * @author Liang
 * @create 2024/3/31
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * 初始化后执行，注册服务
     *
     * @param bean bean实例
     * @param beanName bean名称
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException  {
        Class<?> beanClass = bean.getClass();
        // 获取 @RpcService 注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        // 如果bean添加 @RpcService ,说明需要注册服务
        if (rpcService != null) {
            // 1.获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2.服务注册
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
