package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Reaccion;
import com.example.facultades.model.Universidad;
import com.example.facultades.util.IFindComenByEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUniversidadRepository extends IGenericRepository<Universidad, Long>, IFindComenByEntity {

    Page<Universidad> findByEliminacionLogicaFalse(Pageable pageable);


    @Query(value = "Select nombre FROM universidad WHERE nombre = :nombreUniversidad", nativeQuery = true)
    String buscarUniversidadPorNombre(String nombreUniversidad);


    //@Query("SELECT u FROM Universidad u ORDER BY u.id ASC LIMIT 3")
    //List<Universidad> findImagenesPrimerasTres();

    @Query("SELECT u, AVG(c.nota) AS promedio " +
            "FROM Universidad u " +
            "JOIN u.listaCalificacion c " +
            "GROUP BY u " +
            "ORDER BY promedio DESC")
    List<Universidad> getTopUniversidades(Pageable pageable);

    @Query("SELECT u " +
            "FROM Universidad u " +
            "JOIN u.listaCarreras lc " +
            "WHERE lc.id = :listaCarrerasId")
    Universidad getIDUniversidadPorCarreraId(@Param("listaCarrerasId") Long carreraId);

    @Query("SELECT u AS universidad " +
            "FROM Universidad u " +
            "WHERE u.nombre LIKE %:nombreUniversidad%")
    List<Universidad> getUniversidadByName(String nombreUniversidad);

    @Override
    @Query("SELECT c " +
            "FROM Comentario c " +
            "WHERE c.id IN (" +
            "    SELECT uc.id " +
            "    FROM Universidad u " +
            "    JOIN u.listaComentarios uc " +
            "    WHERE u.id = :universidadId" +
            ") " +
            "ORDER BY c.fecha DESC") // Ordenar por fecha descendente
    List<Comentario> getAllComenByEntity(Long universidadId);

    @Query(value = "SELECT id FROM universidad WHERE eliminacion_logica = 0", nativeQuery = true)
    List<Long> buscarUniversidadesActivas();

}
