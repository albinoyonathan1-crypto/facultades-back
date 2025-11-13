package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.ReaccionDTO;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Reaccion;
import com.example.facultades.repository.IReaccionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReaccionService extends GenericService<Reaccion, Long> implements IReaccionService {

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public BaseDTO<Reaccion> convertirDTO(Reaccion reaccion) {
        return modelMapper.map(reaccion, ReaccionDTO.class);
    }

    @Override
    public Reaccion converirEntidad(BaseDTO<Reaccion> DTO) {
        return modelMapper.map(DTO, Reaccion.class);
    }
}
