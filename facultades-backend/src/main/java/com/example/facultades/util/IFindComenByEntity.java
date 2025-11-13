package com.example.facultades.util;

import com.example.facultades.model.Comentario;

import java.util.List;

public interface IFindComenByEntity {
    List<Comentario> getAllComenByEntity(Long id);
}
