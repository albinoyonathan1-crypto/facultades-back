package com.example.facultades.repository;


import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Reaccion;
import com.example.facultades.model.UsuarioLeido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioLeidoRepository extends IGenericRepository<UsuarioLeido, Long> {
}
