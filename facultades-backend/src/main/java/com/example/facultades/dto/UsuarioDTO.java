package com.example.facultades.dto;
import com.example.facultades.model.Usuario;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@ToString(callSuper = true)
public class UsuarioDTO extends BaseDTO<Usuario> {
        private String nick;
        private String username;
        private String password;
        private String imagen;
        private int infracciones;
        private boolean baneada;
        private boolean enable;
        private boolean emailVerified;
        private RefreshTokenDto refreshToken;
        private List<RolDTO> listaRoles;
        private List<UniversidadDTO> listaUniversidad;
        private List<CalificacionDTO> listaCalificacion;
        private List<ComentarioDTO> listaComentarios;
        private List<RespuestaDTO> listaRespuesta;
        private List<ReaccionDTO> listaReaccion;
        private Long TokenRecuperacionContraseniaId;
        private List<Long> idsUsuariosNotificar;

}
