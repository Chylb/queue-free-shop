package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.Receipt;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.ReceiptRepository;
import agh.queueFreeShop.service.UserService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setUser(user);

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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/receipts"))
                .andExpect(status().isOk())
                .andReturn();

        Receipt[] receipts = mapper.readValue(mvcResult.getResponse().getContentAsString(), Receipt[].class);

        assertThat(receipts.length).isEqualTo(1);
        for(Receipt receipt : receipts) {
            assertThat(receipt.getUser().getId()).isEqualTo(1);
        }
    }
}
