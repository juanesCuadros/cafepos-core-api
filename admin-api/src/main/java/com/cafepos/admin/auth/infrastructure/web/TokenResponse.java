package com.cafepos.admin.auth.infrastructure.web;

public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {
}
