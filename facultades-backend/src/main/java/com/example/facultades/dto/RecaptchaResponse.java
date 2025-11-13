package com.example.facultades.dto;

public record RecaptchaResponse(boolean success, String challenge_ts, String hostname) {}

