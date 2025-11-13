package com.example.facultades.controller;

import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.dto.TokenVerificacionEmailDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.service.IEmailService;
import com.example.facultades.service.ITokenVerificacionEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/tokenVerificacionEmail")
public class TokenVerificacionEmailController extends ControllerGeneric<TokenVerificacionEmail, TokenVerificacionEmailDTO, Long> {
    @Autowired
    private ITokenVerificacionEmailService verificacionEmailService;

    @GetMapping("/actualizarToken/{id}")
    @Operation(
            summary = "Actualizar y enviar un nuevo token de verificación por correo electrónico",
            description = "Este endpoint permite actualizar el token de verificación para un usuario especificado por su ID y enviar el nuevo token por correo electrónico.",
            tags = {"Usuarios", "Verificación de Correo Electrónico"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "El ID del usuario para el cual se actualizará el token de verificación.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token de verificación actualizado y enviado con éxito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error al actualizar o enviar el token de verificación"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> actualizarTokenVerificacion(@PathVariable Long id) {
        verificacionEmailService.actualizarYEnviarToken(id);
        return ResponseEntity.ok(new MensajeRetornoSimple("Token de verificación actualizado y enviado con éxito"));
    }

}
