package com.code.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.code.rpc.constant.RpcConstant;
import lombok.Data;

/**
 * 服务元信息
 *
 * @author Liang
 * @create 2024/3/22
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     *
     * @return 服务键名  (服务名称:服务版本号)
     */
    public String getServiceKey() {
        // 后期可拓展分组服务
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名 （一个服务可能有多个提供者，即多个注册节点）
     *
     * @return 服务注册节点键名  (服务键名/节点域名:端口)
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取完整服务节点地址
     *
     * @return 服务节点地址
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
