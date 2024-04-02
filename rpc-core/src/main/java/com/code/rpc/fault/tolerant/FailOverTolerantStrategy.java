package com.code.rpc.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.code.rpc.loadbalancer.LoadBalancer;
import com.code.rpc.loadbalancer.LoadBalancerFactory;
import com.code.rpc.model.RpcRequest;
import com.code.rpc.model.RpcResponse;
import com.code.rpc.model.ServiceMetaInfo;
import com.code.rpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 故障转移 - 容错策略 （转移到其他服务节点再次进行调用）
 *
 * @author Liang
 * @create 2024/3/29
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws ExecutionException, InterruptedException {
        // 从上下文获取相关对象（服务列表，故障服务，请求）
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceList");
        ServiceMetaInfo errorService = (ServiceMetaInfo) context.get("errorService");
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        String loadBalancerKey = (String) context.get("loadBalancer");
        String ip = (String) context.get("ip");
        // 从服务列表移除故障服务，避免重试故障服务
        serviceMetaInfoList.remove(errorService);
        // 重试，调用其他服务节点
        if (CollUtil.isNotEmpty(serviceMetaInfoList)) {
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(loadBalancerKey);
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams =  new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println("Tolerant Strategy Selected Service Meta Info: " + selectedServiceMetaInfo);
            return VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
        }
        return null;
    }
}
