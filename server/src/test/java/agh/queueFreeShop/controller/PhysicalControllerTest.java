package agh.queueFreeShop.controller;

import agh.queueFreeShop.physical.scanner.EntranceScanner;
import agh.queueFreeShop.physical.scanner.ExitScanner;
import agh.queueFreeShop.physical.weight.EntranceWeight;
import agh.queueFreeShop.physical.weight.ExitWeight;
import agh.queueFreeShop.service.ShopService;
import agh.queueFreeShop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test of PhysicalController.
 */

@WebMvcTest(value = PhysicalController.class)
@AutoConfigureMockMvc
@WithMockUser(roles = "PHYSICAL_INFRASTRUCTURE")
public class PhysicalControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    private EntranceWeight entranceWeight;
    @MockBean
    private ExitWeight exitWeight;
    @MockBean
    private EntranceScanner entranceScanner;
    @MockBean
    private ExitScanner exitScanner;

    @Test
    void should_read_entrance_weight() throws Exception {
        when(entranceWeight.readWeight()).thenReturn(1);
        mockMvc.perform(MockMvcRequestBuilders.get("/physical/weight/entrance"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void should_update_entrance_weight() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/physical/weight/entrance?weight=3"))
                .andExpect(status().isOk());

        verify(entranceWeight, times(1)).updateReading(3);
    }

    @Test
    void should_read_exit_weight() throws Exception {
        when(exitWeight.readWeight()).thenReturn(1);
        mockMvc.perform(MockMvcRequestBuilders.get("/physical/weight/exit"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void should_update_exit_weight() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/physical/weight/exit?weight=3"))
                .andExpect(status().isOk());

        verify(exitWeight, times(1)).updateReading(3);
    }

    @Test
    void should_send_id_to_entranceScanner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/physical/scanner/entrance?userId=123"))
                .andExpect(status().isOk());

        verify(entranceScanner, times(1)).scan(123L);
    }

    @Test
    void should_send_id_to_exitScanner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/physical/scanner/exit?userId=123"))
                .andExpect(status().isOk());

        verify(exitScanner, times(1)).scan(123L);
    }
}
