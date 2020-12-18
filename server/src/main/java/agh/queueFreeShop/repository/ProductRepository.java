package agh.queueFreeShop.repository;

import agh.queueFreeShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findByBarcode(String barcode);
}
