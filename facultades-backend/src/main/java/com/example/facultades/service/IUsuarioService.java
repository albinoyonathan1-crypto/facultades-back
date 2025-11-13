package com.example.facultades.service;



import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.dto.UsuarioRecord;
import com.example.facultades.model.RefreshToken;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.model.Usuario;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    public String encriptPassword(String password);
    public Usuario saveUserOauth(String email, String nick, String imagenPerfil);
    public  Optional<Usuario> findUserEntityByusername(String username);
    List<Usuario> getUsuarioListByRole(String nombreRol);
    public Usuario buscarUsuarioPorNombre(String username);
    public TokenVerificacionEmail generarTokenVerificacion(Usuario usuario);
    public void encriptarContrasenia(Usuario usuario);
    public void infraccionarUsuario(Long idUsuario);
    void cambiarContrasenia(Long idUsuario, String contrasenia) throws Exception;
    public void actualizarContrasenia(Long idUsuario, String nuevaContrasenia, String contraseniaActual);
    public  String buscarImagenPorIdUser(Long id);
    public MensajeRetornoSimple findUsernamesByUniversidadId(Long universidadId);
    public RefreshToken crearRefreshToken(Usuario usuario);
    Usuario buscarUsuarioPorNick(String nick);
    String obtenerRefreshTokenPorUsuario(Long idUsuario);
    List<UsuarioRecord> buscarUsuariosPorListIds(List<Long> ids);
    List<UsuarioRecord> buscarTodosLosUsuarios();
    public void eliminarNotificacionUsuario(Long usuarioId, Long usuarioNotificarId);
}
