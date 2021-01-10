package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.ForbiddenException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ProductService;
import agh.queueFreeShop.service.ShopService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Used for all shopping related matters.
 */

@RestController
@RequestMapping(path = "/shoppingCart")
@ApiResponses({@ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Customer not in shop / Customer should make payment / Customer should head towards exit")
})
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
    @ApiOperation(value = "Get shopping cart", notes = "Returns user's shopping cart.")
    public ShoppingCart getShoppingCart() {
        ShoppingCart cart = getUsersShoppingCart();
        return cart;
    }

    @PostMapping
    @ApiOperation(value = "Add product to shopping cart")
    @ApiResponses(@ApiResponse(code = 404, message = "Product not found"))
    public ShoppingCart addProduct(@RequestParam(name = "Product's barcode") String barcode) {
        Product product = productService.getProduct(barcode);

        ShoppingCart cart = getUsersShoppingCart();
        shopService.addProductToCart(cart, product);

        return cart;
    }

    @DeleteMapping
    @ApiOperation(value = "Remove product from shopping cart")
    @ApiResponses(@ApiResponse(code = 404, message = "Product not found"))
    public ShoppingCart removeProduct(@RequestParam(name = "Product's barcode") String barcode) {
        Product product = productService.getProduct(barcode);

        ShoppingCart cart = getUsersShoppingCart();
        shopService.removeProductFromCart(cart, product);

        return cart;
    }

    @PostMapping("/finalize")
    @ApiOperation(value = "Finalize shopping", notes = "From now on the only action you can take is to pay for shopping")
    public Receipt finalizeShopping() {
        ShoppingCart cart = getUsersShoppingCart();
        Receipt receipt = shopService.finalizeShopping(cart);

        return receipt;
    }

    @PostMapping("/confirmEntry")
    @ApiOperation(value = "Confirm entry", notes = "Opens the entrance gate. Customer must be scanned right before this action.")
    @ApiResponses(@ApiResponse(code = 403, message = "Customer not at entrance"))
    public ShoppingCart confirmEntry() {
        User user = new User();
        user.setId(getUserId());

        ShoppingCart cart = shopService.onCustomerConfirmedEntry(user);
        return cart;
    }

    @PostMapping("/confirmExit")
    @ApiOperation(value = "Confirm exit", notes = "Opens the exit gate. Customer must be scanned right before this action.")
    @ApiResponses(@ApiResponse(code = 403, message = "Customer not at exit / Final weight is incorrect"))
    public void confirmExit() {
        User user = new User();
        user.setId(getUserId());

        shopService.onCustomerConfirmedExit(user);
    }

    @PostMapping("/pay")
    @ApiOperation(value = "Make payment")
    @ApiResponses(@ApiResponse(code = 403, message = "Customer not in shop / Shopping not finalized / Shopping already paid"))
    public void makePayment() {
        Long userId = getUserId();
        ShoppingCart cart = shoppingCartRepository.getByUserId(userId);
        if (cart == null)
            throw new ForbiddenException("Customer not in shop");

        shopService.handlePayment(cart);
    }

    private ShoppingCart getUsersShoppingCart() {
        Long userId = getUserId();
        ShoppingCart cart = shoppingCartRepository.getByUserId(userId);
        if (cart == null)
            throw new ForbiddenException("Customer not in shop");
        if (cart.isFinalized())
            if (cart.isPaid())
                throw new ForbiddenException("Customer should head towards exit");
            else
                throw new ForbiddenException("Customer should make payment");

        return cart;
    }

    private Long getUserId() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
