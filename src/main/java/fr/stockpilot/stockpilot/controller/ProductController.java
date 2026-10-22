package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.dto.ProductRequest;
import fr.stockpilot.stockpilot.dto.ProductResponse;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    private final ProductService productService;

    /**
     * Liste les produits. Filtres optionnels : {@code search} (nom) ou
     * {@code categoryId}. Si les deux sont fournis, {@code search} est prioritaire.
     */
    @GetMapping
    public List<ProductResponse> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productService.searchProductsByName(search.trim());
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }
        return toResponses(products);
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(
            @RequestParam(defaultValue = "" + DEFAULT_LOW_STOCK_THRESHOLD) Integer threshold) {
        return toResponses(productService.getLowStockProducts(threshold));
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return ProductResponse.from(productService.getProductById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.createProduct(request.toEntity()));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.updateProduct(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    private List<ProductResponse> toResponses(List<Product> products) {
        return products.stream().map(ProductResponse::from).toList();
    }
}
