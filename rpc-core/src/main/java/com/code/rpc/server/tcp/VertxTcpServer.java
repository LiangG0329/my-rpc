package com.code.rpc.server.tcp;

import com.code.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * Vertx TCP 服务器
 *
 * @author Liang
 * @create 2024/3/23
 */
public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer netServer = vertx.createNetServer();

        // 设置请求处理器
        netServer.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器，并监听指定端口
        netServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port: " + port);
            } else {
                System.out.println("Fail to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
