package com.example.facultades.service;

import com.example.facultades.dto.MensajeDTO;
import com.example.facultades.model.Mensaje;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMensajeService {

    List<MensajeDTO> findByEmisorAndReceptor(Long emisor, Long receptor);
}
