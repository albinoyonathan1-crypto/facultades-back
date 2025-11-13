package com.example.facultades.util;

import com.example.facultades.dto.DetalleNotificacion;
import com.example.facultades.enums.MensajeNotificacionAdmin;
import com.example.facultades.enums.Socket;
import com.example.facultades.generics.BaseEntity;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.*;
import com.example.facultades.service.INotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utili {



    //Verifica si se agrego un nuevo comentario a la entidad
    public static  <E extends BaseEntity> Boolean verificarInsercionNuevoComentario(E entidad, IFindComenByEntity servicio, List<Comentario> listaComentarios){
        List<Comentario> listaGuardada = servicio.getAllComenByEntity(entidad.getId());

        if(listaComentarios != null && listaGuardada != null){
            if(listaComentarios.size() > listaGuardada.size()){
                return true;
            }else
                return false;
        }else {
            return false;
        }
    }

    public static Comentario recuperarUltimoComentario(List<Comentario> list, IgenericService<Comentario, Long> genericService){
        if(list != null && !list.isEmpty()){
            Long idComentario = list.get(list.size() -1 ).getId();
            return genericService.findById(idComentario).get();
        }
        return null;
    }

    public static <E extends BaseEntity & INotificable<E>> DetalleNotificacion generarDetalleNotificacion(String evento, E entidad){
        return new DetalleNotificacion(evento, entidad.getDetalleEvento(),entidad.getId());
    }



    public static <E extends BaseEntity & INotificable<E>> void manejarNotificacionAdmin(String evento, E entidadGuardada, INotificacionService notificacionService, Notificacion notificacion){
        DetalleNotificacion detalleNotificacion = new DetalleNotificacion(evento, entidadGuardada.getDetalleEvento(), entidadGuardada.getId());
        notificacionService.enviarNotificacionByWebSocket(Socket.ADMIN_PREFIJO.getRuta(),detalleNotificacion);
        notificacionService.guardarNotificacionAdmin(entidadGuardada.getId(), evento, notificacion);
    }

    public static String obtenerRol(Authentication authentication){
        List<String> listaAutitys = convertirAuthoritysAListString(authentication);
        String role = "";
        for (String authority : listaAutitys) {
            if(authority.equals("ROLE_ADMIN") || authority.equals("ROLE_USER") || authority.equals("ROLE_AUTOR") ) {
                //Validar que si un usuario tiene mas de un rol se guarde el rol con mayor permiso
                return role = authority;
            }
        }return null;
    }

    public static List<String> convertirAuthoritysAListString(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Mapea cada GrantedAuthority a su String
                .collect(Collectors.toList());  // Convierte a lista
    }

    public static List<SimpleGrantedAuthority> crearAuthotitys(Usuario usuario){
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        usuario.getListaRoles()
                .stream()
                .forEach(roles -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(roles.getNombreRol()))));

        usuario.getListaRoles()
                .stream()
                .flatMap(role -> role.getListaPermiso().stream())
                .forEach(permiso -> authorityList.add(new SimpleGrantedAuthority(permiso.getNombrePermiso())));

        return  authorityList;
    }


}
