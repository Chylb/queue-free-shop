package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.service.ProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simply returns products.
 */

@RestController
@RequestMapping(path = "/products")
@ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
public class ProductController {
    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    @ApiOperation(value = "Get all products in the shop")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{barcode}")
    @ApiOperation(value = "Get product with given barcode")
    @ApiResponses(@ApiResponse(code=404, message = "Product not found"))
    public Product getProduct(@PathVariable String barcode) {
        return productService.getProduct(barcode);
    }
}
