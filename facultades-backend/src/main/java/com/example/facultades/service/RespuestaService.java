package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.dto.RespuestaDTO;
import com.example.facultades.enums.Socket;
import com.example.facultades.excepciones.ComentarioEliminadoException;
import com.example.facultades.excepciones.RespuestaVaciaException;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Respuesta;
import com.example.facultades.repository.IRespuestaRepository;
import com.example.facultades.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RespuestaService extends GenericService<Respuesta, Long> implements IRespuestaService, IEntidadAsociable<Respuesta> {

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private IRespuestaRepository respuestaRepository;



    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private  INotificacionService notificacionService;

    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Override
    public Respuesta update(Respuesta respuesta) {return  respuestaRepository.save(respuesta);}

    @Override
    public BaseDTO<Respuesta> convertirDTO(Respuesta respuesta) {
        // Mapeo inicial de Respuesta a RespuestaDTO
        RespuestaDTO respuestaDTO = modelMapper.map(respuesta, RespuestaDTO.class);

        // Asignar username del usuario principal si existe
        if (respuesta.getUsuario() != null && respuesta.getUsuario().getUsername() != null) {
            respuestaDTO.setUsername(respuesta.getUsuario().getUsername());
            respuestaDTO.setNickname(respuesta.getUsuario().getNick());
        }

        // Mapeo recursivo de la lista de respuestas
        if (respuesta.getListaRespuesta() != null) {
            List<RespuestaDTO> listaRespuestaDTO = new ArrayList<>();
            for (Respuesta r : respuesta.getListaRespuesta()) {
                listaRespuestaDTO.add((RespuestaDTO) this.convertirDTO(r));
            }
            respuestaDTO.setListaRespuesta(listaRespuestaDTO);
        }

        return respuestaDTO;
    }


    @Override
    public Respuesta converirEntidad(BaseDTO<Respuesta> DTO) {
        return modelMapper.map(DTO, Respuesta.class);
    }

    @Override
    public Respuesta save(Respuesta respuesta) {
        if(respuesta.getMensaje() == null || respuesta.getMensaje().isEmpty() || respuesta.getMensaje().trim().isEmpty()){
            throw new RespuestaVaciaException();
        }
        this.asociar(respuesta);
        Respuesta respuestaGuardada =  respuestaRepository.save(respuesta);
        if(respuestaGuardada.getId() != null){
            Notificacion notificacion = new Notificacion();
            notificacion.setRespuesta(true);
            DetalleNotificacion detalleNotificacion = Utili.generarDetalleNotificacion("Se creo una nueva respuesta", respuestaGuardada);
            notificacionService.enviarNotificacionByWebSocket(Socket.ADMIN_PREFIJO.getRuta(), detalleNotificacion);
            notificacionService.guardarNotificacionAdmin(respuestaGuardada.getId(), "Se creo una nueva respuesta", notificacion);
            return  respuestaGuardada;
        }else
            throw new RuntimeException("La respuesta no se pudo guardar");
    }

    @Override
    public void asociar(Respuesta respuesta) {
        //respuesta.setListaRespuesta(asociarEntidades.relacionar(respuesta.getListaRespuesta(), repositoryFactory.generarRepositorio(NombreRepositorio.RESPUESTA.getRepoName())));
        //respuesta.setListaReaccion(asociarEntidades.relacionar(respuesta.getListaReaccion(), repositoryFactory.generarRepositorio(NombreRepositorio.REACCION.getRepoName())));
    }

    @Override
    public ComentarioDTO findComentariosByListaRespuestaId(Long idRespuesta) {
        Optional<Comentario> comentarioOptional = respuestaRepository.findComentariosByListaRespuestaId(idRespuesta);
        Comentario comentario;

        if(comentarioOptional.isEmpty()){
            throw  new ComentarioEliminadoException();
        }else{
            comentario = comentarioOptional.get();
        }

        ComentarioDTO comentarioDTO = modelMapper.map(comentario, ComentarioDTO.class);

        if (comentario.getUsuario() != null && comentario.getUsuario().getUsername() != null) {
            comentarioDTO.setUsername(comentario.getUsuario().getUsername());
        }
        // Convertir la lista de respuestas del comentario usando el servicio de Respuesta
        if(comentario.getListaRespuesta() != null){
            List<RespuestaDTO> listaRespuestasDTO = new ArrayList<>();
            for (Respuesta respuesta : comentario.getListaRespuesta()) {
                RespuestaDTO respuestaDTO = (RespuestaDTO) this.convertirDTO(respuesta);
                listaRespuestasDTO.add(respuestaDTO);
            }
            comentarioDTO.setListaRespuesta(listaRespuestasDTO);
        }
        return comentarioDTO;
    }
}
