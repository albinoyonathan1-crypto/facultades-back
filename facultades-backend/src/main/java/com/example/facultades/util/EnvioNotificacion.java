package com.example.facultades.util;

import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.enums.MensajeNotificacionAdmin;
import com.example.facultades.enums.Socket;
import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Universidad;
import com.example.facultades.service.INotificacionService;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EnvioNotificacion {

    @Autowired
    private IgenericService<Carrera,Long> carreraService;

    @Autowired
    private IgenericService<Universidad, Long> universidadService;

    public <E extends BaseEntity & ItipoEntidad>
    void enviarGuardarNotificacionNuevoComentario(String nombreEntidad, E entidad, Long idUniversidad,
                                                  List<Comentario> listaComentarios,
                                                  IgenericService<Comentario, Long> comentarioService,
                                                  INotificacionService notificacionService) {
        Comentario ultimoComentario = obtenerUltimoComentario(listaComentarios, comentarioService);
        validarComentarioConUsuario(ultimoComentario);

        DetalleNotificacion detalleAdmin = crearDetalleNotificacionAdmin(ultimoComentario);
        procesarNotificacionAdmin(detalleAdmin, ultimoComentario, notificacionService);

        this.enviarNotificacionAlPropietarioSiEsNecesario(nombreEntidad, idUniversidad, entidad,
                ultimoComentario, notificacionService);
    }

    // Separa la validación de comentarios en un método
    private static void validarComentarioConUsuario(Comentario comentario) {
        if (comentario.getUsuario() == null) {
            throw new IllegalStateException("El comentario no tiene un usuario asociado");
        }
    }

    // Procesa la notificación para el administrador
    private static void procesarNotificacionAdmin(DetalleNotificacion detalleNotificacion,
                                                  Comentario ultimoComentario,
                                                  INotificacionService notificacionService) {
        enviarNotificacionAdmin(detalleNotificacion, notificacionService);
        guardarNotificacionAdmin(ultimoComentario, notificacionService);
    }

    // Crea y envía una notificación al propietario si es necesario
    private  <E extends BaseEntity & ItipoEntidad>
    void enviarNotificacionAlPropietarioSiEsNecesario(String nombreEntidad, Long idUniversidad,
                                                      E entidad, Comentario ultimoComentario,
                                                      INotificacionService notificacionService) {
        Long idPropietario = obtenerIdPropietario(entidad);

        if (!idPropietario.equals(ultimoComentario.getUsuario().getId())) {
            Notificacion notificacion = crearNotificacionParaEntidad(entidad);
            notificacion.setPublicacionComentada(true);

            enviarNotificacionAlPropietario(nombreEntidad, idUniversidad, idPropietario,
                    ultimoComentario, notificacionService, notificacion);
        }
    }

    // Método auxiliar para obtener el ID del propietario según el tipo de entidad
    private <E extends BaseEntity & ItipoEntidad> Long obtenerIdPropietario(E entidad) {
        if (entidad.obtenerTipoClase() == Carrera.class) {
            return obtenerIdPropietarioCarrera((Carrera) entidad);
        } else if (entidad.obtenerTipoClase() == Universidad.class) {
            return obtenerIdPropietarioUniversidad((Universidad) entidad);
        }
        return null;
    }

    // Obtener el ID del propietario para una entidad de tipo Carrera
    private Long obtenerIdPropietarioCarrera(Carrera carrera) {
        if (carrera.getUniversidad() != null) {
            Universidad universidad = encontrarUniversidad(carrera.getUniversidad().getId());
            if (universidad != null && universidad.getUsuario() != null) {
                return universidad.getUsuario().getId();
            }
        }
        return null;
    }

    // Obtener el ID del propietario para una entidad de tipo Universidad
    private Long obtenerIdPropietarioUniversidad(Universidad universidad) {
        Universidad universidadEncontrada = encontrarUniversidad(universidad.getId());
        if (universidadEncontrada != null && universidadEncontrada.getUsuario() != null) {
            return universidadEncontrada.getUsuario().getId();
        }
        return null;
    }

    public Universidad encontrarUniversidad(Long id){
        return universidadService.findById(id).orElse(null);
    }

    // Crea una notificación con el tipo de la entidad (Universidad o Carrera)
    private static <E extends BaseEntity & ItipoEntidad> Notificacion crearNotificacionParaEntidad(E  entidad) {
        Notificacion notificacion = new Notificacion();
        if (entidad.obtenerTipoClase() == Universidad.class) {
            notificacion.setUniversidad(true);
        } else if (entidad.obtenerTipoClase() == Carrera.class) {
            notificacion.setCarrera(true);
        }
        return notificacion;
    }

    // Crear detalle de notificación para el administrador
    private static DetalleNotificacion crearDetalleNotificacionAdmin(Comentario comentario) {
        String mensaje = MensajeNotificacionAdmin.PUBLICACION_COMENTARIO.getNotificacion();
        return Utili.generarDetalleNotificacion(mensaje, comentario);
    }

    // Enviar notificación de WebSocket al administrador
    private static void enviarNotificacionAdmin(DetalleNotificacion detalleNotificacion,
                                                INotificacionService notificacionService) {
        notificacionService.enviarNotificacionByWebSocket(Socket.ADMIN_PREFIJO.getRuta(), detalleNotificacion);
    }

    // Enviar notificación de WebSocket al usuario
    private static void enviarNotificacionUsuario(DetalleNotificacion detalleNotificacion,
                                                  INotificacionService notificacionService, Long idUsuario) {
        notificacionService.enviarNotificacionByWebSocket(Socket.TOPICO_PERSONAL.getRuta() + "/" + idUsuario, detalleNotificacion);
    }

    // Guarda la notificación para el administrador
    private static void guardarNotificacionAdmin(Comentario comentario, INotificacionService notificacionService) {
        Notificacion notificacion = new Notificacion();
        notificacion.setComentario(true);
        String mensaje = MensajeNotificacionAdmin.PUBLICACION_COMENTARIO.getNotificacion();
        notificacionService.guardarNotificacionAdmin(comentario.getId(), mensaje, notificacion);
    }

    // Enviar notificación al propietario de la entidad
    private static void enviarNotificacionAlPropietario(String nombreEntidad, Long idUniversidad,
                                                        Long idPropietario, Comentario ultimoComentario,
                                                        INotificacionService notificacionService,
                                                        Notificacion notificacion) {
        String mensaje = "Han comentado tu publicación (" + nombreEntidad + ")";
        notificacionService.guardarNotificacionUsuario(idPropietario, idUniversidad, mensaje, notificacion);


        DetalleNotificacion detallePropietario = new DetalleNotificacion(
                "Han publicado un comentario en tu publicación: (" + nombreEntidad+")", "Comentario: "+ultimoComentario.getMensaje(), idUniversidad);
        enviarNotificacionUsuario(detallePropietario, notificacionService, idPropietario);
    }

    // Método reutilizado para obtener el último comentario
    private static Comentario obtenerUltimoComentario(List<Comentario> listaComentarios,
                                                      IgenericService<Comentario, Long> comentarioService) {
        return Utili.recuperarUltimoComentario(listaComentarios, comentarioService);
    }


}
