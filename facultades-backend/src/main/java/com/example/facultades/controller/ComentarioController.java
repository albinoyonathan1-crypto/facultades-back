package com.example.facultades.controller;

import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Comentario;
import com.example.facultades.service.ComentarioService;
import com.example.facultades.service.IComentarioService;
import com.example.facultades.service.INotificacionService;
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

import java.util.List;

@RestController
@RequestMapping("/comentario")
public class ComentarioController extends ControllerGeneric<Comentario, ComentarioDTO,Long> {

    @Autowired
    private IComentarioService comentarioService;

    @Autowired
    private IgenericService<Comentario, Long> igenericService;

    /*@GetMapping("/entidad/{id}")
    public ResponseEntity<Comentario> entidad(@PathVariable Long id ){
        return  ResponseEntity.ok(igenericService.findById(id).get());
    }*/

    @Autowired
    private INotificacionService notificacionService;


    @GetMapping("/enviarNotificacionUsuario")
    @Operation(
            summary = "Enviar notificación a un usuario",
            description = "Envía una notificación de prueba a un usuario específico mediante WebSockets.",
            tags = {"Notificaciones"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notificación enviada correctamente"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error al enviar la notificación"
                    )
            }
    )
    public void enviarNotificacionUsuario() {
        DetalleNotificacion detalleNotificacion = new DetalleNotificacion("prueba", "prueba", 1L);
        notificacionService.enviarNotificacionByWebSocket("/usuario/152", detalleNotificacion);
    }


    @GetMapping("/encontrarComentariosPorIdUniversidad/{idUniversidad}")
    @Operation(
            summary = "Obtener comentarios de una universidad",
            description = "Recupera una lista paginada de comentarios asociados a una universidad específica, con opciones de ordenamiento.",
            tags = {"Comentarios"},
            parameters = {
                    @Parameter(
                            name = "idUniversidad",
                            description = "ID de la universidad para obtener los comentarios",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página a recuperar (por defecto 0)",
                            example = "1",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de comentarios por página (por defecto 10)",
                            example = "10",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "recientes",
                            description = "Si es `true`, ordena los comentarios por los más recientes primero",
                            example = "true",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "antiguos",
                            description = "Si es `true`, ordena los comentarios por los más antiguos primero",
                            example = "false",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "votados",
                            description = "Si es `true`, ordena los comentarios por los más votados primero",
                            example = "false",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de comentarios obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ComentarioDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Universidad no encontrada"
                    )
            }
    )
    public ResponseEntity<List<ComentarioDTO>> encontrarComentariosPorIdUniversidad(
            @PathVariable long idUniversidad,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            @RequestParam boolean recientes,
            @RequestParam boolean antiguos,
            @RequestParam boolean votados) {
        List<ComentarioDTO> listaComentarios = comentarioService.findComentariosByUniversidadId(idUniversidad, pagina, tamanio, recientes, antiguos, votados);
        return new ResponseEntity<>(listaComentarios, HttpStatus.OK);
    }


    @GetMapping("/encontrarComentariosPorIdCarrera/{idCarrera}")
    @Operation(
            summary = "Obtener comentarios de una carrera",
            description = "Recupera una lista paginada de comentarios asociados a una carrera específica, con opciones de ordenamiento.",
            tags = {"Comentarios"},
            parameters = {
                    @Parameter(
                            name = "idCarrera",
                            description = "ID de la carrera para obtener los comentarios",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página a recuperar (por defecto 0)",
                            example = "1",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de comentarios por página (por defecto 10)",
                            example = "10",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "recientes",
                            description = "Si es `true`, ordena los comentarios por los más recientes primero",
                            example = "true",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "antiguos",
                            description = "Si es `true`, ordena los comentarios por los más antiguos primero",
                            example = "false",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "votados",
                            description = "Si es `true`, ordena los comentarios por los más votados primero",
                            example = "false",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de comentarios obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ComentarioDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Carrera no encontrada"
                    )
            }
    )
    public ResponseEntity<List<ComentarioDTO>> encontrarComentariosPorIdCarrera(
            @PathVariable long idCarrera,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            @RequestParam boolean recientes,
            @RequestParam boolean antiguos,
            @RequestParam boolean votados) {
        List<ComentarioDTO> listaComentarios = comentarioService.findComentariosByCarreraId(idCarrera, pagina, tamanio, recientes, antiguos, votados);
        return new ResponseEntity<>(listaComentarios, HttpStatus.OK);
    }


   /* @GetMapping("/findComentarioByRespuestaRespuestaId/{idRespuestaRespuesta}")
    public ResponseEntity<ComentarioDTO> findComentariosByRespuestaRespuestaId(@PathVariable Long idRespuestaRespuesta){
        return  ResponseEntity.ok(comentarioService.findComentariosByRespuestaRespuestaId(idRespuestaRespuesta));
    }*/
}
