package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Product wrapper that also contains information about quantity of the product in a ShoppingCart.
 * CartItem is persisted as long as ShoppingCart that contains it.
 */

@NoArgsConstructor
@Setter
@Getter
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @ManyToOne
    @JsonIgnore
    private ShoppingCart shoppingCart;

    @ManyToOne
    private Product product;

    private int quantity;

    public void addOne(){
        quantity++;
    }

    public void removeOne(){
        if(quantity > 0)
            quantity--;
    }

    public CartItem(ShoppingCart shoppingCart, Product product, int quantity) {
        this.shoppingCart = shoppingCart;
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Generates ReceiptItem needed to construct receipt.
     */
    public ReceiptItem generateReceiptItem(){
        ReceiptItem receiptItem = new ReceiptItem();
        receiptItem.setProductName(product.getName());
        receiptItem.setPrice(product.getPrice());
        receiptItem.setQuantity(quantity);

        return receiptItem;
    }
}