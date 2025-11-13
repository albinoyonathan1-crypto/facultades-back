package com.example.facultades.controller;


import com.example.facultades.dto.PermisoDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Permiso;
import com.example.facultades.repository.IPermisoRepository;
import com.example.facultades.service.INotificacionService;
import com.example.facultades.service.IPermisoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/permiso")
//@PreAuthorize("hasRole('ADMIN')")
public class PermisoController extends ControllerGeneric<Permiso, PermisoDTO,Long> {

    @Autowired
    IPermisoRepository permisoRepository;

    @Autowired
    private ModelMapper modelMapper;


    /*@GetMapping("/obtenerPermisoDTO/{id}")
    public ResponseEntity<PermisoDTO> obtener(@PathVariable Long id){
        PermisoDTO permisoDTO = new PermisoDTO(modelMapper);
        Optional<Permiso> permiso = permisoRepository.findById(id);
        return ResponseEntity.ok(permisoDTO.convertirDTO(permiso.get()));
    }*/


    /*@Autowired
    private IPermisoService permisoService;

    @Autowired
    private INotificacionService notificacionService;

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Permiso>> getAll(){
        return ResponseEntity.ok(permisoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> findById(@PathVariable Long id){
        Optional<Permiso> permiso = permisoService.findById(id);
        return permiso.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Permiso> update(@RequestBody Permiso permiso){
        return ResponseEntity.ok(permisoService.update(permiso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id){
          permisoService.delete(id);
          return ResponseEntity.ok("Permiso eliminado");
    }

    @PostMapping
    public ResponseEntity<Permiso> save(@RequestBody Permiso permiso){
        notificacionService.enviarNotificacion("/tema/admin/notificacion", "Se creo un nuevo permiso");
        notificacionService.guardarNotificacionAdmin(1L, "Se creo un permiso");
        return ResponseEntity.ok(permisoService.save(permiso));
    }*/
}
