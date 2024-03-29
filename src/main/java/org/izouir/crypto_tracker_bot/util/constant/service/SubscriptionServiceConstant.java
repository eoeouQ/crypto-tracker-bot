package org.izouir.crypto_tracker_bot.util.constant.service;

public class SubscriptionServiceConstant {
    public static final Integer MAX_SUBSCRIBERS_POOL_SIZE = 10;
    public static final String SUBSCRIBERS_POOL_OVERLOAD_MESSAGE
            = "В данный момент количество подписчиков достигло максимума.Попробуйте снова чуть позже";
    public static final String SUBSCRIBE_SUCCESS_MESSAGE = "Вы успешно подписались";
    public static final String ALREADY_SUBSCRIBER_MESSAGE = "У вас уже есть активная подписка (%.0f%%)";
    public static final String UNSUBSCRIBE_SUCCESS_MESSAGE = "Вы успешно отписались";
    public static final String ALREADY_NOT_SUBSCRIBER_MESSAGE = "У вас нет активных подписок";

    public static final String SUBSCRIBER_PERCENT_CHOICE_MESSAGE
            = "Выберите % изменения курса криптовалют при котором вас нужно оповестить";
    public static final String LOW_SUBSCRIBER_PERCENT_CALLBACK_DATA = "LOW_SUBSCRIBER_PERCENT";
    public static final Float LOW_SUBSCRIBER_PERCENT_VALUE = 0.05f;
    public static final String MEDIUM_SUBSCRIBER_PERCENT_CALLBACK_DATA = "MEDIUM_SUBSCRIBER_PERCENT";
    public static final Float MEDIUM_SUBSCRIBER_PERCENT_VALUE = 0.1f;
    public static final String HIGH_SUBSCRIBER_PERCENT_CALLBACK_DATA = "HIGH_SUBSCRIBER_PERCENT";
    public static final Float HIGH_SUBSCRIBER_PERCENT_VALUE = 0.15f;
}
