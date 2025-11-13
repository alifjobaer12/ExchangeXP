package com.Shinigami_Coderz.ExchangeXP.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "blog")
@Data
@NoArgsConstructor
public class Blog {

    @Id
    private ObjectId blogId;

    @NonNull
    private String blogTitle;

    @NonNull
    private String blogContent;

    private LocalDateTime blogDate;

    private String username;

    @DBRef
    private List<BlogComment> blogComments = new ArrayList<>();
}
