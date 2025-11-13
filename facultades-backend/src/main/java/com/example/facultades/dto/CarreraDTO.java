package com.example.facultades.dto;

import com.example.facultades.model.Calificacion;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Universidad;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CarreraDTO extends BaseDTO<Carrera> {

    private String nombre;
    private String grado;
    private String duracion;
    private Long idUsuario;
    private boolean activa;
    private boolean eliminacionLogica;
    private List<ComentarioDTO> listaComentarios;
    private List<CalificacionDTO> listaCalificacion;
    private Long universidadId;



   /* @Override
    public BaseDTO convertirDTO(Carrera entidad) {
        return null;
    }

    @Override
    public Carrera convertirEntidad(BaseDTO dto) {
        return null;
    }*/
}
