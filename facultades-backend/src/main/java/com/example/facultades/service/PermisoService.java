package com.example.facultades.service;


import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.dto.PermisoDTO;
import com.example.facultades.enums.Socket;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Permiso;
import com.example.facultades.util.Utili;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermisoService extends GenericService<Permiso, Long> implements IPermisoService {

    @Autowired
    private IGenericRepository<Permiso, Long> genericRepository;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public Permiso save(Permiso permiso){
        Permiso permisoGuardado = genericRepository.save(permiso);
        //notificacionService.enviarNotificacionByWebSocket("/tema/admin/notificacion", "Se creo un nuevo permiso");
        Notificacion notificacion = new Notificacion();
        notificacion.setPermiso(true);
        DetalleNotificacion detalleNotificacion = Utili.generarDetalleNotificacion("Se creo un nuevo permiso", permisoGuardado);
        notificacionService.enviarNotificacionByWebSocket(Socket.ADMIN_PREFIJO.getRuta(), detalleNotificacion);
        notificacionService.guardarNotificacionAdmin(permisoGuardado.getId(), "Se creo un permiso :", notificacion);
        return permisoGuardado;
    }

    @Override
    public BaseDTO<Permiso> convertirDTO(Permiso permiso) {
        return modelMapper.map(permiso, PermisoDTO.class);
    }

    @Override
    public Permiso converirEntidad(BaseDTO<Permiso> DTO) {
        return modelMapper.map(DTO, Permiso.class);
    }

}
