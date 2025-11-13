package com.example.facultades.dto;

public record ContactoRequest(String captchaToken, String email, String mensaje) {
}
