package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Notificacion extends BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private  Long id;*/
    private String informacion;
    private Long idRedireccionamiento;
    private Boolean leida;
    private boolean carrera;
    private boolean comentario;
    private boolean usuario;
    private boolean universidad;
    private boolean permiso;
    private boolean respuesta;
    private boolean publicacionComentada;
    private boolean respuestaComentarioRecibida;
    private boolean  respuestaAunaRespuesta;
    private boolean carreraAgregada;
    private boolean comentarioAgregadoCarrera;
    private boolean auditada;
    private Date fecha;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "notificaciones_usuarios", joinColumns = @JoinColumn(name = "notificacion_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private List<Usuario> listaUsuarios = new ArrayList<>();

    @OneToMany()
    private List<UsuarioLeido> listaDeusuariosLeidos = new ArrayList<>();
}
