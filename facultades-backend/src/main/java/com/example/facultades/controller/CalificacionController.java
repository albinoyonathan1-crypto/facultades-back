package com.example.facultades.controller;

import com.example.facultades.dto.CalificacionDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Calificacion;
import com.example.facultades.service.ICalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/calificacion")
public class CalificacionController extends ControllerGeneric<Calificacion, CalificacionDTO,Long> {

    @Autowired
    private IGenericRepository<Calificacion, Long> calificacionIGenericRepository;

    @GetMapping("/entidad/{id}")
    @Operation(
            summary = "Obtener calificación por ID",
            description = "Busca y devuelve una calificación específica según el ID proporcionado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calificación encontrada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Calificacion.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Calificación no encontrada"
                    )
            }
    )
    public Calificacion xxx(@PathVariable Long id) {
        return calificacionIGenericRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Calificación no encontrada para el ID: " + id));
    }

}
