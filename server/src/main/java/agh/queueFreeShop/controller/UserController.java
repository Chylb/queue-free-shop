package agh.queueFreeShop.controller;

import agh.queueFreeShop.model.ShoppingCart;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.UserRepository;
import agh.queueFreeShop.service.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = User.class)})
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userRepository.findById(getUserId()));
    }

    private Long getUserId(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
