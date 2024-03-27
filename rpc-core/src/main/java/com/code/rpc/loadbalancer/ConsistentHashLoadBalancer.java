package com.code.rpc.loadbalancer;

import com.code.rpc.model.ServiceMetaInfo;
import net.openhft.hashing.LongHashFunction;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希-负载均衡器
 * 一致性哈希：一种经典的哈希算法，用于将请求分配到多个节点或服务器上，所以非常适用于负载均衡。核心思想是将整个哈希值空间划分成一个环状结构（哈希环），
 * 每个节点或服务器在哈希环上占据一个位置，每个请求根据其哈希值映射到环上的一个点，然后顺时针寻找第一个大于或等于该哈希值的节点，将请求路由到该节点上。
 * 一致性哈希还解决了 节点下线 和 倾斜问题 。
 * 1）节点下线：当某个节点下线时，其负载会被平均分摊到其他节点上，而不会影响到整个系统的稳定性，因为只有部分请求会受到影响。
 * 2）倾斜问题：通过虚拟节点的引入，将每个物理节点映射到多个虚拟节点上，使得节点在哈希环上的 分布更加均匀，减少了节点间的负载差异
 *
 * @author Liang
 * @create 2024/3/26
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性 Hash 环，环上存放虚拟节点（采用 TreeMap 有序字典实现）
     */
    private final TreeMap<Long, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建 Hash 环，添加虚拟节点，每个服务相同数量（100）的虚拟节点
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取请求的 hash 值
        long hash = getHash(requestParams);
        // 在 hash 环上选择大于等于请求 hash 值且最近的虚拟节点
        Map.Entry<Long, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // 如果没有大于等于请求 hash 值的虚拟节点，则返回 hash 环首部节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 获取 hash 值，采用 MurmurHash 算法
     * @param key 键
     * @return hash 值
     */
    private long getHash(Object key) {
        // 获取 murmur3 实例
        LongHashFunction murmur_3 = LongHashFunction.murmur_3();
        // 计算 key 的 hash 值
        return murmur_3.hashChars(key.toString());
    }
}
