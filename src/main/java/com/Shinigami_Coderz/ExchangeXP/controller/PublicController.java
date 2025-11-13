package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.dto.UserReqDto;
import com.Shinigami_Coderz.ExchangeXP.dto.UserResDto;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Public APIs")
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;


    @GetMapping("/google-signin/{email}")
    public ResponseEntity<?> googleSignIn(@PathVariable String email) {
        log.info("sign in with google, {}", email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/health-check")                                                  //  Health Check
    public ResponseEntity<?> healthCheck() {
        long start = System.currentTimeMillis();
        log.info("PublicController.healthCheck: Received health check request.");

        try {
            log.info("PublicController.healthCheck: Application is healthy. (elapsed={}ms)", System.currentTimeMillis() - start);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            log.error("PublicController.healthCheck: Exception occurred during health check. error={}", e.getMessage(), e);
            return new ResponseEntity<>("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        }    }

    @PostMapping("/create-user")                                                       //  Create a User
    public ResponseEntity<?> createUser(@RequestBody(required = false) UserReqDto request){
        long start = System.currentTimeMillis();
        log.info("PublicController.createUser: Received request to create new user.");

        try {
            // Null-safety check
            if (request == null) {
                log.warn("PublicController.createUser: Request body is null.");
                return new ResponseEntity<>("Request body is missing", HttpStatus.BAD_REQUEST);
            }

            String username = request.getUsername().trim();
            String password = request.getPassword().trim();
            String email = request.getEmail().trim();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                log.warn("PublicController.createUser: Missing required fields (username/password/email). username={}", username);
                return new ResponseEntity<>("Missing required fields", HttpStatus.NOT_ACCEPTABLE);
            }

            // Duplicate check (optional improvement)
            User existingUser = userService.findUserByUsername(username);
            if (existingUser != null) {
                log.warn("PublicController.createUser: Username '{}' already exists. Rejecting registration.", username);
                return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
            }

            // DTO -> Entity
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Save user
            User saved = userService.saveNewUser(user);
            if (saved == null) {
                log.error("PublicController.createUser: Failed to create new user '{}'.", username);
                return new ResponseEntity<>("User creation failed", HttpStatus.BAD_REQUEST);
            }

            // Entity -> DTO
            UserResDto response = new UserResDto(
                    saved.getUserId() != null ? saved.getUserId().toString() : null,
                    saved.getUsername(),
                    saved.getEmail()
            );

            log.info("PublicController.createUser: Successfully created user '{}' (elapsed={}ms)", username, System.currentTimeMillis() - start);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("PublicController.createUser: Exception while creating user. error={}", e.getMessage(), e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
