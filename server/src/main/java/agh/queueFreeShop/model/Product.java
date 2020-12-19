package agh.queueFreeShop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Representation of available products.
 * Only one product exists with given barcode.
 */

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Product {
    @Id
    @Column(length = 12)
    private String barcode;

    private String name;

    private int price;
}
