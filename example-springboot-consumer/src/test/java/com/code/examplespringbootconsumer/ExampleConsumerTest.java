package com.code.examplespringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 单元测试
 *
 * @author Liang
 */
@SpringBootTest
class ExampleConsumerTest {

    @Resource
    private ExampleConsumer exampleConsumer;

    @Resource
    private ExampleConsumer2 exampleConsumer2;

    @Resource
    private ExampleConsumer3 exampleConsumer3;

    @Test
    void test1() throws InterruptedException {
        exampleConsumer.test();
        Thread.sleep(200000);
        //exampleConsumer2.test();
        //Thread.sleep(3000);
        //exampleConsumer3.test();
        //Thread.sleep(3000);
    }

    @Test
    void testCacheExpire() throws InterruptedException {
        // 缓存设置修改后40s过期
        exampleConsumer.test(); // 缓存未命中
        Thread.sleep(10000);
        exampleConsumer.test();  // 缓存命中
        Thread.sleep(5000);
        exampleConsumer.test();  // 缓存命中
        Thread.sleep(30000);
        exampleConsumer.test();  // 缓存未命中
        Thread.sleep(10000);
        exampleConsumer.test();  // 缓存命中
    }
}