package com.code.example.consumer;

import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.RpcApplication;
import com.code.rpc.config.RpcConfig;
import com.code.rpc.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 *
 * @author Liang
 * @create 2024/3/14
 */
public class EasyConsumerExample {

    public static void main(String[] args) throws InterruptedException {
        // RPC框架初始化
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig = " + rpcConfig);

        // 代理 - 实现消费者发起调用
        // 静态代理
        // UserService userService = new UserServiceProxy();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("GL");
        System.out.println("user = " + user.getName());
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        Thread.sleep(3000);
        newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        Thread.sleep(3000);
        newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        System.exit(0);
//        long number = userService.getNumber();
//        System.out.println("number = " + number);
    }
}
