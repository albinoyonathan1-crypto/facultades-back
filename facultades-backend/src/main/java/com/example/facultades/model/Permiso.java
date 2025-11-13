package com.example.facultades.model;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.util.INotificable;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
public class Permiso extends BaseEntity implements INotificable<Permiso> {
   /* @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long idPermiso;*/
    private String nombrePermiso;


    @Override
    public String getDetalleEvento() {
        return this.getNombrePermiso();
    }
}
