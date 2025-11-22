package com.Shinigami_Coderz.ExchangeXP.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateProfileDto {
    private String userId;
    @NonNull
    private String username;
    private String userPhotoUrl;
    private String phoneNumber;
    private String address;
}
