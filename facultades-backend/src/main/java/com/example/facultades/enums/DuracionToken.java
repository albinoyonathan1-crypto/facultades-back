package com.example.facultades.enums;

public enum DuracionToken {
    //REFRESH_TOKEN(7*24*60*60*1000),
    REFRESH_TOKEN(60 * 60000),
    ACCES_TOKEN(15*60*1000);

    private final long duracion;

    DuracionToken(long duracion) {
        this.duracion = duracion;
    }

    public long getDuracion() {
        return duracion;
    }
}
