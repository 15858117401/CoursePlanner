package com.yupi.agent.App;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoursePlannerTest {

    @Resource
    private CoursePlanner coursePlanner;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮
        String message = "你好，我是大一新生";
        String answer = coursePlanner.doChat(message, chatId);
        // 第二轮
        message = "我是统计专业的";
        answer = coursePlanner.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我之前说过我是什么专业的";
        answer = coursePlanner.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }
}