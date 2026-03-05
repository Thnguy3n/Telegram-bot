package com.milktea.bot.bot.handler;

import com.milktea.bot.bot.session.SessionManager;
import com.milktea.bot.bot.session.UserSession;
import com.milktea.bot.bot.state.BotState;
import com.milktea.bot.bot.util.KeyboardUtil;
import com.milktea.bot.entity.Customer;
import com.milktea.bot.entity.Order;
import com.milktea.bot.entity.OrderItem;
import com.milktea.bot.entity.Product;
import com.milktea.bot.repository.CustomerRepository;
import com.milktea.bot.repository.OrderRepository;
import com.milktea.bot.repository.ProductRepository;
import com.milktea.bot.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotUpdateHandler {

    private final SessionManager sessionManager;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderNotificationService notificationService;

    public Object handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            return handleCallbackQuery(update.getCallbackQuery());
        }
        return null;
    }

    private Object handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        UserSession session = sessionManager.getSession(chatId);

        // Always ensure customer exists
        ensureCustomerExists(message);

        if ("/start".equals(text)) {
            session.clearCart();
            session.setState(BotState.START);
            
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("Chào mừng bạn đến với Quán Trà Sữa! 🧋\nNhấn nút bên dưới để xem Menu nha.");
            response.setReplyMarkup(KeyboardUtil.createStartKeyboard());
            return response;
        }

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("Xin lỗi, mình không hiểu. Bạn gõ /start để bắt đầu nhé!");
        return response;
    }

    private void ensureCustomerExists(Message message) {
        Long chatId = message.getChatId();
        if (!customerRepository.existsById(chatId)) {
            Customer customer = Customer.builder()
                    .chatId(chatId)
                    .name(message.getFrom().getFirstName() + " " + (message.getFrom().getLastName() != null ? message.getFrom().getLastName() : ""))
                    .username(message.getFrom().getUserName())
                    .build();
            customerRepository.save(customer);
        }
    }

    private Object handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        UserSession session = sessionManager.getSession(chatId);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());

        if ("CMD_MENU".equals(data)) {
            List<Product> products = productRepository.findByAvailableTrue();
            session.setState(BotState.CHOOSE_ITEM);
            response.setText("Vui lòng chọn món:");
            response.setReplyMarkup(KeyboardUtil.createMenuKeyboard(products));
            return response;
        }

        if (session.getState() == BotState.CHOOSE_ITEM && data.startsWith("ITEM_")) {
            String itemId = data.substring(5);
            Optional<Product> optProd = productRepository.findByItemId(itemId);
            if (optProd.isPresent()) {
                Product p = optProd.get();
                session.setCurrentProduct(p);
                session.setState(BotState.CHOOSE_SIZE);
                
                response.setText("Bạn chọn: " + p.getName() + "\nVui lòng chọn Size:");
                response.setReplyMarkup(KeyboardUtil.createSizeKeyboard(p));
                return response;
            }
        }

        if (session.getState() == BotState.CHOOSE_SIZE && data.startsWith("SIZE_")) {
            String size = data.substring(5);
            session.setCurrentSize(size);
            session.setState(BotState.CHOOSE_QUANTITY);
            
            response.setText("Chọn số lượng cho " + session.getCurrentProduct().getName() + " (Size " + size + "):");
            response.setReplyMarkup(KeyboardUtil.createQuantityKeyboard());
            return response;
        }

        if (session.getState() == BotState.CHOOSE_QUANTITY && data.startsWith("QTY_")) {
            int qty = Integer.parseInt(data.substring(4));
            
            UserSession.CartItem cartItem = new UserSession.CartItem();
            cartItem.setProduct(session.getCurrentProduct());
            cartItem.setSize(session.getCurrentSize());
            cartItem.setQuantity(qty);
            session.getCart().add(cartItem);
            
            session.setState(BotState.CONFIRM_ORDER);
            session.setCurrentProduct(null);
            session.setCurrentSize(null);
            
            response.setText("✅ Đã thêm " + qty + " " + cartItem.getProduct().getName() + " vào giỏ hàng!\n\nBạn muốn làm gì tiếp theo?");
            response.setReplyMarkup(KeyboardUtil.createConfirmKeyboard());
            return response;
        }

        if ("CMD_CONFIRM".equals(data)) {
            if (session.getCart().isEmpty()) {
                response.setText("Giỏ hàng của bạn đang trống! Hãy chọn /start để đặt hàng.");
                return response;
            }
            return finalizeOrder(chatId, session, response);
        }

        response.setText("Có lỗi xảy ra, vui lòng gõ /start để đặt lại từ đầu.");
        return response;
    }

    @Transactional
    public Object finalizeOrder(Long chatId, UserSession session, SendMessage response) {
        Customer customer = customerRepository.findById(chatId).orElseThrow();
        
        Order order = Order.builder()
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        StringBuilder billText = new StringBuilder("🧾 <b>HÓA ĐƠN CỦA BẠN</b> 🧾\n\n");

        for (UserSession.CartItem cItem : session.getCart()) {
            Product p = cItem.getProduct();
            BigDecimal price = "M".equals(cItem.getSize()) ? p.getPriceM() : p.getPriceL();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(p)
                    .size(cItem.getSize())
                    .quantity(cItem.getQuantity())
                    .price(price)
                    .build();
            order.addItem(orderItem);

            billText.append("- ")
                    .append(p.getName()).append(" (Size ").append(cItem.getSize()).append(")\n")
                    .append("  ").append(cItem.getQuantity()).append(" x ").append(price).append("đ = ").append(subtotal).append("đ\n");
        }

        order.setTotalPrice(total);
        billText.append("\n💰 <b>Tổng cộng:</b> ").append(total).append("đ\n\n");
        billText.append("🎉 Đặt hàng thành công, vui lòng chờ trong giây lát! Cảm ơn bạn.");

        // Save order and fire event
        Order savedOrder = orderRepository.save(order);
        notificationService.sendOrderNotification(savedOrder);

        // Clean user session
        session.clearCart();

        response.setText(billText.toString());
        response.setParseMode("HTML");
        return response;
    }
}
