
package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindCategoryByNameIgnoringCase() {
        categoryRepository.saveAndFlush(
                new Category("Informatique", "Matériel informatique")
        );

        var result = categoryRepository.findByNameIgnoreCase("INFORMATIQUE");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getName())
                .isEqualTo("Informatique");
    }

    @Test
    void shouldCheckIfCategoryNameExistsIgnoringCase() {
        categoryRepository.saveAndFlush(
                new Category("Bureautique", "Fournitures de bureau")
        );

        assertThat(categoryRepository.existsByNameIgnoreCase("bureautique"))
                .isTrue();

        assertThat(categoryRepository.existsByNameIgnoreCase("Jardinage"))
                .isFalse();
    }

}
