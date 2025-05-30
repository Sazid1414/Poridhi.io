package com.ecommerce.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}