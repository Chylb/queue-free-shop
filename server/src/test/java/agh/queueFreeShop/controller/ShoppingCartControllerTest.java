package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.NotFoundException;
import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import agh.queueFreeShop.service.ProductService;
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

import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test of ShoppingCartController.
 *
 * users:
 * 1 - shopping customer
 * 2 - user outside the shop
 * 3 - customer that already finalized shopping
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
    private ProductService productService;

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private ShopService shopService;

    private ShoppingCart cart;
    private User user = new User();

    @BeforeEach
    void setup() {
        user.setId(1L);

        cart = new ShoppingCart();

        Product product1 = new Product();
        product1.setPrice(1);
        product1.setName("product1");

        CartItem item1 = new CartItem();
        item1.setQuantity(1);
        item1.setProduct(product1);

        Set<CartItem> items = Sets.newHashSet(item1);
        cart.setItems(items);

        ShoppingCart finalizedCart = new ShoppingCart();
        finalizedCart.setFinalized(true);

        ShoppingCart emptyCart = new ShoppingCart();
        emptyCart.setItems(new LinkedHashSet<>());

        given(this.productService.getProduct("0123456789012")).willReturn(new Product());
        given(this.productService.getProduct("000")).willThrow(new NotFoundException("msg"));
        given(this.shoppingCartRepository.getByUserId(1L)).willReturn(cart);
        given(this.shoppingCartRepository.getByUserId(2L)).willReturn(null);
        given(this.shoppingCartRepository.getByUserId(3L)).willReturn(finalizedCart);
        given(this.shopService.finalizeShopping(cart)).willReturn(cart.generateReceipt());
        given(this.shopService.onCustomerConfirmedEntry(any())).willReturn(emptyCart);
    }

    //getCart()

    @Test
    void should_get_cart() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "2")
    void should_receive_403_when_getCart_while_not_in_shop() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "3")
    void should_receive_403_when_getCart_and_cart_is_finalized() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isForbidden());
    }

    //addProduct()

    @Test
    void should_add_product() throws Exception {
        mockMvc.perform(post("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void should_receive_404_when_adding_nonexistent_product() throws Exception {
        mockMvc.perform(post("/shoppingCart?barcode=000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "2")
    void should_receive_403_when_addProduct_while_not_in_shop() throws Exception {
        mockMvc.perform(post("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "3")
    void should_receive_403_when_addProduct_and_cart_is_finalized() throws Exception {
        mockMvc.perform(post("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isForbidden());
    }

    //removeProduct()

    @Test
    void should_remove_product() throws Exception {
        mockMvc.perform(delete("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    void should_receive_404_when_removing_nonexistent_product() throws Exception {
        mockMvc.perform(delete("/shoppingCart?barcode=000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "2")
    void should_receive_403_when_removeProduct_while_not_in_shop() throws Exception {
        mockMvc.perform(delete("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "3")
    void should_receive_403_when_removeProduct_and_cart_is_finalized() throws Exception {
        mockMvc.perform(delete("/shoppingCart?barcode=0123456789012"))
                .andExpect(status().isForbidden());
    }

    //finalizeShopping()

    @Test
    void should_finalize_shopping() throws Exception {
        mockMvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(1));
    }

    @Test
    @WithMockUser(username = "2")
    void should_receive_403_when_finalizeShopping_while_not_in_shop() throws Exception {
        mockMvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "3")
    void should_receive_403_when_finalizeShopping_and_cart_is_finalized() throws Exception {
        mockMvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().isForbidden());
    }

    //confirmEntry()

    @Test
    void should_confirm_entry() throws Exception {
        mockMvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(0)));
    }

    //confirmExit()

    @Test
    void should_confirm_exit() throws Exception {
        mockMvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    //JSON

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
    void cart_json_shouldnt_contain_initialWeight() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("initialWeight").doesNotHaveJsonPath());
    }

    @Test
    void cart_json_shouldnt_contain_finalized() throws Exception {
        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("finalized").doesNotHaveJsonPath());
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
