package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ShopService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping(path = "/shoppingCart")
public class ShoppingCartController {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShopService shopService;

    ShoppingCartController(ShoppingCartRepository shoppingCartRepository, ShopService shopService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shopService = shopService;
    }

    @GetMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> getShoppingCart() {
        ShoppingCart cart = shoppingCartRepository.getByUserId(getUserId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> addProduct(@RequestParam String barcode) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(getUserId());
        try {
            shopService.addProductToCart(cart, barcode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        }

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> removeProduct(@RequestParam String barcode) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(getUserId());
        try {
            shopService.removeProductFromCart(cart, barcode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        }

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/finalize")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Receipt.class)})
    public ResponseEntity<?> finalizeShopping() {
        ShoppingCart cart = shoppingCartRepository.getByUserId(getUserId());
        Receipt receipt = shopService.finalizeShopping(cart);

        return ResponseEntity.ok(receipt);
    }

    //TODO: REMOVE THIS BODGE
    @PostMapping("/enter")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> enter() {
        User user = new User();
        user.setId( getUserId());

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cart.setItems(new HashSet<>());
        cart = shoppingCartRepository.save(cart);

        return ResponseEntity.ok(cart);
    }

    private Long getUserId(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
