package com.Shinigami_Coderz.ExchangeXP.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "blog_likes")
@CompoundIndex(name = "blog_user_idx", def = "{'blogId': 1, 'userId': 1}", unique = true)
public class BlogLike {

    @Id
    private ObjectId id;

    private ObjectId blogId;   // ID of the blog that was liked
    private ObjectId userId;   // ID of the user who liked it

    private long createdAt;    // optional: for tracking when user liked it

    public BlogLike(ObjectId blogId, ObjectId userId) {
        this.blogId = blogId;
        this.userId = userId;
        this.createdAt = System.currentTimeMillis();
    }
}

