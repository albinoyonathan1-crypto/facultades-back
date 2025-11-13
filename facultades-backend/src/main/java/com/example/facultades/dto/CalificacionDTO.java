package com.example.facultades.dto;

import com.example.facultades.model.Calificacion;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CalificacionDTO extends BaseDTO<Calificacion> {

    private Double nota;
    private Long usuarioId;
}
