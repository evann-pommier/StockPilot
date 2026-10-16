package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockMovementRepositoryIntegrationTest {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindMovementsByProductId() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Informatique", "Matériel informatique")
        );

        Product product = productRepository.saveAndFlush(
                new Product(
                        "SKU-MOV-001",
                        "Clavier",
                        "Clavier mécanique",
                        new BigDecimal("79.90"),
                        10,
                        category
                )
        );

        StockMovement movement = stockMovementRepository.saveAndFlush(
                new StockMovement(MovementType.IN, 5, product)
        );

        var result = stockMovementRepository.findByProductId(product.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(movement.getId());
        assertThat(result.getFirst().getType()).isEqualTo(MovementType.IN);
    }

    @Test
    void shouldFindMovementsByType() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Accessoires", "Accessoires informatiques")
        );

        Product product = productRepository.saveAndFlush(
                new Product(
                        "SKU-MOV-002",
                        "Souris",
                        "Souris sans fil",
                        new BigDecimal("29.90"),
                        10,
                        category
                )
        );

        stockMovementRepository.saveAndFlush(
                new StockMovement(MovementType.IN, 5, product)
        );

        stockMovementRepository.saveAndFlush(
                new StockMovement(MovementType.OUT, 2, product)
        );

        var result = stockMovementRepository.findByType(MovementType.OUT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getQuantity()).isEqualTo(2);
    }
}
