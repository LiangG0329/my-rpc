package com.code.rpc.loadbalancer;

import com.code.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器（消费端）
 *
 * @author Liang
 * @create 2024/3/26
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return 根据负载均衡算法从服务列表选择的服务调用
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
