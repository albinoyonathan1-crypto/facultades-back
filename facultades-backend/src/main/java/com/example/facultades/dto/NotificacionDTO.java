package com.example.facultades.dto;

import com.example.facultades.model.Notificacion;
import lombok.*;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class NotificacionDTO extends BaseDTO<Notificacion> {

    private String informacion;
    private Long idRedireccionamiento;
    private Boolean leida;
    private boolean carrera;
    private boolean comentario;
    private boolean usuario;
    private boolean universidad;
    private boolean permiso;
    private boolean respuesta;
    private boolean publicacionComentada;
    private boolean respuestaComentarioRecibida;
    private boolean  respuestaAunaRespuesta;
    private boolean carreraAgregada;
    private boolean comentarioAgregadoCarrera;
    private boolean auditada;
    private Date fecha;

    // Representa solo los IDs de los usuarios en la lista
    private List<Long> listaUsuariosIds;

    // Representa solo los IDs de los usuarios leídos
    private List<Long> listaDeusuariosLeidosIds;

}
