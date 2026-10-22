package fr.stockpilot.stockpilot.service.impl;

import fr.stockpilot.stockpilot.exception.BusinessRuleException;
import fr.stockpilot.stockpilot.exception.ResourceNotFoundException;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.repository.ProductRepository;
import fr.stockpilot.stockpilot.repository.StockMovementRepository;
import fr.stockpilot.stockpilot.service.StockMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/** Scénarios d'annulation d'un mouvement de stock (deleteMovement). */
@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplDeletionTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("SKU-001", "Clavier", null, new BigDecimal("79.99"), 10, null);
    }

    @Test
    void deleteMovementShouldRemoveStockAddedByIncomingMovement() {
        StockMovement movement = new StockMovement(MovementType.IN, 4, product);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));

        stockMovementService.deleteMovement(1L);

        assertEquals(6, product.getQuantity());
        verify(productRepository).save(product);
        verify(stockMovementRepository).delete(movement);
    }

    @Test
    void deleteMovementShouldRestoreStockTakenByOutgoingMovement() {
        StockMovement movement = new StockMovement(MovementType.OUT, 3, product);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));

        stockMovementService.deleteMovement(1L);

        assertEquals(13, product.getQuantity());
        verify(stockMovementRepository).delete(movement);
    }

    @Test
    void deleteMovementShouldRefuseWhenCurrentStockCannotCoverCancelledEntry() {
        StockMovement movement = new StockMovement(MovementType.IN, 25, product);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));

        assertThrows(BusinessRuleException.class, () -> stockMovementService.deleteMovement(1L));

        assertEquals(10, product.getQuantity());
        verify(stockMovementRepository, never()).delete(any(StockMovement.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteMovementShouldRejectAdjustments() {
        StockMovement movement = new StockMovement(MovementType.ADJUSTMENT, 2, product);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(movement));

        assertThrows(IllegalArgumentException.class, () -> stockMovementService.deleteMovement(1L));

        verify(stockMovementRepository, never()).delete(any(StockMovement.class));
    }

    @Test
    void deleteMovementShouldThrowNotFoundWhenMovementDoesNotExist() {
        when(stockMovementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockMovementService.deleteMovement(99L));

        verifyNoInteractions(productRepository);
    }
}
