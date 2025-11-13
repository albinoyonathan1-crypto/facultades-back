package com.example.facultades.service;


import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.RolDTO;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Rol;
import com.example.facultades.repository.IRolRepository;
import com.example.facultades.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService extends GenericService<Rol, Long> implements IRolService, IEntidadAsociable<Rol> {

    @Autowired
    private IRolRepository rolRepo;

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Override
    public Rol update(Rol rol) {
        return this.save(rol);
    }

    @Override
    public BaseDTO<Rol> convertirDTO(Rol rol) {
        return modelMapper.map(rol, RolDTO.class);
    }

    @Override
    public Rol converirEntidad(BaseDTO<Rol> DTO) {
        return modelMapper.map(DTO, Rol.class);
    }

    @Override
    public Rol save(Rol rol) {
        this.asociar(rol);
        return rolRepo.save(rol);
    }

    @Override
    public Rol findRolByName(String name) {
        return rolRepo.findRolBynombreRol(name);
    }

    @Override
    public void asociar(Rol rol ) {
        rol.setListaPermiso(asociarEntidades.relacionar(rol.getListaPermiso(), repositoryFactory.generarRepositorio(NombreRepositorio.PERMISO.getRepoName())));
    }

}
