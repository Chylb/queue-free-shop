package agh.queueFreeShop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {
    @Autowired
    MockMvc mvc;

    @Test
    //@WithMockUser
    public void get_product_by_barcode() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/products?barcode=996379301167"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("barcode").value("996379301167"));
    }

    @Test
    //@WithMockUser
    public void get_product_by_id() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/products?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1));
    }
}
