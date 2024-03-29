package org.izouir.crypto_tracker_bot.keyboard.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.izouir.crypto_tracker_bot.util.constant.keyboard.service.SubscriptionServiceKeyboardConstant.SUBSCRIBER_PERCENT_BUTTON_TEXT_FORMAT;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.HIGH_SUBSCRIBER_PERCENT_CALLBACK_DATA;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.HIGH_SUBSCRIBER_PERCENT_VALUE;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.LOW_SUBSCRIBER_PERCENT_CALLBACK_DATA;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.LOW_SUBSCRIBER_PERCENT_VALUE;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.MEDIUM_SUBSCRIBER_PERCENT_CALLBACK_DATA;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.MEDIUM_SUBSCRIBER_PERCENT_VALUE;

@Component
public class SubscriptionServiceKeyboard {
    public ReplyKeyboard buildSubscriberPercentChoiceKeyboard() {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        final List<InlineKeyboardButton> lowPercentRow = new ArrayList<>();
        final InlineKeyboardButton lowPercentButton = new InlineKeyboardButton();
        lowPercentButton.setText(String.format(SUBSCRIBER_PERCENT_BUTTON_TEXT_FORMAT, 100 * LOW_SUBSCRIBER_PERCENT_VALUE));
        lowPercentButton.setCallbackData(LOW_SUBSCRIBER_PERCENT_CALLBACK_DATA);
        lowPercentRow.add(lowPercentButton);

        final List<InlineKeyboardButton> mediumPercentRow = new ArrayList<>();
        final InlineKeyboardButton mediumPercentButton = new InlineKeyboardButton();
        mediumPercentButton.setText(String.format(SUBSCRIBER_PERCENT_BUTTON_TEXT_FORMAT, 100 * MEDIUM_SUBSCRIBER_PERCENT_VALUE));
        mediumPercentButton.setCallbackData(MEDIUM_SUBSCRIBER_PERCENT_CALLBACK_DATA);
        mediumPercentRow.add(mediumPercentButton);

        final List<InlineKeyboardButton> highPercentRow = new ArrayList<>();
        final InlineKeyboardButton highPercentButton = new InlineKeyboardButton();
        highPercentButton.setText(String.format(SUBSCRIBER_PERCENT_BUTTON_TEXT_FORMAT, 100 * HIGH_SUBSCRIBER_PERCENT_VALUE));
        highPercentButton.setCallbackData(HIGH_SUBSCRIBER_PERCENT_CALLBACK_DATA);
        highPercentRow.add(highPercentButton);

        keyboard.add(lowPercentRow);
        keyboard.add(mediumPercentRow);
        keyboard.add(highPercentRow);
        markup.setKeyboard(keyboard);
        return markup;
    }
}
