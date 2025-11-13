package com.example.facultades.repository;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.TokenVerificacionEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ITokenVerificacionEmailRepository extends IGenericRepository<TokenVerificacionEmail, Long> {
    @Query("SELECT t FROM TokenVerificacionEmail t WHERE t.token = :token")
    TokenVerificacionEmail findByToken(String token);
}
