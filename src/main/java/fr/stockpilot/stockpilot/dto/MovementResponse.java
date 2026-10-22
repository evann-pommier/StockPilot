package fr.stockpilot.stockpilot.dto;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;

import java.time.LocalDateTime;

public record MovementResponse(
        Long id,
        MovementType type,
        Integer quantity,
        LocalDateTime createdAt,
        ProductSummary product
) {
    public record ProductSummary(Long id, String sku, String name) {
    }

    public static MovementResponse from(StockMovement movement) {
        Product product = movement.getProduct();
        return new MovementResponse(
                movement.getId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getCreatedAt(),
                new ProductSummary(product.getId(), product.getSku(), product.getName()));
    }
}
