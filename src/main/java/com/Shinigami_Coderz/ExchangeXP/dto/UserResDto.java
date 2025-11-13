package com.Shinigami_Coderz.ExchangeXP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResDto {
    private String userId;
    private String username;
    private String email;
}
