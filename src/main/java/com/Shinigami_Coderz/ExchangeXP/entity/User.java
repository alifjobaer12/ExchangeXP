package com.Shinigami_Coderz.ExchangeXP.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
@Data
@NoArgsConstructor
public class User {
    @Id
    private ObjectId userId;

    @NonNull
    @Indexed(unique = true)
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String email;

    private List<String> roles;

    private List<Integer> rating;

    @DBRef
    private List<Skill> skills = new ArrayList<>();

    @DBRef
    private List<Blog> blogs = new ArrayList<>();

    @DBRef
    private List<User> friends = new ArrayList<>();

}
