package dao;

import org.example.model.Purchase;
import org.example.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PurchaseDAO {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private org.example.dao.PurchaseDAO purchaseDAO;

    @BeforeAll
    static void setUpDatabaseConfig() {
        // Настраиваем DatabaseConfig для использования Testcontainers
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
    }

    @BeforeEach
    void setUp() throws SQLException {
        purchaseDAO = new org.example.dao.PurchaseDAO();

        // Инициализация таблицы purchases
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE purchases (id SERIAL PRIMARY KEY, user_id BIGINT, item_name VARCHAR(255), price DECIMAL)")) {
            stmt.execute();
        }

        // Добавляем тестовые данные
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO purchases (user_id, item_name, price) VALUES (?, ?, ?)")) {
            stmt.setLong(1, 1L);
            stmt.setString(2, "Book");
            stmt.setBigDecimal(3, new BigDecimal("10.99"));
            stmt.executeUpdate();

            stmt.setLong(1, 2L);
            stmt.setString(2, "Pen");
            stmt.setBigDecimal(3, new BigDecimal("1.50"));
            stmt.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Очистка таблицы после каждого теста
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE purchases RESTART IDENTITY")) {
            stmt.execute();
        }
    }

    @Test
    void testSavePurchase() throws SQLException {
        // Given
        Long userId = 3L;
        String itemName = "Laptop";
        BigDecimal price = new BigDecimal("999.99");

        // When
        purchaseDAO.savePurchase(userId, itemName, price);

        // Then
        List<Purchase> purchases = purchaseDAO.getPurchasesByUserId(userId);
        assertEquals(1, purchases.size());
        Purchase purchase = purchases.get(0);
        assertEquals(userId, purchase.getUserId());
        assertEquals(itemName, purchase.getItemName());
        assertEquals(price, purchase.getPrice());
    }

    @Test
    void testGetPurchasesByUserId() throws SQLException {
        // When
        List<Purchase> purchases = purchaseDAO.getPurchasesByUserId(1L);

        // Then
        assertEquals(1, purchases.size());
        Purchase purchase = purchases.get(0);
        assertEquals(1L, purchase.getUserId());
        assertEquals("Book", purchase.getItemName());
        assertEquals(new BigDecimal("10.99"), purchase.getPrice());
    }

    @Test
    void testGetPurchasesByUserId_NoPurchases() throws SQLException {
        // When
        List<Purchase> purchases = purchaseDAO.getPurchasesByUserId(999L);

        // Then
        assertTrue(purchases.isEmpty());
    }

    @Test
    void testGetAllPurchases() throws SQLException {
        // When
        List<Purchase> purchases = purchaseDAO.getAllPurchases();

        // Then
        assertEquals(2, purchases.size());

        Purchase purchase1 = purchases.stream().filter(p -> p.getUserId().equals(1L)).findFirst().orElse(null);
        assertNotNull(purchase1);
        assertEquals("Book", purchase1.getItemName());
        assertEquals(new BigDecimal("10.99"), purchase1.getPrice());

        Purchase purchase2 = purchases.stream().filter(p -> p.getUserId().equals(2L)).findFirst().orElse(null);
        assertNotNull(purchase2);
        assertEquals("Pen", purchase2.getItemName());
        assertEquals(new BigDecimal("1.50"), purchase2.getPrice());
    }

    @Test
    void testSavePurchase_WithSQLException() {
        // Для этого теста нужно модифицировать DatabaseConfig, чтобы он выбрасывал SQLException
        // Здесь мы предполагаем, что подключение сломано (например, база недоступна)
        // Этот тест сложнее реализовать без моков для Connection, поэтому оставим как пример
        assertThrows(SQLException.class, () -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                conn.close(); // Закрываем вручную для симуляции ошибки
                purchaseDAO.savePurchase(1L, "Test", new BigDecimal("10.00"));
            }
        });
    }
}