package com.Shinigami_Coderz.ExchangeXP.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "blog_Comment")
@Data
@NoArgsConstructor
public class BlogComment {

    @Id
    private ObjectId blogCommentId;

    private String username;

    private String userPhotoUrl;

    private LocalDateTime commentAt;

    @NonNull
    private String comment;

    private ObjectId blogId;

}
