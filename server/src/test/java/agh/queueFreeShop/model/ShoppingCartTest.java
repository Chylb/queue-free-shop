package agh.queueFreeShop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of ShoppingCart.
 */

public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product p1, p2, p3;
    private CartItem c1, c2;

    @BeforeEach
    public void setup() {
        cart = new ShoppingCart();

        p1 = new Product();
        p1.setBarcode("98765432101");
        p1.setPrice(1);
        p1.setName("One");

        p2 = new Product();
        p2.setBarcode("98765432102");
        p2.setPrice(2);
        p2.setName("Two");

        p3 = new Product();
        p3.setBarcode("00000000000");

        c1 = new CartItem();
        c1.setProduct(p1);
        c1.setQuantity(2);

        c2 = new CartItem();
        c2.setProduct(p2);
        c2.setQuantity(0);

        Set<CartItem> items = new LinkedHashSet<>(Arrays.asList(c1, c2));
        cart.setItems(items);
    }

    @Test
    public void should_get_cart_item_by_product() {
        CartItem item = cart.getCartItem(p1);
        assertThat(item).isNotNull();
        assertThat(item).isEqualTo(c1);
    }

    @Test
    public void shouldnt_get_cart_item_by_product() {
        CartItem item = cart.getCartItem(p3);
        assertThat(item).isNull();
    }

    @Test
    public void generated_receipt_shouldnt_be_null() {
        Receipt receipt = cart.generateReceipt();
        assertThat(receipt).isNotNull();
    }

    @Test
    public void receipt_good_total_amount() {
        Receipt receipt = cart.generateReceipt();
        assertThat(receipt).isNotNull();
        assertThat(receipt.getTotal()).isEqualTo(2);
    }

    @Test
    public void receipt_items_should_have_positive_quantity() {
        Receipt receipt = cart.generateReceipt();
        for (ReceiptItem item: receipt.getItems()){
            assertThat(item.getQuantity()).isGreaterThan(0);
        }
    }
}
