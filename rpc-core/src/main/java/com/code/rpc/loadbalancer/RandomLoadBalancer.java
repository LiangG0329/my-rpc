package com.code.rpc.loadbalancer;

import com.code.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机-负载均衡器
 *
 * @author Liang
 * @create 2024/3/26
 */
public class RandomLoadBalancer implements LoadBalancer {

    /**
     * 采用 Random 类实现随机选取
     */
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfoList.size();
        // 只有1个服务，无需随机
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        } else {
            // 随机选取服务
            return serviceMetaInfoList.get(random.nextInt(size));
        }
    }
}
