package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.exception.BusinessRuleException;
import fr.stockpilot.stockpilot.exception.DuplicateResourceException;
import fr.stockpilot.stockpilot.exception.GlobalExceptionHandler;
import fr.stockpilot.stockpilot.exception.ResourceNotFoundException;
import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.service.CategoryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController controller;

    private MockMvc mockMvc;
    private Category category;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        category = new Category("Informatique", "Matériel");
        ReflectionTestUtils.setField(category, "id", 1L);
    }

    @Test
    void getAllCategoriesShouldReturnList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Informatique"));
    }

    @Test
    void getCategoryByIdShouldReturn404WhenMissing() throws Exception {
        when(categoryService.getCategoryById(9L))
                .thenThrow(new ResourceNotFoundException("Catégorie introuvable avec l'identifiant : 9"));

        mockMvc.perform(get("/api/categories/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategoryByIdShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Matériel"));
    }

    @Test
    void createCategoryShouldReturn201() throws Exception {
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Informatique","description":"Matériel"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Informatique"));
    }

    @Test
    void createCategoryShouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Le nom de la catégorie est obligatoire."));

        verifyNoInteractions(categoryService);
    }

    @Test
    void createCategoryShouldReturn409WhenNameAlreadyExists() throws Exception {
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new DuplicateResourceException("Une catégorie portant ce nom existe déjà."));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Informatique"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCategoryShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(category);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Informatique"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategoryShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void deleteCategoryShouldReturn409WhenItContainsProducts() throws Exception {
        doThrow(new BusinessRuleException("Impossible de supprimer une catégorie contenant des produits."))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isConflict());
    }
}
