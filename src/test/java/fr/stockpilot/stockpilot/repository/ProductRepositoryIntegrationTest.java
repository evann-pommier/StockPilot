package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveAndFindProduct() {
        // Arrange : enregistrer d'abord la catégorie
        Category category = new Category(
                "Informatique",
                "Matériel informatique"
        );
        category = categoryRepository.saveAndFlush(category);

        Product product = new Product(
                "SKU-TEST-001",
                "Clavier de test",
                "Clavier mécanique",
                new BigDecimal("79.99"),
                10,
                category
        );

        // Act : enregistrer puis rechercher le produit
        Product savedProduct = productRepository.saveAndFlush(product);

        Product foundProduct = productRepository
                .findById(savedProduct.getId())
                .orElseThrow();

        // Assert : vérifier les données persistées
        assertThat(foundProduct.getSku()).isEqualTo("SKU-TEST-001");
        assertThat(foundProduct.getName()).isEqualTo("Clavier de test");
        assertThat(foundProduct.getPrice())
                .isEqualByComparingTo("79.99");
        assertThat(foundProduct.getQuantity()).isEqualTo(10);
        assertThat(foundProduct.getCategory().getId())
                .isEqualTo(category.getId());
    }

    @Test
    void shouldFindProductBySkuIgnoringCase() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Bureautique", "Fournitures de bureau")
        );

        Product product = new Product(
                "SKU-CASE-001",
                "Souris",
                "Souris sans fil",
                new BigDecimal("24.90"),
                8,
                category
        );
        productRepository.saveAndFlush(product);

        var result = productRepository.findBySkuIgnoreCase("sku-case-001");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getName()).isEqualTo("Souris");
    }

    @Test
    void shouldFindProductsWithQuantityBelowOrEqualToThreshold() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Accessoires", "Accessoires informatiques")
        );

        productRepository.saveAndFlush(new Product(
                "SKU-STOCK-001",
                "Webcam",
                "Webcam HD",
                new BigDecimal("49.90"),
                2,
                category
        ));

        productRepository.saveAndFlush(new Product(
                "SKU-STOCK-002",
                "Casque",
                "Casque audio",
                new BigDecimal("59.90"),
                10,
                category
        ));

        var result = productRepository.findByQuantityLessThanEqual(3);

        assertThat(result)
                .extracting(Product::getName)
                .containsExactly("Webcam");
    }

    @Test
    void shouldFindProductsByNameIgnoringCase() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Écrans", "Écrans informatiques")
        );

        productRepository.saveAndFlush(new Product(
                "SKU-NAME-001",
                "Écran Samsung",
                "Écran 27 pouces",
                new BigDecimal("199.90"),
                5,
                category
        ));

        var result = productRepository.findByNameContainingIgnoreCase("sAmSuNg");

        assertThat(result)
                .extracting(Product::getName)
                .containsExactly("Écran Samsung");
    }

    @Test
    void shouldFindProductsByCategoryId() {
        Category category = categoryRepository.saveAndFlush(
                new Category("Claviers", "Claviers informatiques")
        );

        Category otherCategory = categoryRepository.saveAndFlush(
                new Category("Imprimantes", "Imprimantes")
        );

        productRepository.saveAndFlush(new Product(
                "SKU-CAT-001",
                "Clavier mécanique",
                "Clavier USB",
                new BigDecimal("89.90"),
                4,
                category
        ));

        productRepository.saveAndFlush(new Product(
                "SKU-CAT-002",
                "Imprimante laser",
                "Imprimante réseau",
                new BigDecimal("149.90"),
                3,
                otherCategory
        ));

        var result = productRepository.findByCategoryId(category.getId());

        assertThat(result)
                .extracting(Product::getName)
                .containsExactly("Clavier mécanique");
    }


}
