package com.secondproj.revconnect.dto;

public class TokenResponseDTO {

    private String token;
    private String tokenType = "Bearer";

    public TokenResponseDTO() {}

    public TokenResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }
}