package com.revseev.vkbotpoc.core;


import com.vk.api.sdk.objects.messages.Message;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class BotScheduler {
    private final ChatBot bot;
    private long timeCounter;

    public BotScheduler(ChatBot bot){
        this.bot = bot;
    }

    @Scheduled(fixedDelayString = "${bot.operations_interval}")
    public void scheduleFixedDelayTask() {
        log.info("Бот работает уже " + (timeCounter++) + " с.");
        List<Message> messages = bot.readMessages();
        bot.replyForMessages(messages);
    }
}
