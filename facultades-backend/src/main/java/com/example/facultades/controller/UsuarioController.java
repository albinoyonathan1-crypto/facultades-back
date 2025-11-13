package com.example.facultades.controller;

import com.example.facultades.dto.*;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.excepciones.CaptchaException;
import com.example.facultades.excepciones.NickEnUsoException;
import com.example.facultades.excepciones.UsuarioExistenteException;
import com.example.facultades.excepciones.UsuarioNoEncontradoException;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Rol;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.model.Usuario;
import com.example.facultades.service.*;
import com.example.facultades.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
//@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController extends ControllerGeneric<Usuario, UsuarioDTO, Long> {

    @Autowired
    private ITokenVerificacionEmailService verificacionEmailService;

    @Autowired
    private IgenericService<Usuario, Long> genericUsuarioService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private RecaptchaService recaptchaService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping("/findUsernamesByUniversidadId/{id}")
    @Operation(
            summary = "Obtener nombres de usuario por ID de universidad",
            description = "Este endpoint permite obtener una lista de nombres de usuario asociados a una universidad específica identificada por su ID.",
            tags = {"Usuarios", "Universidades"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID de la universidad para la cual se desean obtener los nombres de usuario.",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de nombres de usuario obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Universidad no encontrada"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> findUsernamesByUniversidadId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findUsernamesByUniversidadId(id));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/cambiarContrasenia")
    @Operation(
            summary = "Cambiar la contraseña de un usuario",
            description = "Este endpoint permite a los usuarios con los roles 'ADMIN' o 'USER' cambiar su contraseña.",
            tags = {"Usuarios", "Seguridad"},
            parameters = {
                    @Parameter(
                            name = "idUsuario",
                            description = "ID del usuario cuya contraseña se desea cambiar.",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "1"
                    ),
                    @Parameter(
                            name = "nuevaContrasena",
                            description = "La nueva contraseña que se desea establecer para el usuario.",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "NuevaContraseña123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contraseña actualizada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición incorrecta, puede ser debido a parámetros inválidos"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "El usuario no tiene permisos suficientes para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> cambiarContrasenia(@RequestParam Long idUsuario, @RequestParam String nuevaContrasena) throws Exception {
        usuarioService.cambiarContrasenia(idUsuario, nuevaContrasena);
        return ResponseEntity.ok(new MensajeRetornoSimple("Contraseña actualizada exitosamente"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/actualizarContrasenia")
    @Operation(
            summary = "Actualizar la contraseña de un usuario",
            description = "Permite a los usuarios con los roles 'ADMIN' o 'USER' actualizar la contraseña de un usuario proporcionado. La solicitud debe incluir la contraseña actual y la nueva contraseña.",
            tags = {"Usuarios", "Seguridad"},
            requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cuerpo de la solicitud que contiene los datos necesarios para actualizar la contraseña de un usuario.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarContraseniaRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contraseña actualizada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición incorrecta, parámetros inválidos o datos no válidos"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Usuario no autorizado para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> actualizarContrasenia(@RequestBody ActualizarContraseniaRequest actualizarContraseniaRequest) {
        usuarioService.actualizarContrasenia(actualizarContraseniaRequest.idUsuario(), actualizarContraseniaRequest.nuevaContrasenia(), actualizarContraseniaRequest.contraseniaActual());
        return ResponseEntity.ok(new MensajeRetornoSimple("Contraseña actualizada exitosamente"));
    }



    @PostMapping("/registro")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Este endpoint permite registrar un nuevo usuario en el sistema. Antes de registrar al usuario, se verifica que el captcha proporcionado sea válido. Además, se realiza la validación del email y el nombre de usuario (nick) para evitar duplicados.",
            tags = {"Usuarios", "Registro"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cuerpo de la solicitud que contiene los datos necesarios para registrar un nuevo usuario.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistroRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario registrado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición incorrecta o datos inválidos"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Usuario ya existe o nombre de usuario (nick) en uso"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Captcha no válido"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> save(@RequestBody RegistroRequest registroRequest) {

        boolean isCaptchaValid = recaptchaService.verifyRecaptcha(registroRequest.captchaToken());
        if (!isCaptchaValid) {
            throw new CaptchaException();
        }

        Usuario usuarioBuscado = usuarioService.buscarUsuarioPorNombre(registroRequest.email());
        Usuario usuarioBuscadoNick = usuarioService.buscarUsuarioPorNick(registroRequest.nick());

        // Si el usuario con el email ya existe
        if (usuarioBuscado != null) {
            // Si el email no ha sido verificado, se elimina y se crea uno nuevo
            if (!usuarioBuscado.isEmailVerified()) {
                genericUsuarioService.delete(usuarioBuscado.getId());
                return this.crearUsuario(registroRequest);
            }
            // Si el usuario ya está verificado, se lanza la excepción
            throw new UsuarioExistenteException();
        }

        // Si el nick ya está en uso, lanzar una excepción
        if (usuarioBuscadoNick != null) {
            throw new NickEnUsoException();
        }

        // Si no hay usuario con el email ni con el nick, crear el usuario
        return this.crearUsuario(registroRequest);
    }



    private ResponseEntity<MensajeRetornoSimple> crearUsuario(RegistroRequest registroRequest){
        Usuario usuario = new Usuario();
        usuario.setUsername(registroRequest.email());
        usuario.setPassword(registroRequest.contrasenia());
        usuario.setNick(registroRequest.nick());
        genericUsuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MensajeRetornoSimple("El usuario fue creado"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/infraccionar/{id}")
    @Operation(
            summary = "Infraccionar un usuario",
            description = "Este endpoint permite que un usuario con rol de ADMIN infraccione a otro usuario. El proceso de infracción se realiza proporcionando el ID del usuario a ser infraccionado.",
            tags = {"Usuarios", "Administración"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del usuario que será infraccionado",
                            required = true,
                            schema = @Schema(type = "Long")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario infraccionado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. Se requiere el rol de ADMIN"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> infraccionarUsuario(@PathVariable Long id){
        usuarioService.infraccionarUsuario(id);
        return ResponseEntity.ok(new MensajeRetornoSimple("Usuario infraccionado con exito"));
    }


    @GetMapping("/verificarEmail/{token}/{idTokenVerificador}")
    @Operation(
            summary = "Verificar cuenta de usuario",
            description = "Este endpoint se utiliza para verificar la cuenta de usuario mediante un token de verificación. Si el token es válido y no ha expirado, se activa el correo electrónico del usuario y se genera un nuevo token de acceso.",
            tags = {"Usuarios", "Verificación de cuenta"},
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "Token de verificación enviado al correo del usuario",
                            required = true,
                            schema = @Schema(type = "String")
                    ),
                    @Parameter(
                            name = "idTokenVerificador",
                            description = "ID del token de verificación asociado al usuario",
                            required = true,
                            schema = @Schema(type = "Long")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirección a la URL con el token de acceso y el rol del usuario",
                            headers = @Header(name = "Location", description = "URL de redirección con el token de acceso")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Token de verificación no encontrado o expirado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    public void verifyAccount(@PathVariable String token, @PathVariable Long idTokenVerificador, HttpServletResponse response) throws IOException {
        TokenVerificacionEmail verificationToken = verificacionEmailService.findByToken(token);

        if (verificationToken == null) {
            // Token no encontrado
            redirectWithError(response, idTokenVerificador);
            return;
        }

        if (verificationToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            // Token expirado
            redirectWithError(response, idTokenVerificador);
            return;
        }

        // Actualizar estado del usuario
        Usuario usuario = verificationToken.getUsuario();
        usuario.setEmailVerified(true);
        usuario.setRefreshToken(usuarioService.crearRefreshToken(usuario));
        genericUsuarioService.update(usuario);

        // Obtener el rol del usuario
        String role = obtenerRol(usuario);

        Authentication authentication = userDetailsServiceImp.authenticate(usuario.getUsername());

        String accesToken = jwtUtil.createToken(authentication, DuracionToken.ACCES_TOKEN.getDuracion());

        // Obtener el token y el ID del usuario
        Long idUsuario = usuario.getId();

        // Construir URL de redirección
        //String redirectUrl = "http://vps-4741837-x.dattaweb.com:80/" + accesToken + "&role=" + role + "&idUsuario=" + idUsuario;
        String redirectUrl = "http://vps-4741837-x.dattaweb.com:80?token=" + accesToken + "&role=" + role + "&idUsuario=" + idUsuario;


        // Redirigir a la URL de destino
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);
    }

    @GetMapping("/buscar-imagen-user/{id}")
    public ResponseEntity<ImagenUsuario> buscarImagenPorIdUser(@PathVariable Long id){
        ImagenUsuario imagenUsuario = new ImagenUsuario(usuarioService.buscarImagenPorIdUser(id));
        return ResponseEntity.ok(imagenUsuario);
    }


    // Método auxiliar para obtener el rol del usuario
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
        response.setHeader("Location", "http://localhost:4200/error/token/" + idTokenVerificador + "?email=" + true + "&contrasenia=" + false);
    }


    @GetMapping("/obtenerRefreshTokenPorUsuario/{idUsuario}")
    public ResponseEntity<MensajeRetornoSimple> obtenerRefreshTokenPorUsuario(@PathVariable Long idUsuario){
        String token = "";
        token = usuarioService.obtenerRefreshTokenPorUsuario(idUsuario);
        MensajeRetornoSimple retornoSimple = new MensajeRetornoSimple(token);
        return ResponseEntity.ok(retornoSimple);
    }

    @PostMapping("/buscarUsuariosPorListIds")
    public ResponseEntity<List<UsuarioRecord>> buscarUsuariosPorListIds(@RequestBody List<Long> listaIds) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPorListIds(listaIds));
    }

    @GetMapping("/buscarTodosLosUsuarios")
    public ResponseEntity<List<UsuarioRecord>> buscarTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.buscarTodosLosUsuarios());
    }


    @DeleteMapping("eliminarNotificacionUsuarioId/{usuarioId}/{usuarioNotificarId}")
    public ResponseEntity<MensajeRetornoSimple> eliminarNotificacionUsuarioId(@PathVariable Long usuarioId, @PathVariable Long usuarioNotificarId){
        usuarioService.eliminarNotificacionUsuario(usuarioId, usuarioNotificarId);
        messagingTemplate.convertAndSend("/tema/notificacionNuevoMensajeEliminada/" + usuarioNotificarId, new MensajeRetornoSimple("Notificación eliminada"));

        return ResponseEntity.ok(new MensajeRetornoSimple("Notificación eliminada"));
    }
}
