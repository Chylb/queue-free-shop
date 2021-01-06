package agh.queueFreeShop.service;

import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

/**
 * Unit test of UserService.
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setName("Product");
        product.setBarcode("123456789012");
        product.setPrice(1);

        given(this.productRepository.findByBarcode("123456789012")).willReturn(product);
        given(this.productRepository.findByBarcode("000000000000")).willReturn(null);
        given(this.productRepository.findAll()).willReturn(Arrays.asList(product));
    }

    @Test
    public void should_get_product_by_barcode() {
        Product p = productService.getProduct("123456789012");

        assertThat(p).isNotNull();
        assertThat(p.getBarcode()).isEqualTo("123456789012");
    }

    @Test
    public void should_get_all_products() {
        List<Product> products = productService.getAllProducts();

        assertThat(products).isNotNull();
        assertThat(products.size()).isEqualTo(1);
    }

    @Test
    public void should_throw_notFoundException_when_wrong_barcode() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            productService.getProduct("000000000000");
        });
    }

    @Test
    public void thrown_notFoundException_should_have_proper_message() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            productService.getProduct("000000000000");
        });
        assertThat(exception.getMessage()).isEqualTo("Product not found");
    }
}
