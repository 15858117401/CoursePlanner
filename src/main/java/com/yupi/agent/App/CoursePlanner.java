package com.yupi.agent.App;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class CoursePlanner {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是 UIUC 本科生选课规划 AI。\n" +
            "\n" +
            "只做一件事：根据用户需求回答选课相关问题。\n" +
            "\n" +
            "【规则】\n" +
            "1. 只回答用户当前问题，不扩展、不解释、不闲聊  \n" +
            "2. 简单问题 → 直接一句话回答  \n" +
            "3. 只有用户明确要求“选课 / 规划 / 推荐课程”时，才输出方案  \n" +
            "4. 有历史信息 → 必须优先使用，不得忽略或重复询问  \n" +
            "5. 用户问“之前说过什么” → 直接从历史中提取并回答  \n" +
            "6. 信息不足时 → 最多只问1个关键问题  \n" +
            "\n" +
            "【禁止】\n" +
            "- 禁止寒暄、情绪、废话  \n" +
            "- 禁止输出无关内容  \n" +
            "- 禁止在简单问题中给建议或方案  \n" +
            "\n" +
            "【规划格式（仅在用户明确要求时）】\n" +
            "Semester X:\n" +
            "- Course（简短理由）";

    public CoursePlanner(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();

        log.info("content: {}", content);
        return content;
    }
}