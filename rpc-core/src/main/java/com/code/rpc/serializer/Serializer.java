package com.code.rpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 *
 * @author Liang
 * @create 2024/3/14
 */
public interface Serializer {
    /**
     * 序列化
     * @param object 需序列化的对象
     * @return  序列化后的字节数组
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param bytes  字节数组
     * @param type  目标对象类型
     * @return  反序列化后的对象
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
