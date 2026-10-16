package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.repository.CategoryRepository;
import fr.stockpilot.stockpilot.repository.ProductRepository;
import fr.stockpilot.stockpilot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        validateSku(product.getSku());

        Category category = getCategory(product.getCategory().getId());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit introuvable avec l'identifiant : " + id
                ));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);

        if (!existingProduct.getSku().equalsIgnoreCase(product.getSku())) {
            validateSku(product.getSku());
            existingProduct.setSku(product.getSku());
        }

        Category category = getCategory(product.getCategory().getId());

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(category);

        // La quantité est volontairement modifiée via les mouvements de stock.
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);

        if (!product.getStockMovements().isEmpty()) {
            throw new IllegalStateException(
                    "Impossible de supprimer un produit ayant un historique de stock."
            );
        }

        productRepository.delete(product);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        getCategory(categoryId);
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException(
                    "Le seuil de stock doit être supérieur ou égal à zéro."
            );
        }

        return productRepository.findByQuantityLessThanEqual(threshold);
    }

    private void validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException(
                    "La référence SKU est obligatoire."
            );
        }

        if (productRepository.existsBySkuIgnoreCase(sku)) {
            throw new IllegalArgumentException(
                    "Un produit utilisant cette référence SKU existe déjà."
            );
        }
    }

    private Category getCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException(
                    "La catégorie du produit est obligatoire."
            );
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Catégorie introuvable avec l'identifiant : " + categoryId
                ));
    }
}
