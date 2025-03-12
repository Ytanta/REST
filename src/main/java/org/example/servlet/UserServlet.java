package org.example.servlet;

import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.example.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet("/users/*") // Работает без web.xml
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService(new UserRepository());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<UserDTO> users = userService.getAllUsers();
                resp.getWriter().write(objectMapper.writeValueAsString(users));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                Long id = parseIdFromPath(pathInfo);
                UserDTO user = userService.getUserById(id);
                resp.getWriter().write(objectMapper.writeValueAsString(user));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (NoSuchElementException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserDTO userDTO = parseRequestBody(req);
            userService.createUser(userDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "Invalid request body");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "Missing user ID in URL");
            return;
        }

        try {
            Long id = parseIdFromPath(pathInfo);
            UserDTO userDTO = parseRequestBody(req);
            userService.updateUser(id, userDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NoSuchElementException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "Missing user ID in URL");
            return;
        }

        try {
            Long id = parseIdFromPath(pathInfo);
            userService.deleteUser(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NoSuchElementException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeError(resp, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, e.getMessage());
        }
    }

    private Long parseIdFromPath(String pathInfo) {
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    private UserDTO parseRequestBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        return objectMapper.readValue(reader, UserDTO.class);
    }

    private void writeError(HttpServletResponse resp, String message) throws IOException {
        resp.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}