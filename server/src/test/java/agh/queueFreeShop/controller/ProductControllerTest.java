package agh.queueFreeShop.controller;


import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.repository.ProductRepository;
import agh.queueFreeShop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProductController.class)
//@AutoConfigureMockMvc
@AutoConfigureMockMvc(addFilters = false)
//@ActiveProfiles("test")
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        Product product = new Product();
        product.setName("Product");
        product.setBarcode("123456789012");
        product.setPrice(1);

        given(this.productRepository.findByBarcode("123456789012")).willReturn(product);
        given(this.productRepository.findByBarcode("000000000000")).willReturn(null);
    }

    @Test
    public void should_get_product_by_barcode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("barcode").value("123456789012"));
    }

    @Test
    public void should_get_404_when_wrong_barcode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/000000000000"))
                .andExpect(status().isNotFound());
    }
}
