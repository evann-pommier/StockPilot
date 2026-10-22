package fr.stockpilot.stockpilot.repository;

import fr.stockpilot.stockpilot.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Les EntityGraph chargent la catégorie dans la même requête (évite le N+1).

    @Override
    @EntityGraph(attributePaths = "category")
    List<Product> findAll();

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    @EntityGraph(attributePaths = "category")
    List<Product> findByNameContainingIgnoreCase(String name);

    @EntityGraph(attributePaths = "category")
    List<Product> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = "category")
    List<Product> findByQuantityLessThanEqual(Integer threshold);
}
