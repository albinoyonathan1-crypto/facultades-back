package com.example.facultades.service;



import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.dto.NotificacionDTO;
import com.example.facultades.model.Notificacion;

import java.util.List;

public interface INotificacionService {
    public void enviarNotificacionByWebSocket(String topic,DetalleNotificacion detalleNotificacion);
    public void guardarNotificacionAdmin(Long idEvento, String informacion, Notificacion notificacion);
    public void guardarNotificacionUsuario(Long idUsuario,Long idEvento, String informacion, Notificacion notificacion);
    public List<NotificacionDTO> getNotificacionByIdUser(Long idUser);
    public String eliminarUsuarioNotificacion(Long idNotificacion, Long idUsuario);
    public List<Notificacion> findByLeidaFalse(Long usuarioId);
    List<NotificacionDTO> findNotificacionesNoLeidasPorUsuario(Long userId);
    public String setNotificacionLeidaPorUsuario(Long userId);
    String visualizarNotificacionesByUserID( Long userId);
}
