package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.ForbiddenException;
import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.repository.ReceiptRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simply returns receipts.
 */

@RestController
@RequestMapping(path = "/receipts")
@ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
public class ReceiptController {
    private final ReceiptRepository receiptRepository;

    ReceiptController(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get receipt by id", notes = "Receipt must be owned by user making request.")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Receipt not found"),
            @ApiResponse(code = 403, message = "Access forbidden")})
    public Receipt getById(@PathVariable Long id) {
        Receipt receipt = receiptRepository.getById(id);
        if (receipt == null)
            throw new NotFoundException("Receipt not found");
        if (!receipt.getUser().getId().equals(getUserId()))
            throw new ForbiddenException("Access forbidden");

        return receipt;
    }

    @GetMapping("")
    @ApiOperation(value = "Get all receipts", notes = "Returns all receipts owned by user.")
    public List<Receipt> getAllByUser() {
        return receiptRepository.getAllByUserId(getUserId());
    }

    private Long getUserId() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
