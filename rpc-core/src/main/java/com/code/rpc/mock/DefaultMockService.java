package com.code.rpc.mock;

/**
 * 默认模拟服务（降级服务）
 *
 * @author Liang
 * @create 2024/3/31
 */
public class DefaultMockService implements MockService{

    @Override
    public Object mock() {
        return null;
    }
}
