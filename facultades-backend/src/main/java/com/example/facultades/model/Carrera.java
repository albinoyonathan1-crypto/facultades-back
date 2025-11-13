package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.util.INotificable;
import com.example.facultades.util.ItipoEntidad;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Carrera extends BaseEntity implements INotificable<Carrera>, ItipoEntidad {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;*/
    private String nombre;
    private String grado;
    private String duracion;
    private Long idUsuario;
    private boolean eliminacionLogica;
    private boolean activa =  true;

   // @OneToMany(cascade = CascadeType.ALL)
   // private List<Comentario> listaComentarios;

   /* @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrera_id") // Necesario para la clave foránea en Comentario
    private List<Comentario> listaComentarios;*/

    @OneToMany(cascade = CascadeType. ALL)
    private List<Comentario> listaComentarios;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Calificacion> listaCalificacion;

    @ManyToOne
    @JsonBackReference(value = "universidad-carrera")
    @JoinColumn(name = "universidad_id")
    private Universidad universidad;

    @Override
    @JsonIgnore
    public String getDetalleEvento() {
        return getNombre();
    }



    @Override
    @JsonIgnore
    public Class<?> obtenerTipoClase() {
        return Carrera.class;
    }
}
