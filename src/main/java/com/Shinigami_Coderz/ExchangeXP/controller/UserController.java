package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.dto.UserReqDto;
import com.Shinigami_Coderz.ExchangeXP.dto.UserResDto;
import com.Shinigami_Coderz.ExchangeXP.dto.UserUpdateProfileDto;
import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "User APIs")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        long start = System.currentTimeMillis();
        log.info("UserController.findUser: Received update request.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("UserController.findUser: Unauthenticated request for username={}", username);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String requester = authentication.getName();
        if (!requester.equals(username)) {
            log.warn("UserController.findUser: Forbidden - requester='{}' cannot find username='{}'", requester, username);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User userByUsername = userService.findUserByUsername(username);
        if(userByUsername == null){
            log.warn("UserController.findUser: User not found username={}", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("UserController.findUser: Find for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
        return new ResponseEntity<>(userByUsername, HttpStatus.OK);
    }

    @PostMapping("/update/password")                                                  //  Update User Password
    public ResponseEntity<?> updateUser(@RequestBody(required = false) UserReqDto request) {
        long start = System.currentTimeMillis();
        log.info("UserController.updateUser: Received update request.");

        try {
            if (request == null) {
                log.warn("UserController.updateUser: Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String username = request.getUsername().trim();
            String newPassword = request.getPassword().trim();

            if (username.isEmpty() || newPassword.isEmpty()) {
                log.warn("UserController.updateUser: Missing username or password. username='{}'", username);
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            // Authentication & authorization: only allow the user themselves to update password
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("UserController.updateUser: Unauthenticated request for username={}", username);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String requester = authentication.getName();
            if (!requester.equals(username)) {
                log.warn("UserController.updateUser: Forbidden - requester='{}' cannot update username='{}'", requester, username);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            User userByUsername = userService.findUserByUsername(username);
            if(userByUsername == null){
                log.warn("UserController.updateUser: User not found username={}", username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            userByUsername.setPassword(newPassword);
            User saved = userService.saveUser(userByUsername);

            // Entity -> DTO
            UserResDto response = new UserResDto(
                    saved.getUserId() != null ? saved.getUserId().toString() : null,
                    saved.getUsername(),
                    saved.getEmail()
            );

            log.info("UserController.updateUser: Password updated for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.updateUser: Exception while updating user. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")                                                  //  Delete User
    public ResponseEntity<?> deleteUser(@RequestBody(required = false) UserReqDto request){
        long start = System.currentTimeMillis();
        log.info("UserController.deleteUser: Received delete request.");

        try {
            if (request == null) {
                log.warn("UserController.deleteUser: Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String username = request.getUsername().trim();
            if (username.isEmpty()) {
                log.warn("UserController.deleteUser: Missing username in request.");
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            // Authentication & authorization: only allow the user themselves to delete account
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("UserController.deleteUser: Unauthenticated delete attempt for username={}", username);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String requester = authentication.getName();
            if (!requester.equals(username)) {
                log.warn("UserController.deleteUser: Forbidden - requester='{}' cannot delete username='{}'", requester, username);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            User userByUsername = userService.findUserByUsername(username);
            if(userByUsername == null){
                log.warn("UserController.deleteUser: User not found username={}", username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            boolean b = blogService.deleteAllBlogAndUser(userByUsername);
            if (!b) {
                log.error("UserController.deleteUser: Failed to delete all blogs for username={}", username);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Entity -> DTO
            UserResDto response = new UserResDto(
                    userByUsername.getUserId() != null ? userByUsername.getUserId().toString() : null,
                    userByUsername.getUsername(),
                    userByUsername.getEmail()
            );

            log.info("UserController.deleteUser: Successfully deleted blogs for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.deleteUser: Exception while deleting user. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-blogs")                                               //  Find all Blogs
    public ResponseEntity<?> findAllBlog(){

        long start = System.currentTimeMillis();
        log.info("UserController.findAllBlog: Received request to fetch all blogs.");

        try {
            List<Blog> allBlog = blogService.findAllBlog();
            if (allBlog == null || allBlog.isEmpty()){
                log.warn("UserController.findAllBlog: No blogs found in the database.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("UserController.findAllBlog: Found {} blogs in the database. (elapsed={}ms)", allBlog.size(), System.currentTimeMillis() - start);
            return new ResponseEntity<>(allBlog, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.findAllBlog: Exception while fetching blogs. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update/profile")                                                  //  Update User Profile
    public ResponseEntity<?> updateUserProfile(@RequestBody(required = false) UserUpdateProfileDto request) {
        long start = System.currentTimeMillis();
        log.info("UserController.updateUserProfile: Received update request.");

        try {
            if (request == null) {
                log.warn("UserController.updateUserProfile: Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String username = request.getUsername().trim();

            // Authentication & authorization: only allow the user themselves to update password
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("UserController.updateUserProfile: Unauthenticated request for username={}", username);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String requester = authentication.getName();
            if (!requester.equals(username)) {
                log.warn("UserController.updateUserProfile: Forbidden - requester='{}' cannot update username='{}'", requester, username);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            User userByUsername = userService.findUserByUsername(username);
            if(userByUsername == null){
                log.warn("UserController.updateUserProfile: User not found username={}", username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            userByUsername.setAddress(request.getAddress());
            userByUsername.setPhoneNumber(request.getPhoneNumber());
            userByUsername.setUserPhotoUrl(request.getUserPhotoUrl());

            User user = userService.saveUser(userByUsername);

            // Entity -> DTO
            UserUpdateProfileDto response = new UserUpdateProfileDto(
                    user.getUserId() != null ? user.getUserId().toString() : null,
                    user.getUsername(),
                    user.getUserPhotoUrl(),
                    user.getPhoneNumber(),
                    user.getAddress()
            );

            log.info("UserController.updateUserProfile: Password updated for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.updateUserProfile: Exception while updating user. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
