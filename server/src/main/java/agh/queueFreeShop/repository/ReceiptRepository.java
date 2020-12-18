package agh.queueFreeShop.repository;

import agh.queueFreeShop.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Receipt getById(Long id);
    List<Receipt> getAllByUserId(Long id);
}
