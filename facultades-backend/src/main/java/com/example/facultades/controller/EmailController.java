package com.example.facultades.controller;

import com.example.facultades.dto.EmailDtoContacto;
import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.service.IEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private IEmailService iEmailService;

    @PostMapping()
    @Operation(
            summary = "Enviar correo electrónico",
            description = "Envía un correo electrónico utilizando los datos proporcionados en el cuerpo de la solicitud.",
            tags = {"Correo Electrónico"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para enviar el correo electrónico, como el destinatario, asunto y mensaje",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmailDtoContacto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Correo electrónico enviado con éxito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al enviar el correo"
                    )
            }
    )
    public ResponseEntity<String> sendEmail(@RequestBody EmailDtoContacto email) throws MessagingException {
        iEmailService.sendMail(email);
        return new ResponseEntity<>("{\"mensaje\": \"Email enviado con éxito\"}", HttpStatus.OK);
    }


    @PostMapping("/enviar-emal-nueva-contrasenia")
    @Operation(
            summary = "Enviar correo de notificación de cambio de contraseña",
            description = "Envía un correo electrónico notificando al usuario sobre un cambio en su contraseña, con un mensaje HTML que incluye los detalles del cambio.",
            tags = {"Correo Electrónico", "Seguridad"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "No se requiere cuerpo en la solicitud, los parámetros son enviados a través de la URL como parámetros de consulta.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Correo de notificación enviado con éxito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al enviar el correo"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> enviarEmail(@RequestParam String emailDestinatario,
                                                            @RequestParam String asunto,
                                                            @RequestParam String mensaje)
            throws MessagingException {
        String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                + "<h2 style='color: #0066cc;'>Cambio de Contraseña en FacusArg</h2>"
                + "<h3 style='color: #0066cc;'>Notificación de Cambio de Contraseña</h3>"
                + "<p>Tu contraseña ha sido cambiada exitosamente. Si no fuiste tú quien realizó este cambio, por favor, comunícate con el soporte inmediatamente.</p>"
                + "<p>A continuación, se detallan los cambios realizados:</p>"
                + "<p style='background: #f4f4f4; padding: 10px; border-radius: 5px; word-break: break-all; text-align: center;'>"
                + mensaje + "</p>"
                + "<p style='color: #777;'>Si no realizaste este cambio, por favor, contacta a nuestro equipo de soporte lo antes posible.</p>"
                + "<p style='color: #777;'>Este es un mensaje automático. No es necesario responder.</p>"
                + "</div>";

        iEmailService.enviarEmail(emailDestinatario, asunto, mensajeHtml);
        return ResponseEntity.ok(new MensajeRetornoSimple("Email enviado con exito"));
    }


}
