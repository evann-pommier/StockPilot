package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.exception.BusinessRuleException;
import fr.stockpilot.stockpilot.exception.DuplicateResourceException;
import fr.stockpilot.stockpilot.exception.GlobalExceptionHandler;
import fr.stockpilot.stockpilot.exception.ResourceNotFoundException;
import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final String VALID_BODY = """
            {"sku":"SKU-001","name":"Clavier","description":"Mécanique",
             "price":79.99,"quantity":10,"categoryId":1}
            """;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController controller;

    private MockMvc mockMvc;
    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Category category = new Category("Informatique", null);
        ReflectionTestUtils.setField(category, "id", 1L);
        product = new Product(
                "SKU-001", "Clavier", "Mécanique", new BigDecimal("79.99"), 10, category);
        ReflectionTestUtils.setField(product, "id", 7L);
    }

    @Test
    void getProductsShouldReturnAllProductsWithCategorySummary() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"))
                .andExpect(jsonPath("$[0].category.name").value("Informatique"))
                .andExpect(jsonPath("$[0].stockMovements").doesNotExist());
    }

    @Test
    void getProductsShouldSearchByNameWhenSearchParamIsPresent() throws Exception {
        when(productService.searchProductsByName("clav")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products").param("search", "clav"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Clavier"));

        verify(productService, never()).getAllProducts();
    }

    @Test
    void getProductsShouldFilterByCategoryWhenCategoryIdIsPresent() throws Exception {
        when(productService.getProductsByCategory(1L)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products").param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLowStockProductsShouldUseDefaultThresholdOfFive() throws Exception {
        when(productService.getLowStockProducts(5)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLowStockProductsShouldReturn400WhenThresholdIsInvalid() throws Exception {
        when(productService.getLowStockProducts(-1))
                .thenThrow(new IllegalArgumentException("Le seuil de stock doit être positif."));

        mockMvc.perform(get("/api/products/low-stock").param("threshold", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Le seuil de stock doit être positif."));
    }

    @Test
    void getProductByIdShouldReturnProduct() throws Exception {
        when(productService.getProductById(7L)).thenReturn(product);

        mockMvc.perform(get("/api/products/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(79.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getProductByIdShouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Produit introuvable avec l'identifiant : 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Produit introuvable avec l'identifiant : 99"));
    }

    @Test
    void createProductShouldReturn201AndPassCategoryReferenceToService() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).createProduct(captor.capture());
        assertEquals("SKU-001", captor.getValue().getSku());
        assertEquals(1L, captor.getValue().getCategory().getId());
        assertEquals(10, captor.getValue().getQuantity());
    }

    @Test
    void createProductShouldDefaultQuantityToZeroWhenOmitted() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"SKU-002","name":"Souris","price":10,"categoryId":1}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).createProduct(captor.capture());
        assertEquals(0, captor.getValue().getQuantity());
    }

    @Test
    void createProductShouldReturn400AndNotCallServiceWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"","name":"Clavier","price":-5,"quantity":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Données invalides"));

        verifyNoInteractions(productService);
    }

    @Test
    void createProductShouldReturn400WhenBodyIsNotJson() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("pas du json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProductShouldReturn409WhenSkuAlreadyExists() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new DuplicateResourceException(
                        "Un produit utilisant cette référence SKU existe déjà."));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail")
                        .value("Un produit utilisant cette référence SKU existe déjà."));
    }

    @Test
    void updateProductShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(eq(7L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clavier"));
    }

    @Test
    void deleteProductShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/7"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(7L);
    }

    @Test
    void deleteProductShouldReturn409WhenProductHasMovements() throws Exception {
        doThrow(new BusinessRuleException(
                "Impossible de supprimer un produit ayant un historique de stock."))
                .when(productService).deleteProduct(7L);

        mockMvc.perform(delete("/api/products/7"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Règle métier non respectée"));
    }
}
