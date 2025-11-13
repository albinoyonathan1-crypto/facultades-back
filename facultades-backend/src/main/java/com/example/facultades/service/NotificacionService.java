package com.example.facultades.service;
import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.dto.NotificacionDTO;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.enums.Socket;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Usuario;
import com.example.facultades.model.UsuarioLeido;
import com.example.facultades.repository.INotificacionRepository;
import com.example.facultades.repository.IUsuarioLeidoRepository;
import com.example.facultades.repository.IUsuarioRepository;
import com.example.facultades.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NotificacionService extends GenericService<Notificacion, Long> implements INotificacionService, IEntidadAsociable<Notificacion> {

    @Autowired
    private INotificacionRepository notificacionRepo;

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Autowired
    private IUsuarioRepository usuRepo;

    @Autowired
    private IUsuarioLeidoRepository usuarioLeidoRepository;

    @Autowired
    private IgenericService<UsuarioLeido, Long> usuarioLeidoService;

    @Autowired
    private ModelMapper modelMapper;


    public NotificacionService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notificacion save(Notificacion notificacion) {
        this.asociar(notificacion);
        notificacion.setFecha(new Date());
        return notificacionRepo.save(notificacion);
    }

    @Override
    public Notificacion update(Notificacion notificacion) {
        return this.save(notificacion);
    }

    @Override
    public BaseDTO<Notificacion> convertirDTO(Notificacion notificacion) {
         NotificacionDTO notificacionDTO =  modelMapper.map(notificacion, NotificacionDTO.class);
         List<Long> listaIdsUsuarios = new ArrayList<>();
        List<Long> listaUsuariosLeidsIds = new ArrayList<>();

         for(Usuario usuario : notificacion.getListaUsuarios()){
             listaIdsUsuarios.add(usuario.getId());
         }
         notificacionDTO.setListaUsuariosIds(listaIdsUsuarios);

        for(UsuarioLeido usuarioLeido : notificacion.getListaDeusuariosLeidos()){
            listaUsuariosLeidsIds.add(usuarioLeido.getId());
        }
        notificacionDTO.setListaDeusuariosLeidosIds(listaUsuariosLeidsIds);
        return  notificacionDTO;
    }


    @Override
    public Notificacion converirEntidad(BaseDTO<Notificacion> DTO) {
        if (DTO == null) {
            throw new IllegalArgumentException("El DTO no puede ser nulo");
        }
        NotificacionDTO notificacionDTO = (NotificacionDTO) DTO;
        Notificacion notificacion = modelMapper.map(DTO, Notificacion.class);
        List<Usuario> listaUsuarios = new ArrayList<>();
        List<UsuarioLeido> listaUsuariosLeidos = new ArrayList<>();

        for (Long id : notificacionDTO.getListaUsuariosIds()) {
            Usuario usuario = usuRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
            listaUsuarios.add(usuario);
        }
        notificacion.setListaUsuarios(listaUsuarios);

        for (Long id : notificacionDTO.getListaDeusuariosLeidosIds()) {
            UsuarioLeido usuarioLeido = usuarioLeidoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario leído no encontrado con ID: " + id));
            listaUsuariosLeidos.add(usuarioLeido);
        }
        notificacion.setListaDeusuariosLeidos(listaUsuariosLeidos);
        return notificacion;
    }



    @Override
    public void asociar(Notificacion notificacion) {
        notificacion.setListaUsuarios(asociarEntidades.relacionar(notificacion.getListaUsuarios(), repositoryFactory.generarRepositorio(NombreRepositorio.USUARIO.getRepoName())));
        notificacion.setListaDeusuariosLeidos(asociarEntidades.relacionar(notificacion.getListaDeusuariosLeidos(), repositoryFactory.generarRepositorio(NombreRepositorio.USUARIO_LEIDO.getRepoName())));
    }


    @Override
    public void enviarNotificacionByWebSocket(String topic,DetalleNotificacion detalleNotificacion) {
        ObjectMapper objectMapper = new ObjectMapper();
       // DetalleNotificacion detalleNotificacion = new DetalleNotificacion(evento, detalle, id);
        try {
            String detalleJson = objectMapper.writeValueAsString(detalleNotificacion);
            messagingTemplate.convertAndSend(topic, detalleJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void guardarNotificacionAdmin(Long idEvento, String informacion, Notificacion notificacion) {
        List<UsuarioLeido> listaLeidos = inicializarListaUsuariosLeidos();
        List<Usuario> listaUsuarios = obtenerUsuariosAdmin();

        configurarNotificacion(notificacion, idEvento, informacion, listaLeidos, listaUsuarios);
        this.save(notificacion);
    }

    @Override
    public void guardarNotificacionUsuario(Long idUsuario, Long idEvento, String informacion, Notificacion notificacion) {
        List<UsuarioLeido> listaLeidos = crearListaUsuariosLeidos(idUsuario);
        List<Usuario> listaUsuario = obtenerUsuarioPorId(idUsuario);

        configurarNotificacion(notificacion, idEvento, informacion, listaLeidos, listaUsuario);
        Notificacion notificacionGuardada = this.save(notificacion); // Guardar y obtener la entidad persistida

        if(notificacionGuardada.getId() != null){
            DetalleNotificacion detalleNotificacion = new DetalleNotificacion("",informacion,idEvento);
            this.enviarNotificacionByWebSocket(Socket.TOPICO_PERSONAL.getRuta() +"/"+idUsuario, detalleNotificacion);
        }
    }


    private List<UsuarioLeido> inicializarListaUsuariosLeidos() {
        return new ArrayList<>();
    }

    private List<Usuario> obtenerUsuariosAdmin() {
        return usuRepo.findUserEntityByListaRolesNombreRol("ADMIN");
    }

    private List<UsuarioLeido> crearListaUsuariosLeidos(Long idUsuario) {
        List<Long> listaIdsUsuariosLeidos = notificacionRepo.filtrarId(idUsuario);
        List<UsuarioLeido> listaLeidos = new ArrayList<>();
        for (Long id : listaIdsUsuariosLeidos) {
            UsuarioLeido usuarioLeido = new UsuarioLeido();
            usuarioLeido.setIdUsuario(id);
            listaLeidos.add(usuarioLeidoService.save(usuarioLeido));
        }
        return listaLeidos;
    }

    private List<Usuario> obtenerUsuarioPorId(Long idUsuario) {
        List<Usuario> listaUsuario = new ArrayList<>();
        usuRepo.findById(idUsuario).ifPresent(listaUsuario::add);
        return listaUsuario;
    }

    private void configurarNotificacion(Notificacion notificacion, Long idEvento, String informacion,
                                        List<UsuarioLeido> listaLeidos, List<Usuario> listaUsuarios) {
        notificacion.setListaDeusuariosLeidos(listaLeidos);
        notificacion.setIdRedireccionamiento(idEvento);
        notificacion.setLeida(false);
        notificacion.setInformacion(informacion);
        notificacion.setListaUsuarios(listaUsuarios);
    }

    //es esteeeeee
    @Override
    public List<NotificacionDTO> getNotificacionByIdUser(Long idUser) {
        List<Notificacion> listaNotificaciones = notificacionRepo.findNotificacionesByUsuarioId(idUser);

        List<NotificacionDTO> listaNotificacionesDTO = transformarListaNotificacionesADTO(listaNotificaciones);
        return ordenarListaDTOMasReciente(listaNotificacionesDTO);
    }

    @Override
    public String eliminarUsuarioNotificacion(Long idNotificacion, Long idUsuario) {
        Notificacion notificacion = this.findById(idNotificacion).orElse(null);
        List<Usuario> nuevaListaUsu = new ArrayList<>();
        if(notificacion != null){
            for (Usuario usuario : notificacion.getListaUsuarios()){
                if(usuario.getId() != idUsuario){
                    nuevaListaUsu.add(usuario);
                }
            }
            notificacion.setListaUsuarios(nuevaListaUsu);
            this.update(notificacion);
            return "Notificación eliminada";
        }
        return "La notificación no existe";
    }

    @Override
    public List<NotificacionDTO> findNotificacionesNoLeidasPorUsuario(Long userId) {
        List<Notificacion> listaNotificaciones = notificacionRepo.findNotificacionesByUsuarioId(userId);
        List<Notificacion> nuevaLista = new ArrayList<>();

        for (Notificacion notificacion : listaNotificaciones) {
            if (!isNotificacionLeidaPorUsuario(notificacion, userId)) {
                nuevaLista.add(notificacion);
            }
        }

        // Convierte la lista de notificaciones a DTO y ordena por fecha
        List<NotificacionDTO> listaNotificacionesDTO = transformarListaNotificacionesADTO(nuevaLista);
        return ordenarListaDTOMasReciente(listaNotificacionesDTO);
    }

    // Método auxiliar para verificar si una notificación ha sido leída por el usuario
    private boolean isNotificacionLeidaPorUsuario(Notificacion notificacion, Long userId) {
        for (UsuarioLeido usuarioLeido : notificacion.getListaDeusuariosLeidos()) {
            if (userId.equals(usuarioLeido.getIdUsuario())) {
                return true;
            }
        }
        return false;
    }

    // Método para transformar una lista de Notificacion a NotificacionDTO
    public List<NotificacionDTO> transformarListaNotificacionesADTO(List<Notificacion> listaNotificaciones) {
        List<NotificacionDTO> listaNotificacionesDTO = new ArrayList<>();
        for (Notificacion notificacion : listaNotificaciones) {
            listaNotificacionesDTO.add((NotificacionDTO) convertirDTO(notificacion));
        }
        return listaNotificacionesDTO;
    }

    // Ordena la lista de NotificacionDTO en base a la fecha en orden descendente
    public List<NotificacionDTO> ordenarListaDTOMasReciente(List<NotificacionDTO> listaNotificacionesDTO) {

        List<NotificacionDTO> nuevaLista = new ArrayList<>(listaNotificacionesDTO);


        nuevaLista.sort((n1, n2) -> {
            if (n1.getFecha() == null && n2.getFecha() == null) return 0;
            if (n1.getFecha() == null) return 1;
            if (n2.getFecha() == null) return -1;
            return n1.getFecha().compareTo(n2.getFecha()); // Orden descendente
        });
        return nuevaLista;
    }


    @Override
    public String setNotificacionLeidaPorUsuario(Long userId) {
        List<Notificacion> listaNotificaciones = notificacionRepo.findNotificacionesByUsuarioId(userId);
        List<UsuarioLeido> listaUsuariosLeidos = new ArrayList<>();

        for (Notificacion notificacion : listaNotificaciones) {

            for (UsuarioLeido usuarioLeido : notificacion.getListaDeusuariosLeidos()){
                if (!usuarioLeido.getIdUsuario().equals(userId)) {
                    UsuarioLeido nuevoUsuarioLeido = new UsuarioLeido();
                    nuevoUsuarioLeido.setIdUsuario(userId);
                    listaUsuariosLeidos.add(nuevoUsuarioLeido);
                }
            }
            notificacion.setListaDeusuariosLeidos(listaUsuariosLeidos);
            this.update(notificacion);
        }
        return "Notificaciones vistas";
    }


    @Override
    public String visualizarNotificacionesByUserID(Long userId) {
        List<Notificacion> listaNotificaciones = notificacionRepo.findNotificacionesByUsuarioId(userId);

        for (Notificacion notificacion : listaNotificaciones){
            UsuarioLeido  usuarioLeido = new UsuarioLeido();
            usuarioLeido.setIdUsuario(userId);

            notificacion.getListaDeusuariosLeidos().add(usuarioLeidoRepository.save(usuarioLeido));
            this.update(notificacion);
        }
        return "Notificaciones visualizadas";
    }


    @Override
    public List<Notificacion> findByLeidaFalse(Long usuarioId) {
        return notificacionRepo.findByLeidaFalse(usuarioId);
    }

}
