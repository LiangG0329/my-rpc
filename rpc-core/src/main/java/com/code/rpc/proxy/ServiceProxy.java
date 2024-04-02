package com.code.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.config.ServiceRpcConfig;
import com.code.rpc.constant.RpcConstant;
import com.code.rpc.fault.retry.RetryStrategy;
import com.code.rpc.fault.retry.RetryStrategyFactory;
import com.code.rpc.fault.tolerant.TolerantStrategy;
import com.code.rpc.fault.tolerant.TolerantStrategyFactory;
import com.code.rpc.interceptor.Interceptor;
import com.code.rpc.interceptor.InterceptorFactory;
import com.code.rpc.interceptor.proxycreator.ProxyCreator;
import com.code.rpc.interceptor.proxycreator.ProxyCreatorFactory;
import com.code.rpc.loadbalancer.LoadBalancer;
import com.code.rpc.loadbalancer.LoadBalancerFactory;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcRequestAction;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.serializer.Serializer;
import com.code.rpc.serializer.SerializerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理 （JDK动态代理）
 *
 * @author Liang
 * @create 2024/3/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ServiceProxy implements InvocationHandler {

    private ServiceRpcConfig serviceRpcConfig;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取配置类指定的序列化器
        // Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造rpc请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 获取服务配置
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            if (serviceRpcConfig != null) {
                buildServiceRpcConfig(rpcConfig, serviceRpcConfig);
            }
            log.info("Service RPC Config: " + rpcConfig);
            // 从注册中心获取服务提供者地址
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 负载均衡，选择服务
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println("Selected Service Meta Info: " + selectedServiceMetaInfo);

            // 发送携带 RPC 请求 的 HTTP 请求，从响应获取 RPC 响应
            // RPC 请求序列化
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // rpc响应反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

            // 发送携带 RPC 请求 的 TCP 请求，从响应获取 RPC 响应
            RpcResponse rpcResponse;
            // 保存上下文信息（服务列表，故障服务，RPC 请求，负载均衡器），异常时传递给容错处理策略
            Map<String, Object> context = new HashMap<>();
            context.put("serviceList", serviceMetaInfoList);
            context.put("errorService", selectedServiceMetaInfo);
            context.put("rpcRequest", rpcRequest);
            context.put("loadBalancer", rpcConfig.getLoadBalancer());
            String ip = NetUtil.getLocalhostStr();
            context.put("ip", ip);
            RpcRequestAction rpcRequestAction = new RpcRequestAction(ip, rpcRequest, selectedServiceMetaInfo);
            // 使用重试机制发起请求
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                // 添加拦截器
                ProxyCreator proxyCreator = ProxyCreatorFactory.getInstance(rpcConfig.getProxyCreator());
                Interceptor interceptor = InterceptorFactory.getInstance(rpcConfig.getInterceptor());
                RetryStrategy interceptorProxy = proxyCreator.createProxy(retryStrategy, interceptor);
                rpcResponse = interceptorProxy.doRetry(rpcRequestAction);
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(context, e);
            }
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败", e);
        }
    }

    /**
     * 发送携带 RPC 请求 的 HTTP 请求，从响应获取 RPC 响应
     * @param serviceMetaInfo 服务元信息
     * @param bodyBytes 消息体字节流
     * @return RPC 响应
     */
    public static RpcResponse doHttpRequest(ServiceMetaInfo serviceMetaInfo, byte[] bodyBytes) {
        // 获取配置类指定的序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // rpc响应反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void buildServiceRpcConfig(RpcConfig rpcConfig, ServiceRpcConfig serviceRpcConfig) {
        if (StrUtil.isNotBlank(serviceRpcConfig.getLoadBalancer())) {
            rpcConfig.setLoadBalancer(serviceRpcConfig.getLoadBalancer());
        }
        if (StrUtil.isNotBlank(serviceRpcConfig.getRetryStrategy())) {
            rpcConfig.setRetryStrategy(serviceRpcConfig.getRetryStrategy());
        }
        if (StrUtil.isNotBlank(serviceRpcConfig.getTolerantStrategy())) {
            rpcConfig.setTolerantStrategy(serviceRpcConfig.getTolerantStrategy());
        }
        if (StrUtil.isNotBlank(serviceRpcConfig.getMockService())) {
            rpcConfig.setMockService(serviceRpcConfig.getMockService());
        }
    }
}
