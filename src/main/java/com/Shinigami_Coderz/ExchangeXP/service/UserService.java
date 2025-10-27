package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.repository.UserRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public boolean saveNewUser(User user){                                                  //  Create a User
        try {
            user.setRoles(Collections.singletonList("USER"));
            userRepo.save(user);
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void saveUser(User user){                                                  //  Save a User
        try {
            userRepo.save(user);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<User> findAllUsers(){                                                  //  All User
        return userRepo.findAll();
    }

    public User findUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public void deleteUserByUsername(String username){
        userRepo.deleteByUsername(username);
    }

    public void deleteUserById(ObjectId id){
        userRepo.deleteById(id);
    }

}
