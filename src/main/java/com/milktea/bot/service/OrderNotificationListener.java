package com.milktea.bot.service;

import com.milktea.bot.dto.OrderNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final TelegramNotificationSender telegramNotificationSender;

    @Value("${telegram.bot.owner-chat-id}")
    private Long ownerChatId;

    @RabbitListener(queues = "${rabbitmq.queue.order-notification}")
    public void receiveOrderNotification(OrderNotificationMessage message) {
        log.info("Received order notification from RabbitMQ for Order ID: {}", message.getOrderId());
        
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 <b>CÓ ĐƠN HÀNG MỚI!</b> 🚨\n\n");
        sb.append("<b>Mã đơn:</b> #").append(message.getOrderId()).append("\n");
        sb.append("<b>Khách hàng:</b> ").append(message.getCustomerName())
          .append(" (").append(message.getCustomerChatId()).append(")\n\n");
        
        sb.append("<b>Chi tiết đơn hàng:</b>\n");
        for (OrderNotificationMessage.OrderItemDto item : message.getItems()) {
            sb.append("- ").append(item.getQuantity()).append(" x ")
              .append(item.getProductName()).append(" (Size ").append(item.getSize()).append(") : ")
              .append(item.getPrice()).append(" VND\n");
        }
        
        sb.append("\n<b>Tổng tiền:</b> ").append(message.getTotalPrice()).append(" VND");

        // Send to owner
        telegramNotificationSender.sendMessage(ownerChatId, sb.toString());
    }
}
