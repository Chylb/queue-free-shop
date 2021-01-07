package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.ReceiptItem;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.service.UserService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test of ReceiptController.
 */

@WebMvcTest(value = ReceiptController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "1")
public class ReceiptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiptRepository receiptRepository;

    @MockBean
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);

    private Receipt receipt;
    private ReceiptItem receiptItem;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        receipt = new Receipt();
        receipt.setId(1);
        receipt.setUser(user);
        receipt.setTotal(10);
        receipt.setDate(new Date());

        receiptItem = new ReceiptItem();
        receiptItem.setPrice(5);
        receiptItem.setQuantity(2);
        receiptItem.setProductName("productName");
        Set<ReceiptItem> receiptItems = Sets.newHashSet(receiptItem);
        receipt.setItems(receiptItems);

        Receipt receipt2 = new Receipt();
        receipt2.setId(2);
        receipt2.setUser(user2);

        given(this.receiptRepository.getById(1L)).willReturn(receipt);
        given(this.receiptRepository.getById(2L)).willReturn(receipt2);
        given(this.receiptRepository.getById(3L)).willReturn(null);
        given(this.receiptRepository.getAllByUserId(1L)).willReturn(Arrays.asList(receipt));
        given(this.receiptRepository.getAllByUserId(2L)).willReturn(new LinkedList<>());
    }

    @Test
    public void should_get_receipt_by_id() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"));
    }

    @Test
    public void should_get_404_when_wrong_receipt_id() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_get_403_when_requested_receipt_doesnt_belong_to_us() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void should_get_all_receipts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("*", hasSize(1)));
    }

    @Test
    public void receipt_json_should_contain_total_price() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(receipt.getTotal()));
    }

    @Test
    public void receipt_json_should_contain_items() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(1)));
    }

    @Test
    public void receipt_json_should_contain_date() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("date").exists());
    }

    @Test
    public void receipt_json_shouldnt_contain_user() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("user").doesNotHaveJsonPath());
    }

    @Test
    public void receipt_json_should_contain_4_fields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    public void receiptItem_json_should_contain_product_name() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").value(receiptItem.getProductName()));
    }

    @Test
    public void receiptItem_json_should_contain_price() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].price").value(receiptItem.getPrice()));
    }

    @Test
    public void receiptItem_json_should_contain_quantity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(receiptItem.getQuantity()));
    }

    @Test
    public void receiptItem_json_shouldnt_contain_receipt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].receipt").doesNotHaveJsonPath());
    }

    @Test
    public void receiptItem_json_should_contain_3_fields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].*", hasSize(3)));
    }
}
