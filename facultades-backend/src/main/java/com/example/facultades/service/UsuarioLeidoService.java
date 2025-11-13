package com.example.facultades.service;


import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.UsuarioLeidoDTO;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Universidad;
import com.example.facultades.model.UsuarioLeido;
import com.example.facultades.repository.IUsuarioLeidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioLeidoService extends GenericService<UsuarioLeido, Long> implements IUsuarioLeidoService{

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public BaseDTO<UsuarioLeido> convertirDTO(UsuarioLeido usuarioLeido) {
        return modelMapper.map(usuarioLeido, UsuarioLeidoDTO.class);
    }

    @Override
    public UsuarioLeido converirEntidad(BaseDTO<UsuarioLeido> DTO) {
        return null;
    }
}
