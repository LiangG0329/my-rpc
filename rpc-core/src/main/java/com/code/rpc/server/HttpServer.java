package com.code.rpc.server;

/**
 * Http 服务器接口
 *
 * @author Liang
 * @create 2024/3/14
 */
public interface HttpServer {
    /**
     * 启动服务器
     * @param port 端口号
     */
    void doStart(int port);
}
