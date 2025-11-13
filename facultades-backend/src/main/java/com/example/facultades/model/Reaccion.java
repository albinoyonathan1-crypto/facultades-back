package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Reaccion extends BaseEntity  {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;*/
    private int meGusta;
    private  int noMegusta;

    @ManyToOne
    @JsonBackReference(value = "usuario-reaccion")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


}
