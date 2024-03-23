package com.code.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.code.rpc.RpcApplication;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx TCP 请求客户端
 *
 * @author Liang
 * @create 2024/3/23
 */
public class VertxTcpClient {

    /**
     * 发送 携带 RPC 请求的 TCP 请求
     *
     * @param rpcRequest      携带的 RPC 请求
     * @param serviceMetaInfo 服务元信息
     * @return RPC响应数据
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 发送包含 RPC 请求 的 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        // Vertx提供的请求处理器是异步、反应式的，为了更方便地获取结果，使用CompletableFuture转异步为同步
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(),
                serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                    }

                    System.out.println("Connected to TCP server");

                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    // 初始化消息头
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer());
                    if (serializerEnum == null) {
                        throw new RuntimeException(RpcApplication.getRpcConfig().getSerializer() + "序列化协议不存在");
                    }
                    header.setSerializer((byte) serializerEnum.getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    // 生成全局请求 ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 接收响应，解码
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            // 异步任务已经完成，设置异步任务结果
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息解码错误");
                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                });

        // 阻塞，直到响应完成获取结果，将异步转为同步
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭连接，返回RPC响应的响应数据部分
        netClient.close();
        return rpcResponse;
    }
}
