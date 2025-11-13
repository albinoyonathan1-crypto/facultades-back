package com.example.facultades.dto;

import com.example.facultades.model.Rol;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RolDTO extends BaseDTO<Rol> {

    private String nombreRol;
    private List<PermisoDTO> listaPermiso;
}
