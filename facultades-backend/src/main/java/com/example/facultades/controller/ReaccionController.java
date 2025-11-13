package com.example.facultades.controller;

import com.example.facultades.dto.ReaccionDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.service.IReaccionService;
import com.example.facultades.model.Reaccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reaccion")
public class ReaccionController extends ControllerGeneric<Reaccion, ReaccionDTO,Long> {

    /*@Autowired
    private IReaccionService reaccionService;

    @GetMapping()
    public ResponseEntity<List<Reaccion>> getMeGustas(){
        List<Reaccion> listaReaccion = reaccionService.getMegustas();
        return  new ResponseEntity<>(listaReaccion, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Reaccion> saveMegusta(@RequestBody Reaccion reaccion){
        Reaccion reaccion1 = reaccionService.saveMegusta(reaccion);
        return new ResponseEntity<>(reaccion1, HttpStatus.OK);
    }

     */
}
