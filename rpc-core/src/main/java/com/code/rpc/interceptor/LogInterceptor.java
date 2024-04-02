package com.code.rpc.interceptor;

import com.code.rpc.interceptor.Interceptor;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcRequestAction;
import com.code.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

/**
 * 日志拦截器
 *
 * @author Liang
 * @create 2024/4/2
 */
@Slf4j
public class LogInterceptor implements Interceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args) throws Throwable {
        RpcRequestAction action = (RpcRequestAction) args[0];
        String ip = action.getIp();
        RpcRequest request = action.getRpcRequest();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = now.format(formatter);
        // 打印请求日志
        log.info("请求开始，time = {}, ip = {}, request = {}", formatDate, ip, request);
        RpcResponse result = (RpcResponse) method.invoke(obj, args);
        now = LocalDateTime.now();
        formatDate = now.format(formatter);
        // 打印响应日志
        log.info("请求结束，time = {}, response = {}",formatDate, result);

        return result;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, Callable<?> zuper) throws Throwable {
        RpcRequestAction action = (RpcRequestAction) args[0];
        String ip = action.getIp();
        RpcRequest request = action.getRpcRequest();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = now.format(formatter);
        // 打印请求日志
        log.info("请求开始，time = {}, ip = {}, request = {}", formatDate, ip, request);
        RpcResponse result = (RpcResponse) zuper.call();  // 使用 Callable<?> zuper 来执行原始方法
        now = LocalDateTime.now();
        formatDate = now.format(formatter);
        // 打印响应日志
        log.info("请求结束，time = {}, response = {}",formatDate, result);

        return result;
    }

}
