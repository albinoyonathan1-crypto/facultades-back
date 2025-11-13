package com.example.facultades.dto;

import com.example.facultades.model.Universidad;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class UniversidadDTO extends BaseDTO<Universidad> {

    private String nombre;
    private String imagen;
    private String direccion;
    private String descripcion;
    private String direccionWeb;
    private Long usuarioId;
    private boolean eliminacionLogica;
    private List<CarreraDTO> listaCarreras;
    private List<CalificacionDTO> listaCalificacion;
    private List<ComentarioDTO> listaComentarios;
}
