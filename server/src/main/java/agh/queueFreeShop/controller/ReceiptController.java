package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.repository.ReceiptRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/receipts")
public class ReceiptController {
    private final ReceiptRepository receiptRepository;

    ReceiptController(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Receipt.class)})
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Receipt receipt = receiptRepository.getById(id);
        if (receipt == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        if (!receipt.getUser().getId().equals(getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Message");

        return ResponseEntity.ok(receipt);
    }

    @GetMapping("")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Receipt[].class)})
    public ResponseEntity<?> getAllByUser() {
        return ResponseEntity.ok(receiptRepository.getAllByUserId(getUserId()));
    }

    private Long getUserId(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
