package com.example.facultades.repository;


import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermisoRepository extends IGenericRepository<Permiso, Long> {
}
