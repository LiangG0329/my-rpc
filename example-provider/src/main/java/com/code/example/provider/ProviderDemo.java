package com.code.example.provider;

import com.code.example.common.service.UserService;
import com.code.rpc.bootstrap.ProviderBootstrap;
import com.code.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 *
 * @author Liang
 * @create 2024/3/31
 */
public class ProviderDemo {

    public static void main(String[] args) {
        // 服务列表
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl_2.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
