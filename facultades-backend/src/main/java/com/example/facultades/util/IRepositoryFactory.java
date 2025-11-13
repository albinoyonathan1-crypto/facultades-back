package com.example.facultades.util;

import com.example.facultades.generics.IGenericRepository;

public interface IRepositoryFactory {
    public IGenericRepository generarRepositorio(String nombreRepositorio);
}
