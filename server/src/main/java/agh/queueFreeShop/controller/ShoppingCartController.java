package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public ResponseEntity<?> getShoppingCart(Principal principal) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(Long.parseLong(principal.getName()));
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(Principal principal, @RequestParam String barcode) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(Long.parseLong(principal.getName()));
        try {
            shopService.addProductToCart(cart, barcode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        }

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<?> removeProduct(Principal principal, @RequestParam String barcode) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(Long.parseLong(principal.getName()));
        try {
            shopService.removeProductFromCart(cart, barcode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        }

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/finalize")
    public ResponseEntity<?> finalize(Principal principal) {
        ShoppingCart cart = shoppingCartRepository.getByUserId(Long.parseLong(principal.getName()));
        Receipt receipt = shopService.finalizeShopping(cart);

        return ResponseEntity.ok(receipt);
    }

    //TODO: REMOVE THIS BODGE
    @PostMapping("/enter")
    public ResponseEntity<?> enter(Principal principal) {
        User user = new User();
        user.setId( Long.parseLong(principal.getName()));

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cart.setItems(new HashSet<>());
        cart = shoppingCartRepository.save(cart);

        return ResponseEntity.ok(cart);
    }
}
