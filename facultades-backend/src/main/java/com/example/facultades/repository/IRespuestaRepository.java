package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Reaccion;
import com.example.facultades.model.Respuesta;
import com.example.facultades.model.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRespuestaRepository extends IGenericRepository<Respuesta, Long> {



    @Query("SELECT u " +
            "FROM Universidad u " +
            "JOIN u.listaCarreras lc " +
            "WHERE lc.id = :listaCarrerasId")
    Universidad getIDUniversidadPorCarreraId(@Param("listaCarrerasId") Long carreraId);

    @Query("SELECT DISTINCT c " +
            "FROM Comentario c " +
            "JOIN c.listaRespuesta cr " + // Relación entre Comentario y listaRespuestas
            "WHERE cr.id = :listaRespuestaId")
    Optional<Comentario> findComentariosByListaRespuestaId(@Param("listaRespuestaId") Long listaRespuestaId);



}
