package agh.queueFreeShop.service;

import agh.queueFreeShop.exception.ForbiddenException;
import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.*;
import agh.queueFreeShop.physical.gate.EntranceGate;
import agh.queueFreeShop.physical.gate.ExitGate;
import agh.queueFreeShop.physical.weight.EntranceWeight;
import agh.queueFreeShop.physical.weight.ExitWeight;
import agh.queueFreeShop.repository.CartItemRepository;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;

import static java.lang.Math.abs;

/**
 * Responsible for business logic - all transactions are executed there.
 */

@Service
public class ShopService {
    private final ShoppingCartRepository cartRepository;
    private final ReceiptRepository receiptRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private final EntranceWeight entranceWeight;
    private final ExitWeight exitWeight;
    private final EntranceGate entranceGate;
    private final ExitGate exitGate;

    private User enteringCustomer;
    private User leavingCustomer;

    ShopService(ShoppingCartRepository cartRepository, ReceiptRepository receiptRepository, CartItemRepository cartItemRepository, UserRepository userRepository,
                EntranceWeight entranceWeight, ExitWeight exitWeight, EntranceGate entranceGate, ExitGate exitGate) {
        this.cartRepository = cartRepository;
        this.receiptRepository = receiptRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.entranceWeight = entranceWeight;
        this.exitWeight = exitWeight;
        this.entranceGate = entranceGate;
        this.exitGate = exitGate;
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
            } else
                cartItem.removeOne();
        }

        cartRepository.save(cart);
    }

    @Transactional
    public Receipt finalizeShopping(ShoppingCart cart) {
        Receipt receipt = cart.generateReceipt();
        receipt.setDate( new Date());
        cart.setFinalized(true);
        cartRepository.save(cart);
        return receiptRepository.save(receipt);
    }

    public void onScannedEnteringCustomer(Long userId) {
        User user = userRepository.getById(userId);
        enteringCustomer = user;

        if (user == null)
            throw new NotFoundException("User not found");

        sendNotification(user);
    }

    public void onScannedLeavingCustomer(Long userId) {
        User user = userRepository.getById(userId);
        leavingCustomer = user;

        if (user == null)
            throw new NotFoundException("User not found");
        if(!cartRepository.getByUserId(userId).isPaid())
            throw new ForbiddenException("Must pay first");

        sendNotification(user);
    }

    @Transactional
    public ShoppingCart onCustomerConfirmedEntry(User user) {
        if (enteringCustomer == null || user.getId() != enteringCustomer.getId())
            throw new ForbiddenException("Customer not at entrance");

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);

        int weight = entranceWeight.readWeight();
        cart.setInitialWeight(weight);

        cart.setItems(new HashSet<>());
        cart.setFinalized(false);
        cart = cartRepository.save(cart);

        entranceGate.open();
        return cart;
    }

    @Transactional
    public void onCustomerConfirmedExit(User user) {
        if (leavingCustomer == null || user.getId() != leavingCustomer.getId())
            throw new ForbiddenException("Customer not at exit");

        ShoppingCart cart = cartRepository.getByUserId(user.getId());

        int finalWeight = exitWeight.readWeight();

        if (!validateWeight(cart, finalWeight))
            throw new ForbiddenException("Final weight is incorrect");

        cartRepository.delete(cart);
        exitGate.open();
    }

    @Transactional
    public void handlePayment(ShoppingCart cart){
        if(!cart.isFinalized())
            throw new ForbiddenException("Shopping not finalized");
        if(cart.isPaid())
            throw new ForbiddenException("Shopping already paid");

        cart.setPaid(true);
        cartRepository.save(cart);
    }

    public boolean validateWeight(ShoppingCart cart, int finalWeight) {
        int expectedWeight = cart.getProductsWeight() + cart.getInitialWeight();
        return abs(finalWeight - expectedWeight) < 1000;
    }

    public void sendNotification(User user) {
        //TODO
    }
}
