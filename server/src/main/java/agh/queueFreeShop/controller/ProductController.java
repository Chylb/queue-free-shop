package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/products")
public class ProductController {
    private final ProductRepository productRepository;

    ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<?> getProduct(@PathVariable String barcode) {
        Product product = productRepository.findByBarcode(barcode);

        if (product == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");

        return ResponseEntity.ok(product);
    }
}
