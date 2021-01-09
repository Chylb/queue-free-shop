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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit test of ShopService.
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ShopServiceUnitTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShoppingCartRepository cartRepository;
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private EntranceWeight entranceWeight;
    @Mock
    private ExitWeight exitWeight;
    @Mock
    private EntranceGate entranceGate;
    @Mock
    private ExitGate exitGate;

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    private User user1;
    private User user2;
    private Product product;
    private CartItem cartItem;
    private ShoppingCart finalizedCart;
    private ShoppingCart paidCart;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);

        product = new Product();
        product.setBarcode("0");

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        finalizedCart = new ShoppingCart();
        finalizedCart.setFinalized(true);

        paidCart = new ShoppingCart();
        paidCart.setItems(new HashSet<>());
        paidCart.setPaid(true);

        given(userRepository.getById(2L)).willReturn(user2);
        given(userRepository.getById(1L)).willReturn(user1);
        given(userRepository.getById(0L)).willReturn(null);

        when(receiptRepository.save(Mockito.any(Receipt.class)))
                .thenAnswer(i -> i.getArguments()[0]);
    }

    //addProductToCart()

    @Test
    void should_add_product_to_cart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());

        shopService.addProductToCart(cart, product);

        assertThat(cart.getCartItem(product)).isNotNull();
        assertThat(cart.getCartItem(product).getQuantity()).isEqualTo(1);
    }

    @Test
    void addProduct_should_increase_quantity_if_already_in_cart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());

        shopService.addProductToCart(cart, product);
        shopService.addProductToCart(cart, product);

        assertThat(cart.getCartItem(product)).isNotNull();
        assertThat(cart.getCartItem(product).getQuantity()).isEqualTo(2);
    }

    //removeProductFromCart

    @Test
    void removeProduct_should_decrease_quantity() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());

        shopService.addProductToCart(cart, product);
        shopService.addProductToCart(cart, product);
        shopService.removeProductFromCart(cart, product);

        assertThat(cart.getCartItem(product)).isNotNull();
        assertThat(cart.getCartItem(product).getQuantity()).isEqualTo(1);
    }

    @Test
    void removeProduct_should_remove_cartItem_when_0_quantity() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());

        shopService.addProductToCart(cart, product);
        shopService.removeProductFromCart(cart, product);

        assertThat(cart.getCartItem(product)).isNull();
    }

    //finalizeShopping()

    @Test
    void should_finalize_shopping() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());

        shopService.finalizeShopping(cart);

        assertThat(cart.isFinalized()).isTrue();
    }

    @Test
    void finalize_shopping_should_return_receipt() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());
        shopService.addProductToCart(cart, product);
        Receipt receipt = shopService.finalizeShopping(cart);

        assertThat(receipt).isNotNull();
    }

    @Test
    void finalize_shopping_receipt_should_have_date() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new LinkedHashSet<>());
        Receipt receipt = shopService.finalizeShopping(cart);

        Date y2021 = new Date(2021, 1, 1);

        assertThat(receipt).isNotNull();
        assertThat(receipt.getDate().after(y2021));
    }

    //onScannedEnteringCustomer()

    @Test
    void onScannedEntering_should_set_enteringCustomer() {
        shopService.onScannedEnteringCustomer(1L);
        assertThat(shopService.getEnteringCustomer()).isNotNull();
        assertThat(shopService.getEnteringCustomer().getId()).isEqualTo(1L);
    }

    @Test
    void onScannedEntering_should_set_enteringCustomer_to_null_when_wrong_id() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            shopService.onScannedEnteringCustomer(0L);
        });
        assertThat(shopService.getEnteringCustomer()).isNull();
    }

    @Test
    void onScannedEntering_should_set_enteringCustomer_to_null_when_already_in_shop() {
        shopService.onScannedEnteringCustomer(1L);
        assertThat(shopService.getEnteringCustomer()).isNotNull();

        when(cartRepository.getByUserId(1L)).thenReturn(new ShoppingCart());
        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.onScannedEnteringCustomer(1L);
        });

        assertThat(shopService.getEnteringCustomer()).isNull();
    }

    //onScannedLeavingCustomer()

    @Test
    void onScannedLeaving_should_set_leavingCustomer() {
        when(cartRepository.getByUserId(1L)).thenReturn(paidCart);

        shopService.onScannedLeavingCustomer(1L);
        assertThat(shopService.getLeavingCustomer()).isNotNull();
        assertThat(shopService.getLeavingCustomer().getId()).isEqualTo(1L);
    }

    @Test
    void onScannedLeaving_should_set_leavingCustomer_to_null_when_wrong_id() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            shopService.onScannedLeavingCustomer(0L);
        });

        assertThat(shopService.getLeavingCustomer()).isNull();
    }

    @Test
    void onScannedLeaving_should_set_leavingCustomer_to_null_when_not_in_shop() {
        when(cartRepository.getByUserId(1L)).thenReturn(null);

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.onScannedLeavingCustomer(1L);
        });

        assertThat(shopService.getLeavingCustomer()).isNull();
    }

    @Test
    void onScannedLeaving_should_set_leavingCustomer_to_null_when_not_paid() {
        when(cartRepository.getByUserId(1L)).thenReturn(finalizedCart);

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.onScannedLeavingCustomer(1L);
        });

        assertThat(shopService.getLeavingCustomer()).isNull();
    }

    //onCustomerConfirmedEntry()

    @Test
    void should_confirm_entry() {
        when(cartRepository.getByUserId(1L)).thenReturn(null);

        shopService.onScannedEnteringCustomer(1L);
        shopService.onCustomerConfirmedEntry(user1);

        verify(entranceGate, times(1)).open();
    }

    @Test
    void shouldnt_confirm_entry_when_enteringCustomer_is_different() {
        shopService.onScannedEnteringCustomer(2L);

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.onCustomerConfirmedEntry(user1);
        });

        verify(entranceGate, times(0)).open();
    }

    //onCustomerConfirmedExit

    @Test
    void should_confirm_exit() {
        when(cartRepository.getByUserId(1L)).thenReturn(paidCart);
        shopService.onScannedLeavingCustomer(1L);

        shopService.onCustomerConfirmedExit(user1);

        verify(exitGate, times(1)).open();
    }

    @Test
    void shouldnt_confirm_exit_when_leavingCustomer_is_different() {
        when(cartRepository.getByUserId(2L)).thenReturn(paidCart);
        shopService.onScannedLeavingCustomer(2L);

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.onCustomerConfirmedExit(user1);
        });

        verify(exitGate, times(0)).open();
    }

    //handlePayment()

    @Test
    void should_handle_payment() {
        ShoppingCart cart = new ShoppingCart();
        cart.setFinalized(true);
        shopService.handlePayment(cart);

        assertThat(cart.isPaid()).isTrue();
    }

    @Test
    void shouldnt_handle_payment_when_not_finalized() {
        ShoppingCart cart = new ShoppingCart();

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.handlePayment(cart);
        });

        assertThat(cart.isPaid()).isFalse();
    }

    @Test
    void shouldnt_handle_payment_when_already_paid() {
        ShoppingCart cart = new ShoppingCart();
        cart.setPaid(true);

        Exception exception = assertThrows(ForbiddenException.class, () -> {
            shopService.handlePayment(cart);
        });
    }

    //validateWeight()

    @Test
    void should_validate_weight() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashSet<>());
        cart.setInitialWeight(0);

        shopService.validateWeight(cart, 999);
    }

    @Test
    void should_validate_weight2() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashSet<>());
        cart.setInitialWeight(1000);

        shopService.validateWeight(cart, 1999);
    }

    @Test
    void should_validate_weight3() {
        ShoppingCart cart = mock(ShoppingCart.class);
        when(cart.getProductsWeight()).thenReturn(1000);
        cart.setInitialWeight(0);

        shopService.validateWeight(cart, 1999);
    }

    @Test
    void shouldnt_validate_weight() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashSet<>());
        cart.setInitialWeight(0);

        shopService.validateWeight(cart, 1000);
    }

    @Test
    void shouldnt_validate_weight2() {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashSet<>());
        cart.setInitialWeight(1000);

        shopService.validateWeight(cart, 2000);
    }

    @Test
    void shouldnt_validate_weight3() {
        ShoppingCart cart = mock(ShoppingCart.class);
        when(cart.getProductsWeight()).thenReturn(1000);
        cart.setInitialWeight(0);

        shopService.validateWeight(cart, 2000);
    }
}
