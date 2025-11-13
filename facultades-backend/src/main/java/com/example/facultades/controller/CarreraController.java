package com.example.facultades.controller;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.CarreraDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Universidad;
import com.example.facultades.service.ICarreraService;
import com.example.facultades.service.IComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrera")
public class CarreraController extends ControllerGeneric<Carrera,CarreraDTO,Long> {

    @Autowired
    private ICarreraService carreraService;

    @Autowired
    private IComentarioService comentarioService;

    @Autowired
    private IgenericService<Carrera, Long> IgenericServiceCarrera;


    @GetMapping("/paginadas")
    @Operation(
            summary = "Obtener carreras paginadas",
            description = "Recupera una lista de carreras paginadas con la posibilidad de especificar el número de página y el tamaño de la página.",
            tags = {"Carreras"},
            parameters = {
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página a recuperar (por defecto 0)",
                            example = "1",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de elementos por página (por defecto 10)",
                            example = "10",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de carreras obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Carrera.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros de paginación inválidos"
                    )
            }
    )
    public ResponseEntity<List<Carrera>> obtenerCarrerasPaginadas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Carrera> carreras = carreraService.obtenerCarrerasPaginadas(pageable);
        List<Carrera> listaCarreras = carreras.getContent();
        return new ResponseEntity<>(listaCarreras, HttpStatus.OK);
    }


    @GetMapping("/obtenerTopCarreras")
    @Operation(
            summary = "Obtener las mejores carreras",
            description = "Recupera una lista de las carreras mejor valoradas, con paginación opcional.",
            tags = {"Carreras"},
            parameters = {
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página a recuperar (por defecto 0)",
                            example = "1",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de elementos por página (por defecto 10)",
                            example = "10",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de las mejores carreras obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Carrera.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros de paginación inválidos"
                    )
            }
    )
    public ResponseEntity<List<Carrera>> obtenerTopCarreras(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        List<Carrera> carreras = carreraService.getTopCarreras(pagina, tamanio).getContent();
        return new ResponseEntity<>(carreras, HttpStatus.OK);
    }


    @GetMapping("/getAllComents/{id}")
    @Operation(
            summary = "Obtener cantidad de comentarios de una carrera",
            description = "Devuelve el número total de comentarios asociados a una carrera específica.",
            tags = {"Comentarios"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID de la carrera para obtener la cantidad de comentarios",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cantidad de comentarios obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "integer", example = "5")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Carrera no encontrada"
                    )
            }
    )
    public ResponseEntity<Integer> getAllComents(@PathVariable Long id) {
        Carrera carrera = IgenericServiceCarrera.findById(id).get();
        return ResponseEntity.ok(carrera.getListaComentarios().size());
    }



}
