package com.code.rpc.fault.retry;

import com.code.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 *
 * @author Liang
 * @create 2024/3/29
 */
public interface RetryStrategy {

    /**
     * 重试
     *
     * @param callable 执行的重试任务
     * @return RPC响应
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
