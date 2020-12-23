package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ShopService;
import agh.queueFreeShop.service.UserService;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test of ShoppingCartController.
 */

@WebMvcTest(value = ShoppingCartController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "1")
public class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private ShopService shopService;

    private ShoppingCart cart;

    @BeforeEach
    void setup() {
        cart = new ShoppingCart();

        Product product1 = new Product();
        product1.setPrice(1);
        product1.setName("product1");

        CartItem item1 = new CartItem();
        item1.setQuantity(1);
        item1.setProduct(product1);

        Set<CartItem> items = Sets.newHashSet(item1);
        cart.setItems(items);

        given(this.shoppingCartRepository.getByUserId(1L)).willReturn(cart);
        given(this.shopService.finalizeShopping(cart)).willReturn(cart.generateReceipt());
    }

    @Test
    void should_get_cart() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void should_add_product() throws Exception {
        mockMvc.perform(post("/shoppingCart?barcode=123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void should_remove_product() throws Exception {
        mockMvc.perform(delete("/shoppingCart?barcode=123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void should_finalize_shopping() throws Exception {
        mockMvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(1));
    }

    @Test
    void cart_json_should_contain_items() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void cart_json_shouldnt_contain_user() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("user").doesNotHaveJsonPath());
    }

    @Test
    void cart_json_should_contain_2_fields() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void cartItem_json_should_contain_quantity() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items[0].quantity").value(1));
    }

    @Test
    void cartItem_json_should_contain_product() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items[0].product").isNotEmpty());
    }

    @Test
    void cartItem_json_shouldnt_contain_cart() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items[0].cart").doesNotHaveJsonPath());
    }

    @Test
    void cartItem_json_should_contain_2_fields() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items[0].*", hasSize(2)));
    }
}
