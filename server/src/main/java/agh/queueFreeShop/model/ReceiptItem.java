package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents item on Receipt.
 */

@NoArgsConstructor
@Setter
@Getter
@Entity
public class ReceiptItem {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @ManyToOne
    @JsonIgnore
    private Receipt receipt;

    private int quantity;
    private String productName;
    private int price; //of single product
}
