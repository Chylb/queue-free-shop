package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.ForbiddenException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ProductService;
import agh.queueFreeShop.service.ShopService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/shoppingCart")
public class ShoppingCartController {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;
    private final ShopService shopService;

    ShoppingCartController(ShoppingCartRepository shoppingCartRepository, ProductService productService, ShopService shopService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
        this.shopService = shopService;
    }

    @GetMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> getShoppingCart() {
        ShoppingCart cart = getUsersShoppingCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> addProduct(@RequestParam String barcode) {
        Product product = productService.getProduct(barcode);

        ShoppingCart cart = getUsersShoppingCart();
        shopService.addProductToCart(cart, product);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> removeProduct(@RequestParam String barcode) {
        Product product = productService.getProduct(barcode);

        ShoppingCart cart = getUsersShoppingCart();
        shopService.removeProductFromCart(cart, product);

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/finalize")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Receipt.class)})
    public ResponseEntity<?> finalizeShopping() {
        ShoppingCart cart = getUsersShoppingCart();
        Receipt receipt = shopService.finalizeShopping(cart);

        return ResponseEntity.ok(receipt);
    }

    @PostMapping("/confirmEntry")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ShoppingCart.class)})
    public ResponseEntity<?> confirmEntry() {
        User user = new User();
        user.setId(getUserId());

        ShoppingCart cart = shopService.onCustomerConfirmedEntry(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/confirmExit")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    public void confirmExit() {
        User user = new User();
        user.setId(getUserId());

        shopService.onCustomerConfirmedExit(user);
    }

    private ShoppingCart getUsersShoppingCart() {
        Long userId = getUserId();
        ShoppingCart cart = shoppingCartRepository.getByUserId(userId);
        if (cart == null)
            throw new ForbiddenException("Customer not in shop");
        if (cart.isFinalized())
            throw new ForbiddenException("Customer should head towards exit");

        return cart;
    }

    private Long getUserId() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
