package com.milktea.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationMessage implements Serializable {

    private Long orderId;
    private Long customerChatId;
    private String customerName;
    private BigDecimal totalPrice;
    private List<OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto implements Serializable {
        private String productName;
        private String size;
        private int quantity;
        private BigDecimal price;
    }
}
