package com.example.facultades.controller;

import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.dto.TokenRecuperacionContraseniaDTO;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.excepciones.EmailNoEncontradoException;
import com.example.facultades.excepciones.EmailNoVerificadoException;
import com.example.facultades.excepciones.UsuarioNoEncontradoException;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Rol;
import com.example.facultades.model.TokenRecuperacionContrasenia;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.model.Usuario;
import com.example.facultades.service.*;
import com.example.facultades.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/TokenRecuperacionContrasenia")
public class TokenRecuperacionContraseniaController  extends ControllerGeneric<TokenRecuperacionContrasenia, TokenRecuperacionContraseniaDTO, Long> {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IgenericService<TokenRecuperacionContrasenia, Long> genericTokenRecuperacionContrasenia;

    @Autowired
    private ITokenRecuperacionContraseniaService tokenRecuperacionContraseniaService;

    @Autowired
    private IgenericService<Usuario, Long> genericUsuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;



    @GetMapping("/actualizarToken/{id}")
    @Operation(
            summary = "Actualizar y enviar token de verificación",
            description = "Este endpoint permite actualizar el token de verificación de un usuario dado su ID, y luego enviar dicho token por el medio correspondiente. El token se utiliza comúnmente para procesos de recuperación de contraseña u otros mecanismos de validación.",
            tags = {"Tokens"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "El ID del usuario al que se le actualizará y enviará el token de verificación.",
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
                            responseCode = "404",
                            description = "No se encontró el usuario con el ID proporcionado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al actualizar y enviar el token de verificación"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> actualizarTokenVerificacion(@PathVariable Long id) {
        tokenRecuperacionContraseniaService.actualizarYEnviarToken(id);
        return ResponseEntity.ok(new MensajeRetornoSimple("Token de verificación actualizado y enviado con éxito"));
    }



    @PostMapping("/recuperarContraseña/{email}")
    @Operation(
            summary = "Recuperar contraseña del usuario",
            description = "Este endpoint permite iniciar el proceso de recuperación de contraseña para un usuario. Se enviará un correo con un token de recuperación de contraseña al correo del usuario si este existe y su email está verificado. Si el usuario no está registrado o su correo no está verificado, se lanza una excepción.",
            tags = {"Usuarios", "Recuperación de Contraseña"},
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "El correo electrónico del usuario para recuperar su contraseña.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Correo de recuperación enviado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "El correo electrónico no está verificado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno en el servidor"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> recuperarContrasenia(@PathVariable String email){
        Optional<Usuario> usuario = usuarioService.findUserEntityByusername(email);

        if(usuario.isPresent()){
            if(!usuario.get().isEmailVerified()){
                throw new EmailNoVerificadoException();
            }
        }

        if(usuario.isEmpty())
            throw new UsuarioNoEncontradoException();

        TokenRecuperacionContrasenia tokenRecuperacionContrasenia = null;
        if(usuario.get().getTokenRecuperacionContrasenia() != null){
            tokenRecuperacionContrasenia = tokenRecuperacionContraseniaService.actualizarToken(usuario.get().getTokenRecuperacionContrasenia());
        }else{
            tokenRecuperacionContrasenia = tokenRecuperacionContraseniaService.generarTokenVerificacion(usuario.get());
        }
        emailService.enviarCorreoRecuperacionContrasena(usuario.get().getUsername(),tokenRecuperacionContrasenia.getToken(), tokenRecuperacionContrasenia.getId());
        return ResponseEntity.ok(new MensajeRetornoSimple("Correo de recuperación enviado"));
    }



    @GetMapping("/reestablecerContrasenia/{token}/{idTokenVerificador}")
    @Operation(
            summary = "Reestablecer la contraseña del usuario",
            description = "Este endpoint permite a los usuarios reestablecer su contraseña usando un token de recuperación válido. Si el token es válido y no ha expirado, el sistema generará un nuevo token de acceso y redirigirá al usuario a una página para completar el proceso de reestablecimiento de la contraseña.",
            tags = {"Usuarios", "Recuperación de Contraseña"},
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "El token de recuperación utilizado para verificar la solicitud.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idTokenVerificador",
                            description = "El ID del verificador de token utilizado para asociar la solicitud.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirige a la página de reestablecimiento de contraseña con el nuevo token de acceso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Token no encontrado o inválido"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Token expirado"
                    )
            }
    )
    public void reestablecerContrasenia(@PathVariable String token, @PathVariable Long idTokenVerificador, HttpServletResponse response) throws IOException {
        TokenRecuperacionContrasenia verificationToken = tokenRecuperacionContraseniaService.findByToken(token);

        if (verificationToken == null) {
            // Token no encontrado
            redirectWithError(response, idTokenVerificador);
            System.out.println("TOKEN NULL");
            return;
        }

        if (verificationToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            // Token expirado
            redirectWithError(response, idTokenVerificador);
            System.out.println("token invalido");
            return;
        }

        // Actualizar estado del usuario
        Usuario usuario = verificationToken.getUsuario();

        // Obtener el rol del usuario
        String role = obtenerRol(usuario);
        Long idUsuario = usuario.getId();
        Authentication authentication = userDetailsServiceImp.authenticate(usuario.getUsername());
        String nuevoAccesToken = createAccessToken(authentication);

        // Construir URL de redirección
        String redirectUrl = "http://localhost:4200/reestablecerContrasenia?token=" + nuevoAccesToken + "&role=" + role + "&idUsuario=" + idUsuario;

        // Redirigir a la URL de destino
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);
    }


    private String obtenerRol(Usuario usuario) {
        List<Rol> roles = usuario.getListaRoles();
        for (Rol rol : roles) {
            if ("ADMIN".equals(rol.getNombreRol()) || "USER".equals(rol.getNombreRol())) {
                return rol.getNombreRol();
            }
        }
        return "";  // En caso de no encontrar rol, retorna vacío (puedes ajustar esto si es necesario)
    }

    // Método para redirigir con error
    private void redirectWithError(HttpServletResponse response, Long idTokenVerificador) throws IOException {
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "http://localhost:4200/error/token/" + idTokenVerificador + "?email=" + false + "&contrasenia=" + true);
    }

    private String createAccessToken(Authentication authentication) {
        // Lógica para crear un nuevo token de acceso
        return jwtUtil.createToken(authentication, 60 * 2000);
    }

}
