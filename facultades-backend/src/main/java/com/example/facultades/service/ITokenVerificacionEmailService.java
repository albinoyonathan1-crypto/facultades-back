package com.example.facultades.service;

import com.example.facultades.model.TokenVerificacionEmail;

public interface ITokenVerificacionEmailService {
    TokenVerificacionEmail findByToken(String token);
    void actualizarYEnviarToken(Long id);
}
