package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents content of a shopping cart.
 */

@NoArgsConstructor
@Setter
@Getter
@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne
    @JsonIgnore
    private User user;

    @OneToMany(cascade = {CascadeType.ALL})
    private Set<CartItem> items;

    /**
     * Returns CartItem of given product.
     * Returns null if there is no such a product in ShoppingCart.
     */
    public CartItem getCartItem(Product product) {
        for (CartItem item : items) {
            if (item.getProduct().getBarcode().equals(product.getBarcode()))
                return item;
        }
        return null;
    }

    public Receipt generateReceipt() {
        Receipt receipt = new Receipt();
        Set<ReceiptItem> receiptItems = new LinkedHashSet<>();

        int total = 0;

        for (CartItem cartItem : items) {
            if (cartItem.getQuantity() == 0) continue;

            ReceiptItem receiptItem = cartItem.generateReceiptItem();
            receiptItem.setReceipt(receipt);
            receiptItems.add(receiptItem);

            total += receiptItem.getPrice() * receiptItem.getQuantity();
        }

        receipt.setTotal(total);
        receipt.setItems(receiptItems);
        receipt.setUser(user);
        return receipt;
    }
}
