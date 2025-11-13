package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TokenRecuperacionContrasenia extends BaseEntity {
    private String  token;
    private LocalDateTime fechaExpiracion;

    @OneToOne
    @JsonManagedReference(value = "tokenRecuperacionContrasenia-usuario")
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}
