package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.exception.BusinessRuleException;
import fr.stockpilot.stockpilot.exception.DuplicateResourceException;
import fr.stockpilot.stockpilot.exception.ResourceNotFoundException;
import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.repository.CategoryRepository;
import fr.stockpilot.stockpilot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new DuplicateResourceException(
                    "Une catégorie portant ce nom existe déjà."
            );
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie introuvable avec l'identifiant : " + id
                ));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getCategoryById(id);

        boolean nameChanged = !existingCategory.getName()
                .equalsIgnoreCase(category.getName());

        if (nameChanged
                && categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new DuplicateResourceException(
                    "Une catégorie portant ce nom existe déjà."
            );
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        if (!category.getProducts().isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de supprimer une catégorie contenant des produits."
            );
        }

        categoryRepository.delete(category);
    }
}
