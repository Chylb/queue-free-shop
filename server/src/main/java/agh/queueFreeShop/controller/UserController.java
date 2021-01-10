package agh.queueFreeShop.controller;

import agh.queueFreeShop.exception.UnprocessableEntityException;
import agh.queueFreeShop.model.User;
import agh.queueFreeShop.repository.UserRepository;
import agh.queueFreeShop.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Responsible for user registration and user account management.
 */

@Controller
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register user", notes = "Username between 4-64 characters. Password between 8-64 characters.")
    @ApiResponses({@ApiResponse(code = 422, message = "Username already exists"),
            @ApiResponse(code = 400, message = "User not valid")})
    public ResponseEntity<?> registerUserAccount(@Valid @RequestBody User requestUser) {
        User user = userRepository.findByUsername(requestUser.getUsername());
        if (user != null)
            throw new UnprocessableEntityException("Username already exists");

        return ResponseEntity.ok(userService.save(requestUser).getId());
    }

    @GetMapping("/user")
    @ApiOperation(value = "Get user")
    @ApiResponse(code = 401, message = "Unauthorized")
    public User getUser() {
        return userRepository.getById(getUserId());
    }

    private Long getUserId() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(user.getUsername());
    }
}
