package agh.queueFreeShop.repository;

import agh.queueFreeShop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    protected ProductRepository repository;

    @Test
    public void barcode_length_should_be_12(){
        List<Product> products = repository.findAll();
        for(Product p : products) {
            assertThat(p.getBarcode().length()).isEqualTo(12);
        }
    }

    @Test
    public void cost_should_be_positive(){
        List<Product> products = repository.findAll();
        for(Product p : products) {
            assertThat(p.getCost()).isGreaterThan(0);
        }
    }

    @Test
    public void should_find_product_by_barcode(){
        Optional<Product> p = repository.findByBarcode("996379301167");
        assertThat(p.isPresent()).isTrue();
        assertThat(p.get().getBarcode()).isEqualTo("996379301167");
    }

    @Test
    public void should_find_product_by_id(){
        Optional<Product> p = repository.findById(1);
        assertThat(p.isPresent()).isTrue();
        assertThat(p.get().getId()).isEqualTo(1);
    }
}
