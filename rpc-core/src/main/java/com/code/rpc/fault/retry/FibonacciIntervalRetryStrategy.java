package com.code.rpc.fault.retry;

import com.code.rpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 斐波那契时间间隔 - 重试策略
 *
 * @author Liang
 * @create 2024/3/29
 */
@Slf4j
public class FibonacciIntervalRetryStrategy implements RetryStrategy {

    /**
     * 斐波那契时间间隔重试
     * <p>
     * 重试条件：出现 Exception 异常时重试
     * <p>
     * 重试等待策略：斐波那契时间间隔（初始等待时间 1s，最大等待时间 8s）
     * <p>
     * 重试停止策略：超过最大重试次数（5次）停止
     * <p>
     * 重试工作：打印重试日志，重新执行任务
     *
     * @param callable 执行的重试任务
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fibonacciWait(1000L, 8L, TimeUnit.SECONDS))
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
