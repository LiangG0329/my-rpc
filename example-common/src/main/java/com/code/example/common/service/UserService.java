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
}
