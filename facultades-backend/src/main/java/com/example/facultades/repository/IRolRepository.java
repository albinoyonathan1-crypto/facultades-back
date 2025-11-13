package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Reaccion;
import com.example.facultades.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolRepository extends IGenericRepository<Rol, Long> {
    public Rol findRolBynombreRol(String roleName);
}
