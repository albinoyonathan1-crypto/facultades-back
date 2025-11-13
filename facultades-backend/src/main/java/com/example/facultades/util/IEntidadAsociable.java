package com.example.facultades.util;

import com.example.facultades.generics.BaseEntity;

public interface IEntidadAsociable<E extends BaseEntity> {
    public  void asociar(E entidad);
}
