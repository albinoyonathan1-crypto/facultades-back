package com.example.facultades.service;

import com.example.facultades.dto.*;
import com.example.facultades.enums.MensajeNotificacionAdmin;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.enums.Socket;
import com.example.facultades.excepciones.RegistroExistenteException;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Universidad;
import com.example.facultades.repository.IUniversidadRepository;
import com.example.facultades.util.*;
import org.aspectj.weaver.ast.Not;
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
public class UniversidadService extends GenericService<Universidad, Long> implements IUniversidadService, IEntidadAsociable<Universidad> {

    @Autowired
    private IUniversidadRepository universidadRepository;

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IgenericService<Comentario, Long> comentarioService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ComentarioService comenService;

    @Autowired
    @Lazy
    private EnvioNotificacion envioNotificacion;


    @Override
    public Universidad update(Universidad universidad) {
        if (Utili.verificarInsercionNuevoComentario(universidad, universidadRepository, universidad.getListaComentarios())) {
            envioNotificacion.enviarGuardarNotificacionNuevoComentario(universidad.getNombre(),universidad, universidad.getId(),universidad.getListaComentarios(), comentarioService, notificacionService);
        }

        Carrera carreraAgregada = this.verificarInsercionCarrera(universidad);
        if(carreraAgregada != null){
            Notificacion notificacion = new Notificacion();
            notificacion.setCarreraAgregada(true);
            notificacionService.guardarNotificacionUsuario(universidad.getUsuario().getId(), carreraAgregada.getId(),"Han agregado una nueva carrera: ("+carreraAgregada.getNombre() +") a tu universidad: " +"(" + universidad.getNombre() +")", notificacion);
           // notificacionService.enviarNotificacionByWebSocket();
        }
        this.asociar(universidad);
        return universidadRepository.save(universidad);
    }

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

    @Override
    public Universidad converirEntidad(BaseDTO<Universidad> DTO) {
        Universidad universidad = modelMapper.map(DTO, Universidad.class);
        return universidad;
    }

    @Override
    public Universidad save(Universidad universidad){
        this.asociar(universidad);
        Universidad universidadGuardada = universidadRepository.save(universidad);
        if(universidadGuardada.getId() != null){
            Notificacion notificacion = new Notificacion();
            notificacion.setUniversidad(true);
            Utili.manejarNotificacionAdmin(MensajeNotificacionAdmin.CREACION_UNIVERSIDAD.getNotificacion() + ": ( "+ universidadGuardada.getNombre()+" )", universidadGuardada, notificacionService, notificacion);
            return universidadGuardada;
        }
        //manejar error en caso de no guarda la uni
        return universidadGuardada;
    }

    public boolean universidadExistente(String nombreUniversidad) throws RegistroExistenteException {
        String nombre = universidadRepository.buscarUniversidadPorNombre(nombreUniversidad);
        if(nombre == null)
            return false;
        throw new RegistroExistenteException("La universidad que desas ingresar ya existe");
    }


    @Override
    public List<Universidad> obtenerTopUniversidades(int cantidadRegistros, int pagina) {
        Pageable pageable = PageRequest.of(cantidadRegistros, pagina);
        List<Universidad> universidades = universidadRepository.getTopUniversidades(pageable);
        return universidades;
    }

    @Override
    public Page<Universidad> obtenerUniversidadesPaginadas(Pageable pageable) {
        return universidadRepository.findByEliminacionLogicaFalse(pageable);
    }

    @Override
    public Universidad getIDUniversidadPorCarreraId(Long carreraId) {
        return  universidadRepository.getIDUniversidadPorCarreraId(carreraId);
    }

    @Override
    public List<Universidad> getUniversidadByName(String nombreUniversidad) {
        List<Universidad> ListaUniversidades;
        ListaUniversidades = universidadRepository.getUniversidadByName(nombreUniversidad);
        return ListaUniversidades;
    }

    @Override
    public void asociar(Universidad universidad) {
        universidad.setListaComentarios(asociarEntidades.relacionar(universidad.getListaComentarios(), repositoryFactory.generarRepositorio(NombreRepositorio.COMENTARIO.getRepoName())));
        universidad.setListaCarreras(asociarEntidades.relacionar(universidad.getListaCarreras(), repositoryFactory.generarRepositorio(NombreRepositorio.CARRERA.getRepoName())));
        universidad.setListaCalificacion(asociarEntidades.relacionar(universidad.getListaCalificacion(), repositoryFactory.generarRepositorio(NombreRepositorio.CALIFICACION.getRepoName())));
    }

    @Override
    public Carrera verificarInsercionCarrera(Universidad universidad) {
        Universidad universidadBuscada = this.findById(universidad.getId()).orElse(null);

        if (universidadBuscada == null || universidadBuscada.getListaCarreras() == null || universidad.getListaCarreras() == null) {
            return null; // Retorna null si la universidad no existe o si alguna lista es nula
        }

        List<Carrera> listaCarrerasEnDb = universidadBuscada.getListaCarreras();
        List<Carrera> listaCarrerasActual = universidad.getListaCarreras();

        if (!listaCarrerasEnDb.isEmpty() && !listaCarrerasActual.isEmpty() && listaCarrerasActual.size() > listaCarrerasEnDb.size()) {
            return listaCarrerasActual.get(listaCarrerasActual.size() - 1);
        }

        return null;
    }

    @Override
    public List<Long> buscarUniversidadesActivas() {
        return universidadRepository.buscarUniversidadesActivas();
    }

}
