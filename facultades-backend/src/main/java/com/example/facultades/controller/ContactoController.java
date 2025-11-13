package com.example.facultades.controller;

import com.example.facultades.dto.ContactoRequest;
import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.excepciones.CaptchaException;
import com.example.facultades.service.IEmailService;
import com.example.facultades.service.RecaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacto")
public class ContactoController {

    @Autowired
    private RecaptchaService recaptchaService;

    @Autowired
    private IEmailService emailService;

    @PostMapping()
    @Operation(
            summary = "Enviar mensaje de contacto",
            description = "Permite a los usuarios enviar un mensaje de contacto mediante correo electrónico, validando un CAPTCHA.",
            tags = {"Contacto"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del mensaje de contacto, incluyendo el CAPTCHA para validación",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContactoRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Correo enviado con éxito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "CAPTCHA inválido o solicitud incorrecta"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al enviar el correo"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> enviarMensaje(@RequestBody ContactoRequest contactoRequest) {
        boolean isCaptchaValid = recaptchaService.verifyRecaptcha(contactoRequest.captchaToken());
        if (!isCaptchaValid) {
            throw new CaptchaException();
        }

        emailService.enviarMailContacto(contactoRequest.email(), contactoRequest.mensaje());
        return ResponseEntity.ok(new MensajeRetornoSimple("Correo enviado con exito"));
    }


}
