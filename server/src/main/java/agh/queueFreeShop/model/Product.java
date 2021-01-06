package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(length = 13)
    private String barcode;

    private String name;

    private int price;

    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int weight;
}
