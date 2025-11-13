package com.example.facultades.dto;

import com.example.facultades.model.Comentario;
import com.example.facultades.model.Mensaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO extends BaseDTO<Mensaje>{
    private String contenido;
    private Long idEmisor;
    private Long idReceptor;
    private Date fecha;
    private boolean leida;
}
