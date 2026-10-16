
package fr.stockpilot.stockpilot.service.impl;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.repository.CategoryRepository;
import fr.stockpilot.stockpilot.repository.ProductRepository;
import fr.stockpilot.stockpilot.repository.StockMovementRepository;
import fr.stockpilot.stockpilot.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = new Category("Informatique", "Matériel informatique");
        category.setId(1L);

        product = new Product(
                "SKU-001",
                "Clavier",
                "Clavier mécanique",
                new BigDecimal("79.99"),
                10,
                category
        );
    }

    @Test
    void createProductShouldSaveProductWhenSkuIsAvailable() {
        when(productRepository.existsBySkuIgnoreCase("SKU-001"))
                .thenReturn(false);
        when(categoryRepository.findById(any()))
                .thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
        verify(productRepository).save(product);
    }

    @Test
    void createProductShouldThrowWhenSkuAlreadyExists() {
        when(productRepository.existsBySkuIgnoreCase("SKU-001"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductByIdShouldReturnProductWhenFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertSame(product, result);
    }

    @Test
    void getProductByIdShouldThrowWhenNotFound() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(99L)
        );
    }
    @Test
    void updateProductShouldUpdateProductWhenFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        product.setSku("SKU-002");
        product.setName("Clavier amélioré");

        Product result = productService.updateProduct(1L, product);

        assertEquals("SKU-002", result.getSku());
        assertEquals("Clavier amélioré", result.getName());
        verify(productRepository).save(product);
    }

    @Test
    void updateProductShouldThrowWhenProductNotFound() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(99L, product)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void searchProductsByNameShouldReturnMatchingProducts() {
        when(productRepository.findByNameContainingIgnoreCase("clavier"))
                .thenReturn(java.util.List.of(product));

        var result = productService.searchProductsByName("clavier");

        assertEquals(1, result.size());
        assertEquals("Clavier", result.getFirst().getName());
    }
    @Test
    void getProductsByCategoryShouldReturnMatchingProducts() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategoryId(1L))
                .thenReturn(java.util.List.of(product));

        var result = productService.getProductsByCategory(1L);

        assertEquals(1, result.size());
        assertSame(product, result.getFirst());
    }
    @Test
    void getLowStockProductsShouldReturnProductsBelowThreshold() {
        when(productRepository.findByQuantityLessThanEqual(5))
                .thenReturn(java.util.List.of());

        var result = productService.getLowStockProducts(5);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteProductShouldDeleteProductWhenFoundAndWithoutMovements() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductShouldThrowWhenProductNotFound() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void deleteProductShouldThrowWhenProductHasStockMovements() {
        Product productWithMovements = spy(product);

        doReturn(List.of(mock(StockMovement.class)))
                .when(productWithMovements).getStockMovements();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(productWithMovements));

        assertThrows(
                IllegalStateException.class,
                () -> productService.deleteProduct(1L)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }

}
