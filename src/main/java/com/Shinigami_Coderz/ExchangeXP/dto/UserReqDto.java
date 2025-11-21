package com.Shinigami_Coderz.ExchangeXP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String email;
    private String userPhotoUrl;
}
