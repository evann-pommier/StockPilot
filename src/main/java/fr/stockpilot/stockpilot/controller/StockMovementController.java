package fr.stockpilot.stockpilot.controller;

import fr.stockpilot.stockpilot.dto.MovementRequest;
import fr.stockpilot.stockpilot.dto.MovementResponse;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public List<MovementResponse> getAllMovements() {
        return toResponses(stockMovementService.getAllMovements());
    }

    @GetMapping("/{id}")
    public MovementResponse getMovementById(@PathVariable Long id) {
        return MovementResponse.from(stockMovementService.getMovementById(id));
    }

    @GetMapping("/product/{productId}")
    public List<MovementResponse> getMovementsByProduct(@PathVariable Long productId) {
        return toResponses(stockMovementService.getMovementsByProduct(productId));
    }

    @GetMapping("/type/{type}")
    public List<MovementResponse> getMovementsByType(@PathVariable MovementType type) {
        return toResponses(stockMovementService.getMovementsByType(type));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse createMovement(@Valid @RequestBody MovementRequest request) {
        return MovementResponse.from(stockMovementService.recordMovement(
                request.productId(), request.type(), request.quantity()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovement(@PathVariable Long id) {
        stockMovementService.deleteMovement(id);
    }

    private List<MovementResponse> toResponses(List<StockMovement> movements) {
        return movements.stream().map(MovementResponse::from).toList();
    }
}
