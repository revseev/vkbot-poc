package com.revseev.vkbotpoc.bot;

import com.revseev.vkbotpoc.handlers.MessageExecutor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class BotServer {
    private final static Logger LOG = LoggerFactory.getLogger(BotServer.class);

    @Autowired
    private Bot bot;

    public BotServer(){
    }

    public void run() throws InterruptedException{
        LOG.info("Starting up VK Polling Bot...");

        while (true) {
            Thread.sleep(1000);

            try {
                Message message = bot.getMessage();
                if (message != null) {
//                    ExecutorService exec = Executors.newCachedThreadPool();
//                    exec.execute(new MessageExecutor(message));
                    bot.sendMessage("привет", message.getPeerId());
                }
            } catch (ApiException e) {
                LOG.error("Ошибка API ...", e);
                //TODO do smth with exc
            } catch (ClientException e) {
                LOG.error("Ошибка клиента или уровня транспорта...", e);
                final int RECONNECT_TIME = 10000;
                LOG.info("Повторное соединение через " + RECONNECT_TIME / 1000 + " секунд");
                Thread.sleep(RECONNECT_TIME);
            }
        }
    }
}
