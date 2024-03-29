package com.code.rpc.fault.tolerant;

import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.mock.MockService;
import com.code.rpc.mock.MockServiceFactory;
import com.code.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 失败自动降级恢复 - 容错策略 （调用消费者本地服务方法）
 * @author Liang
 * @create 2024/3/29
 */
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取本地(模拟)服务提供者
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        MockService mockService = MockServiceFactory.getInstance(rpcConfig.getMockService());
        Object mock = mockService.mock();
        return RpcResponse.builder().data(mock).message("ok").build();
    }
}
