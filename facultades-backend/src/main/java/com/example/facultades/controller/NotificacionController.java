package com.example.facultades.controller;

import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.dto.NotificacionDTO;
import com.example.facultades.excepciones.ComentarioNoEncontradoException;
import com.example.facultades.excepciones.RespuestaNoEncontradaException;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Respuesta;
import com.example.facultades.repository.INotificacionRepository;
import com.example.facultades.service.INotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notificacion")
public class NotificacionController extends ControllerGeneric<Notificacion, NotificacionDTO,Long> {

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private INotificacionRepository notificacionRepository;

    @Autowired
    private IgenericService<Comentario,Long> icomentarioServiceGeneric;

    @Autowired
    private IgenericService<Respuesta,Long> irespuestaServiceGeneric;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/leidas/{userId}")
    @Operation(
            summary = "Actualizar notificaciones como leídas",
            description = "Marca todas las notificaciones de un usuario específico como leídas en el sistema.",
            tags = {"Notificaciones", "Usuarios"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario cuya notificaciones serán marcadas como leídas.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones marcadas como leídas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos"
                    )
            }
    )
    public ResponseEntity<String> actualizarNotificacionesALeidas(@PathVariable Long userId){
        notificacionRepository.marcarNotificacionesALeidas(userId);
        return ResponseEntity.ok("Notificaciones leidas");
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/false/{usuarioId}")
    @Operation(
            summary = "Obtener notificaciones no leídas",
            description = "Obtiene todas las notificaciones no leídas para un usuario específico.",
            tags = {"Notificaciones", "Usuarios"},
            parameters = {
                    @Parameter(
                            name = "usuarioId",
                            description = "ID del usuario para el cual se consultan las notificaciones no leídas.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones no leídas obtenidas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Notificacion.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para acceder a esta información"
                    )
            }
    )
    public ResponseEntity<List<Notificacion>> getNotificationsFalse(@PathVariable Long usuarioId){
        return ResponseEntity.ok(notificacionService.findByLeidaFalse(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/noLeidas/{usuarioId}")
    @Operation(
            summary = "Obtener notificaciones no leídas de un usuario",
            description = "Obtiene todas las notificaciones no leídas para un usuario específico, devolviendo una lista de objetos `NotificacionDTO`.",
            tags = {"Notificaciones", "Usuarios"},
            parameters = {
                    @Parameter(
                            name = "usuarioId",
                            description = "ID del usuario para el cual se consultan las notificaciones no leídas.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones no leídas obtenidas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificacionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para acceder a esta información"
                    )
            }
    )
    public ResponseEntity<List<NotificacionDTO>> getNotificationsNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.findNotificacionesNoLeidasPorUsuario(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/setNotificacioneLeidasPorUsuario/{usuarioId}")
    @Operation(
            summary = "Marcar todas las notificaciones de un usuario como leídas",
            description = "Este endpoint marca todas las notificaciones no leídas de un usuario específico como leídas.",
            tags = {"Notificaciones", "Usuarios"},
            parameters = {
                    @Parameter(
                            name = "usuarioId",
                            description = "ID del usuario cuya notificación será marcada como leída.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones marcadas como leídas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<String> setNotificacioneLeidasPorUsuario(@PathVariable Long usuarioId) {
        String respuesta = notificacionService.setNotificacionLeidaPorUsuario(usuarioId);
        String mensaje = "{\"mensaje\":\"" + respuesta + "\"}";
        return ResponseEntity.ok(mensaje);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/visualizarNotificacionesByUserID/{userId}")
    @Operation(
            summary = "Visualizar notificaciones por usuario",
            description = "Este endpoint permite marcar las notificaciones de un usuario específico como visualizadas.",
            tags = {"Notificaciones", "Usuarios"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario cuyas notificaciones serán marcadas como visualizadas.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones visualizadas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<String> visualizarNotificacionesByUserID(@PathVariable Long userId) {
        String respuesta = notificacionService.visualizarNotificacionesByUserID(userId);
        String mensaje = "{\"mensaje\":\"" + respuesta + "\"}";
        return ResponseEntity.ok(mensaje);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/notificarRespuestaRecibidaAcomentario/{idPropietarioComentario}/{idComentario}/{idRespuesta}")
    @Operation(
            summary = "Notificar respuesta recibida a comentario",
            description = "Este endpoint notifica al propietario de un comentario sobre una respuesta recibida en su comentario.",
            tags = {"Notificaciones", "Comentarios", "Respuestas"},
            parameters = {
                    @Parameter(
                            name = "idPropietarioComentario",
                            description = "ID del propietario del comentario al que se ha recibido una respuesta.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idComentario",
                            description = "ID del comentario al que se ha respondido.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idRespuesta",
                            description = "ID de la respuesta recibida.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificación enviada correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> notificarRespuestaRecibidaAcomentario(
            @PathVariable Long idPropietarioComentario,
            @PathVariable Long idComentario,
            @PathVariable Long idRespuesta
    ) {
        return notificarRespuestaRecibida(idPropietarioComentario, idComentario, idRespuesta, "comentario");
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/notificarRespuestaRecibidaAUnaRespuesta/{idPropietarioRespuesta}/{idRespuesta}/{idRespuestaRecibida}")
    @Operation(
            summary = "Notificar respuesta recibida a una respuesta",
            description = "Este endpoint notifica al propietario de una respuesta cuando se recibe una nueva respuesta a su respuesta anterior.",
            tags = {"Notificaciones", "Respuestas"},
            parameters = {
                    @Parameter(
                            name = "idPropietarioRespuesta",
                            description = "ID del propietario de la respuesta a la que se ha recibido una nueva respuesta.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idRespuesta",
                            description = "ID de la respuesta que está recibiendo una nueva respuesta.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idRespuestaRecibida",
                            description = "ID de la respuesta que ha sido recibida.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificación enviada correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRetornoSimple.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                    )
            }
    )
    public ResponseEntity<MensajeRetornoSimple> notificarRespuestaRecibidaAUnaRespuesta(
            @PathVariable Long idPropietarioRespuesta,
            @PathVariable Long idRespuesta,
            @PathVariable Long idRespuestaRecibida
    ) {
        return notificarRespuestaRecibida(idPropietarioRespuesta, idRespuesta, idRespuestaRecibida, "respuesta");
    }

    private ResponseEntity<MensajeRetornoSimple> notificarRespuestaRecibida(Long propietarioId, Long entidadId, Long respuestaId, String tipoEntidad) {
        String informacion = obtenerInformacionEntidad(entidadId, tipoEntidad);

        Notificacion notificacion = new Notificacion();
        notificacion.setFecha(new Date());

        // Dependiendo del tipo de entidad (comentario o respuesta), se marca la notificación correspondiente
        if ("comentario".equals(tipoEntidad)) {
            notificacion.setRespuestaComentarioRecibida(true);
        } else {
            notificacion.setRespuestaAunaRespuesta(true);
        }

        // Guardar la notificación en el servicio
        notificacionService.guardarNotificacionUsuario(propietarioId, respuestaId, "Has recibido una respuesta a tu " + tipoEntidad + ": " + informacion, notificacion);

        return ResponseEntity.ok(new MensajeRetornoSimple("Notificación enviada con éxito"));
    }


    private String obtenerInformacionEntidad(Long id, String tipoEntidad) {
        Optional<?> entidad = "comentario".equals(tipoEntidad) ? icomentarioServiceGeneric.findById(id) : irespuestaServiceGeneric.findById(id);
        if (entidad.isPresent()) {
            return tipoEntidad.equals("comentario") ? ((Comentario) entidad.get()).getMensaje() : ((Respuesta) entidad.get()).getMensaje();
        } else {
            if ("comentario".equals(tipoEntidad)) {
                throw new ComentarioNoEncontradoException();
            } else {
                throw new RespuestaNoEncontradaException();
            }
        }
    }


    @GetMapping("/byUserId/{idUser}")
    @Operation(
            summary = "Obtener notificaciones por ID de usuario",
            description = "Este endpoint permite obtener una lista de notificaciones asociadas a un usuario específico, identificado por su ID.",
            tags = {"Notificaciones"},
            parameters = {
                    @Parameter(
                            name = "idUser",
                            description = "ID del usuario para el cual se desean obtener las notificaciones.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificaciones obtenidas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificacionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    )
            }
    )
    public ResponseEntity<List<NotificacionDTO>> getNotificacionByIdUser(@PathVariable Long idUser) {
        return ResponseEntity.ok(notificacionService.getNotificacionByIdUser(idUser));
    }


    @PutMapping("/{idNotificacion}/{idUsuario}")
    @Operation(
            summary = "Eliminar usuario asignado a una notificación",
            description = "Este endpoint permite eliminar la asignación de un usuario a una notificación específica, eliminando su vinculación.",
            tags = {"Notificaciones"},
            parameters = {
                    @Parameter(
                            name = "idNotificacion",
                            description = "ID de la notificación de la cual se desea eliminar la asignación de usuario.",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "idUsuario",
                            description = "ID del usuario que será eliminado de la notificación.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado correctamente de la notificación",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida o notificación/usuario no encontrados"
                    )
            }
    )
    public ResponseEntity<String> eliminarUsuarioAsignado(@PathVariable Long idNotificacion, @PathVariable Long idUsuario) {
        String respuesta = notificacionService.eliminarUsuarioNotificacion(idNotificacion, idUsuario);
        String mensaje = "{\"mensaje\":\"" + respuesta + "\"}";
        return ResponseEntity.ok(mensaje);
    }

}
