package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.repository.ProductRepository;
import fr.stockpilot.stockpilot.repository.StockMovementRepository;
import fr.stockpilot.stockpilot.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public StockMovement recordMovement(
            Long productId,
            MovementType type,
            Integer quantity
    ) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Le type de mouvement est obligatoire."
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "La quantité doit être strictement positive."
            );
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit introuvable avec l'identifiant : " + productId
                ));

        switch (type) {
            case IN -> product.setQuantity(product.getQuantity() + quantity);

            case OUT -> {
                if (product.getQuantity() < quantity) {
                    throw new IllegalStateException(
                            "Stock insuffisant pour effectuer cette sortie."
                    );
                }
                product.setQuantity(product.getQuantity() - quantity);
            }

            case ADJUSTMENT -> throw new IllegalArgumentException(
                    "Les ajustements ne sont pas encore pris en charge. "
                            + "Utilisez IN ou OUT."
            );
        }

        StockMovement movement = new StockMovement(type, quantity, product);

        productRepository.save(product);

        return stockMovementRepository.save(movement);
    }

    @Override
    public StockMovement getMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Mouvement introuvable avec l'identifiant : " + id
                ));
    }

    @Override
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<StockMovement> getMovementsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException(
                    "Produit introuvable avec l'identifiant : " + productId
            );
        }

        return stockMovementRepository
                .findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<StockMovement> getMovementsByType(MovementType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Le type de mouvement est obligatoire."
            );
        }

        return stockMovementRepository.findByType(type);
    }
}