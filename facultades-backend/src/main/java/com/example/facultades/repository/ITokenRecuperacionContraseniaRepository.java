package com.example.facultades.repository;

import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.TokenRecuperacionContrasenia;
import com.example.facultades.model.TokenVerificacionEmail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ITokenRecuperacionContraseniaRepository extends IGenericRepository<TokenRecuperacionContrasenia, Long> {
    @Query("SELECT t FROM TokenRecuperacionContrasenia t WHERE t.token = :token")
    TokenRecuperacionContrasenia findByToken(String token);

    @Query("SELECT t FROM TokenRecuperacionContrasenia t " +
            "INNER JOIN t.usuario u " +
            "WHERE u.id = :usuarioId")
    TokenRecuperacionContrasenia findTokenRCbyUsuarioId(Long usuarioId);
}
