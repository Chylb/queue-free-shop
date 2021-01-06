package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.ForbiddenException;
import agh.queueFreeShop.exception.UnprocessableEntityException;
import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.repository.ReceiptRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
            throw new NotFoundException("Receipt not found");
        if (!receipt.getUser().getId().equals(getUserId()))
            throw new ForbiddenException("Access forbidden");

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
