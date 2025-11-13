package com.example.facultades.security.filtros;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.facultades.dto.ErrorResponse;
import com.example.facultades.model.Usuario;
import com.example.facultades.service.IUsuarioService;
import com.example.facultades.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final IUsuarioService usuarioService;

    public JwtTokenValidator(JwtUtil jwtUtil, IUsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = extractToken(request);
        if (jwtToken != null) {
            try {
                DecodedJWT decodedJWT = jwtUtil.validateToken(jwtToken);
                authenticateUser(decodedJWT);
            } catch (Exception ex) {
                handleException(response, ex);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del encabezado Authorization.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * Autentica al usuario basado en el token JWT decodificado.
     */
    private void authenticateUser(DecodedJWT decodedJWT) {
        String username = jwtUtil.extractUsername(decodedJWT);
        cuentaBaneada(username);
        String authorities = jwtUtil.getSpecifClaim(decodedJWT, "authorities").asString();
        Collection<? extends GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void cuentaBaneada(String nombreUsuario) {
        Optional<Usuario> usuarioOptional = usuarioService.findUserEntityByusername(nombreUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (usuario.isBaneada()) {
                //System.out.println("La cuenta del usuario está bloqueada.");
                throw new LockedException("La cuenta del usuario está bloqueada.");
            }
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado.");
        }
    }
    /**
     * Maneja excepciones y envía una respuesta de error al cliente.
     */
    private void handleException(HttpServletResponse response, Exception ex) throws IOException {
       // String errorMessage = "Unauthorized";
        //HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse error = new ErrorResponse();

        // Verifica el tipo de excepción y personaliza la respuesta en consecuencia
        if (ex instanceof LockedException) {
            System.out.println("cuenta baneada");
            error.setMessage("La cuenta del usuario está bloqueada.");
            error.setCode(403);
        } else if (ex instanceof UsernameNotFoundException) {
            error.setMessage( "Usuario no encontrado.");
            error.setCode(404);
        } else {
           error.setMessage(ex.getMessage());
           error.setCode(500);
        }

        // Establece el código de estado y el cuerpo de la respuesta
        response.setStatus(error.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(error);
        response.getWriter().write(responseJson);
    }
}
