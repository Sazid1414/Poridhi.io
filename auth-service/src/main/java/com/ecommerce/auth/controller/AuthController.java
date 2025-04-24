package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.LoginResponse;
import com.ecommerce.auth.exception.AuthenticationFailedException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(loginRequest.getUsername())
                .password(loginRequest.getPassword())
                .build();

        try {
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(tokenResponse.getToken());
            loginResponse.setRefreshToken(tokenResponse.getRefreshToken());
            loginResponse.setExpiresIn(tokenResponse.getExpiresIn());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            throw new AuthenticationFailedException("Invalid username or password");
        } finally {
            keycloak.close();
        }
    }
}