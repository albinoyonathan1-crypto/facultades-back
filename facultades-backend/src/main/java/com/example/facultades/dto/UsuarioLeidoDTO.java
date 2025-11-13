package com.example.facultades.dto;

import com.example.facultades.model.UsuarioLeido;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class UsuarioLeidoDTO extends BaseDTO<UsuarioLeido> {

    private Long idUsuario;
}
