package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.MensajeDTO;
import com.example.facultades.dto.NotificacionDTO;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Mensaje;
import com.example.facultades.model.Notificacion;
import com.example.facultades.model.Usuario;
import com.example.facultades.repository.IMensajeRepository;
import com.example.facultades.repository.IUsuarioRepository;
import com.example.facultades.util.IEntidadAsociable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MensajeService extends GenericService<Mensaje, Long> implements IMensajeService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IMensajeRepository iMensajeRepository;

    @Override
    public BaseDTO<Mensaje> convertirDTO(Mensaje mensaje) {
        return modelMapper.map(mensaje, MensajeDTO.class);
    }

    @Override
    public Mensaje converirEntidad(BaseDTO<Mensaje> DTO) {
        return modelMapper.map(DTO, Mensaje.class);
    }

    public void agregarIdReceptorAListaIdsEmisor(Long idEmisor, Long idReceptor){
        Usuario usuario = usuarioRepository.findById(idEmisor).get();
        if(!usuario.getIdsUsuariosNotificar().contains(idReceptor)) {
            usuario.getIdsUsuariosNotificar().add(idReceptor);
        }
        usuarioRepository.save(usuario);
    }

    @Override
    public List<MensajeDTO> findByEmisorAndReceptor(Long emisor, Long receptor) {
        List<Mensaje> listaMensajes =  iMensajeRepository.findMensajesEntreUsuarios(emisor, receptor);
        List<MensajeDTO> listaMensajesDTO = new ArrayList<>();
        for (Mensaje mensaje:listaMensajes) {
            listaMensajesDTO.add( (MensajeDTO) this.convertirDTO(mensaje));
        }
        return  listaMensajesDTO;
    }
}
