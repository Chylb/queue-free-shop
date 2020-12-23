package agh.queueFreeShop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
