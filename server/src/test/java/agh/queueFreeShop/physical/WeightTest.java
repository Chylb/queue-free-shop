package agh.queueFreeShop.physical;

import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.physical.weight.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of weight.
 */

public class WeightTest {
    private Weight weight;

    @BeforeEach
    public void setup() {
        weight = new Weight();
    }

    @Test
    public void should_read_weight() {
        int w = weight.readWeight();
        assertThat(w).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void should_update_weight() {
        weight.updateReading(10);
        assertThat(weight.readWeight()).isEqualTo(10);
    }
}
