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
    private ExampleConsumer exampleService;

    @Test
    void test1() {
        exampleService.test();
    }
}