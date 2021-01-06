package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Product.class)})
    public ResponseEntity<?> getProduct(@PathVariable String barcode) {
        Product product = productRepository.findByBarcode(barcode);

        if (product == null)
            throw new NotFoundException("Product not found");

        return ResponseEntity.ok(product);
    }
}
