package org.example.dao;

import org.example.model.Purchase;
import org.example.util.DatabaseConfig;

import java.sql.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    // Константы для SQL-запросов
    private static final String INSERT_PURCHASE_SQL = "INSERT INTO purchases (user_id, item_name, price) VALUES (?, ?, ?)";
    private static final String SELECT_PURCHASES_BY_USER_ID_SQL = "SELECT * FROM purchases WHERE user_id = ?";
    private static final String SELECT_ALL_PURCHASES_SQL = "SELECT * FROM purchases";

    // Метод для добавления новой покупки
    public void savePurchase(Long userId, String itemName, BigDecimal price) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PURCHASE_SQL)) {

            stmt.setLong(1, userId);       // Устанавливаем ID пользователя
            stmt.setString(2, itemName);  // Устанавливаем название товара
            stmt.setBigDecimal(3, price); // Устанавливаем цену товара

            stmt.executeUpdate();
        }
    }

    // Метод для получения всех покупок пользователя по его ID
    public List<Purchase> getPurchasesByUserId(Long userId) throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PURCHASES_BY_USER_ID_SQL)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id"); // Получаем id покупки
                    String itemName = rs.getString("item_name"); // Получаем название товара
                    BigDecimal price = rs.getBigDecimal("price"); // Получаем цену товара

                    // Теперь создаем объект Purchase, передавая все необходимые параметры
                    purchases.add(new Purchase(id, userId, itemName, price));
                }
            }
        }
        return purchases;
    }

    // Метод для получения всех покупок
    public List<Purchase> getAllPurchases() throws SQLException {
        List<Purchase> purchases = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PURCHASES_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id"); // Получаем id покупки
                Long userId = rs.getLong("user_id"); // Получаем id пользователя
                String itemName = rs.getString("item_name"); // Получаем название товара
                BigDecimal price = rs.getBigDecimal("price"); // Получаем цену товара

                // Теперь создаем объект Purchase, передавая все необходимые параметры
                purchases.add(new Purchase(id, userId, itemName, price));
            }
        }
        return purchases;
    }}