package com.code.rpc.interceptor.nouse;

import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcRequestAction;
import com.code.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志拦截器 （CGLib实现）
 *
 * @author Liang
 * @create 2024/4/1
 */
@Deprecated
@Slf4j
public class LogInterceptorByCGLib implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        RpcRequestAction action = (RpcRequestAction) args[0];
        String ip = action.getIp();
        RpcRequest request = action.getRpcRequest();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = now.format(formatter);
        // 打印请求日志
        log.info("请求开始，time = {}, ip = {}, request = {}", formatDate, ip, request);
        RpcResponse result = (RpcResponse) methodProxy.invokeSuper(obj, args);
        now = LocalDateTime.now();
        formatDate = now.format(formatter);
        // 打印响应日志
        log.info("请求结束，time = {}, response = {}",formatDate, result);

        return result;
    }
}
