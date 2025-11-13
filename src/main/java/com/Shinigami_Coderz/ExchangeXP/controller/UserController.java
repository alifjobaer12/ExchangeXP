package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
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

    @PostMapping("/update")                                                  //  Update User Password
    public ResponseEntity<?> updateUser(@RequestBody(required = false) User user){
        long start = System.currentTimeMillis();
        log.info("UserController.updateUser: Received update request.");

        try {
            if (user == null) {
                log.warn("UserController.updateUser: Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String username = user.getUsername().trim();
            String newPassword = user.getPassword().trim();

            if (username.isEmpty() || newPassword.isEmpty()) {
                log.warn("UserController.updateUser: Missing username or password. username='{}'", username);
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            // Authentication & authorization: only allow the user themselves or admin to update password
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
            userService.saveUser(userByUsername);

            userByUsername.setPassword("null");

            log.info("UserController.updateUser: Password updated for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(userByUsername, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.updateUser: Exception while updating user. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")                                                  //  Delete User
    public ResponseEntity<?> deleteUser(@RequestBody(required = false) User user){
        long start = System.currentTimeMillis();
        log.info("UserController.deleteUser: Received delete request.");

        try {
            if (user == null) {
                log.warn("UserController.deleteUser: Request body is null.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String username = user.getUsername().trim();
            if (username.isEmpty()) {
                log.warn("UserController.deleteUser: Missing username in request.");
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            // Authentication & authorization: only allow the user themselves or admin to delete account
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

            userByUsername.setPassword("null");

            log.info("UserController.deleteUser: Successfully deleted blogs for username={} (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(userByUsername, HttpStatus.OK);
        } catch (Exception e) {
            log.error("UserController.deleteUser: Exception while deleting user. error={}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
