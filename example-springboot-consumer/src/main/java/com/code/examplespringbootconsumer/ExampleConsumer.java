package com.code.examplespringbootconsumer;

import com.code.example.common.model.User;
import com.code.example.common.service.UserService;
import com.code.rpc.fault.tolerant.TolerantStrategyKeys;
import com.code.rpc.interceptor.InterceptorKeys;
import com.code.rpc.interceptor.proxycreator.ProxyCreatorKeys;
import com.code.rpc.loadbalancer.LoadBalancerKeys;
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
    @RpcReference(interfaceClass = UserService.class,
            tolerantStrategy = TolerantStrategyKeys.FAIL_BACK,
            loadBalancer = LoadBalancerKeys.ROUND_ROBIN,
            mock = false,
        mockService = "userMockService",
        proxyCreator = ProxyCreatorKeys.JDK,
        interceptor = InterceptorKeys.LOG)
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("what good thing we lose");
        System.out.println("origin user name: " + user.getName());
        User resultUser = userService.getUser(user);
        if (resultUser != null) {
            System.out.println("new user name: " + resultUser.getName());
        } else {
            System.out.println("new user: null");
        }
    }

}
