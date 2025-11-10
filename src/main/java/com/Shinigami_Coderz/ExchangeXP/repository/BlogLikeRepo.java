package com.Shinigami_Coderz.ExchangeXP.repository;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogLike;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BlogLikeRepo extends MongoRepository<BlogLike, ObjectId> {
    Optional<BlogLike> findByBlogIdAndUserId(ObjectId blogId, ObjectId userId);
    long countByBlogId(ObjectId blogId);
    List<BlogLike> findByBlogId(ObjectId blogId);
    void deleteByBlogIdAndUserId(ObjectId blogId, ObjectId userId);
}
