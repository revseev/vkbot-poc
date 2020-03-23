package com.revseev.vkbotpoc.bot;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class Bot {
    private final static Logger LOG = LoggerFactory.getLogger(Bot.class);
    /**
     * <b>Timestamp</b>.
     * Идентификатор сообщения с которого нужно начинать обрабатывать сообщения. То есть, если значение ts будет константой, то VK API будет отправлять вам одни и те же сообщения. Поэтому после получения любого сообщения, необходимо его менять.
     */
    private static int ts;
    /**
     * maxMsgId – сначала этот параметр может показаться ненужным, так как без него бот будет работать, но не долго, а именно как говорят в документации около суток, но лично у меня бот упал уже после 12 часов работы. К тому же выйдет непонятная ошибка internal server error, что означает внутреннюю ошибку сервера. Эта ошибка может возникнуть если ts – очень старый (более суток) и не передается параметр max_msg_id, который хранит значение максимального идентификатора сообщения среди уже имеющихся в локальной копии.
     */
    private static int maxMsgId = -1;
    private VkApiClient vk;
    private GroupActor actor;

    public Bot(VkApiClient vk, GroupActor actor) throws ClientException, ApiException {
        this.vk = vk;
        this.actor = actor;
        ts = vk.messages().getLongPollServer(actor).execute().getTs();
    }

    public GroupActor getActor() {
        return actor;
    }

    public VkApiClient getVk() {
        return vk;
    }

    public Message getMessage() throws ClientException, ApiException {

        MessagesGetLongPollHistoryQuery eventsQuery = vk.messages()
                .getLongPollHistory(actor)
                .ts(ts);
        if (maxMsgId > 0) {
            eventsQuery.maxMsgId(maxMsgId);
        }
        List<Message> messages = eventsQuery.execute().getMessages().getItems();

        if (!messages.isEmpty()) { //TODO переделать if
            try {
                ts = vk.messages()
                        .getLongPollServer(actor)
                        .execute()
                        .getTs();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

        if (!messages.isEmpty() && !messages.get(0).isOut()) {
            // messageId - максимально полученный ID, нужен, чтобы не было ошибки 10 internal server error,
            // который является ограничением в API VK. В случае, если ts слишком старый (больше суток),
            // а max_msg_id не передан, метод может вернуть ошибку 10 (Internal server error).
            int messageId = messages.get(0).getId();
            if (messageId > maxMsgId) {
                maxMsgId = messageId;
            }
            return messages.get(0);
        }
        return null;
    }

    /**
     * Обращается к VK API и получает объект, описывающий пользователя.
     *
     * @param id идентификатор пользователя в VK
     * @return {@link UserXtrCounters} информацию о пользователе
     * @see UserXtrCounters
     */
    public UserXtrCounters getUserInfo(int id) {
        try {
            return vk.users()
                    .get(actor)
                    .userIds(String.valueOf(id))
                    .execute()
                    .get(0);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String msg, int peerId) {
        System.out.println(maxMsgId);

        if (msg == null) {
            LOG.error("Пустое сообщение не может быть отправлено!");
            return;
        }
        try {
            vk.messages()
                    .send(actor)
                    .peerId(peerId)
                    .message(msg)
                    .randomId(new Random().nextInt(10000))
                    .execute();
        } catch (ApiException | ClientException e) {
            LOG.error("Исключение при отправке сообщения", e);
        }
    }

//    public MessagesSendQuery getSendQuery() {
//        return vkCore.getVk().messages()
//                .send(vkCore.getActor());
//    }
}