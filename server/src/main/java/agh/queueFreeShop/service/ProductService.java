package agh.queueFreeShop.service;

import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProductRepository wrapper that throws NotFoundException when product is not found.
 */

@Service
public class ProductService {
    private final ProductRepository productRepository;

    ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Returns product by barcode if found or else throws:
     * @exception NotFoundException when product not found
     */
    public Product getProduct(String barcode) {
        Product product = productRepository.findByBarcode(barcode);

        if (product == null)
            throw new NotFoundException("Product not found");

        return product;
    }
}
