
package fr.stockpilot.stockpilot.service;

import fr.stockpilot.stockpilot.model.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    List<Product> searchProductsByName(String name);

    List<Product> getProductsByCategory(Long categoryId);

    List<Product> getLowStockProducts(Integer threshold);
}
