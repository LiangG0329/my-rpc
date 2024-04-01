package com.code.rpc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

/**
 * .yml 配置文件工具类
 *
 * @author Liang
 * @create 2024/4/1
 */
public class YamlConfigUtils {

    /**
     * 加载配置对象，不指定环境
     * @param tClass 读取配置文件加载目标对象的 Class 对象
     * @param prefix 配置项前缀
     * @return 配置对象
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境  application-test/prod/xxx.yml
     *
     * @param tClass 读取配置文件加载目标对象的 Class 对象
     * @param prefix 配置项前缀
     * @param environment 环境
     * @param <T>
     * @return 配置对象
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (!environment.isEmpty()) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".yml");
        String configFilePath = configFileBuilder.toString();

        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(configFilePath);
            Map<String, Object> obj = yaml.load(inputStream);
            Map<String, Object> targetMap = obj;
            for (String key : prefix.split("\\.")) {
                targetMap = (Map<String, Object>) targetMap.get(key);
            }
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("加载 .yml 文件");
            return mapper.convertValue(targetMap, tClass);
        } catch (Exception e) {
            System.err.println("加载 .yml 文件失败");
            // e.printStackTrace();
        }

        return null;
    }
}
