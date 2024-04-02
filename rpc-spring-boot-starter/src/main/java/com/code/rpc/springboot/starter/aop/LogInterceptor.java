package com.code.rpc.springboot.starter.aop;

import com.code.rpc.model.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请求响应日志 AOP
 *
 * @author Liang
 * @create 2024/4/1
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截，日志记录
     */
    @Around("execution(* com.code.rpc.server.tcp.VertxTcpClient.doRequest(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDate = now.format(formatter);

        // 获取方法参数值
        Object[] args = point.getArgs();
        String ip = (String) args[0];
        RpcRequest rpcRequest = (RpcRequest) args[1];
        String reqParam = "[" + rpcRequest.toString() + "]";
        // 打印请求日志
        log.info("请求开始. Time: {}, Client Ip: {}, Params {}", formatDate, ip, reqParam);
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("请求结束, Cost: {}ms", totalTimeMillis);
        return result;
    }
}
