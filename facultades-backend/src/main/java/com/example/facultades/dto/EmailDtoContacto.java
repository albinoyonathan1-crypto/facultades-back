package com.example.facultades.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDtoContacto {

    private String destinatario;
    private String emisor;
    private String nombre;
    private String apellido;
    private String asunto;
    private String mensaje;

}
