package com.example.facultades.repository;


import com.example.facultades.dto.UsuarioRecord;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Reaccion;
import com.example.facultades.model.Usuario;
import com.example.facultades.util.IFindComenByEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends IGenericRepository<Usuario, Long> {
    public Optional<Usuario> findUserEntityByusername(String username);
    public List<Usuario> findUserEntityByListaRolesNombreRol(String nombreRol);
    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Usuario buscarUsuarioPorNombre(@Param("username")String username);

    @Query("SELECT u.imagen FROM Usuario u WHERE u.id = :id")
    String buscarImagenPorIdUser(@Param("id")Long id);

    @Query(value = "SELECT u.username FROM usuario u " +
            "INNER JOIN usuario_lista_universidad ul ON u.id = ul.usuario_id " +
            "INNER JOIN universidad un ON ul.lista_universidad_id = un.id " +
            "WHERE un.id = :universidadId " +
            "LIMIT 1", nativeQuery = true)
    String findFirstUsernameByUniversidadIdNative(@Param("universidadId") Long universidadId);



    @Query("SELECT u FROM Usuario u WHERE u.nick = :nick")
    Usuario buscarUsuarioPorNick(@Param("nick")String nick);

    @Query(value = "SELECT r.token FROM refresh_token r " +
            "INNER JOIN usuario u ON u.refresh_token_id = r.id " +
            "WHERE u.id = :usuarioId", nativeQuery = true)
    String obtenerRefreshTokenPorUsuario(@Param("usuarioId") Long usuarioId);


    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.idsUsuariosNotificar WHERE u.id IN :ids")
    List<Usuario> buscarUsuariosPorListIds(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT u FROM Usuario u")
    List<Usuario> buscarTodosLosUsuarios();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuario_mensaje WHERE usuario_id = :usuarioId AND ids_usuarios_notificar = :notificarUsuarioId", nativeQuery = true)
    void eliminarNotificacionParaUsuarioId(@Param("usuarioId") Long usuarioId, @Param("notificarUsuarioId") Long notificarUsuarioId);


}
