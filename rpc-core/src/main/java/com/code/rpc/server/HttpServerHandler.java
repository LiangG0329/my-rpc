package com.code.rpc.server;

import com.code.rpc.RpcApplication;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.registry.LocalRegistry;
import com.code.rpc.serializer.Serializer;
import com.code.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Http 请求处理器
 *
 * @author Liang
 * @create 2024/3/14
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 获取指定的序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());  // 请求方式 + uri

        // 异步处理 HTTP 请求
        request.bodyHandler(body -> {
            // 反序列化 rpc请求 为对象
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest =  serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构造rpc响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为null,直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取需要调用的服务实现类(从服务注册中心)和目标方法，通过反射调用
                Class<?> serviceImplClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = serviceImplClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterType());
                Object result = method.invoke(serviceImplClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 设置 http响应
     * @param request 请求
     * @param rpcResponse rpc响应
     * @param serializer 序列化器
     */
    private static void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {

        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");

        try {
            // rpc响应序列化
            byte[] bytes = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
