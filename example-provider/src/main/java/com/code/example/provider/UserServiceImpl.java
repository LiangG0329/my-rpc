package com.code.example.provider;

import com.code.example.common.model.User;
import com.code.example.common.service.UserService;

/**
 * 用户服务实现类
 * @author Liang
 * @create 2024/3/14
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("修改前用户名：" + user.getName());
        user.setName("hello world");
        System.out.println("修改后用户名： " + user.getName());
        return user;
    }
}
