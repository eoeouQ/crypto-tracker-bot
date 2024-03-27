package org.izouir.crypto_tracker_bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "users")
@Entity
public class User {
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @NotNull
    @Column(name = "username")
    private String username;

    @NotNull
    @Column(name = "is_subscriber")
    private Boolean isSubscriber;

    @Column(name = "subscriber_percent")
    private Float subscriberPercent;

    public User() {
    }

    public User(final Long chatId, final String username, final Boolean isSubscriber) {
        this.chatId = chatId;
        this.username = username;
        this.isSubscriber = isSubscriber;
    }

    public String getUsername() {
        return username;
    }

    public Long getChatId() {
        return chatId;
    }

    public Boolean isSubscriber() {
        return isSubscriber;
    }

    public void setIsSubscriber(final Boolean subscriber) {
        isSubscriber = subscriber;
    }

    public Float getSubscriberPercent() {
        return subscriberPercent;
    }

    public void setSubscriberPercent(final Float subscriberPercent) {
        this.subscriberPercent = subscriberPercent;
    }
}
