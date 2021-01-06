package agh.queueFreeShop.service;

import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.repository.CartItemRepository;
import agh.queueFreeShop.repository.ProductRepository;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Responsible for business logic - all transactions are executed there.
 */

@Service
public class ShopService {
    private final ShoppingCartRepository cartRepository;
    private final ReceiptRepository receiptRepository;
    private final CartItemRepository cartItemRepository;

    ShopService(ShoppingCartRepository cartRepository, ReceiptRepository receiptRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.receiptRepository = receiptRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Adds one product into ShoppingCart.
     */
    @Transactional
    public void addProductToCart(ShoppingCart cart, Product product) {
        CartItem cartItem = cart.getCartItem(product);
        if (cartItem == null) {
            cartItem = new CartItem(cart, product, 1);
            cart.getItems().add(cartItem);
        } else {
            cartItem.addOne();
        }

        cartRepository.save(cart);
    }

    /**
     * Removes one product from ShoppingCart. If quantity is 0, CartItem is also removed.
     */
    @Transactional
    public void removeProductFromCart(ShoppingCart cart, Product product) {
        CartItem cartItem = cart.getCartItem(product);
        if (cartItem != null) {
            if (cartItem.getQuantity() == 1) {
                cart.getItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            }
            else
                cartItem.removeOne();
        }

        cartRepository.save(cart);
    }

    @Transactional
    public Receipt finalizeShopping(ShoppingCart cart) {
        Receipt receipt = cart.generateReceipt();
        cartRepository.delete(cart);
        return receiptRepository.save(receipt);
    }
}
