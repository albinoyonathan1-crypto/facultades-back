package com.example.facultades.enums;

public enum NombreRepositorio {
    USUARIO("usuarioRepo"),
    UNIVERSIDAD("universidadRepo"),
    ROL("rolRepo"),
    NOTIFICACION("notificacionRepo"),
    CALIFICACION("calificacionRepo"),
    CARRERA("carreraRepo"),
    COMENTARIO("comentarioRepo"),
    PERMISO("permisoRepo"),
    USUARIO_LEIDO("usuarioLeidoRepo"),
    RESPUESTA("respuestaRepo"),
    REACCION("reaccionRepo");

    private final String repoName;

    NombreRepositorio(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoName() {
        return repoName;
    }
}
