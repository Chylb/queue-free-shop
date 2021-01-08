package agh.queueFreeShop.service;

import agh.queueFreeShop.model.CartItem;
import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.physical.ExitWeight;
import agh.queueFreeShop.repository.CartItemRepository;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.repository.ShoppingCartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test of ShopService.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "1")
public class ShopServiceTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private ShopService shopService;

    @Autowired
    private ExitWeight exitWeight;

    private String barcode1 = "0123456789011";

    @Test
    void should_enter_shop() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
    }

    @Test
    void should_not_enter_shop_when_scanned_someone_else() throws Exception {
        try {
            shopService.onScannedEnteringCustomer(999L);
        } catch (Exception ignored) {
        }
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cart_should_be_empty() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(0)));
    }

    @Test
    void should_add_product_to_cart() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].product.barcode").value(barcode1));
    }

    @Test
    void should_receive_404_when_adding_nonexistent_product_to_cart() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=0000"))
                .andExpect(status().is(404));
    }

    @Test
    void added_product_should_have_1_quantity() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(1));
    }

    @Test
    void cartItem_quantity_should_be_1_greater() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(1));
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(2));
    }

    @Test
    void should_remove_product() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(delete("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
    }

    @Test
    void should_receive_404_when_removing_nonexistent_product_from_cart() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(delete("/shoppingCart?barcode=0000"))
                .andExpect(status().is(404));
    }

    @Test
    void cartItem_quantity_should_be_1_lower() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(1));
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(2));
        mvc.perform(delete("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(1));
    }

    @Test
    void cartItem_should_be_deleted_when_0_quantity() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items[0].quantity").value(1));
        mvc.perform(delete("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200))
                .andExpect(jsonPath("items", hasSize(0)));
    }

    @Test
    void should_finalize_shopping() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
    }

    @Test
    void cart_should_be_finalized_after_finalization() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));

        ShoppingCart cart = shoppingCartRepository.getByUserId(1L);
        assertThat(cart.isFinalized()).isTrue();
    }

    @Test
    void should_make_payment() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/pay"))
                .andExpect(status().is(200));

        ShoppingCart cart = shoppingCartRepository.getByUserId(1L);
        assertThat(cart.isPaid()).isTrue();
    }

    @Test
    void should_let_exit() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/pay"))
                .andExpect(status().is(200));
        shopService.onScannedLeavingCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isOk());
    }

    @Test
    void should_not_let_exit_when_scanned_someone_else() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        try {
            shopService.onScannedLeavingCustomer(999L);
        } catch (Exception ignored) {
        }
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_not_let_exit_when_wrong_weight() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        exitWeight.updateReading(10000);
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isForbidden());

        exitWeight.updateReading(0);
    }

    @Test
    void should_not_let_exit_when_not_paid() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        exitWeight.updateReading(10000);
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isForbidden());

        exitWeight.updateReading(0);
    }

    @Test
    public void shopping_cart_should_be_deleted_after_exit() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/pay"))
                .andExpect(status().is(200));
        shopService.onScannedLeavingCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isOk());

        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        assertThat(carts).isNotNull();
        assertThat(carts.size()).isEqualTo(0);
    }

    @Test
    public void cart_items_should_be_deleted_after_exit() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/pay"))
                .andExpect(status().is(200));
        shopService.onScannedLeavingCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmExit"))
                .andExpect(status().isOk());

        List<CartItem> items = cartItemRepository.findAll();
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(0);
    }

    @Test
    public void receipt_should_be_persisted_after_finalization() throws Exception {
        shopService.onScannedEnteringCustomer(1L);
        mvc.perform(post("/shoppingCart/confirmEntry"))
                .andExpect(status().isOk());
        mvc.perform(post("/shoppingCart?barcode=" + barcode1))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/finalize"))
                .andExpect(status().is(200));
        mvc.perform(post("/shoppingCart/pay"))
                .andExpect(status().is(200));

        List<Receipt> receipts = receiptRepository.findAll();
        assertThat(receipts).isNotNull();
        assertThat(receipts.size()).isEqualTo(1);
    }
}
