package agh.queueFreeShop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of CartItem.
 */

public class CartItemTest {

    private Product product;
    private CartItem cartItem;

    @BeforeEach
    public void setup() {
        product = new Product();
        product.setName("name");
        product.setPrice(5);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(3);
    }

    @Test
    public void addOne_should_increase_quantity() {
        int q0 = cartItem.getQuantity();
        cartItem.addOne();
        assertThat(cartItem.getQuantity()).isEqualTo(q0 + 1);
    }

    @Test
    public void removeOne_should_decrease_quantity() {
        int q0 = cartItem.getQuantity();
        cartItem.removeOne();
        assertThat(cartItem.getQuantity()).isEqualTo(q0 - 1);
    }

    @Test
    public void removeOne_shouldnt_decrease_quantity_when_already_0() {
        CartItem cartItem0 = new CartItem();
        cartItem0.setQuantity(0);
        cartItem0.removeOne();
        assertThat(cartItem0.getQuantity()).isEqualTo(0);
    }

    @Test
    public void should_generate_ReceiptItem() {
        ReceiptItem receiptItem = cartItem.generateReceiptItem();
        assertThat(receiptItem).isNotNull();
    }

    @Test
    public void generated_receiptItem_productName_should_be_equal_to_product_name() {
        ReceiptItem receiptItem = cartItem.generateReceiptItem();
        assertThat(receiptItem).isNotNull();
        assertThat(receiptItem.getProductName().equals(product.getName()));
    }

    @Test
    public void generated_receiptItem_price_should_be_equal_to_product_price() {
        ReceiptItem receiptItem = cartItem.generateReceiptItem();
        assertThat(receiptItem).isNotNull();
        assertThat(receiptItem.getPrice() == product.getPrice());
    }

    @Test
    public void generated_receiptItem_quantity_should_be_equal_to_cartItem_quantity() {
        ReceiptItem receiptItem = cartItem.generateReceiptItem();
        assertThat(receiptItem).isNotNull();
        assertThat(receiptItem.getQuantity() == cartItem.getQuantity());
    }
}
