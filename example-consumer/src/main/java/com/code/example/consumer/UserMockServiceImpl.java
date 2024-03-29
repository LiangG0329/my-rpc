package com.code.example.consumer;

import com.code.example.common.model.User;
import com.code.rpc.mock.MockService;

/**
 * userService 本地模拟服务
 *
 * @author Liang
 * @create 2024/3/29
 */
public class UserMockServiceImpl implements MockService {

    @Override
    public Object mock() {
        System.out.println("what good thing we lost, what bad thing we knew");
        return new User("what good thing we lost, what bad thing we knew");
    }
}
