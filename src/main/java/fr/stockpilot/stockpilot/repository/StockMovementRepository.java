package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.StockMovement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    // Les EntityGraph chargent le produit dans la même requête (évite le N+1).

    @EntityGraph(attributePaths = "product")
    List<StockMovement> findByProductId(Long productId);

    @EntityGraph(attributePaths = "product")
    List<StockMovement> findByType(MovementType type);

    @EntityGraph(attributePaths = "product")
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    @EntityGraph(attributePaths = "product")
    List<StockMovement> findAllByOrderByCreatedAtDesc();

    boolean existsByProductId(Long productId);
}
