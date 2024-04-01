package com.code.examplespringbootprovider;

import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author Liang
 */
@Service
@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("origin user name:" + user.getName());
        user.setName("what bad thing we knew");
        System.out.println("new user name: " + user.getName());
        return user;
    }
}
