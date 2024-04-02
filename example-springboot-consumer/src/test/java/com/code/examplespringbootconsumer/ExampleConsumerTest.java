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
        Thread.sleep(3000);
        exampleConsumer2.test();
        Thread.sleep(3000);
        exampleConsumer3.test();
        Thread.sleep(3000);
    }
}