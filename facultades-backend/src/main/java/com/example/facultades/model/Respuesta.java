package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.util.INotificable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Respuesta extends BaseEntity  implements INotificable<Respuesta> {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;*/
    private String mensaje;
    private Date fecha;
    private Long idComentarioPadre;
    private boolean editado;
    private boolean eliminado;

    @OneToMany()
  //  @JsonManagedReference(value = "respuesta")
    private List<Respuesta> listaRespuesta = new ArrayList<>();

   /* @ManyToOne
    @JsonBackReference(value = "respuesta")
    private Respuesta respuestaPadre; // Referencia a la respuesta padre*/

    @OneToMany()
    private List<Reaccion> listaReaccion;

    @ManyToOne
    @JsonBackReference(value = "usuario-respuesta")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Override
    public String getDetalleEvento() {
        return this.getMensaje();
    }
}
