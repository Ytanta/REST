package org.example.repository;

import org.example.util.DatabaseConfig;
import org.example.model.Purchase;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRepository {

    // Метод для поиска покупок по ID пользователя
    public List<Purchase> findByUserId(Long userId) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT id, user_id, item_name, price FROM purchases WHERE user_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId); // userId теперь Long
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                purchases.add(new Purchase(
                        resultSet.getLong("id"),     // id теперь Long
                        resultSet.getLong("user_id"),// user_id теперь Long
                        resultSet.getString("item_name"),
                        resultSet.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return purchases;
    }

    // Метод для поиска покупки по ID
    public Purchase findById(Long id) {
        String sql = "SELECT id, user_id, item_name, price FROM purchases WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id); // id теперь Long
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Purchase(
                        resultSet.getLong("id"),     // id теперь Long
                        resultSet.getLong("user_id"),// user_id теперь Long
                        resultSet.getString("item_name"),
                        resultSet.getBigDecimal("price")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Метод для добавления новой покупки
    public void save(Purchase purchase) {
        String sql = "INSERT INTO purchases (user_id, item_name, price) VALUES (?, ?, ?) RETURNING id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, purchase.getUserId()); // userId теперь Long
            statement.setString(2, purchase.getItemName());
            statement.setBigDecimal(3, purchase.getPrice());

            statement.executeUpdate();

            // Получаем сгенерированный ключ
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                purchase.setId(generatedKeys.getLong(1)); // id теперь Long
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для обновления информации о покупке
    public void update(Purchase purchase) {
        String sql = "UPDATE purchases SET user_id = ?, item_name = ?, price = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, purchase.getUserId()); // userId теперь Long
            statement.setString(2, purchase.getItemName());
            statement.setBigDecimal(3, purchase.getPrice());
            statement.setLong(4, purchase.getId()); // id теперь Long

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для удаления покупки по ID
    public void delete(Long id) {
        String sql = "DELETE FROM purchases WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id); // id теперь Long
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для получения всех покупок
    public List<Purchase> findAll() throws SQLException {
        String query = "SELECT * FROM purchases";
        List<Purchase> purchases = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                // Маппинг данных из ResultSet в объект Purchase
                Long id = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                String itemName = resultSet.getString("item_name");
                BigDecimal price = resultSet.getBigDecimal("price");

                purchases.add(new Purchase(id, userId, itemName, price));
            }
        }
        return purchases;
    }

    // Метод для отображения результата запроса в объект Purchase

}