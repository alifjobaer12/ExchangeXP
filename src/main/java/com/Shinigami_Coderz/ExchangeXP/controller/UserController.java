package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        User userByUsername = userService.findUserByUsername(user.getUsername());
        if(userByUsername == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userByUsername.setPassword(user.getPassword());
        userService.saveUser(userByUsername);
        return new ResponseEntity<>(userByUsername, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody User user){
        User userByUsername = userService.findUserByUsername(user.getUsername());
        if(userByUsername == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean b = blogService.deleteAllBlogFromUser(userByUsername);
        return b ? new ResponseEntity<>(userByUsername, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
