package com.code.rpc.fault.retry;

import com.code.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不重试 - 重试策略
 *
 * @author Liang
 * @create 2024/3/29
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试
     * @param callable 执行的重试任务
     * @return RPC响应
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
