package com.code.rpc.fault.retry;

import com.code.rpc.model.RpcResponse;
import org.junit.Test;

/**
 * 重试策略测试
 *
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();
    RetryStrategy retryStrategy2 = new RandomDelayRetryStrategy();
    RetryStrategy retryStrategy3 = new FixedIntervalRetryStrategy();
    RetryStrategy retryStrategy4 = new FibonacciIntervalRetryStrategy();
    RetryStrategy retryStrategy5 = new ExponentialBackoffRetryStrategy();
    RetryStrategy retryStrategy6 = new CompositeRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy5.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}