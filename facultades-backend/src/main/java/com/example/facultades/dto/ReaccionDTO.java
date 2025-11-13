package com.example.facultades.dto;

import com.example.facultades.model.Reaccion;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ReaccionDTO extends BaseDTO<Reaccion> {

    private int meGusta;
    private  int noMegusta;
    private Long usuarioId;
}
