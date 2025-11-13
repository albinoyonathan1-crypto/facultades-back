package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Comentario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IComentarioRepository extends IGenericRepository<Comentario, Long> {
//
//    @Query("SELECT c " +
//            "FROM Comentario c " +
//            "WHERE c.id IN (" +
//            "    SELECT ulc.listaComentariosId " +
//            "    FROM Universidad.listaComentarios ulc " +
//            "    WHERE ulc.universidadId = :universidadId" +
//            ")")
//    List<Comentario> findComentariosByUniversidadId(@Param("universidadId") Long universidadId);


//    @Query("SELECT c " +
//            "FROM Comentario c " +
//            "WHERE c.id IN (" +
//            "    SELECT uc.id " +
//            "    FROM Universidad u " +
//            "    JOIN u.listaComentarios uc " +
//            "    WHERE u.id = :universidadId" +
//            ")")
//    List<Comentario> findComentariosByUniversidadId(@Param("universidadId") Long universidadId , Pageable pageable);


    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Universidad u " +
            "    JOIN u.listaComentarios uc " +
            "    WHERE u.id = :universidadId" +
            ") " +
            "ORDER BY c.fecha DESC") // Ordenar por fecha reciente
    List<Comentario> findComentariosByUniversidadId(@Param("universidadId") Long universidadId , Pageable pageable);

    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Carrera carrera " +
            "    JOIN carrera.listaComentarios uc " +
            "    WHERE carrera.id = :carrerraId" +
            ") " +
            "ORDER BY c.fecha DESC") // Ordenar por fecha reciente
    List<Comentario> findComentariosByCarreraId(@Param("carrerraId") Long carrerraId , Pageable pageable);


    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Universidad u " +
            "    JOIN u.listaComentarios uc " +
            "    WHERE u.id = :universidadId" +
            ") " +
            "ORDER BY c.fecha ASC") // Ordenar por fecha reciente
    List<Comentario> findComentariosByUniversidadIdAsc(@Param("universidadId") Long universidadId , Pageable pageable);

    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Carrera carrera " +
            "    JOIN carrera.listaComentarios uc " +
            "    WHERE carrera.id = :carrerraId" +
            ") " +
            "ORDER BY c.fecha ASC") // Ordenar por fecha reciente
    List<Comentario> findComentariosByCarreraIdAsc(@Param("carrerraId") Long carrerraId , Pageable pageable);

    @Query(value = """
            SELECT c.*, 
                   COALESCE(SUM(r.me_gusta), 0) AS total_me_gusta
            FROM comentario c
            INNER JOIN universidad_lista_comentarios uc ON c.id = uc.lista_comentarios_id
            LEFT JOIN comentario_lista_reaccion lr ON c.id = lr.comentario_id
            LEFT JOIN reaccion r ON lr.lista_reaccion_id = r.id  
            WHERE uc.universidad_id = :universidadId
            GROUP BY c.id, c.mensaje, c.fecha, c.editado, c.eliminado, c.usuario_id
            HAVING SUM(r.me_gusta) > 0 
            ORDER BY total_me_gusta DESC
            """, nativeQuery = true)
    List<Comentario> buscarComentariosOrdenadosMeGustaUniversidad(@Param("universidadId") Long universidadId, Pageable pageable);

    @Query(value = """
        SELECT c.*, 
               COALESCE(SUM(r.me_gusta), 0) AS total_me_gusta
        FROM comentario c
        INNER JOIN carrera_lista_comentarios cc ON c.id = cc.lista_comentarios_id
        LEFT JOIN comentario_lista_reaccion lr ON c.id = lr.comentario_id
        LEFT JOIN reaccion r ON lr.lista_reaccion_id = r.id  
        WHERE cc.carrera_id = :carreraId
        GROUP BY c.id, c.mensaje, c.fecha, c.editado, c.eliminado, c.usuario_id
        HAVING SUM(r.me_gusta) > 0
        ORDER BY total_me_gusta DESC
        """, nativeQuery = true)
    List<Comentario> buscarComentariosOrdenadosMeGustaCarrera(@Param("carreraId") Long carreraId, Pageable pageable);


    /*@Query(value = """
        SELECT c.* 
        FROM comentario c
        INNER JOIN comentario_lista_respuesta clr 
            ON clr.comentario_id = c.id
        WHERE clr.lista_respuesta_id = (
            SELECT rlr.respuesta_id 
            FROM respuesta_lista_respuesta rlr 
            WHERE rlr.lista_respuesta_id = :respuestaRespuestaId
        )
    """, nativeQuery = true)
    Optional<Comentario> findComentariosByRespuestaRespuestaId(@Param("respuestaRespuestaId") Long respuestaRespuestaId);*/
}






