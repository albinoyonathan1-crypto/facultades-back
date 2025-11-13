package com.example.facultades.service;

import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Respuesta;

import java.util.List;
import java.util.Optional;

public interface IRespuestaService  {
    public ComentarioDTO findComentariosByListaRespuestaId(Long idRespuesta);

}
