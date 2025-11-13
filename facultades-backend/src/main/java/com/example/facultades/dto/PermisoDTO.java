package com.example.facultades.dto;

import com.example.facultades.model.Permiso;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PermisoDTO extends BaseDTO<Permiso> {

    private String nombrePermiso;
    @JsonIgnore
    private ModelMapper modelMapper;

    public PermisoDTO(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    /*@Override
    public PermisoDTO convertirDTO(Permiso entidad) {
        if (entidad == null) {
            return null; //
        }
        return modelMapper.map(entidad, PermisoDTO.class);
    }

    @Override
    public Permiso convertirEntidad(BaseDTO dto) {
        if (!(dto instanceof PermisoDTO)) {
            throw new IllegalArgumentException("El DTO debe ser una instancia de PermisoDTO");
        }
        return modelMapper.map(dto, Permiso.class);
    }*/
}
