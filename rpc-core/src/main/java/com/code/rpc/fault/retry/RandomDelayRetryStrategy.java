package com.code.rpc.fault.retry;

import com.code.rpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 随机延迟 - 重试策略
 *
 * @author Liang
 * @create 2024/3/29
 */
@Slf4j
public class RandomDelayRetryStrategy implements RetryStrategy {

    /**
     * 随机延迟重试
     * <p>
     * 重试条件：出现 Exception 异常时重试
     * <p>
     * 重试等待策略：随机延迟（1s-5s）
     * <p>
     * 重试停止策略：超最大重试次数（5次）停止
     * </p>
     * 重试工作：打印重试日志，重新执行任务
     *
     * @param callable 执行的重试任务
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                // 随机等待策略，最小等待时间1秒，最大等待时间5秒
                .withWaitStrategy(WaitStrategies.randomWait(1L, TimeUnit.SECONDS, 5L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.getAttemptNumber() == 1) {
                            log.info("重试次数 {}, 第一次调用", attempt.getAttemptNumber() - 1);
                        } else {
                            log.info("重试次数 {}, 距离第一次重试的延迟 {} 毫秒", attempt.getAttemptNumber() - 1, attempt.getDelaySinceFirstAttempt());
                        }
                        if (attempt.hasException()) {
                            log.error("Retry Fail causeBy=" + attempt.getExceptionCause().toString());
                        }
                    }
                }).build();

        return retryer.call(callable);
    }
}
