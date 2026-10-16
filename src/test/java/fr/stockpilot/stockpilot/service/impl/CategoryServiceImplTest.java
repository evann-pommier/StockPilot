package fr.stockpilot.stockpilot.service.impl;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.repository.CategoryRepository;
import fr.stockpilot.stockpilot.service.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Informatique", "Matériel informatique");
    }

    @Test
    void createCategoryShouldSaveCategoryWhenNameIsAvailable() {
        when(categoryRepository.existsByNameIgnoreCase("Informatique"))
                .thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Informatique", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void createCategoryShouldThrowWhenNameAlreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase("Informatique"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory(category)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryByIdShouldReturnCategoryWhenFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertSame(category, result);
    }

    @Test
    void getCategoryByIdShouldThrowWhenNotFound() {
        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.getCategoryById(99L)
        );
    }

    @Test
    void getAllCategoriesShouldReturnRepositoryResults() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertSame(category, result.get(0));
    }

    @Test
    void updateCategoryShouldUpdateExistingCategory() {
        Category updatedCategory =
                new Category("Bureautique", "Fournitures de bureau");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Bureautique"))
                .thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertEquals("Bureautique", result.getName());
        assertEquals("Fournitures de bureau", result.getDescription());
        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategoryShouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.updateCategory(
                        99L,
                        new Category("Bureautique", "Fournitures")
                )
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategoryShouldDeleteCategoryWithoutProducts() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }


    @Test
    void deleteCategoryShouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.deleteCategory(99L)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }


    @Test
    void deleteCategoryShouldThrowWhenCategoryContainsProducts() {
        Category categoryWithProducts = spy(category);

        doReturn(List.of(mock(Product.class)))
                .when(categoryWithProducts).getProducts();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(categoryWithProducts));

        assertThrows(
                IllegalStateException.class,
                () -> categoryService.deleteCategory(1L)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }


}
