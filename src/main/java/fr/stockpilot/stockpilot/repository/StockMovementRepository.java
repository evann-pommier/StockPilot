
package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByType(MovementType type);

    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<StockMovement> findAllByOrderByCreatedAtDesc();

    boolean existsByProductId(Long productId);
}
