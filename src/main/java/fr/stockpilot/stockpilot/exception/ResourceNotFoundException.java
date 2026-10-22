package fr.stockpilot.stockpilot.exception;

/**
 * Ressource demandée inexistante (HTTP 404).
 * Étend {@link IllegalArgumentException} pour rester compatible avec le code existant.
 */
public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
