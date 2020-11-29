package agh.queueFreeShop.repository;

import agh.queueFreeShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(long id);
    Optional<Product> findByBarcode(String barcode);
}
