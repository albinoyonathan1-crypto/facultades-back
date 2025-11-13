package com.example.facultades.enums;

public enum RutasFront {
    DETALLE_UNIVERSIDAD("/detalleUniversidad/");

    private static final String RUTA_DESPLIEGUE = "http://localhost:4200";
    private final String ruta;

    RutasFront(String ruta) {
        this.ruta = RUTA_DESPLIEGUE + ruta;  // Combinamos la ruta base con la ruta relativa
    }

    public String getRuta() {
        return ruta;
    }
}
