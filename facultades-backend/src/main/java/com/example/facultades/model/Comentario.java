package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.util.INotificable;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Comentario extends BaseEntity  implements INotificable<Comentario> {
   /* @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;*/
    private Date fecha;
    @Column(length = 1000) // Por ejemplo, aquí estamos especificando una longitud máxima de 1000 caracteres
    private String mensaje;
    private boolean editado;
    private boolean eliminado;
    @OneToMany()
    private List<Reaccion> listaReaccion;

    @OneToMany()
    private List<Respuesta> listaRespuesta;

    /*@OneToMany
    private List<Comentario> listaComentario;*/

    @ManyToOne
    @JsonBackReference(value = "usuario-comentario")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    @Override
    @JsonIgnore
    public String getDetalleEvento() {
        return getMensaje();
    }
}
