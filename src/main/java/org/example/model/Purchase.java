package org.example.model;

import java.math.BigDecimal;

public class Purchase {
    private Long id;        // Делаем id типа Long
    private Long userId;    // Делаем userId типа Long
    private String itemName;
    private BigDecimal price;

    // Конструктор
    public Purchase(Long id, Long userId, String itemName, BigDecimal price) {
        this.id = id;
        this.userId = userId;
        this.itemName = itemName;
        this.price = price;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}