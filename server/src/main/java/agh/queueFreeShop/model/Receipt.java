package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Represents shopping receipt. It consists of ReceiptItem objects.
 */

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(cascade = {CascadeType.ALL})
    private Set<ReceiptItem> items;

    int total;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date date;
}
