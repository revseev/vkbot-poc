package com.revseev.vkbotpoc.core;

import com.revseev.vkbotpoc.util.StringParser;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.Dialog;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Component
@Log4j2
public class VkBotImpl implements ChatBot {
    private final VkApiClient apiClient = new VkApiClient(HttpTransportClient.getInstance());
    private GroupActor groupActor;

    @Value("${bot.group.id}")
    private int groupID;

    @Value("${bot.group.client_secret}")
    private String groupToken;

    private Properties chatProperties = new Properties();


    public VkBotImpl() {
    }

    // читаем chat.properties
    @PostConstruct
    public void init() throws IOException {
        this.groupActor = new GroupActor(groupID, groupToken);
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("chat.properties");
        assert stream != null;
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        chatProperties.load(reader);
        stream.close();
    }

    @Override
    public List<Message> readMessages() {
        List<Message> result = new ArrayList<>();
        try {
            List<Dialog> dialogs = apiClient.messages().getDialogs(groupActor).unanswered1(true).execute().getItems();
            for (Dialog item : dialogs) {
                result.add(item.getMessage());
            }
        } catch (ApiException | ClientException e) {
            log.error("Ошибка получении сообщений", e);
        }
        return result;
    }


    @Override
    public void replyForMessages(List<Message> messages) {
        String body;

        for (Message message : messages) {
            body = message.getBody();

            if (body.equals("Начать")){
                System.out.println(defaultKeyboard);
                System.out.println("========");
                System.out.println(startKeyboard);
                // отправить пользователю две кнопки "Ответить на вопросы" и "Посмотреть результаты"
                sendMessage("Привет! Выбери один из вариантов", defaultKeyboard, message.getUserId());
                return;
            }
            if (body.equals("Пройти опрос")) {
                sendMessage("Укажите ваш возраст.", message.getUserId());
                return;
            }
            if (StringParser.isNumeric(body)) {
                if (Integer.parseInt(body) < 100) {
                    if (Integer.parseInt(body) < 4) {
                        // ответ на опрос
                        sendMessage("Спасибо за участие в опросе!", message.getUserId());
                        return;
                    }
                    // сохранить возраст
                    sendMessage(poll, message.getUserId());
                    return;

                }
            }
            if (body.equals("Посмотреть результаты")) {
               // возврат из БД
                sendMessage("Результаты из БД:", message.getUserId());
                return;

            }

            sendMessage("Команда не распознана:" + message.getBody(), startKeyboard, message.getUserId());
        }
    }

    @Override
    public void sendMessage(String message, int userId) {
        Random random = new Random();
        try {
            this.apiClient.messages()
                    .send(groupActor)
                    .message(message)
                    .userId(userId)
                    .randomId(random.nextInt())
                    .execute();
        } catch (ApiException | ClientException e) {
            log.error("Исключение при отправке сообщения", e);
        }
    }

    @Override
    public void sendMessage(String message, String keyboard, int userId) {
        Random random = new Random();
        try {
            this.apiClient.messages()
                    .send(groupActor)
                    .message(message)
                    .unsafeParam("keyboard", keyboard)
                    .userId(userId).randomId(random.nextInt())
                    .execute();
        } catch (ApiException | ClientException e) {
            log.error("Исключение при отправке сообщения", e);
        }
    }

    String poll = "Выберите интересную вам тему:\n" +
            "[1] Туризм и путешествия\n" +
            "[2] Программирование и cs\n" +
            "[3] Финансы и бизнес ";

    String defaultKeyboard = "{\n" +
            "  \"one_time\": false,\n" +
            "  \"buttons\": [\n" +
            "    [\n" +
            "      {\n" +
            "        \"action\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"payload\": \"{\\\"button\\\": \\\"1\\\"}\",\n" +
            "          \"label\": \"Пройти опрос\"\n" +
            "        },\n" +
            "        \"color\": \"positive\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"action\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"payload\": \"{\\\"button\\\": \\\"2\\\"}\",\n" +
            "          \"label\": \"Посмотреть результаты\"\n" +
            "        },\n" +
            "        \"color\": \"default\"\n" +
            "      }\n" +
            "    ]\n" +
            "  ]\n" +
            "} ";

    String startKeyboard = "{\n" +
            "  \"one_time\": false,\n" +
            "  \"buttons\": [\n" +
            "      {\n" +
            "        \"action\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"payload\": \"{\\\"button\\\": \\\"1\\\"}\",\n" +
            "          \"label\": \"Начать\"\n" +
            "        },\n" +
            "        \"color\": \"primary\"\n" +
            "      }\n" +
            "  ]\n" +
            "} ";
}