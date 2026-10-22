package fr.stockpilot.stockpilot.dto;

import fr.stockpilot.stockpilot.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovementRequest(
        @NotNull(message = "Le produit est obligatoire.")
        Long productId,

        @NotNull(message = "Le type de mouvement est obligatoire.")
        MovementType type,

        @NotNull(message = "La quantité est obligatoire.")
        @Min(value = 1, message = "La quantité doit être supérieure à zéro.")
        Integer quantity
) {
}
