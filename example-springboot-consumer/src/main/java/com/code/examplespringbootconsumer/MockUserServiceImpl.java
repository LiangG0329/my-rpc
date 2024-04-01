package com.code.examplespringbootconsumer;

import com.code.example.common.model.User;
import com.code.rpc.mock.MockService;

/**
 * 降级模拟服务实现类
 *
 * @author Liang
 */
public class MockUserServiceImpl implements MockService {

    @Override
    public Object mock() {
        System.out.println("mock service is running");
        return new User("hello world");
    }
}
