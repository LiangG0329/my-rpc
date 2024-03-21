package com.code.rpc.server;


import io.vertx.core.Vertx;

/**
 * Vertx Http 服务器
 *
 * @author Liang
 * @create 2024/3/14
 */
public class VertxHttpServer implements HttpServer{

    /**
     * 启动服务器
     * @param port 端口号
     */
    @Override
    public void doStart(int port) {
        // 创建 vertx 实例
        Vertx vertx = Vertx.vertx();

        // 创建 http 服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 设置服务器的请求处理器
        httpServer.requestHandler(new HttpServerHandler());

        // 启动 http 服务器并监听指定端口
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}
