package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.service.ProductService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/products")
public class ProductController {
    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{barcode}")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Product.class)})
    public Product getProduct(@PathVariable String barcode) {
        return productService.getProduct(barcode);
    }
}
