package com.Shinigami_Coderz.ExchangeXP.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "blogComment")
@Data
@NoArgsConstructor
public class BlogComment {

    @Id
    private ObjectId blogCommentId;

    @NonNull
    private String comment;

    private LocalDateTime commentAt;

    private String user;

    @DBRef
    private Blog blog;

}
