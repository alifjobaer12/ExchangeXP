package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/all-users")                                                  //  All User
    public ResponseEntity<?> findAllUsers(){
        List<User> allUsers = userService.findAllUsers();
        return allUsers.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/all-blogs")                                               //  Find all Blogs
    public ResponseEntity<?> findAllBlog(){
        List<Blog> allBlog = blogService.findAllBlog();
        return allBlog.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(allBlog, HttpStatus.OK);
    }
}
