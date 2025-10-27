package com.Shinigami_Coderz.ExchangeXP.repository;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogRepo extends MongoRepository<Blog, ObjectId> {
}
