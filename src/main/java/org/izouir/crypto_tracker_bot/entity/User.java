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

    public User() {
    }

    public User(final Long chatId, final String username) {
        this.chatId = chatId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
