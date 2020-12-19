package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.UserRepository;
import agh.queueFreeShop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUserAccount(@RequestBody User requestUser) {
        User user = userRepository.findByUsername(requestUser.getUsername());
        if (user == null) {
            return ResponseEntity.ok(userService.save(requestUser).getId());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error Message");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Principal principal) {
        return ResponseEntity.ok(userRepository.findById(Long.parseLong(principal.getName())));
    }
}
