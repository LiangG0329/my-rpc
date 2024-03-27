package com.code.rpc.loadbalancer;

import com.code.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂（工厂模式，获取负载均衡器对象）
 *
 * @author Liang
 * @create 2024/3/27
 */
public class LoadBalancerFactory {

    /**
     * 是否完成 SPI 加载
     */
    private static volatile boolean isLoadBalancerSpiLoaded = false;

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取指定负载均衡器实例
     *
     * @param key 键
     * @return 负载均衡器实例
     */
    public static LoadBalancer getInstance(String key) {
        if (!isLoadBalancerSpiLoaded) {
            synchronized (LoadBalancerFactory.class) {
                if (!isLoadBalancerSpiLoaded) {
                    SpiLoader.load(LoadBalancer.class);
                    isLoadBalancerSpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
