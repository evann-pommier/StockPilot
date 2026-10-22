package fr.stockpilot.stockpilot.dto;

import fr.stockpilot.stockpilot.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Le nom de la catégorie est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String name,

        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères.")
        String description
) {
    public Category toEntity() {
        return new Category(name.trim(), description);
    }
}
