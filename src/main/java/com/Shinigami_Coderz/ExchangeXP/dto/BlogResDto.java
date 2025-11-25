package com.Shinigami_Coderz.ExchangeXP.dto;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogResDto {
    private String blogId;
    private String blogTitle;
    private String blogContent;
    private LocalDateTime blogDate;
    private String username;
    private String userPhotoUrl;
    private List<String> blogImageUrl =  new ArrayList<>();
    private List<BlogComment> blogComments = new ArrayList<>();
    private List<String> likes = new ArrayList<>();

}
