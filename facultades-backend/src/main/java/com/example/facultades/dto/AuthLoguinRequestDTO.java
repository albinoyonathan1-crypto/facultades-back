package com.example.facultades.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoguinRequestDTO(@NotBlank String nombreUsuario,
                                   @NotBlank String contrasenia) {
}
