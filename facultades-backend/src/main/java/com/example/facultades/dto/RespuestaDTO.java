package com.example.facultades.dto;

import com.example.facultades.model.Respuesta;
import lombok.*;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RespuestaDTO extends BaseDTO<Respuesta> {

    private String mensaje;
    private Date fecha;
    private List<RespuestaDTO> listaRespuesta;  // Relación recursiva de respuestas
    private List<ReaccionDTO> listaReaccion;
    private Long usuarioId;         // Solo el ID del usuario
    private String username;
    private String nickname;
    private boolean editado;
    private boolean eliminado;
    private Long idComentarioPadre;
   // private Long respuestaPadreId; // ID de la respuesta padre, si la hay
}
