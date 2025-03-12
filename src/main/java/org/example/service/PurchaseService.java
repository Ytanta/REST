package org.example.service;

import org.example.model.Purchase;
import org.example.repository.PurchaseRepository;

import java.sql.SQLException;
import java.util.List;

import org.example.dto.PurchaseDTO;


import java.util.stream.Collectors;

public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<PurchaseDTO> getAllPurchases() {
        try {
            return purchaseRepository.findAll().stream()
                    .map(this::toDTO) // Маппинг Purchase -> PurchaseDTO
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching all purchases", e); // Обработка исключения
        }
    }

    public List<PurchaseDTO> getPurchasesByUserId(Long userId) {
        return purchaseRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void createPurchase(PurchaseDTO purchaseDTO) {
        Purchase purchase = toEntity(purchaseDTO);
        purchaseRepository.save(purchase);
    }

    public void updatePurchase(Long id, PurchaseDTO purchaseDTO) {
        Purchase purchase = toEntity(purchaseDTO);
        purchase.setId(id);  // Присваиваем ID для обновления
        purchaseRepository.update(purchase);
    }

    public void deletePurchase(Long id) {
        purchaseRepository.delete(id);  // ID теперь типа Long
    }

    private PurchaseDTO toDTO(Purchase purchase) {
        if (purchase == null) {
            return null;
        }
        return new PurchaseDTO(
                purchase.getId(),
                purchase.getUserId(),
                purchase.getItemName(),
                purchase.getPrice()
        );
    }

    private Purchase toEntity(PurchaseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Purchase(
                dto.getId(),
                dto.getUserId(),
                dto.getItemName(),
                dto.getPrice()
        );
    }
}