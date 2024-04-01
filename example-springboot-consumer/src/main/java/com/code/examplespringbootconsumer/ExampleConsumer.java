package com.code.examplespringbootconsumer;

import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 服务消费者
 *
 * @author Liang
 */
@Service
public class ExampleConsumer {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference(interfaceClass = UserService.class)
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("what good thing we lose");
        System.out.println("origin user name: " + user.getName());
        User resultUser = userService.getUser(user);
        System.out.println("new user name: " + resultUser.getName());
    }

}
