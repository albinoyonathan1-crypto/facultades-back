package com.example.facultades.dto;

public record ActualizarContraseniaRequest(Long idUsuario, String nuevaContrasenia, String contraseniaActual) {}
