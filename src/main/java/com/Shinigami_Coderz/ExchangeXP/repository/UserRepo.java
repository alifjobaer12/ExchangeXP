package com.Shinigami_Coderz.ExchangeXP.repository;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, ObjectId> {
    public User findByUsername(String username);
    public User deleteByUsername(String username);
    public User findByEmail(String email);

}
