package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.service.StockMovementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(
            StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public List<StockMovement> getAllMovements() {
        return stockMovementService.getAllMovements();
    }

    @GetMapping("/{id}")
    public StockMovement getMovementById(@PathVariable Long id) {
        return stockMovementService.getMovementById(id);
    }

    @GetMapping("/product/{productId}")
    public List<StockMovement> getMovementsByProduct(
            @PathVariable Long productId) {
        return stockMovementService.getMovementsByProduct(productId);
    }

    @GetMapping("/type/{type}")
    public List<StockMovement> getMovementsByType(
            @PathVariable MovementType type) {
        return stockMovementService.getMovementsByType(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovement createMovement(
            @RequestParam Long productId,
            @RequestParam MovementType type,
            @RequestParam int quantity) {
        return stockMovementService.recordMovement(
                productId, type, quantity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovement(@PathVariable Long id) {
        stockMovementService.deleteMovement(id);
    }

}