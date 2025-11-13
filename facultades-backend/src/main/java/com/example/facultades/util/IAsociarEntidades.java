package com.example.facultades.util;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.IGenericRepository;

import java.util.List;

public interface IAsociarEntidades {
    public  <T extends BaseEntity> List<T> relacionar(List<T> listaOriginal, IGenericRepository repo);
}
