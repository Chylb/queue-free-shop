package agh.queueFreeShop.model;

import agh.queueFreeShop.service.ShopService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
}