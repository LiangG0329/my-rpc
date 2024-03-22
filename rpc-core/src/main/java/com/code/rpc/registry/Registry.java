package com.code.rpc.registry;

import com.code.rpc.config.RegistryConfig;
import com.code.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 *
 * @author Liang
 * @create 2024/3/22
 */
public interface Registry {

    /**
     * 初始化
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注册服务，指定过期时间（服务端）
     * @param serviceMetaInfo 服务元信息
     * @param timeOut 服务过期时间
     */
    void register(ServiceMetaInfo serviceMetaInfo, long timeOut) throws Exception;

    /**
     * 注销未关联租约的服务 （服务端）
     * @param serviceMetaInfo 服务元信息
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现 （根据服务键名，获取相关服务的所有节点  消费端）
     * @param serviceKey 服务键名
     * @return 服务元信息列表
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();
}
