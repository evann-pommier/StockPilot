package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.repository.CategoryRepository;
import fr.stockpilot.stockpilot.repository.ProductRepository;
import fr.stockpilot.stockpilot.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StockMovementServiceIntegrationTest {

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.save(
                new Category("Tests stock", "Catégorie de test")
        );

        product = productRepository.save(
                new Product(
                        "SKU-INTEGRATION-001",
                        "Produit de test",
                        "Produit pour les tests d'intégration",
                        new BigDecimal("19.90"),
                        10,
                        category
                )
        );
    }

    @Test
    void shouldPersistIncomingMovementAndUpdateStock() {
        StockMovement movement = stockMovementService.recordMovement(
                product.getId(),
                MovementType.IN,
                5
        );

        Product updatedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        List<StockMovement> movements =
                stockMovementService.getMovementsByProduct(product.getId());

        assertThat(updatedProduct.getQuantity()).isEqualTo(15);
        assertThat(movement.getId()).isNotNull();
        assertThat(movements).hasSize(1);
        assertThat(movements.getFirst().getType()).isEqualTo(MovementType.IN);
        assertThat(movements.getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldPersistOutgoingMovementAndUpdateStock() {
        StockMovement movement = stockMovementService.recordMovement(
                product.getId(),
                MovementType.OUT,
                3
        );

        Product updatedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertThat(updatedProduct.getQuantity()).isEqualTo(7);
        assertThat(movement.getId()).isNotNull();
        assertThat(stockMovementRepository.findByProductId(product.getId()))
                .hasSize(1);
    }
}
