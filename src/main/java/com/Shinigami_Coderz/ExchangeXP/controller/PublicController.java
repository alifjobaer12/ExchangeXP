package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")                                                  //  Health Check
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/create-user")                                                       //  Create a User
    public ResponseEntity<?> createUser(@RequestBody User user){
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getEmail().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return userService.saveNewUser(user) ? new ResponseEntity<>(user, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
