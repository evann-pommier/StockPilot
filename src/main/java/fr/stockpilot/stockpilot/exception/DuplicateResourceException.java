package fr.stockpilot.stockpilot.exception;

/**
 * Violation d'une contrainte d'unicité, par exemple un SKU ou un nom de catégorie
 * déjà utilisé (HTTP 409).
 */
public class DuplicateResourceException extends IllegalArgumentException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
