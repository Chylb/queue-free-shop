package agh.queueFreeShop.controller;


import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.service.ProductService;
import agh.queueFreeShop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test of ProductController.
 */

@WebMvcTest(value = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setName("Product");
        product.setBarcode("123456789012");
        product.setPrice(1);
        product.setImageUrl("url");

        given(this.productService.getProduct("123456789012")).willReturn(product);
        given(this.productService.getProduct("000000000000")).willThrow(new NotFoundException("msg"));
        given(this.productService.getAllProducts()).willReturn(Arrays.asList(product));
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

    @Test
    public void should_get_all_products() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void product_json_should_contain_barcode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("barcode").value(product.getBarcode()));
    }

    @Test
    public void product_json_should_contain_product_name() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(product.getName()));
    }

    @Test
    public void product_json_should_contain_price() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("price").value(product.getPrice()));
    }

    @Test
    public void product_json_should_contain_image_url() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("imageUrl").value(product.getImageUrl()));
    }

    @Test
    public void product_json_should_contain_4_fields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }
}
