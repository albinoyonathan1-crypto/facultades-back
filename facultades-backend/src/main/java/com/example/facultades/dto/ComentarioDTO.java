package com.example.facultades.dto;

import com.example.facultades.model.Comentario;
import lombok.*;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ComentarioDTO extends BaseDTO<Comentario> {

    private Date fecha;
    private String mensaje;
    private List<ReaccionDTO> listaReaccion;
    private List<RespuestaDTO> listaRespuesta;
    private Long usuarioId;         // Solo el ID del usuario
    private String username;
    private String nickname;
    private boolean editado;
    private boolean eliminado;
}
