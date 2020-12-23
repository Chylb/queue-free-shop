package agh.queueFreeShop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test of UserService.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceTest {
    @Autowired
    private MockMvc mvc;

    String userJson = "{\"username\":\"register_test\" , \"password\":\"register_password\"}";

    @Test
    void should_register_user() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(200));
    }

    @Test
    void shouldnt_register_user_when_username_already_occupied() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(200));

        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(422));
    }

    @Test
    void should_login() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(200));

        mvc.perform(post("/login")
                .param("username", "register_test")
                .param("password", "register_password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().is(200));
    }

    @Test
    void shouldnt_login_when_bad_password() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().is(200));

        mvc.perform(post("/login")
                .param("username", "register_test")
                .param("password", "bad_password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().is(401));
    }

    @Test
    @WithMockUser(username = "1")
    void should_get_user() throws Exception {
        mvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("password").doesNotHaveJsonPath());
    }

    @Test
    void should_get_401_when_not_authenticated() throws Exception {
        mvc.perform(get("/user")
        ).andExpect(status().isUnauthorized());
    }
}
