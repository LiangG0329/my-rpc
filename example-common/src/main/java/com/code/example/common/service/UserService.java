package com.code.example.common.service;

import com.code.example.common.model.User;

/**
 * 用户服务接口
 *
 * @author Liang
 * @create 2024/3/14
 */
public interface UserService {
    /**
     * 获取用户
     * @param user  用户
     * @return
     */
    User getUser(User user);

    /**
     * 用于测试 mock 接口返回值
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
