package agh.queueFreeShop;

import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Product;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.repository.CartItemRepository;
import agh.queueFreeShop.repository.ProductRepository;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@WithMockUser(username = "1")
public class IntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Product product1, product2;
    static private long receiptId;

    @BeforeEach
    public void setup() {
        product1 = productRepository.findByBarcode("123456789011");
        product2 = productRepository.findByBarcode("123456789012");
    }

    @Test
    @Order(0)
    public void should_create_shopping_cart() throws Exception {
        mvc.perform(post("/shoppingCart/enter"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    public void should_get_product_by_barcode() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/products/123456789011"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("barcode").value("123456789011"))
                .andExpect(jsonPath("name").value("one"));
    }

    @Test
    @Order(2)
    public void should_add_product_to_cart() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/shoppingCart?barcode=123456789011"))
                .andExpect(status().is(200))
                .andReturn();

        ShoppingCart cart = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ShoppingCart.class);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isNotNull();

        CartItem item = cart.getCartItem(product1);
        assertThat(item).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void should_add_product_to_cart_twice() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/shoppingCart?barcode=123456789011"))
                .andExpect(status().is(200))
                .andReturn();

        ShoppingCart cart = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ShoppingCart.class);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isNotNull();

        CartItem item = cart.getCartItem(product1);
        assertThat(item).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    @Order(4)
    public void should_remove_product_from_cart() throws Exception {
        mvc.perform(post("/shoppingCart?barcode=123456789012"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart?barcode=123456789012"))
                .andExpect(status().is(200));

        MvcResult mvcResult = mvc.perform(delete("/shoppingCart?barcode=123456789012"))
                .andExpect(status().is(200))
                .andReturn();

        ShoppingCart cart = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ShoppingCart.class);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isNotNull();

        CartItem item = cart.getCartItem(product2);
        assertThat(item).isNotNull();
        assertThat(item.getQuantity()).isEqualTo(1);
    }

    @Test
    @Order(5)
    public void should_remove_cart_item_from_cart_when_quantity_0() throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/shoppingCart?barcode=123456789012"))
                .andExpect(status().is(200))
                .andReturn();

        ShoppingCart cart = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ShoppingCart.class);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isNotNull();

        CartItem item = cart.getCartItem(product2);
        assertThat(item).isNull();
    }

    @Test
    @Order(6)
    public void should_finalize_shopping() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200))
                .andReturn();

        Receipt receipt = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Receipt.class);
        assertThat(receipt).isNotNull();
        assertThat(receipt.getItems()).isNotNull();
        assertThat(receipt.getItems().size()).isEqualTo(1);
        assertThat(receipt.getTotal()).isEqualTo(2);

        receiptId = receipt.getId();
    }

    @Test
    @Order(7)
    public void shopping_cart_should_be_deleted_after_finalization() {
        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        assertThat(carts).isNotNull();
        assertThat(carts.size()).isEqualTo(0);
    }

    @Test
    @Order(8)
    public void cart_items_should_be_deleted_after_finalization() {
        List<CartItem> items = cartItemRepository.findAll();
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(0);
    }

    @Test
    @Order(9)
    public void should_get_receipt() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/receipts/" + receiptId))
                .andExpect(status().is(200))
                .andReturn();

        Receipt receipt = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Receipt.class);
        assertThat(receipt).isNotNull();
        assertThat(receipt.getItems()).isNotNull();
        assertThat(receipt.getItems().size()).isEqualTo(1);
        assertThat(receipt.getTotal()).isEqualTo(2);
    }
}
