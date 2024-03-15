package com.code.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.serializer.JDKSerializer;
import com.code.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 服务代理 静态代理
 * @author Liang
 * @create 2024/3/15
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JDKSerializer();

        // 构造 rpc 请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterType(new Class[]{User.class})
                .args(new Object[]{user})
                .build();


        try {
            // 请求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            // 发送包含rpc请求的http请求,从http响应获取rpc响应
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
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
