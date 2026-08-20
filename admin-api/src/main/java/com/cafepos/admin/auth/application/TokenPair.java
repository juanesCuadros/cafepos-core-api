package com.cafepos.admin.auth.application;

public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) {
}
