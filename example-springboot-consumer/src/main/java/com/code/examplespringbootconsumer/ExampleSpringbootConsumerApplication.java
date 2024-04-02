package com.code.examplespringbootconsumer;

import com.code.rpc.fault.retry.RetryStrategyKeys;
import com.code.rpc.fault.tolerant.TolerantStrategyKeys;
import com.code.rpc.interceptor.proxycreator.ProxyCreatorKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
import com.code.rpc.mock.MockServiceKeys;
import com.code.rpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false,
        loadBalancer = LoadBalancerKeys.RANDOM,
        retryStrategy = RetryStrategyKeys.FIBONACCI_INTERVAL,
        tolerantStrategy = TolerantStrategyKeys.FAIL_SAFE,
        mockService = MockServiceKeys.DEFAULT,
        proxyCreator = ProxyCreatorKeys.JDK)
public class ExampleSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }

}
