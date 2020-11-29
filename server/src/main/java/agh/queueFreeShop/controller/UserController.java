package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.UserRepository;
import agh.queueFreeShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUserAccount(@RequestBody User requestUser) {
        User user = userRepository.findByUsername(requestUser.getUsername());
        if (user == null) {
            return ResponseEntity.ok(userService.save(requestUser).getId());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error Message");
    }
}
