package com.milktea.bot.bot.session;

import com.milktea.bot.bot.state.BotState;
import com.milktea.bot.entity.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSession {
    private BotState state;
    
    // Current item being selected
    private Product currentProduct;
    private String currentSize;
    
    // Items added to the cart
    private List<CartItem> cart = new ArrayList<>();

    public UserSession() {
        this.state = BotState.START;
    }

    public void clearCart() {
        cart.clear();
        currentProduct = null;
        currentSize = null;
        state = BotState.START;
    }

    @Data
    public static class CartItem {
        private Product product;
        private String size;
        private int quantity;
    }
}
