package com.code.rpc.config;

import lombok.Data;

/**
 * RPC 框架注册中心配置
 *
 * @author Liang
 * @create 2024/3/22
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类型
     */
    private String registry = "etcd";

    /**
     * 注册中心地址  etcd端口:2379 zookeeper端口:2181<br>
     * 注意：etcd注册中心地址需包含 ”http://"或"https://"前缀  zookeeper注册中心地址不包含前缀
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 30000L;
}
