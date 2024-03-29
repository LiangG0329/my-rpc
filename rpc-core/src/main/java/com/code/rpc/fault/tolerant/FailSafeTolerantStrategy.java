package com.code.rpc.fault.tolerant;

import com.code.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理 - 容错策略 （记录异常日志，正常返回响应对象）
 *
 * @author Liang
 * @create 2024/3/29
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常", e);
        return new RpcResponse();
    }
}
