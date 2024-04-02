package com.code.rpc.model;

import com.code.rpc.server.tcp.VertxTcpClient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;

/**
 * 发送 RPC 请求调用封装类
 *
 * @author Liang
 * @create 2024/4/1
 */
@Data
@AllArgsConstructor
public class RpcRequestAction implements Callable<RpcResponse> {

    private String ip;
    private RpcRequest rpcRequest;
    private ServiceMetaInfo selectedServiceMetaInfo;

    @Override
    public RpcResponse call() throws Exception {
        return VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
    }
}
