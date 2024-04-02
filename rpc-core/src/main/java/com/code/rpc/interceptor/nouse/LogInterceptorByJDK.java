package com.code.rpc.interceptor.nouse;

import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcRequestAction;
import com.code.rpc.model.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志拦截器 （JDK实现）
 *
 * @author Liang
 * @create 2024/4/1
 */
@Deprecated
@Slf4j
@Data
@AllArgsConstructor
public class LogInterceptorByJDK implements InvocationHandler {

    private Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequestAction action = (RpcRequestAction) args[0];
        String ip = action.getIp();
        RpcRequest request = action.getRpcRequest();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = now.format(formatter);
        // 打印请求日志
        log.info("请求开始，time = {}, ip = {}, request = {}", formatDate, ip, request);
        RpcResponse result = (RpcResponse) method.invoke(target, args);
        now = LocalDateTime.now();
        formatDate = now.format(formatter);
        // 打印响应日志
        log.info("请求结束，time = {}, response = {}",formatDate, result);

        return result;
    }
}
