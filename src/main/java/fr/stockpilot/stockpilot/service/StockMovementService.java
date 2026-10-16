package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.StockMovement;

import java.util.List;

public interface StockMovementService {

    StockMovement recordMovement(
            Long productId,
            MovementType type,
            Integer quantity
    );

    StockMovement getMovementById(Long id);

    List<StockMovement> getAllMovements();

    List<StockMovement> getMovementsByProduct(Long productId);

    List<StockMovement> getMovementsByType(MovementType type);

    void deleteMovement(Long id);

}
