package com.Shinigami_Coderz.ExchangeXP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCommentResDto {
    private String blogCommentId;
    private String user;
    @NonNull
    private String comment;
    private LocalDateTime commentAt;
    private ObjectId blogId;
}
