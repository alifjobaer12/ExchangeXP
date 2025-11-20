package com.Shinigami_Coderz.ExchangeXP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<String> blogImageUrl =  new ArrayList<>();
}
