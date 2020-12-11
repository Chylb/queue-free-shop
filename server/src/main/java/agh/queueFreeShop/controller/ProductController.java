package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/products")
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @GetMapping("/all")
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @GetMapping("")
    public ResponseEntity<?> getProduct(@RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "barcode", required = false) String barcode) {
        Optional<Product> product;
        if (id != null)
            product = productRepository.findById(id);
        else if (barcode != null)
            product = productRepository.findByBarcode(barcode);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error Message");

        if (product.isPresent())
            return ResponseEntity.ok(product.get());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
    }
}
