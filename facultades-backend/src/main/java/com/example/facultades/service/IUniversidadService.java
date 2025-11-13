package com.example.facultades.service;

import com.example.facultades.excepciones.RegistroExistenteException;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Universidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUniversidadService  {
    public List<Universidad>obtenerTopUniversidades(int cantidadRegistros, int pagina);
    Page<Universidad> obtenerUniversidadesPaginadas(Pageable pageable);
    public Universidad getIDUniversidadPorCarreraId(Long listaCarrerasId);
    public List<Universidad> getUniversidadByName(String nombreUniversidad);
    public Carrera verificarInsercionCarrera(Universidad universidad);
    public List<Long> buscarUniversidadesActivas();
}
