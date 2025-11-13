package com.example.facultades.controller;


import com.example.facultades.dto.RolDTO;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.model.Rol;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rol")
//@PreAuthorize("hasRole('ADMIN')")
public class RolController extends ControllerGeneric<Rol, RolDTO, Long> {

}
