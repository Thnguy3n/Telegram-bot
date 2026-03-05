package com.milktea.bot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    private String name;
    
    private String username;
}
