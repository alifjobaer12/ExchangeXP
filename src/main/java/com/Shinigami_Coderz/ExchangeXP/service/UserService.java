package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    private static final PasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();


    @Transactional
    public User saveNewUser(User user){                                            //  Create a User
        long start = System.currentTimeMillis();
        log.info("UserService.saveNewUser: Request to create new user: {}", user == null ? null : user.getUsername());

        if (user == null) {
            log.warn("UserService.saveNewUser: Provided user object is null.");
            return null;
        }

        try {
            if (user.getPassword().isEmpty()) {
                log.warn("UserService.saveNewUser: Missing password for username={}", user.getUsername());
                return null;
            }

            if (user.getUsername().isEmpty()) {
                log.warn("UserService.saveNewUser: Missing username in request.");
                return null;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singletonList("USER"));
            User save = userRepo.save(user);

            log.info("UserService.saveNewUser: Successfully created user={} (elapsed={}ms)", user.getUsername(), System.currentTimeMillis() - start);
            return save;
        } catch (Exception e) {
            log.error("UserService.saveNewUser: Exception while saving user {}. error={}", user.getUsername(), e.getMessage(), e); // CHANGED (was System.out)
            return null;
        }
    }

    public User saveUser(User user){                                                  //  Save a User
        long start = System.currentTimeMillis();
        log.info("UserService.saveUser: Request to save/update user={}", user == null ? null : user.getUsername());

        if (user == null) {
            log.warn("UserService.saveUser: Provided user object is null.");
            return null;
        }

        try {
            User save = userRepo.save(user);
            log.info("UserService.saveUser: Successfully saved user={} (elapsed={}ms)", user.getUsername(), System.currentTimeMillis() - start);
            return save;
        } catch (Exception e) {
            log.error("UserService.saveUser: Exception while saving user {}. error={}", user.getUsername(), e.getMessage(), e); // CHANGED (was System.out)
            return null;
        }
    }

    @Transactional
    public User saveAdminUser(User user){                                            //  Create a AdminUser
        long start = System.currentTimeMillis();
        log.info("UserService.saveAdminUser: Request to create admin user: {}", user == null ? null : user.getUsername());

        if (user == null) {
            log.warn("UserService.saveAdminUser: Provided user object is null.");
            return null;
        }

        try {
            if (user.getPassword().isEmpty()) {
                log.warn("UserService.saveAdminUser: Missing password for username={}", user.getUsername());
                return null;
            }

            if (user.getUsername().isEmpty()) {
                log.warn("UserService.saveAdminUser: Missing username in request.");
                return null;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            user.setRoles(Arrays.asList("USER", "ADMIN"));

            User save = userRepo.save(user);
            log.info("UserService.saveAdminUser: Successfully created admin user={} (elapsed={}ms)", user.getUsername(), System.currentTimeMillis() - start);
            return save;
        } catch (Exception e) {
            log.error("UserService.saveAdminUser: Exception while creating admin user {}. error={}", user.getUsername(), e.getMessage(), e);
            return null;
        }
    }

    public List<User> findAllUsers(){                                                  //  All User
        log.debug("UserService.findAllUsers: Fetching all users.");
        try {
            List<User> users = userRepo.findAll();
            log.info("UserService.findAllUsers: Found {} users in database.", users.size());
            return users;
        } catch (Exception e) {
            log.error("UserService.findAllUsers: Exception while fetching all users. error={}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public User findUserByUsername(String username){
        log.debug("UserService.findUserByUsername: Searching for username={}", username);
        if (username == null || username.trim().isEmpty()) {
            log.warn("UserService.findUserByUsername: Provided username is null/empty.");
            return null;
        }

        try {
            User user = userRepo.findByUsername(username);
            if (user == null)
                log.warn("UserService.findUserByUsername: No user found username={}", username);
            else
                log.info("UserService.findUserByUsername: Found user={}", username);
            return user;
        } catch (Exception e) {
            log.error("UserService.findUserByUsername: Exception while finding username={}. error={}", username, e.getMessage(), e);
            return null;
        }
    }

    public void deleteUserByUsername(String username){
        log.info("UserService.deleteUserByUsername: Request to delete user={}", username);
        if (username == null || username.trim().isEmpty()) {
            log.warn("UserService.deleteUserByUsername: Username is null or empty.");
            return;
        }

        try {
            userRepo.deleteByUsername(username);
            log.info("UserService.deleteUserByUsername: Deleted user={}", username);
        } catch (Exception e) {
            log.error("UserService.deleteUserByUsername: Exception while deleting username={}. error={}", username, e.getMessage(), e);
        }
    }

    public void deleteUserById(ObjectId id){
        log.info("UserService.deleteUserById: Request to delete userId={}", id);
        if (id == null) {
            log.warn("UserService.deleteUserById: Provided id is null.");
            return;
        }

        try {
            userRepo.deleteById(id);
            log.info("UserService.deleteUserById: Deleted userId={}", id);
        } catch (Exception e) {
            log.error("UserService.deleteUserById: Exception while deleting userId={}. error={}", id, e.getMessage(), e);
        }
    }

    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        try {
            return userRepo.findByEmail(email);
        } catch (Exception e) {
            log.error("UserService.findUserByEmail: {}", e.getMessage(), e);
            return null;
        }
    }

}
