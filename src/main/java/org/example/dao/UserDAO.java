package org.example.dao;

import org.example.model.User;
import org.example.model.Purchase;
import org.example.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Константы для SQL-запросов
    private static final String INSERT_USER_SQL = "INSERT INTO users (name) VALUES (?) RETURNING id";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_USERS_SQL = "SELECT * FROM users";

    // Метод для добавления нового пользователя
    public void saveUser(String name) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER_SQL)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = Long.valueOf(rs.getInt("id"));
                    System.out.println("User " + name + " saved with ID: " + id);
                }
            }
        }
    }

    // Метод для получения пользователя по ID
    public User getUserById(Long id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new User(id, name);
                }
            }
        }
        return null;
    }

    // Метод для получения всех пользователей
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USERS_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = Long.valueOf(rs.getInt("id"));
                String name = rs.getString("name");
                users.add(new User(id, name));
            }
        }
        return users;
    }
}