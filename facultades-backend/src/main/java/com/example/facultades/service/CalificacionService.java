package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.CalificacionDTO;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Calificacion;
import com.example.facultades.repository.ICalificacionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CalificacionService extends GenericService<Calificacion, Long> implements ICalificacionService{

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BaseDTO<Calificacion> convertirDTO(Calificacion calificacion) {
        return modelMapper.map(calificacion, CalificacionDTO.class);
    }

    @Override
    public Calificacion converirEntidad(BaseDTO<Calificacion> DTO) {
        return modelMapper.map(DTO, Calificacion.class);
    }
}
