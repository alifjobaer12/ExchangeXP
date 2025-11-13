package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.dto.UserReqDto;
import com.Shinigami_Coderz.ExchangeXP.dto.UserResDto;
import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/all-users")                                                  //  All User
    public ResponseEntity<?> findAllUsers(){

        long start = System.currentTimeMillis();
        log.info("AdminController.findAllUsers: Received request to fetch all users.");

        try {
            List<User> allUsers = userService.findAllUsers();
            if (allUsers == null || allUsers.isEmpty()){
                log.warn("AdminController.findAllUsers: No users found in the database.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("AdminController.findAllUsers: Found {} users in the database. (elapsed={}ms)", allUsers.size(), System.currentTimeMillis() - start);
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("AdminController.findAllUsers: Exception while fetching users. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-blogs")                                               //  Find all Blogs
    public ResponseEntity<?> findAllBlog(){

        long start = System.currentTimeMillis();
        log.info("AdminController.findAllBlog: Received request to fetch all blogs.");

        try {
            List<Blog> allBlog = blogService.findAllBlog();
            if (allBlog == null || allBlog.isEmpty()){
                log.warn("AdminController.findAllBlog: No blogs found in the database.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("AdminController.findAllBlog: Found {} blogs in the database. (elapsed={}ms)", allBlog.size(), System.currentTimeMillis() - start);
            return new ResponseEntity<>(allBlog, HttpStatus.OK);
        } catch (Exception e) {
            log.error("AdminController.findAllBlog: Exception while fetching blogs. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-admin")                                                       //  Create a AdminUser
    public ResponseEntity<?> createUser(@RequestBody UserReqDto request){

        long start = System.currentTimeMillis();
        log.info("AdminController.createUser: Received request to create a new admin user. payloadUsername={}", request == null ? "null" : request.getUsername());

        if (request == null) {
            log.warn("AdminController.createUser: Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String email = request.getEmail().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
            log.warn("AdminController.createUser: Missing required user fields for user: {}", username);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            // ADDED: check duplicate username (defensive)
            User existing = userService.findUserByUsername(username);
            if (existing != null) {
                log.warn("AdminController.createUser: Username '{}' already exists. Rejecting creation.", username);
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // DTO -> Entity
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            User saved = userService.saveAdminUser(user);
            if(saved == null){
                log.error("AdminController.createUser: Failed to create admin user: {}", username);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Entity -> DTO
            UserResDto response = new UserResDto(
                    saved.getUserId() != null ? saved.getUserId().toString() : null,
                    saved.getUsername(),
                    saved.getEmail()
            );

            log.info("AdminController.createUser: Successfully created admin user: {} (elapsed={}ms)", username, System.currentTimeMillis() - start); // ADDED
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("AdminController.createUser: Exception while creating admin user '{}'. error={}", username, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
