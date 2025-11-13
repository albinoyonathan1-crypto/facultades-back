package com.example.facultades.dto;

public record RegistroRequest(String email, String contrasenia, String captchaToken, String nick) {
}
