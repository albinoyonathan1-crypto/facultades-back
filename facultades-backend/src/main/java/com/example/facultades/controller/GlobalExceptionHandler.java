package com.example.facultades.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.facultades.excepciones.*;
import com.example.facultades.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Acceso denegado: HTTP 403
   /* @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(403) // Código estándar para acceso denegado
                .message("Acceso denegado: no tiene los permisos necesarios.")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }*/

    // Error general del servidor: HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejadorExcepcionesGlobal(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(500) // Código estándar para errores internos
                .message("Error interno del servidor: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailNoVerificadoException.class)
    public ResponseEntity<ErrorResponse> emailNoVerificadoException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(409)
                .message( ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> JWTVerificationException(JWTVerificationException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(401) // Personalizado: conflicto relacionado con usuario existente
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(NickEnUsoException.class)
    public ResponseEntity<ErrorResponse> nickEnUso(NickEnUsoException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(409) // Personalizado: conflicto relacionado con usuario existente
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }



    // Usuario existente: HTTP 409 (conflicto)
    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<ErrorResponse> elMailEnUso(UsuarioExistenteException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(409) // Personalizado: conflicto relacionado con usuario existente
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }



    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> comentarioNoEncontradoException(ComentarioNoEncontradoException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(404) // Personalizado: conflicto relacionado con usuario existente
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Usuario no encontrado: HTTP 404
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> usuarioNoEncontradoException(UsuarioNoEncontradoException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(404) // Personalizado: recurso no encontrado para usuario
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Email no encontrado: HTTP 404
    @ExceptionHandler(EmailNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> emailNoEncontrado(EmailNoEncontradoException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(404) // Personalizado: recurso no encontrado para email
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Contraseña incorrecta: HTTP 401 (no autorizado)
    @ExceptionHandler(ContrasenaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> contrasenaIncorrecta(ContrasenaNoEncontradaException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(401) // Personalizado: no autorizado por contraseña incorrecta
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // Universidad repetida: HTTP 409 (conflicto)
    @ExceptionHandler(UniversidadRepetidaException.class)
    public ResponseEntity<ErrorResponse> universidadRepetida(UniversidadRepetidaException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(409) // Personalizado: conflicto relacionado con universidad duplicada
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Registro existente: HTTP 409 (conflicto)
    @ExceptionHandler(RegistroExistenteException.class)
    public ResponseEntity<ErrorResponse> registroExistente(RegistroExistenteException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(409) // Personalizado: conflicto relacionado con registro duplicado
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(403) // Personalizado: conflicto relacionado con registro duplicado
                .message("No estás logueado o no tienes los permisos necesarios para realizar esta acción.")
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsException(BadCredentialsException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(500)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }





}
