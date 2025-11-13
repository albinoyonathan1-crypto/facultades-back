package com.example.facultades.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.facultades.dto.AccesToken;
import com.example.facultades.dto.AuthLoguinRequestDTO;
import com.example.facultades.dto.AuthLoguinResponseDTO;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.model.Usuario;
import com.example.facultades.repository.IUsuarioRepository;
import com.example.facultades.service.UserDetailsServiceImp;
import com.example.facultades.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Authentication" ,description = "Controlador para la autenticación")
public class AuthController {

    @Autowired
    private UserDetailsServiceImp userDetails;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    @PostMapping("/loguin/password")
    @Operation(
            summary = "Loguin Usuario",
            description = "Autentica un usuario y retorna el Token e información relevante del usuario",
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Atenticación mediante nombre de usuario y contraseña",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthLoguinRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticación exitosa",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthLoguinResponseDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<AuthLoguinResponseDTO> loguin(@RequestBody AuthLoguinRequestDTO authLoguinRequestDTO){
        return ResponseEntity.ok(userDetails.loguin(authLoguinRequestDTO));
    }

    @GetMapping("/login")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public void  loguinOauth(){
    }

    /*@PreAuthorize("hasRole('REFRESH')")
    @PostMapping("/getAccesToken")
    public ResponseEntity<AuthLoguinResponseDTO> getAccesToken(@RequestBody String refreshToken) {
        System.out.println(refreshToken);

        DecodedJWT decodedJWT = jwtUtil.validateToken(refreshToken);
        String userName = jwtUtil.extractUsername(decodedJWT);

        Authentication authentication = userDetailsServiceImp.authenticate(userName);
        String nuevoAccesToken = jwtUtil.createToken(authentication, 60 * 2000);
        DecodedJWT nuevoAccesTokenDecode = jwtUtil.validateToken(nuevoAccesToken);
        String authoritues = jwtUtil.getSpecifClaim(nuevoAccesTokenDecode, "authorities").asString();

        Long idUsuario = iUsuarioRepository.findUserEntityByusername(userName).get().getId();

        AuthLoguinResponseDTO authLoguinResponseDTO = new AuthLoguinResponseDTO(userName, authoritues, idUsuario,"Loguin correcto", nuevoAccesToken, true);
        return ResponseEntity.ok(authLoguinResponseDTO);
    }*/

    @PreAuthorize("hasRole('REFRESH')")
    @PostMapping("/getAccesToken")
    @Operation(
            summary = "Obtener nuevo Access Token",
            description = "Genera un nuevo Access Token a partir de un Refresh Token válido.",
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token válido para obtener un nuevo Access Token",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access Token generado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthLoguinResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "El Refresh Token es inválido o ha expirado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "El usuario no tiene permisos para obtener un nuevo Access Token"
                    )
            }
    )
    public ResponseEntity<AuthLoguinResponseDTO> getAccesToken(@RequestBody String refreshToken) {

        DecodedJWT decodeJWTRefreshToken = jwtUtil.validateToken(refreshToken);

        String userName = jwtUtil.extractUsername(decodeJWTRefreshToken);
        Authentication authentication = userDetailsServiceImp.authenticate(userName);

        String nuevoAccesToken = createAccessToken(authentication);
        String authorities  = jwtUtil.extractauthorities(nuevoAccesToken);

        Long idUsuario = this.obtenerIdUsuario(userName);

        // Crear la respuesta DTO
        AuthLoguinResponseDTO authLoguinResponseDTO = new AuthLoguinResponseDTO(
                userName, authorities, idUsuario, "Login correcto", nuevoAccesToken, true);

        return ResponseEntity.ok(authLoguinResponseDTO);
    }


    public Long obtenerIdUsuario(String userName){
        Optional<Usuario> usuarioOptional = iUsuarioRepository.findUserEntityByusername(userName);
        if (usuarioOptional.isPresent()) {
            return usuarioOptional.get().getId();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }

    private String createAccessToken(Authentication authentication) {
        // Lógica para crear un nuevo token de acceso
        return jwtUtil.createToken(authentication, 60 * 2000);
    }

}
