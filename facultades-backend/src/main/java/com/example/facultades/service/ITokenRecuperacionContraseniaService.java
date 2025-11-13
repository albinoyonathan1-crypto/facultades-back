package com.example.facultades.service;

import com.example.facultades.model.TokenRecuperacionContrasenia;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.model.Usuario;

public interface ITokenRecuperacionContraseniaService {
    TokenRecuperacionContrasenia findByToken(String token);
    void actualizarYEnviarToken(Long id);
    public TokenRecuperacionContrasenia generarTokenVerificacion(Usuario usuario);
    public  TokenRecuperacionContrasenia actualizarToken(TokenRecuperacionContrasenia tokenRecuperacionContrasenia);
    public TokenRecuperacionContrasenia findTokenRCbyUsuarioId(Long usuarioId);
}


