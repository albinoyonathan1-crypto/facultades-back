package com.example.facultades.dto;

import com.example.facultades.generics.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class BaseDTO<E extends BaseEntity> {
   /* public abstract BaseDTO convertirDTO(E entidad);
    public abstract E convertirEntidad(BaseDTO dto);*/
    private long id;
}
