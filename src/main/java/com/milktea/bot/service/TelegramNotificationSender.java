package com.milktea.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TelegramNotificationSender {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(Long chatId, String text) {
        try {
            String url = "https://api.telegram.org/bot{botToken}/sendMessage?chat_id={chatId}&text={text}&parse_mode=HTML";
            restTemplate.getForObject(url, String.class, botToken, chatId, text);
            log.info("Successfully sent notification to chat {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send telegram notification to {}", chatId, e);
        }
    }
}
