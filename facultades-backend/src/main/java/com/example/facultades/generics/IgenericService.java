package com.example.facultades.generics;

import com.example.facultades.dto.BaseDTO;

import java.util.List;
import java.util.Optional;

public interface IgenericService <E extends BaseEntity, ID extends Number>{
    public List<E> getAll();
    public E save(E entidad);
    public Optional<E> findById(ID id);
    public void delete(ID id);
    public E update(E entidad);
    public BaseDTO<E> convertirDTO(E entiendad);
    public E converirEntidad(BaseDTO<E> DTO);
}
