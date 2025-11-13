package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
public class Mensaje extends BaseEntity {
    String contenido;
    Date fecha = new Date();
    boolean leida;

    @ManyToOne
    @JoinColumn(name = "emisor", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "receptor", nullable = false)
    private Usuario receptor;

}
