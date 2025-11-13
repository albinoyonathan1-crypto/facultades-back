package com.example.facultades.generics;

import com.example.facultades.dto.BaseDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public abstract class GenericService <E extends BaseEntity, ID extends Number> implements IgenericService<E, ID>{

    @Autowired
    private IGenericRepository<E,ID> genericRepository;

    @Override
    public List<E> getAll(){
        return genericRepository.findAll();
    }

    @Override
    public E save(E entidad){
        return genericRepository.save(entidad);
    }

    @Override
    public Optional<E> findById(ID id){
        return genericRepository.findById(id);
    }

    @Override
    public void delete(ID id){
        genericRepository.deleteById(id);
    }

    @Override
    public E update(E entidad){
        return this.save(entidad);
    }
    //public abstract void procersarLista(E entidad);
}
