package com.code.rpc.fault.tolerant;

import com.code.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 - 容错策略 （立即抛出异常，通知外层调用方法）
 * @author Liang
 * @create 2024/3/29
 */
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务调用异常", e);
    }
}
