package com.code.rpc.mock;

import com.code.rpc.spi.SpiLoader;

/**
 * 模拟服务工厂
 *
 * @author Liang
 * @create 2024/3/29
 */
public class MockServiceFactory {

    /**
     * 是否完成 SPI 加载
     */
    private static volatile boolean isMockServiceSpiLoaded = false;

    /**
     * 默认降级模拟服务
     */
    private static final MockService DEFAULT_MOCK_SERVICE = new DefaultMockService();

    /**
     * 获取指定模拟服务实例
     * @param key 键
     * @return 模拟服务实例
     */
    public static MockService getInstance(String key) {
        if (!isMockServiceSpiLoaded) {
            synchronized (MockServiceFactory.class) {
                if (!isMockServiceSpiLoaded) {
                    SpiLoader.load(MockService.class);
                    isMockServiceSpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(MockService.class, key);
    }
}
