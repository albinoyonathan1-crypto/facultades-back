package com.example.facultades.dto;

import java.util.List;

public record UsuarioRecord(Long id, String nick, String imagenUsuario, List<Long> idsUsuariosNotificar) {
}
