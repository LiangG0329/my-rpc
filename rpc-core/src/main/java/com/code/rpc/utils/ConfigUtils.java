package com.code.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 配置工具类
 *
 * @author Liang
 * @create 2024/3/21
 */
public class ConfigUtils {
    /**
     * 加载配置对象，不指定环境
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境  application-test/prod/...
     *
     * @param tClass 读取配置文件加载目标对象的Class对象
     * @param prefix 配置项前缀
     * @param environment 环境
     * @param <T>
     * @return 配置对象
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        String configFilePath = configFileBuilder.toString();
        // 使用UTF-8读取文件
        try {
            Props props = new Props();
            props.load(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(configFilePath)), StandardCharsets.UTF_8));
            return props.toBean(tClass, prefix);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
