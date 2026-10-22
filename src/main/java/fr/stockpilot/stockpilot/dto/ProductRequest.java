package fr.stockpilot.stockpilot.dto;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Corps des requêtes de création et de modification d'un produit.
 * {@code quantity} n'est prise en compte qu'à la création (0 par défaut) :
 * ensuite, le stock évolue uniquement via les mouvements de stock.
 */
public record ProductRequest(
        @NotBlank(message = "La référence SKU est obligatoire.")
        @Size(max = 50, message = "Le SKU ne doit pas dépasser 50 caractères.")
        String sku,

        @NotBlank(message = "Le nom du produit est obligatoire.")
        @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères.")
        String name,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères.")
        String description,

        @NotNull(message = "Le prix est obligatoire.")
        @DecimalMin(value = "0.00", message = "Le prix ne peut pas être négatif.")
        @Digits(integer = 10, fraction = 2, message = "Le prix doit avoir au plus 2 décimales.")
        BigDecimal price,

        @Min(value = 0, message = "La quantité ne peut pas être négative.")
        Integer quantity,

        @NotNull(message = "La catégorie est obligatoire.")
        Long categoryId
) {
    /** La catégorie n'est qu'une référence par id : le service la résout. */
    public Product toEntity() {
        Category categoryRef = new Category(null, null);
        categoryRef.setId(categoryId);
        return new Product(
                sku.trim(), name.trim(), description, price,
                quantity == null ? 0 : quantity, categoryRef);
    }
}
