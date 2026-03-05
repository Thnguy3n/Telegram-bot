package com.milktea.bot.bot.util;

import com.milktea.bot.entity.Product;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtil {

    public static InlineKeyboardMarkup createStartKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("📋 Xem Menu");
        btn.setCallbackData("CMD_MENU");
        row.add(btn);

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createMenuKeyboard(List<Product> products) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product p : products) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(p.getName() + " - " + p.getPriceM() + "đ");
            btn.setCallbackData("ITEM_" + p.getItemId());
            row.add(btn);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createSizeKeyboard(Product product) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        
        InlineKeyboardButton btnM = new InlineKeyboardButton();
        btnM.setText("Size M - " + product.getPriceM() + "đ");
        btnM.setCallbackData("SIZE_M");
        
        InlineKeyboardButton btnL = new InlineKeyboardButton();
        btnL.setText("Size L - " + product.getPriceL() + "đ");
        btnL.setCallbackData("SIZE_L");
        
        row.add(btnM);
        row.add(btnL);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createQuantityKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(String.valueOf(i));
            btn.setCallbackData("QTY_" + i);
            row1.add(btn);
        }
        
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        for (int i = 4; i <= 6; i++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(String.valueOf(i));
            btn.setCallbackData("QTY_" + i);
            row2.add(btn);
        }

        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createConfirmKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btnAdd = new InlineKeyboardButton();
        btnAdd.setText("➕ Đặt thêm món");
        btnAdd.setCallbackData("CMD_MENU");
        
        InlineKeyboardButton btnConfirm = new InlineKeyboardButton();
        btnConfirm.setText("✅ Chốt đơn");
        btnConfirm.setCallbackData("CMD_CONFIRM");
        
        row1.add(btnAdd);
        row1.add(btnConfirm);
        rows.add(row1);

        markup.setKeyboard(rows);
        return markup;
    }
}
