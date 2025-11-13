package com.example.facultades.util;

import com.example.facultades.generics.BaseEntity;

public interface INotificable <E extends BaseEntity>{
    public String getDetalleEvento();
}
