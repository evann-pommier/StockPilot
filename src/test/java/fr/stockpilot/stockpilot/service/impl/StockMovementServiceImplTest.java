package fr.stockpilot.stockpilot.service.impl;

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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(
                "SKU-001",
                "Clavier",
                "Clavier mécanique",
                new BigDecimal("79.99"),
                10,
                null
        );
    }

    @Test
    void recordMovementShouldIncreaseStockForIncomingMovement() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockMovement result = stockMovementService.recordMovement(
                1L, MovementType.IN, 5
        );

        assertNotNull(result);
        assertEquals(15, product.getQuantity());
        assertEquals(MovementType.IN, result.getType());
        assertEquals(5, result.getQuantity());

        verify(productRepository).save(product);
        verify(stockMovementRepository).save(any(StockMovement.class));
    }

    @Test
    void recordMovementShouldDecreaseStockForOutgoingMovement() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockMovement result = stockMovementService.recordMovement(
                1L, MovementType.OUT, 3
        );

        assertNotNull(result);
        assertEquals(7, product.getQuantity());
        assertEquals(MovementType.OUT, result.getType());
        assertEquals(3, result.getQuantity());

        verify(productRepository).save(product);
        verify(stockMovementRepository).save(any(StockMovement.class));
    }

    @Test
    void recordMovementShouldRejectOutgoingMovementWhenStockIsInsufficient() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalStateException.class,
                () -> stockMovementService.recordMovement(
                        1L, MovementType.OUT, 11
                )
        );

        assertEquals(10, product.getQuantity());
        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never())
                .save(any(StockMovement.class));
    }

    @Test
    void recordMovementShouldRejectNullType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.recordMovement(1L, null, 5)
        );

        verify(productRepository, never()).findById(any());
    }

    @Test
    void recordMovementShouldRejectZeroQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.recordMovement(
                        1L, MovementType.IN, 0
                )
        );

        verify(productRepository, never()).findById(any());
    }

    @Test
    void recordMovementShouldRejectMissingProduct() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.recordMovement(
                        99L, MovementType.IN, 5
                )
        );

        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never())
                .save(any(StockMovement.class));
    }

    @Test
    void recordMovementShouldRejectAdjustment() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.recordMovement(
                        1L, MovementType.ADJUSTMENT, 5
                )
        );

        assertEquals(10, product.getQuantity());
        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never())
                .save(any(StockMovement.class));
    }

    @Test
    void getMovementById_shouldReturnMovement_whenItExists() {
        StockMovement movement = new StockMovement(
                MovementType.IN, 5, product
        );

        when(stockMovementRepository.findById(1L))
                .thenReturn(Optional.of(movement));

        StockMovement result = stockMovementService.getMovementById(1L);

        assertNotNull(result);
        assertEquals(movement, result);
        verify(stockMovementRepository).findById(1L);
    }

    @Test
    void getMovementById_shouldThrow_whenMovementDoesNotExist() {
        when(stockMovementRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.getMovementById(99L)
        );
    }

    @Test
    void getAllMovements_shouldReturnMovementsOrderedByDate() {
        StockMovement movement = new StockMovement(
                MovementType.IN, 5, product
        );
        List<StockMovement> movements = List.of(movement);

        when(stockMovementRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(movements);

        List<StockMovement> result = stockMovementService.getAllMovements();

        assertEquals(1, result.size());
        assertSame(movement, result.getFirst());
        verify(stockMovementRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getMovementsByProduct_shouldReturnMovements_whenProductExists() {
        StockMovement movement = new StockMovement(
                MovementType.IN, 5, product
        );
        List<StockMovement> movements = List.of(movement);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(stockMovementRepository.findByProductIdOrderByCreatedAtDesc(1L))
                .thenReturn(movements);

        List<StockMovement> result =
                stockMovementService.getMovementsByProduct(1L);

        assertEquals(1, result.size());
        assertSame(movement, result.getFirst());
        verify(productRepository).existsById(1L);
        verify(stockMovementRepository)
                .findByProductIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMovementsByProduct_shouldThrow_whenProductDoesNotExist() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.getMovementsByProduct(99L)
        );

        verify(stockMovementRepository, never())
                .findByProductIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void getMovementsByType_shouldReturnMovements_whenTypeIsValid() {
        StockMovement movement = new StockMovement(
                MovementType.IN, 5, product
        );
        List<StockMovement> movements = List.of(movement);

        when(stockMovementRepository.findByType(MovementType.IN))
                .thenReturn(movements);

        List<StockMovement> result =
                stockMovementService.getMovementsByType(MovementType.IN);

        assertEquals(1, result.size());
        assertSame(movement, result.get(0));
        verify(stockMovementRepository).findByType(MovementType.IN);
    }

    @Test
    void getMovementsByType_shouldThrow_whenTypeIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.getMovementsByType(null)
        );

        verifyNoInteractions(stockMovementRepository);
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThatThrownBy(() ->
                stockMovementService.recordMovement(
                        1L,
                        MovementType.IN,
                        -5
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La quantité doit être strictement positive.");

        verifyNoInteractions(productRepository, stockMovementRepository);
    }


    @Test
    void recordMovementShouldRejectNullQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stockMovementService.recordMovement(
                        1L,
                        MovementType.IN,
                        null
                )
        );

        verifyNoInteractions(productRepository, stockMovementRepository);
    }


}