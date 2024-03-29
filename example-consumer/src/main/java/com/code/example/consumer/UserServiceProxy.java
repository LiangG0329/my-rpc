package com.code.example.consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.constant.RpcConstant;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.registry.Registry;
import com.code.rpc.registry.RegistryFactory;
import com.code.rpc.serializer.Serializer;
import com.code.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 服务代理 静态代理
 *
 * @author Liang
 * @create 2024/3/15
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造 rpc 请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();


        try {
            // 请求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;

            // 从注册中心获取服务提供者地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName("getUser");
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 获取第一个服务
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发送包含rpc请求的http请求,从http响应获取rpc响应
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            // 响应反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
