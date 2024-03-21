package com.code.rpc.config;

import lombok.Data;

/**
 * RPC 框架全局配置
 *
 * @author Liang
 * @create 2024/3/21
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name ="my-prc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名称
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;
}
