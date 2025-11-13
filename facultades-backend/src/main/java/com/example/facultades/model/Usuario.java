package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.util.INotificable;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Usuario extends BaseEntity implements INotificable<Usuario> {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idUsuario;*/
    private String nick;
    private String username;
    private String password;
    private String imagen;
    private int infracciones;
    private boolean baneada;
    private boolean enable;
    private boolean accountNotExpired;
    private boolean accountNotLocked;
    private boolean credentialNotExpired;
    private boolean emailVerified;


    @ElementCollection
    @CollectionTable(name = "usuario_mensaje", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "ids_usuarios_notificar")
    private List<Long> idsUsuariosNotificar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private List<Rol> listaRoles = new ArrayList<>();

    @OneToMany()
    @JsonManagedReference(value = "usuario-universidad")
    private List<Universidad> listaUniversidad;

    @OneToMany()
    @JsonManagedReference(value = "usuario-calificacion")
    private List<Calificacion> listaCalificacion;

    @OneToOne()
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference(value = "usuario-comentario")
    private List<Comentario> listaComentarios;

    @OneToMany()
    @JsonManagedReference(value = "usuario-respuesta")
    private  List<Respuesta> listaRespuesta;

    @OneToMany()
    @JsonManagedReference(value = "usuario-reaccion")
    private List<Reaccion> listaReaccion;

    @OneToOne(mappedBy = "usuario")
    @JsonBackReference(value = "tokenRecuperacionContrasenia-usuario")
    private TokenRecuperacionContrasenia tokenRecuperacionContrasenia;

    @Override
    @JsonIgnore
    public String getDetalleEvento() {
        return getUsername();
    }
}
