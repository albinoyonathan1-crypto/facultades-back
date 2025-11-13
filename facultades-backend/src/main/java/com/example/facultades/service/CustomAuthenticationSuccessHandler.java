package com.example.facultades.service;


import com.example.facultades.model.OidcUserPersonalizado;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OidcUserPersonalizado oidcUserPersonalizado = (OidcUserPersonalizado) authentication.getPrincipal();

        String token = oidcUserPersonalizado.getTokenInterno();
       // String refreshTokem = oidcUserPersonalizado.getRefreshToken();
        String role = oidcUserPersonalizado.getRole();
        Long idUsuario = oidcUserPersonalizado.getIdUsuario();
        
        String redirectUrl = "http://localhost:4200/?token=" + token  + "&role=" + role + "&idUsuario=" + idUsuario;

        // Configura la redirección HTTP
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);

    }

    }

