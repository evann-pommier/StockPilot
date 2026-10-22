package fr.stockpilot.stockpilot.config;

import fr.stockpilot.stockpilot.model.Category;
import fr.stockpilot.stockpilot.model.MovementType;
import fr.stockpilot.stockpilot.model.Product;
import fr.stockpilot.stockpilot.model.StockMovement;
import fr.stockpilot.stockpilot.service.CategoryService;
import fr.stockpilot.stockpilot.service.ProductService;
import fr.stockpilot.stockpilot.service.StockMovementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

/**
 * Jeu de données de démonstration, actif uniquement avec le profil « demo »
 * (SPRING_PROFILES_ACTIVE=demo). Ne fait rien si la base contient déjà des données.
 */
@Slf4j
@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(
            CategoryService categoryService,
            ProductService productService,
            StockMovementService movementService) {
        return args -> {
            if (!categoryService.getAllCategories().isEmpty()) {
                log.info("Données de démo ignorées : la base n'est pas vide.");
                return;
            }

            Category computing = categoryService.createCategory(
                    new Category("Informatique", "Ordinateurs et périphériques"));
            Category office = categoryService.createCategory(
                    new Category("Bureau", "Mobilier et fournitures"));
            Category audio = categoryService.createCategory(
                    new Category("Audio", "Casques, enceintes et micros"));

            Product keyboard = productService.createProduct(new Product(
                    "CLAV-001", "Clavier mécanique", "Switchs tactiles, rétroéclairé",
                    new BigDecimal("79.90"), 24, computing));
            Product mouse = productService.createProduct(new Product(
                    "SOUR-001", "Souris sans fil", "Capteur 4000 DPI",
                    new BigDecimal("29.90"), 3, computing));
            Product monitor = productService.createProduct(new Product(
                    "ECRAN-27", "Écran 27 pouces", "QHD, 75 Hz",
                    new BigDecimal("249.00"), 8, computing));
            Product desk = productService.createProduct(new Product(
                    "BUR-120", "Bureau réglable", "Hauteur électrique, 120 cm",
                    new BigDecimal("389.00"), 2, office));
            Product headset = productService.createProduct(new Product(
                    "CASQ-001", "Casque audio", "Réduction de bruit active",
                    new BigDecimal("129.00"), 15, audio));

            movementService.recordMovement(keyboard.getId(), MovementType.IN, 10);
            movementService.recordMovement(mouse.getId(), MovementType.OUT, 4);
            movementService.recordMovement(monitor.getId(), MovementType.OUT, 2);
            movementService.recordMovement(desk.getId(), MovementType.IN, 5);
            StockMovement last = movementService.recordMovement(
                    headset.getId(), MovementType.OUT, 3);

            log.info("Données de démo créées (dernier mouvement : #{}).", last.getId());
        };
    }
}
