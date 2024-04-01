package com.code.rpc.mock;

/**
 * 模拟服务 （消费端实现，指定远程服务调用失败后执行的本地服务和方法）
 *
 * @author Liang
 * @create 2024/3/29
 */
public interface MockService {

    /**
     * 本地方法（降级服务）
     * @return data
     */
    Object mock();
}
