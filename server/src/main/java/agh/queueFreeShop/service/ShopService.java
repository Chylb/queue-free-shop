package agh.queueFreeShop.service;

import agh.queueFreeShop.controller.ShoppingCartController;
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
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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

    //getters for testing purposes
    @Getter
    //represents customers at entrance/exit gate. Set only when conditions are met e.g don't set leavingCustomer if hasn't paid yet
    private User enteringCustomer;
    @Getter
    private User leavingCustomer;

    private final SimpMessageSendingOperations messagingTemplate;

    ShopService(ShoppingCartRepository cartRepository, ReceiptRepository receiptRepository, CartItemRepository cartItemRepository, UserRepository userRepository,
                EntranceWeight entranceWeight, ExitWeight exitWeight, EntranceGate entranceGate, ExitGate exitGate,
                SimpMessageSendingOperations messagingTemplate) {
        this.cartRepository = cartRepository;
        this.receiptRepository = receiptRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.entranceWeight = entranceWeight;
        this.exitWeight = exitWeight;
        this.entranceGate = entranceGate;
        this.exitGate = exitGate;
        this.messagingTemplate = messagingTemplate;
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
     * Removes one product from ShoppingCart.
     * If quantity is 0, CartItem is also removed.
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

    /**
     * Finalizes shopping.
     * From now on the only possible action to take is to pay for the shopping ({@link ShoppingCartController#makePayment()}).
     */
    @Transactional
    public Receipt finalizeShopping(ShoppingCart cart) {
        Receipt receipt = cart.generateReceipt();
        receipt.setDate(new Date());
        cart.setFinalized(true);
        cartRepository.save(cart);
        return receiptRepository.save(receipt);
    }

    /**
     * Called when entrance scanner has scanned customer.
     * Sends notification to customer and sets this.enteringCustomer if conditions are met or else throws:
     *
     * @throws NotFoundException  when user doesn't exist
     * @throws ForbiddenException when customer is already inside the shop
     */
    public void onScannedEnteringCustomer(Long userId) {
        enteringCustomer = null;
        sendNotification(userId, "Scanned at entrance");

        User user = userRepository.getById(userId);

        if (user == null)
            throw new NotFoundException("User not found");
        if (cartRepository.getByUserId(userId) != null)
            throw new ForbiddenException("Customer already in shop");

        enteringCustomer = user;
    }

    /**
     * Called when exit scanner has scanned customer.
     * Sends notification to customer and sets this.leavingCustomer if conditions are met or else throws:
     *
     * @throws NotFoundException  when user doesn't exist
     * @throws ForbiddenException when customer is not inside the shop
     * @throws ForbiddenException when customer hasn't paid yet
     */
    public void onScannedLeavingCustomer(Long userId) {
        leavingCustomer = null;
        sendNotification(userId, "Scanned at exit");

        User user = userRepository.getById(userId);

        if (user == null)
            throw new NotFoundException("User not found");
        if (cartRepository.getByUserId(userId) == null)
            throw new ForbiddenException("Customer not in shop");
        if (!cartRepository.getByUserId(userId).isPaid())
            throw new ForbiddenException("Must pay first");

        leavingCustomer = user;
    }

    /**
     * Called by customer in order to enter the shop.
     * Weighs customer, creates shoppingCart and opens the entrance gate if this.enteringCustomer is correct or else throws:
     *
     * @throws ForbiddenException when customer is not at the entrance gate
     */
    @Transactional
    public ShoppingCart onCustomerConfirmedEntry(User user) {
        if (enteringCustomer == null || !user.getId().equals(enteringCustomer.getId()))
            throw new ForbiddenException("Customer not at entrance");

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);

        int weight = entranceWeight.readWeight();
        cart.setInitialWeight(weight);

        cart.setItems(new HashSet<>());
        cart.setFinalized(false);
        cart = cartRepository.save(cart);

        entranceGate.open();
        enteringCustomer = null;
        return cart;
    }

    /**
     * Called by customer in order to leave the shop.
     * Weighs customer and opens the exit gate if this.leavingCustomer is correct or else throws:
     *
     * @throws ForbiddenException when customer is not at the entrance gate
     * @throws ForbiddenException when final weight is incorrect
     */
    @Transactional
    public void onCustomerConfirmedExit(User user) {
        if (leavingCustomer == null || !user.getId().equals(leavingCustomer.getId()))
            throw new ForbiddenException("Customer not at exit");

        ShoppingCart cart = cartRepository.getByUserId(user.getId());

        int finalWeight = exitWeight.readWeight();

        if (!validateWeight(cart, finalWeight))
            throw new ForbiddenException("Final weight is incorrect");

        cartRepository.delete(cart);
        leavingCustomer = null;
        exitGate.open();
    }

    /**
     * Called by customer in order to pay for the shopping.
     *
     * @throws ForbiddenException when shopping is not finalized
     * @throws ForbiddenException when shopping is already paid
     */
    @Transactional
    public void handlePayment(ShoppingCart cart) {
        if (!cart.isFinalized())
            throw new ForbiddenException("Shopping not finalized");
        if (cart.isPaid())
            throw new ForbiddenException("Shopping already paid");

        cart.setPaid(true);
        cartRepository.save(cart);
    }

    /**
     * Validates {@link agh.queueFreeShop.model.ShoppingCart} weight.
     * Returns true if final weight is correct.
     * Returns false if difference between expected and final weight is too big.
     */
    public boolean validateWeight(ShoppingCart cart, int finalWeight) {
        int expectedWeight = cart.getProductsWeight() + cart.getInitialWeight();
        return abs(finalWeight - expectedWeight) < 1000;
    }

    /**
     * Sends notification to user.
     */
    public void sendNotification(Long userId, String notification) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/notification", notification);
    }
}
