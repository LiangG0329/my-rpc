package com.code.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.code.rpc.config.RegistryConfig;
import com.code.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Etcd 注册中心
 *
 * @author Liang
 * @create 2024/3/22
 */
@Slf4j
public class EtcdRegistry implements Registry{

    /**
     * 负责和 etcd 服务器建立网络连接，并提供方法以获取各种不同的子客户端
     */
    private Client client;

    /**
     * kvClient子客户端，用于对 etcd 中键值进行操作
     */
    private KV kvClient;

    /**
     * LeaseClient 子客户端，管理 etcd 的租约机制
     */
    private Lease leaseClient;

    /**
     * WatchClient 子客户端，管理监听机制
     */
    private Watch watchClient;

    /**
     * 本机注册节点 key 的集合（用于维护续期）（服务端）
     */
    private final Set<String> localRegisterNodeKeySet = new ConcurrentHashSet<>();

    /**
     * 注册中心服务缓存（消费端）
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 的集合（消费端）
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 注册节点 -> 租约 Id 的映射（服务端）
     */
    private final Map<String, Long> registerKeyLeaseIdMap = new ConcurrentHashMap<>();

    /**
     * 根节点（etcd数据库内）
     */
    private static final String ETCD_ROOT_PATH = "/rpc/etcd/";

    /**
     * 注册中心配置服务过期时间  单位:ms
     */
    private static long timeout;

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        leaseClient = client.getLeaseClient();
        watchClient = client.getWatchClient();
        timeout = registryConfig.getTimeout() / 1000;
        // 启动心跳检测，定期续约
        heartBeat();
    }

    /**
     * 注册服务（服务端）
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建指定时间的租约
        long leaseId = leaseClient.grant(timeout).get().getID();

        // 设置需要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值域租约关联，设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
        // 保存节点信息和租约Id映射，用于续期
        registerKeyLeaseIdMap.put(registerKey, leaseId);
    }

    /**
     * 注册服务，指定过期时间（服务端）
     * 注意心跳检测10s一次，过期时间不应低于10s，否则无法续期
     * @param serviceMetaInfo 服务元信息
     * @param timeOut 服务过期时间
     */
    public void register(ServiceMetaInfo serviceMetaInfo, long timeOut) throws Exception {
        // 创建指定时间的租约
        long leaseId = leaseClient.grant(timeOut).get().getID();

        // 设置需要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值域租约关联，设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
        // 保存节点信息和租约Id映射，用于续期
        registerKeyLeaseIdMap.put(registerKey, leaseId);
    }

    /**
     * 注销服务（服务端）
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        try {
            kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            throw new RuntimeException(registerKey + "服务注销失败", e);
        }
        // 从本地缓存中移除
        localRegisterNodeKeySet.remove(registerKey);
        // 删除节点信息和租约Id映射
        registerKeyLeaseIdMap.remove(registerKey);
    }

    /**
     * 删除指定租约的节点
     * @param serviceMetaInfo 服务元信息
     * @param leaseId leaseId
     */
    public void unRegister(ServiceMetaInfo serviceMetaInfo, long leaseId) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // 撤销租约，删除键值
        leaseClient.revoke(leaseId);
        // 从本地缓存中移除
        localRegisterNodeKeySet.remove(registerKey);
        // 删除节点信息和租约Id映射
        registerKeyLeaseIdMap.remove(registerKey);
    }

    /**
     * 服务发现（消费者）
     * @param serviceKey 服务键名
     * @return 服务列表
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (!CollUtil.isEmpty(cacheServiceMetaInfoList)) {
            return cacheServiceMetaInfoList;
        }

        // 前缀搜索，从 etcd 数据库中查找 （结尾一定要加 '/'）
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 变化
                        notify(serviceKey);
                        watch(key, serviceKey);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());

            // 写入本地缓存
            registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 心跳检测，每 10s 一次，对本地缓存节点中的正常运行节点执行续期（服务端）
     */
    @Override
    public void heartBeat() {
        // 每10s续期一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本地缓存内所有节点的Key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 当前节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            localRegisterNodeKeySet.remove(key);
                            registerKeyLeaseIdMap.remove(key);
                            continue;
                        }
                        // 节点未过期，重新注册，实现续期
//                        KeyValue keyValue = keyValues.get(0);
//                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
//                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
//                        register(serviceMetaInfo);
                        // 节点未过期，使用 Lease.keepAliveOnce 实现续期
                        Long leaseId = registerKeyLeaseIdMap.get(key);
                        leaseClient.keepAliveOnce(leaseId);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续期失败", e);
                    }
                }
            }
        });

        // 支持秒级定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 监听（消费端）  使用 etcd.Watcher 实现
     * @param serviceNodeKey 服务节点键
     */
    @Override
    public void watch(String serviceNodeKey, String serviceKey) {
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        // key未被监听，开启监听
        if (newWatch) {
            log.info("开始监听服务 {} 的节点 {}", serviceKey, serviceNodeKey);
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            log.info("通知:服务 {} 的节点 {} 下线", serviceKey, serviceNodeKey);
                            watchingKeySet.remove(serviceNodeKey);
                            registryServiceCache.clearCache(serviceKey);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 服务订阅/通知（消费端）
     * @param serviceKey 服务键名
     */
    @Override
    public void notify(String serviceKey) {
        boolean newWatch = watchingKeySet.add(serviceKey);
        // key未被监听，开启监听
        if (newWatch) {
            log.info("订阅服务 {} ，开启通知", serviceKey);
            String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
            ByteSequence watchKey = ByteSequence.from("", StandardCharsets.UTF_8);
            ByteSequence prefix = ByteSequence.from(searchPrefix, StandardCharsets.UTF_8);
            WatchOption option = WatchOption.newBuilder().withPrefix(prefix).build();
            watchClient.watch(watchKey, option, watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        // key 更新(新节点上线)时触发
                        case PUT:
                            // 清空服务缓存
                            String key = event.getKeyValue().getKey().toString(StandardCharsets.UTF_8);
                            log.info("通知:服务 {} 的节点 {} 更新 ", serviceKey, key);
                            registryServiceCache.clearCache(serviceKey);
                            break;
                        case DELETE:
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");

        // 下线节点
        // 遍历删除缓存中的节点
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (leaseClient != null) {
            leaseClient.close();
        }
        if (watchClient != null) {
            watchClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
