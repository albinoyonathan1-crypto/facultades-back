package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Mensaje;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMensajeRepository extends IGenericRepository<Mensaje, Long> {
    /*@Query("SELECT m FROM Mensaje m WHERE m.emisor.id = :emisor AND m.receptor.id = :receptor")
    List<Mensaje> findByEmisorAndReceptor(@Param("emisor") Long emisor, @Param("receptor") Long receptor);*/

    @Query("SELECT m FROM Mensaje m WHERE (m.emisor.id = :idEmisor AND m.receptor.id = :idReceptor) OR (m.emisor.id = :idReceptor AND m.receptor.id = :idEmisor)")
    List<Mensaje> findMensajesEntreUsuarios(@Param("idEmisor") Long idEmisor, @Param("idReceptor") Long idReceptor);



}
