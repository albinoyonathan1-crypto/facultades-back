package com.example.facultades.enums;

public enum Socket {
    TOPICO_PRINCIPAL("/tema"),
    TOPICO_PERSONAL("/tema/usuario"),
    ADMIN_PREFIJO("/tema/admin/notificacion");

    private final String ruta;

    Socket(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }
}
