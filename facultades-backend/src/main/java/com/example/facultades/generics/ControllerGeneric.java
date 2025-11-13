package com.example.facultades.generics;

import com.example.facultades.dto.BaseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Operaciones CRUD genéricas", description = "Operaciones CRUD para entidades de tipo BaseEntity")
public class ControllerGeneric <E extends BaseEntity, D extends BaseDTO<E>, ID extends Number> {

    @Autowired
    private IgenericService<E, ID> genericService;

    @Operation(summary = "Obtener todas las entidades", description = "Recupera todas las entidades de tipo genérico y las convierte a DTOs", tags = {"Operaciones CRUD genéricas"})
    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        List<E> entidades = genericService.getAll();
        if (entidades == null || entidades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<D> listaDTOS = new ArrayList<>();
        for (E entidad : entidades) {
            listaDTOS.add((D) genericService.convertirDTO(entidad));
        }
        return ResponseEntity.ok(listaDTOS);
    }

    @Operation(summary = "Obtener una entidad por ID", description = "Recupera una entidad específica por su ID y la convierte a DTO", tags = {"Operaciones CRUD genéricas"})
    @GetMapping("/{id}")
    public ResponseEntity<BaseDTO<E>> findById(@Parameter(description = "ID de la entidad") @PathVariable ID id) {
        return ResponseEntity.ok(genericService.convertirDTO(genericService.findById(id).get()));
    }

    @Operation(summary = "Actualizar una entidad", description = "Actualiza una entidad utilizando un DTO proporcionado en el cuerpo de la solicitud", tags = {"Operaciones CRUD genéricas"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping
    public ResponseEntity<D> update(@RequestBody D dto) {
        E entidad = genericService.converirEntidad(dto);
        E entidadActualizada = genericService.update(entidad);
        D dto1 = (D) genericService.convertirDTO(entidadActualizada);
        return ResponseEntity.ok(dto1);
    }

    @Operation(summary = "Eliminar una entidad por ID", description = "Elimina una entidad específica por su ID", tags = {"Operaciones CRUD genéricas"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@Parameter(description = "ID de la entidad") @PathVariable ID id) {
        genericService.delete(id);
        String mensaje = "{\"mensaje\":\"" + "Eliminación exitosa" + "\"}";
        return ResponseEntity.ok(mensaje);
    }

    @Operation(summary = "Guardar una nueva entidad", description = "Guarda una nueva entidad utilizando un DTO proporcionado en el cuerpo de la solicitud", tags = {"Operaciones CRUD genéricas"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<D> save(@RequestBody D dto) {
        E entidad = genericService.converirEntidad(dto);
        E entidadGuardada = genericService.save(entidad);
        D dto1 = (D) genericService.convertirDTO(entidadGuardada);
        return ResponseEntity.ok(dto1);
    }
}
