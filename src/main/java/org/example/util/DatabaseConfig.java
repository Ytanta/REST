package org.example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    static {
        try {
            // Загружаем параметры из файла application.properties
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("src/main/resources/db.properties")) {
                properties.load(fis);
            }

            // Конфигурация HikariCP
            HikariConfig config = new HikariConfig();

            // Загрузка параметров из файла
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName("org.postgresql.Driver");  // Драйвер базы данных
            config.setMaximumPoolSize(10);  // Максимальное количество соединений в пуле
            config.setMinimumIdle(5);  // Минимальное количество неиспользуемых соединений

            // Инициализация пула соединений
            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while loading database configuration", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while configuring database connection", e);
        }
    }

    // Метод для получения соединения с базой данных
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Закрытие пула соединений при завершении работы
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}