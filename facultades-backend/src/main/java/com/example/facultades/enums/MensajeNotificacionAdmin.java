package com.example.facultades.enums;

public enum MensajeNotificacionAdmin {
    CREACION_UNIVERSIDAD("Se ha creado una nueva universidad"),
    CREACION_CARRERA("Se ha creado una nueva carrera"),
    CREACION_USUARIO("Se ha creado un nuevo usuario"),
    PUBLICACION_COMENTARIO("Se ha publicado un nuevo comentario" ),
    USUARIO_BANEADO("Un usuario ha sido baneado");

    private  final String notificacion;

    MensajeNotificacionAdmin(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getNotificacion(){
        return notificacion;
    }
}
