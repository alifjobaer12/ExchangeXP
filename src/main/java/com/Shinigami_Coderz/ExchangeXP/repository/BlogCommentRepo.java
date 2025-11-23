package com.Shinigami_Coderz.ExchangeXP.repository;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogCommentRepo extends MongoRepository<BlogComment, ObjectId> {
    List<BlogComment> findByUsername(String username);
}
