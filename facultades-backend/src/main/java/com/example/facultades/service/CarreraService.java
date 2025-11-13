package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.CarreraDTO;
import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.enums.MensajeNotificacionAdmin;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.enums.Socket;
import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Universidad;
import com.example.facultades.repository.ICarreraRepository;
import com.example.facultades.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarreraService extends GenericService<Carrera, Long> implements ICarreraService, IEntidadAsociable<Carrera> {

    @Autowired
    private ICarreraRepository carreraRepository;

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IgenericService<Comentario,Long> comentarioService;

    @Autowired
    private ComentarioService comenService;

    @Autowired
    @Lazy
    private EnvioNotificacion envioNotificacion;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Carrera save(Carrera carrera) {
        this.asociar(carrera);
        Carrera carreraGuardada =  carreraRepository.save(carrera);
        if(carreraGuardada.getId() != null){
            Notificacion notificacion = new Notificacion();
            notificacion.setCarrera(true);
            Utili.manejarNotificacionAdmin(MensajeNotificacionAdmin.CREACION_CARRERA.getNotificacion() +": ("+carreraGuardada.getNombre()+")", carreraGuardada, notificacionService, notificacion);
            return carreraGuardada;
        }
        //Manejar error en caso no se guarde
        return carreraGuardada;
    }


    @Override
    public Carrera update(Carrera carrera) {
        if (esNuevoComentario(carrera)) {
            enviarNotificacionesNuevoComentario(carrera);
        }
        this.asociar(carrera);
        return carreraRepository.save(carrera);
    }

    private boolean esNuevoComentario(Carrera carrera) {
        return Utili.verificarInsercionNuevoComentario(carrera, carreraRepository, carrera.getListaComentarios());
    }

    private void enviarNotificacionesNuevoComentario(Carrera carrera) {
        Long idUniversidad = carrera.getUniversidad().getId();
        List<Comentario> comentarios = carrera.getListaComentarios();

        if (comentarios.isEmpty()) {
            return;
        }

        Comentario comentario = comentarios.get(comentarios.size() - 1);

        Notificacion notificacion = new Notificacion();
        notificacion.setComentarioAgregadoCarrera(true);

        Notificacion notificacionAdmin = new Notificacion();
        notificacionAdmin.setComentario(true);

        notificacionService.guardarNotificacionUsuario(
                carrera.getIdUsuario(),
                idUniversidad,
                "Han publicado un nuevo comentario en tu carrera: (" + carrera.getNombre() + ")",
                notificacion
        );

        notificacionService.guardarNotificacionAdmin(
                comentario.getId(),
                "Se ha creado un nuevo comentario",
                notificacionAdmin
        );

        DetalleNotificacion detalleNotificacion = new DetalleNotificacion(
                "Se ha creado un nuevo comentario",
                comentario.getMensaje(),
                comentario.getId()
        );

        notificacionService.enviarNotificacionByWebSocket(Socket.ADMIN_PREFIJO.getRuta(), detalleNotificacion);
    }

    @Override
    public BaseDTO<Carrera> convertirDTO(Carrera carrera) {
        CarreraDTO carreraDTO = modelMapper.map(carrera, CarreraDTO.class);

        if(carrera.getListaComentarios() != null){
            List<ComentarioDTO> comentarioDTOS = new ArrayList<>();
            for (Comentario comentario : carrera.getListaComentarios()){
                comentarioDTOS.add((ComentarioDTO) comenService.convertirDTO(comentario));
            }
            carreraDTO.setListaComentarios(comentarioDTOS);
        }

        return carreraDTO;
    }

    /*
        @Override
    public BaseDTO<Universidad> convertirDTO(Universidad universidad) {
        UniversidadDTO universidadDTO = modelMapper.map(universidad, UniversidadDTO.class);

        if(universidad.getListaComentarios() != null){
            List<ComentarioDTO> comentarioDTOS = new ArrayList<>();
            for (Comentario comentario : universidad.getListaComentarios()){
                comentarioDTOS.add((ComentarioDTO) comenService.convertirDTO(comentario));
            }
            universidadDTO.setListaComentarios(comentarioDTOS);
        }

        return universidadDTO;
    }
    * */

    @Override
    public Carrera converirEntidad(BaseDTO<Carrera> DTO) {
        CarreraDTO carreraDTO = (CarreraDTO) DTO;
        Carrera carrera = modelMapper.map(carreraDTO, Carrera.class);
        return  carrera;
    }


    @Override
    public void asociar(Carrera carrera) {
        carrera.setListaComentarios(asociarEntidades.relacionar(carrera.getListaComentarios(), repositoryFactory.generarRepositorio(NombreRepositorio.COMENTARIO.getRepoName())));
        carrera.setListaCalificacion(asociarEntidades.relacionar(carrera.getListaCalificacion(), repositoryFactory.generarRepositorio(NombreRepositorio.CALIFICACION.getRepoName())));
    }

    @Override
    public Page<Carrera> obtenerCarrerasPaginadas(Pageable pageable) {
        return carreraRepository.findAll(pageable);
    }

    @Override
    public Page<Carrera> getTopCarreras(int cantidadRegistros, int pagina) {
        Pageable pageable = PageRequest.of(cantidadRegistros, pagina);
        Page<Carrera> carreras = carreraRepository.findByEliminacionLogicaFalse(pageable);
        return carreras;
    }


}
