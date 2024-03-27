package com.code.rpc.loadbalancer;

import com.code.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询-负载均衡器
 *
 * @author Liang
 * @create 2024/3/26
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 当前轮询下标（采用 JUC 包的 AtomicInteger 实现原子计数器，防止并发冲突问题）
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfoList.size();
        // 只有一个服务，无需轮询
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        } else {
            // 取模轮询
            int index = currentIndex.getAndIncrement() % size;
            return serviceMetaInfoList.get(index);
        }
    }
}
