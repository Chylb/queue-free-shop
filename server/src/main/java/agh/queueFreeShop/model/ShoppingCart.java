package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    public CartItem getCartItem(Product product) {
        for (CartItem item : items) {
            if (item.getProduct() == product)
                return item;
        }
        return null;
    }

    public Receipt generateReceipt() {
        int total = 0;
        List<String> productsNames = new LinkedList<>();
        List<Integer> productsPrices = new LinkedList<>();
        List<Integer> productsQuantities = new LinkedList<>();

        for (CartItem item : items) {
            if (item.getQuantity() == 0) continue;

            productsNames.add(item.getProduct().getName());
            productsPrices.add(item.getProduct().getPrice());
            productsQuantities.add(item.getQuantity());
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        Receipt receipt = new Receipt();
        receipt.setTotal(total);
        receipt.setProductsNames(productsNames);
        receipt.setProductsPrices(productsPrices);
        receipt.setProductsQuantities(productsQuantities);
        receipt.setUser(user);
        return receipt;
    }
}
