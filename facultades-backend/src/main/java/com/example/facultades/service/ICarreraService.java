package com.example.facultades.service;

import com.example.facultades.model.Carrera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICarreraService  {
   Page<Carrera> obtenerCarrerasPaginadas(Pageable pageable);
   public Page<Carrera> getTopCarreras(int cantidadRegistros, int pagina);

}
