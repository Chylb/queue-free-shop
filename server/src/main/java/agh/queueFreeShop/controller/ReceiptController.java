package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.repository.ReceiptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/receipts")
public class ReceiptController {
    private final ReceiptRepository receiptRepository;

    ReceiptController(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Principal principal, @PathVariable Long id) {
        Receipt receipt = receiptRepository.getById(id);
        if (receipt == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        if (!receipt.getUser().getId().equals(Long.parseLong(principal.getName())))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Message");

        return ResponseEntity.ok(receipt);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllByUser(Principal principal) {
        return ResponseEntity.ok(receiptRepository.getAllByUserId(Long.parseLong(principal.getName())));
    }
}
