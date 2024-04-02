package com.code.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.code.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器
 * 支持键值对映射
 *
 * @author Liang
 * @create 2024/3/21
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类： 接口名 => (key => 实现类)
     */
    private static final Map<String, Map<String, Class<?>>> loadMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存 类路径（全类名） => 对象实例 （单例模式）
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIR = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载类列表  全类名 com.code.rpc.serializer.Serializer
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 按需加载扫描路径下目标接口的实现类的类对象
     *
     * @param loadClass 接口类
     * @return 目标接口实现类的类对象的键值映射
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 保存 键和实现类的类对象
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        // 扫描路径，先扫描系统SPI，再扫描用户自定义SPI。用户自定义的 SPI 优先级高于系统 SPI
        for (String scanDir : SCAN_DIR) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("#")) continue;
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("SPI resource:{} load error", resource, e);
                }
            }
        }
        // 保存到loadMap
        loadMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取指定接口的指定实例
     * @param tClass 接口
     * @param key 键
     * @return 目标实例
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loadMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 的类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 加载的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        // 缓存不存在则添加实例，保证单例
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.getDeclaredConstructor().newInstance());
                log.info("获取{}类实例", implClassName);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                String errorMsg = String.format("%s类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }

        return (T) instanceCache.get(implClassName);
    }
}

