package com.example.facultades.fabricaDependencia;

import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.repository.*;
import com.example.facultades.util.IRepositoryFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryFactory implements IRepositoryFactory {

    private final Map<String, IGenericRepository> repositoryMap = new HashMap<>();

    public RepositoryFactory(IUsuarioRepository usuarioRepository,
                             IUniversidadRepository universidadRepository,
                             IRolRepository rolRepository,
                             INotificacionRepository notificacionRepository,
                             ICalificacionRepository calificacionRepository,
                             ICarreraRepository carreraRepository,
                             IComentarioRepository comentarioRepository,
                             IPermisoRepository permisoRepository,
                             IUsuarioLeidoRepository usuarioLeidoRepository,
                             IRespuestaRepository respuestaRepository,
                             IReaccionRepository reaccionRepository) {


        repositoryMap.put(NombreRepositorio.USUARIO.getRepoName(), usuarioRepository);
        repositoryMap.put(NombreRepositorio.UNIVERSIDAD.getRepoName(), universidadRepository);
        repositoryMap.put(NombreRepositorio.ROL.getRepoName(), rolRepository);
        repositoryMap.put(NombreRepositorio.NOTIFICACION.getRepoName(), notificacionRepository);
        repositoryMap.put(NombreRepositorio.CALIFICACION.getRepoName(), calificacionRepository);
        repositoryMap.put(NombreRepositorio.CARRERA.getRepoName(), carreraRepository);
        repositoryMap.put(NombreRepositorio.COMENTARIO.getRepoName(), comentarioRepository);
        repositoryMap.put(NombreRepositorio.PERMISO.getRepoName(), permisoRepository);
        repositoryMap.put(NombreRepositorio.USUARIO_LEIDO.getRepoName(), usuarioLeidoRepository);
        repositoryMap.put(NombreRepositorio.RESPUESTA.getRepoName(), respuestaRepository);
        repositoryMap.put(NombreRepositorio.REACCION.getRepoName(), reaccionRepository);
    }

    @Override
    public IGenericRepository generarRepositorio(String nombreRepositorio) {
        IGenericRepository repository = repositoryMap.get(nombreRepositorio);
        if (repository == null) {
            throw new IllegalArgumentException("Repositorio no encontrado: " + nombreRepositorio);
        }
        return repository;
    }

}
