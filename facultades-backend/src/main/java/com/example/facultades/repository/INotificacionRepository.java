package com.example.facultades.repository;


import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Notificacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificacionRepository extends IGenericRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n JOIN n.listaUsuarios u WHERE u.id = :usuarioId")
    List<Notificacion> findNotificacionesByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT n FROM Notificacion n JOIN n.listaUsuarios u WHERE n.leida = false AND u.id = :usuarioId")
    List<Notificacion> findByLeidaFalse(Long usuarioId);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id IN (SELECT n.id FROM Notificacion n JOIN n.listaUsuarios u WHERE u.id = :usuarioId AND n.leida = false)")
    void marcarNotificacionesALeidas(@Param("usuarioId") Long usuarioId);

    @Query("SELECT u.id FROM Usuario u WHERE u.id <> :id")
    List<Long> filtrarId(Long id);
}
