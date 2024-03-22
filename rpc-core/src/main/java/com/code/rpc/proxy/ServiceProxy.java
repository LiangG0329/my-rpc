package com.code.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.code.rpc.RpcApplication;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.serializer.Serializer;
import com.code.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理 （JDK动态代理）
 *
 * @author Liang
 * @create 2024/3/15
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 获取配置类指定的序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterType(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // rpc请求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            // 发送包含rpc请求的http请求，从响应获取rpc响应
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:" + RpcApplication.getRpcConfig().getServerPort())
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
                // rpc响应反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
