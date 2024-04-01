package com.code.rpc.fault.tolerant;

import com.code.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 *
 * @author Liang
 * @create 2024/3/29
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     *
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return  RPC 响应
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception;
}
