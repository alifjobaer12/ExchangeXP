package com.Shinigami_Coderz.ExchangeXP.repository;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogCommentRepo extends MongoRepository<BlogComment, ObjectId> {
}
