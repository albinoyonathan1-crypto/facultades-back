package com.example.facultades.dto;

import com.example.facultades.model.TokenRecuperacionContrasenia;
import com.example.facultades.model.TokenVerificacionEmail;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TokenRecuperacionContraseniaDTO extends BaseDTO<TokenRecuperacionContrasenia>{
    private String  token;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaExpiracion;
    private Long usuarioId;
}
