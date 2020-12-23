package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.UserRepository;
import agh.queueFreeShop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test of UserController.
 */

@WebMvcTest(value = UserController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "1")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User user;
    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");

        given(this.userRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(user));
        given(this.userRepository.findByUsername(user.getUsername())).willReturn(user);
        given(this.userRepository.findById(2L)).willReturn(java.util.Optional.empty());

        User someUser = new User();
        someUser.setId(2L);
        given(this.userService.save(any(User.class))).willReturn(someUser);
    }

    @Test
    void should_get_user() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
    }

    @Test
    void should_register() throws Exception {
        String userJson = "{\"username\":\"unique_username\" , \"password\":\"register_password\"}";

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(200));
    }

    @Test
    void should_get_422_when_register_with_not_unique_username() throws Exception {
        String userJson = "{\"username\":\"username\" , \"password\":\"register_password\"}";

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(422));
    }

    @Test
    void user_json_should_contain_username() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(user.getUsername()));
    }

    @Test
    void user_json_should_contain_id() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(user.getId()));
    }

    @Test
    void user_json_shouldnt_contain_password() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("password").doesNotHaveJsonPath());
    }

    @Test
    void user_json_should_contain_2_fields() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }
}
