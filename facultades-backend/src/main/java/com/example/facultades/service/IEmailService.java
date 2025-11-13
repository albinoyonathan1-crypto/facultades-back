package com.example.facultades.service;

import com.example.facultades.dto.EmailDtoContacto;
import jakarta.mail.MessagingException;


public interface IEmailService {

    public void sendMail(EmailDtoContacto email) throws MessagingException;
    public void enviarEmail(String emailDestinatario, String asunto, String mensaje);
    void enviarCorreoVerificacionEmail(String email, String token, Long idTokenVerificador);
    void enviarCorreoRecuperacionContrasena(String email, String token, Long idTokenVerificador);
    public void enviarEmailContraseniaRecuperada(String emailDestinatario, String nuevacContrasenia);
    public void enviarMailContacto(String correoOrigen, String mensaje);
}
