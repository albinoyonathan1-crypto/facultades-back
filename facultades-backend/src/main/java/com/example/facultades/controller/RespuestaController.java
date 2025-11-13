package com.example.facultades.controller;

import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.RespuestaDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Respuesta;
import com.example.facultades.repository.IRespuestaRepository;
import com.example.facultades.service.IRespuestaService;
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
import java.util.Optional;

@RestController
@RequestMapping("/respuesta")
public class RespuestaController extends ControllerGeneric<Respuesta, RespuestaDTO,Long> {

    @Autowired
   IRespuestaRepository respuestaRepository;

    @Autowired
   private IRespuestaService respuestaService;

    @Autowired
    IGenericRepository<Respuesta, Long> iGenericRepository;

    /*@GetMapping("/getUsuarioByIdRespuesta/{idRespuesta}")
    public String getUsuarioByIdRespuesta(@PathVariable Long idRespuesta){
        Long idUsuario = respuestaRepository.findUsuarioIdByRespuestaId(idRespuesta);
        System.out.println(idUsuario);
        return iGenericRepository.findById(idUsuario).get().getUsuario().getUsername();
    }*/

    @GetMapping("/entidad/{id}")
    @Operation(
            summary = "Obtener respuesta por ID",
            description = "Este endpoint permite obtener una respuesta específica utilizando su ID. El ID debe corresponder a una respuesta existente en el sistema.",
            tags = {"Respuestas"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "El ID de la respuesta que se desea obtener.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Respuesta encontrada y retornada correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Respuesta.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontró una respuesta con el ID proporcionado"
                    )
            }
    )
    public Respuesta traerRespuesta(@PathVariable Long id) {
        return respuestaRepository.findById(id).get();
    }


    @GetMapping("/findComentariosByListaRespuestaId/{idRespuesta}")
    @Operation(
            summary = "Obtener comentarios por ID de respuesta",
            description = "Este endpoint permite obtener los comentarios asociados a una respuesta específica utilizando el ID de la respuesta. Los comentarios serán retornados como una lista de objetos `ComentarioDTO`.",
            tags = {"Comentarios"},
            parameters = {
                    @Parameter(
                            name = "idRespuesta",
                            description = "El ID de la respuesta para la cual se desean obtener los comentarios.",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comentarios encontrados y retornados correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ComentarioDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron comentarios para la respuesta con el ID proporcionado"
                    )
            }
    )
    public ResponseEntity<ComentarioDTO> findComentariosByListaRespuestaId(@PathVariable Long idRespuesta) {
        return ResponseEntity.ok(respuestaService.findComentariosByListaRespuestaId(idRespuesta));
    }

}
