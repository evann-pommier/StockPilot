package fr.stockpilot.stockpilot.dto;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        CategorySummary category
) {
    public record CategorySummary(Long id, String name) {
    }

    public static ProductResponse from(Product product) {
        Category category = product.getCategory();
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                category == null ? null
                        : new CategorySummary(category.getId(), category.getName()));
    }
}
