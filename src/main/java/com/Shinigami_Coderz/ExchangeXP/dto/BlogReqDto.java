package com.Shinigami_Coderz.ExchangeXP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogReqDto {
    @NonNull
    private String blogTitle;

    @NonNull
    private String blogContent;

    private List<String> blogImageUrl =  new ArrayList<>();
}
