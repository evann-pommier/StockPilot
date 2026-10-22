package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.exception.BusinessRuleException;
import fr.stockpilot.stockpilot.exception.GlobalExceptionHandler;
import fr.stockpilot.stockpilot.exception.ResourceNotFoundException;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.service.StockMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StockMovementControllerTest {

    @Mock
    private StockMovementService movementService;

    @InjectMocks
    private StockMovementController controller;

    private MockMvc mockMvc;
    private StockMovement movement;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Product product = new Product(
                "SKU-001", "Clavier", null, new BigDecimal("79.99"), 10, null);
        ReflectionTestUtils.setField(product, "id", 3L);
        movement = new StockMovement(MovementType.IN, 5, product);
        ReflectionTestUtils.setField(movement, "id", 11L);
    }

    @Test
    void getAllMovementsShouldExposeProductNameAndSku() throws Exception {
        when(movementService.getAllMovements()).thenReturn(List.of(movement));

        mockMvc.perform(get("/api/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].type").value("IN"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].product.id").value(3))
                .andExpect(jsonPath("$[0].product.name").value("Clavier"))
                .andExpect(jsonPath("$[0].product.sku").value("SKU-001"));
    }

    @Test
    void getMovementByIdShouldReturnMovement() throws Exception {
        when(movementService.getMovementById(11L)).thenReturn(movement);

        mockMvc.perform(get("/api/movements/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void getMovementsByProductShouldReturnList() throws Exception {
        when(movementService.getMovementsByProduct(3L)).thenReturn(List.of(movement));

        mockMvc.perform(get("/api/movements/product/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMovementsByProductShouldReturn404WhenProductIsUnknown() throws Exception {
        when(movementService.getMovementsByProduct(404L))
                .thenThrow(new ResourceNotFoundException("Produit introuvable avec l'identifiant : 404"));

        mockMvc.perform(get("/api/movements/product/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMovementsByTypeShouldReturnList() throws Exception {
        when(movementService.getMovementsByType(MovementType.IN)).thenReturn(List.of(movement));

        mockMvc.perform(get("/api/movements/type/IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("IN"));
    }

    @Test
    void getMovementsByTypeShouldReturn400WhenTypeIsUnknown() throws Exception {
        mockMvc.perform(get("/api/movements/type/NOPE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(movementService);
    }

    @Test
    void createMovementShouldReturn201() throws Exception {
        when(movementService.recordMovement(3L, MovementType.IN, 5)).thenReturn(movement);

        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":3,"type":"IN","quantity":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.name").value("Clavier"));
    }

    @Test
    void createMovementShouldReturn400WhenQuantityIsNotPositive() throws Exception {
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":3,"type":"OUT","quantity":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La quantité doit être supérieure à zéro."));

        verifyNoInteractions(movementService);
    }

    @Test
    void createMovementShouldReturn409WhenStockIsInsufficient() throws Exception {
        when(movementService.recordMovement(3L, MovementType.OUT, 50))
                .thenThrow(new BusinessRuleException("Stock insuffisant pour effectuer cette sortie."));

        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":3,"type":"OUT","quantity":50}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Stock insuffisant pour effectuer cette sortie."));
    }

    @Test
    void deleteMovementShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/movements/11"))
                .andExpect(status().isNoContent());

        verify(movementService).deleteMovement(11L);
    }
}
