package com.code.rpc.springboot.starter.bootstrap;

import com.code.rpc.config.ServiceRpcConfig;
import com.code.rpc.proxy.ServiceProxyFactory;
import com.code.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC 服务消费者启动
 *
 * @author Liang
 * @create 2024/4/1
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Bean 初始化后执行，注入服务
     *
     * @param bean bean实例
     * @param beanName bean名称
     * @return
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        Class<?> beanClass = bean.getClass();
        // 遍历对象所有属性
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            // 获取字段的 @RpcReference 注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            // 如果字段添加 @RpcReference，说明需要生成动态代理对象并赋值
            if (rpcReference != null) {
                // 为字段生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                // 构建单个服务级别的 PRC 配置
                boolean mock = rpcReference.mock();
                String loadBalancer = rpcReference.loadBalancer();
                String retryStrategy = rpcReference.retryStrategy();
                String tolerantStrategy = rpcReference.tolerantStrategy();
                String mockService = rpcReference.mockService();
                String proxyCreator = rpcReference.proxyCreator();
                String interceptor = rpcReference.interceptor();

                ServiceRpcConfig serviceRpcConfig = ServiceRpcConfig.builder()
                        .mock(mock)
                        .loadBalancer(loadBalancer)
                        .retryStrategy(retryStrategy)
                        .tolerantStrategy(tolerantStrategy)
                        .mockService(mockService)
                        .proxyCreator(proxyCreator)
                        .interceptor(interceptor)
                        .build();
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass, serviceRpcConfig);
                // 为字段注入服务代理对象
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
