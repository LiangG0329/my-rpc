package com.code.rpc.serializer;

import com.code.rpc.spi.SpiLoader;

/**
 * 序列化器工厂（获取序列化器对象）
 *
 * @author Liang
 * @create 2024/3/21
 */
public class SerializerFactory {

    private static volatile boolean isSerializerSpiLoaded = false;

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取指定序列化器实例
     *
     * @param key 键
     * @return 序列化器
     */
    public static Serializer getInstance(String key) {
        if (!isSerializerSpiLoaded) {
            synchronized (SerializerFactory.class) {
                if (!isSerializerSpiLoaded) {
                    SpiLoader.load(Serializer.class);
                    isSerializerSpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
