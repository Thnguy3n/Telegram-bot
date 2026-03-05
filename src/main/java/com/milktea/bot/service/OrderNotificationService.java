package com.milktea.bot.service;

import com.milktea.bot.dto.OrderNotificationMessage;
import com.milktea.bot.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.order}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.order-notification}")
    private String routingKey;

    public void sendOrderNotification(Order order) {
        List<OrderNotificationMessage.OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderNotificationMessage.OrderItemDto.builder()
                        .productName(item.getProduct().getName())
                        .size(item.getSize())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        OrderNotificationMessage message = OrderNotificationMessage.builder()
                .orderId(order.getId())
                .customerChatId(order.getCustomer().getChatId())
                .customerName(order.getCustomer().getName())
                .totalPrice(order.getTotalPrice())
                .items(itemDtos)
                .build();

        log.info("Sending order notification to RabbitMQ for Order ID: {}", order.getId());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
