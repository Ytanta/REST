package org.example.servlet;

import org.example.dto.PurchaseDTO;
import org.example.service.PurchaseService;
import org.example.repository.PurchaseRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/purchases/*")
public class PurchaseServlet extends HttpServlet {

//    private static final Logger LOGGER = Logger.getLogger(PurchaseServlet.class.getName());

    private final PurchaseService purchaseService = new PurchaseService(new PurchaseRepository());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        System.out.println("GET request received on /purchases");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Получение всех покупок
                List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
                writeResponse(resp, HttpServletResponse.SC_OK, purchases);
            } else {
                // Получение покупок по userId
                Long userId = parseIdFromPath(pathInfo);
                List<PurchaseDTO> purchases = purchaseService.getPurchasesByUserId(userId);
                writeResponse(resp, HttpServletResponse.SC_OK, purchases);
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PurchaseDTO purchaseDTO = parseRequestBody(req);
            purchaseService.createPurchase(purchaseDTO);
            writeResponse(resp, HttpServletResponse.SC_CREATED, "Purchase created successfully");
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = parseIdFromPath(req.getPathInfo());
            PurchaseDTO purchaseDTO = parseRequestBody(req);
            purchaseService.updatePurchase(id, purchaseDTO);
            writeResponse(resp, HttpServletResponse.SC_OK, "Purchase updated successfully");
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = parseIdFromPath(req.getPathInfo());
            purchaseService.deletePurchase(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private Long parseIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Invalid ID in path");
        }
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    private PurchaseDTO parseRequestBody(HttpServletRequest req) throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), PurchaseDTO.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid request body format");
        }
    }

    private void writeResponse(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        if (body != null) {
            resp.getWriter().write(objectMapper.writeValueAsString(body));
        }
    }

    private void handleException(HttpServletResponse resp, Exception e) throws IOException {
//        LOGGER.severe("Error: " + e.getMessage());
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writeResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
}