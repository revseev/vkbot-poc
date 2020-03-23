package com.revseev.vkbotpoc.bot;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan("com.revseev.vkbotpoc")
@PropertySource("classpath:vkconfig.properties")
public class BotConfig {
    /**
     * Id группы, от имени которой выступает Бот.
     */
    @Value("${group_id}")
    private int groupId;

    /**
     * Access Token, уникальный параметр, получаемый на сайте в настройках Бота для сообщества
     */
    @Value("${access_token}")
    private String accessToken;

//    private VkApiClient vk;
//    private GroupActor actor;

    /**
     * интерфейс для взаимодействия с VK API с помощью запросов
     */
    @Bean
    public VkApiClient vkApiClient() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }

    /**
     * ваша группа, с помощью которого вы сможете отправлять сообщения от имени группы
     */
    @Bean
    public GroupActor groupActor() {
        return new GroupActor(groupId, accessToken);
    }

//    @Bean
//    public Bot bot() throws ClientException, ApiException {
//        return new Bot(vk, actor);
//    }
}
