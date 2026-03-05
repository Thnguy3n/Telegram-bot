package com.milktea.bot.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(name = "item_id", unique = true, nullable = false)
    private String itemId;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "price_m", nullable = false)
    private BigDecimal priceM;

    @Column(name = "price_l", nullable = false)
    private BigDecimal priceL;

    @Column(nullable = false)
    private boolean available = true;
}
