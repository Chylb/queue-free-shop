package agh.queueFreeShop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    long id;

    @ManyToOne
    private User user;

    int total;

    @ElementCollection
    private List<String> productsNames;

    @ElementCollection
    private List<Integer> productsPrices;

    @ElementCollection
    private List<Integer> productsQuantities;
}
