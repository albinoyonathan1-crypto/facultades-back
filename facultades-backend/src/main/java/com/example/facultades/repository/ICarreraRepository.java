package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Universidad;
import com.example.facultades.util.IFindComenByEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ICarreraRepository extends IGenericRepository<Carrera, Long>, IFindComenByEntity {

    //@Modifying
   // @Query("DELETE FROM UniversidadListaCarreras u WHERE u.listaCarrerasId = :id")
    //void eliminarAsociacionUniversidadCarrera(@Param("id") Long id);

    Page<Carrera> findByEliminacionLogicaFalse(Pageable pageable);

    @Query(value = "SELECT c.id, c.activa, c.duracion, c.eliminacion_logica, " +
            "c.grado, c.id_usuario, c.nombre, c.universidad_id, " +
            "AVG(n.nota) AS promedio " +
            "FROM carrera c " +
            "INNER JOIN universidad u ON c.universidad_id = u.id " +
            "INNER JOIN carrera_lista_calificacion clc ON clc.carrera_id = c.id " +
            "INNER JOIN calificacion n ON clc.lista_calificacion_id = n.id " +
            "WHERE u.eliminacion_logica = 0 " +
            "AND c.eliminacion_logica = 0 " +
            "GROUP BY c.id, c.activa, c.duracion, c.eliminacion_logica, " +
            "c.grado, c.id_usuario, c.nombre, c.universidad_id " +
            "ORDER BY promedio DESC",
            nativeQuery = true)
    List<Carrera> getTopCarreras(Pageable pageable);

    @Override
    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Carrera carrera " +
            "    JOIN carrera.listaComentarios uc " +
            "    WHERE carrera.id = :carrerraId" +
            ") " +
            "ORDER BY c.fecha DESC")
    List<Comentario> getAllComenByEntity(Long carrerraId);

}
