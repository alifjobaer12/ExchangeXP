package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Auth;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Auth APIs")
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // hide sensitive fields
        user.setPassword("null");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Auth> token(@RequestBody Auth auth) {
        log.info("token {}", auth);
        return new ResponseEntity<>(auth, HttpStatus.OK);
    }
}
