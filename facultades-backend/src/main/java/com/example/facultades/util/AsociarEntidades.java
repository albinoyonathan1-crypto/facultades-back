package com.example.facultades.util;

import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.IGenericRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AsociarEntidades implements IAsociarEntidades{

    @Override
    public  <T extends BaseEntity> List<T> relacionar(List<T> listaOriginal, IGenericRepository repo){
        List<T> listaAsociada = new ArrayList<>();

        if(listaOriginal == null){
            return listaAsociada;
        }

        if(listaOriginal.isEmpty())
            return listaAsociada;

        for(T entidad : listaOriginal){
            Optional<T> entidadEncontrada = (Optional<T>) repo.findById(entidad.getId());

            if(entidadEncontrada.isPresent()) {
                listaAsociada.add(entidadEncontrada.get());
            }
        }

        return listaAsociada;
    }

}
