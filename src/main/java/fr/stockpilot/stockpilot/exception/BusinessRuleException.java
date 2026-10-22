package fr.stockpilot.stockpilot.exception;

/**
 * Règle métier non respectée : stock insuffisant, suppression interdite... (HTTP 409).
 * Étend {@link IllegalStateException} pour rester compatible avec le code existant.
 */
public class BusinessRuleException extends IllegalStateException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
